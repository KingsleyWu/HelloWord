package com.kingsley.tetris

import com.kingsley.tetris.piece.PieceFactory.createPiece
import android.widget.GridView
import com.kingsley.tetris.view.LedTextView
import com.kingsley.tetris.view.ShadowImageView
import com.kingsley.tetris.piece.Piece
import com.kingsley.tetris.view.GameOverView
import android.widget.LinearLayout
import android.widget.TextView
import com.kingsley.tetris.util.ConfigSPUtils
import android.text.TextUtils
import com.kingsley.tetris.bean.RecordListBean
import com.kingsley.tetris.bean.RecordBean
import android.content.DialogInterface
import com.kingsley.tetris.dialog.NewRecordDialog
import com.kingsley.tetris.util.StateSPUtils
import com.kingsley.tetris.bean.StateBean
import android.content.Intent
import android.os.*
import android.view.View
import android.view.Window
import com.google.gson.Gson
import com.kingsley.tetris.util.ConfigSPUtils.RECORDLIST
import com.kingsley.tetris.util.StateSPUtils.STATEBEAN
import java.util.*

class MainActivity : BaseActivity(), View.OnClickListener {
    private var gvBlockBoard: GridView? = null
    private var tvScore: LedTextView? = null
    private var tvLevel: LedTextView? = null
    private var tvMaxScore: LedTextView? = null
    private var gvNextPiece: GridView? = null
    private var btnPause: ShadowImageView? = null
    private var btnRecordList: ShadowImageView? = null
    private var btnRestart: ShadowImageView? = null
    private var btnSpace: ShadowImageView? = null
    private var btnUp: ShadowImageView? = null
    private var btnLeft: ShadowImageView? = null
    private var btnRight: ShadowImageView? = null
    private var btnDown: ShadowImageView? = null
    private var currentPiece: Piece? = null
    private var govAnim: GameOverView? = null
    private var llAnim: LinearLayout? = null
    private var tvUserName: TextView? = null

    //方块片左下角在整个界面的行和列
    private var row = 0
    private var column = 0
    private var nextPiece: Piece? = null

    //界面中的方块片数组
    private var currentPieceArray: IntArray? = null

    //“下一个”方块片数组
    private var nextPieceArray: IntArray? = null

    //已经确定的（不含空中方块片）的界面数组
    private var blockBoardArray: IntArray? = null

    //用于更新界面的数组（可含空中方块片，也可不含空中方块片）
    private var tempBlockBoardArray: IntArray? = null
    private var nextPieceAdapter: BlockAdapter? = null
    private var blockBoardAdapter: BlockAdapter? = null

    //非正在重玩状态
    private var isStart = true

    //方块片下落定时器
    private var downTimer: Timer? = null

    //方块片快速下落定时器
    private var spaceTimer: Timer? = null

    //下落的时间间隔
    private var timeInterval = 800

    //等级
    private var level = 1

    //分数
    private var score = 0

    //同一次消除方块片，每一行所获得的分数
    private var scoreStep = 100
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_tetris)
        initView()
        initData()
        handlerThread = HandlerThread(TAG)
        handlerThread!!.start()
        handler = object : Handler(handlerThread!!.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    RESTART -> {
                        isStart = false
                        cancelDownTimer()
                        cancelSpaceTimer()
                        run {
                            var i = BOARD_ROW
                            while (i > 0) {
                                try {
                                    Thread.sleep(100)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                                var j = 1
                                while (j <= BOARD_COLUMN) {
                                    tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] = 1
                                    j++
                                }
                                uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                                i--
                            }
                        }
                        var i = 1
                        while (i <= BOARD_ROW) {
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            var j = 1
                            while (j <= BOARD_COLUMN) {
                                tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] = 0
                                j++
                            }
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                            i++
                        }
                        blockBoardArray = (tempBlockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
                        isStart = true
                        uiHandler.sendEmptyMessage(RESET_DATA)
                    }
                    UP -> {
                        currentPieceArray = currentPiece!!.nextStatePieceArray()
                        if (isCollision) {
                            currentPieceArray = currentPiece!!.previousStatePieceArray()
                        } else {
                            setTempBlockBoardArray()
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                        }
                    }
                    LEFT -> {
                        column--
                        if (isCollision) {
                            column++
                        } else {
                            setTempBlockBoardArray()
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                        }
                    }
                    RIGHT -> {
                        column++
                        if (isCollision) {
                            column--
                        } else {
                            setTempBlockBoardArray()
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                        }
                    }
                    DOWN -> {
                        row++
                        if (isCollision) {
                            row--
                            touchBottom()
                        } else {
                            setTempBlockBoardArray()
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                        }
                    }
                    else -> {}
                }
            }//这里是为了防止游戏结束还在touchBottom，还在REFRESH_NEXT_PIECE

            /**
             *
             * @return 下落或者左移右移旋转中的方块片是否与界面已有方块冲突
             */
            private val isCollision: Boolean
                get() {
                    tempBlockBoardArray = (blockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
                    var count = 0
                    var i = row
                    while (i > row - PIECE_ROW && i > 0) {
                        for (j in column until column + PIECE_COLUMN) {
                            if (i <= BOARD_ROW && j >= 1 && j <= BOARD_COLUMN) {
                                val index = (PIECE_ROW - (row - i) - 1) * PIECE_COLUMN + (j - column)
                                if (currentPieceArray!!.size > index && currentPieceArray!![index] != 0) {
                                    if (tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] != 0) {
                                        if (row <= currentPiece!!.initialColumn) {
                                            uiHandler.sendEmptyMessage(GAME_OVER)
                                            //这里是为了防止游戏结束还在touchBottom，还在REFRESH_NEXT_PIECE
                                            return false
                                        }
                                        return true
                                    }
                                    count++
                                }
                            }
                        }
                        i--
                    }
                    if (count == PIECE_ROW) {
                        return false
                    }
                    return if (row <= PIECE_ROW) {
                        currentPiece!!.isCollision(column)
                    } else true
                }

            private fun setTempBlockBoardArray() {
                tempBlockBoardArray = (blockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
                var i = row
                while (i > row - PIECE_ROW && i > 0) {
                    for (j in column until column + PIECE_COLUMN) {
                        if (i <= BOARD_ROW && j >= 1 && j <= BOARD_COLUMN) {
                            if (tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] == 0) {
                                tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] =
                                    currentPieceArray!![(PIECE_ROW - (row - i) - 1) * PIECE_COLUMN + (j - column)]
                            }
                        }
                    }
                    i--
                }
            }

            /**
             * 方块片到界面底部了
             */
            private fun touchBottom() {
                cancelSpaceTimer()
                cancelDownTimer()
                uiHandler.sendEmptyMessage(PAUSE)
                setTempBlockBoardArray()
                //触底先保存现在的界面状态
                blockBoardArray = (tempBlockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
                //再消除满行
                lineDispear()
                //第一行有亮方块则游戏结束
                for (i in 0 until BOARD_COLUMN) {
                    if (blockBoardArray!![i] != 0) {
                        uiHandler.sendEmptyMessage(GAME_OVER)
                        return
                    }
                }
                row = currentPiece!!.initialRow - 1
                currentPiece = nextPiece
                currentPieceArray = currentPiece!!.getPieceArray()
                column = currentPiece!!.initialColumn
                nextPiece = createPiece()
                nextPieceArray = nextPiece!!.getSimplePieceArray()
                scoreStep = 100
                uiHandler.sendEmptyMessage(REFRESH_NEXT_PIECE)
                uiHandler.sendEmptyMessage(RESUME)
                downTimer = Timer()
                downTimer!!.schedule(timerTask, timeInterval.toLong(), timeInterval.toLong())
            }

            /**
             * 消除满行
             */
            private fun lineDispear() {
                for (i in BOARD_ROW downTo 1) {
                    var count = 0
                    for (j in 1..BOARD_COLUMN) {
                        if (tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] == 1) {
                            count++
                        }
                    }
                    if (count == BOARD_COLUMN) {
                        val splashCount = 5
                        for (k in 0 until splashCount) {
                            for (j in 1..BOARD_COLUMN) {
                                tempBlockBoardArray!![(i - 1) * BOARD_COLUMN + j - 1] = if (k / 2 == 0) 0 else 1
                            }
                            uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        for (x in i downTo 2) {
                            System.arraycopy(
                                tempBlockBoardArray!!,
                                (x - 2) * 10 + 1 - 1,
                                tempBlockBoardArray!!,
                                (x - 1) * 10 + 1 - 1,
                                BOARD_COLUMN
                            )
                        }
                        for (x in 0 until BOARD_COLUMN) {
                            tempBlockBoardArray!![x] = 0
                        }
                        blockBoardArray = (tempBlockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
                        uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
                        score += scoreStep
                        scoreStep += 20
                        uiHandler.sendEmptyMessage(REFRESH_SCORE)
                        lineDispear()
                        break
                    }
                }
            }
        }
    }

    private val uiHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                REFRESH_BLOCK_BOARD -> blockBoardAdapter!!.setColors(tempBlockBoardArray)
                REFRESH_NEXT_PIECE -> nextPieceAdapter!!.setColors(nextPieceArray)
                PAUSE_RESUME -> if (!govAnim!!.isRunning && downTimer == null) {
                    downTimer = Timer()
                    downTimer!!.schedule(timerTask, timeInterval.toLong(), timeInterval.toLong())
                    btnSpace!!.isEnabled = true
                    btnUp!!.isEnabled = true
                    btnLeft!!.isEnabled = true
                    btnRight!!.isEnabled = true
                    btnDown!!.isEnabled = true
                } else {
                    cancelSpaceTimer()
                    cancelDownTimer()
                    btnSpace!!.isEnabled = false
                    btnUp!!.isEnabled = false
                    btnLeft!!.isEnabled = false
                    btnRight!!.isEnabled = false
                    btnDown!!.isEnabled = false
                }
                PAUSE -> {
                    btnSpace!!.isEnabled = false
                    btnUp!!.isEnabled = false
                    btnLeft!!.isEnabled = false
                    btnRight!!.isEnabled = false
                    btnDown!!.isEnabled = false
                }
                RESUME -> {
                    btnSpace!!.isEnabled = true
                    btnUp!!.isEnabled = true
                    btnLeft!!.isEnabled = true
                    btnRight!!.isEnabled = true
                    btnDown!!.isEnabled = true
                }
                REFRESH_SCORE -> {
                    tvScore!!.text = score.toString()
                    when {
                        score <= 1000 -> {
                            level = 1
                            timeInterval = 800
                        }
                        score <= 2000 -> {
                            level = 2
                            timeInterval = 750
                        }
                        score <= 3000 -> {
                            level = 3
                            timeInterval = 700
                        }
                        score <= 5000 -> {
                            level = 4
                            timeInterval = 650
                        }
                        score <= 7500 -> {
                            level = 5
                            timeInterval = 600
                        }
                        score <= 10000 -> {
                            level = 6
                            timeInterval = 550
                        }
                        score <= 12500 -> {
                            level = 7
                            timeInterval = 500
                        }
                        score <= 15000 -> {
                            level = 8
                            timeInterval = 450
                        }
                        else -> {
                            level = 9
                            timeInterval = 400
                        }
                    }
                    tvLevel!!.text = level.toString()
                }
                GAME_OVER -> {
                    cancelSpaceTimer()
                    cancelDownTimer()
                    llAnim!!.visibility = View.VISIBLE
                    govAnim!!.start()
                    val recordList = ConfigSPUtils.getString(application, RECORDLIST)
                    if (!TextUtils.isEmpty(recordList)) {
                        val gson = Gson()
                        val (recordBeanList) = gson.fromJson(recordList, RecordListBean::class.java)
                        if (recordBeanList != null) {
                            val (_, score1) = recordBeanList[0]
                            val lastScore = score1.toInt()
                            if (score > lastScore) {
                                showNewRecordDialog(recordBeanList)
                            }
                        }
                    } else {
                        if (score > 0) {
                            showNewRecordDialog(null)
                        }
                    }
                }
                RESET_DATA -> resetData()
                else -> {}
            }
        }
    }

    private fun showNewRecordDialog(recordBeanList: List<RecordBean>?) {
        val builder = NewRecordDialog.Builder(this@MainActivity, true) { dialog: DialogInterface ->
            val newRecordDialog = dialog as NewRecordDialog
            val listBean = RecordListBean()
            val beanList: MutableList<RecordBean> = ArrayList()
            if (recordBeanList != null) {
                beanList.addAll(recordBeanList)
            }
            val bean = RecordBean()
            if (TextUtils.isEmpty(newRecordDialog.userName)) {
                bean.name = newRecordDialog.userNameHint
            } else {
                bean.name = newRecordDialog.userName
            }
            bean.score = score.toString()
            bean.time = System.currentTimeMillis().toString()
            beanList.add(0, bean)
            listBean.recordBeanList = beanList
            ConfigSPUtils.putString(application, RECORDLIST, Gson().toJson(listBean))
        }.setUserNameHint(R.string.user_name_hint).setScoreValue(score.toString())
        builder.builder().show()
    }

    private fun initView() {
        gvBlockBoard = findViewById(R.id.gv_block_board)
        tvScore = findViewById(R.id.tv_score)
        tvLevel = findViewById(R.id.tv_level)
        tvMaxScore = findViewById(R.id.tv_max_score)
        gvNextPiece = findViewById(R.id.gv_next_piece)
        btnPause = findViewById(R.id.btn_pause)
        btnPause?.setOnClickListener(this)
        btnRecordList = findViewById(R.id.btn_record_list)
        btnRecordList?.setOnClickListener(this)
        btnRestart = findViewById(R.id.btn_restart)
        btnRestart?.setOnClickListener(this)
        btnSpace = findViewById(R.id.btn_space)
        btnSpace?.setOnClickListener(this)
        btnUp = findViewById(R.id.btn_up)
        btnUp?.setOnClickListener(this)
        btnLeft = findViewById(R.id.btn_left)
        btnLeft?.setOnClickListener(this)
        btnRight = findViewById(R.id.btn_right)
        btnRight?.setOnClickListener(this)
        btnDown = findViewById(R.id.btn_down)
        btnDown?.setOnClickListener(this)
        govAnim = findViewById(R.id.gov_anim)
        llAnim = findViewById(R.id.ll_anim)
        tvUserName = findViewById(R.id.tv_user_name)
    }

    private fun initData() {
        govAnim!!.stop()
        llAnim!!.visibility = View.GONE
        tvMaxScore!!.text = "0"
        val recordList = ConfigSPUtils.getString(application, RECORDLIST)
        if (!TextUtils.isEmpty(recordList)) {
            val gson = Gson()
            val (recordBeanList) = gson.fromJson(recordList, RecordListBean::class.java)
            if (recordBeanList != null && recordBeanList.isNotEmpty()) {
                val (name, score1) = recordBeanList[0]
                tvUserName!!.text = name
                val lastScore = score1.toInt()
                tvMaxScore!!.text = lastScore.toString()
            }
        }
        val stateStr = StateSPUtils.getString(application, STATEBEAN)
        if (TextUtils.isEmpty(stateStr)) {
            currentPiece = createPiece()
            currentPiece!!.getSimplePieceArray()
            row = currentPiece!!.initialRow - 1
            column = currentPiece!!.initialColumn
            currentPieceArray = currentPiece!!.getPieceArray()
            nextPiece = createPiece()
            nextPieceArray = nextPiece!!.getSimplePieceArray()
            blockBoardArray = IntArray(BOARD_ROW * BOARD_COLUMN)
            tempBlockBoardArray = IntArray(BOARD_ROW * BOARD_COLUMN)
            for (i in 0 until BOARD_ROW * BOARD_COLUMN) {
                blockBoardArray!![i] = 0
            }
            tempBlockBoardArray = (blockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
            score = 0
            level = 1
        } else {
            val gson = Gson()
            val (row1, column1, currentPieceArray1, currentShape, currentState, nextPieceArray1, nextShape, nextState, blockBoardArray1, tempBlockBoardArray1, level1, score1) = gson.fromJson(
                stateStr,
                StateBean::class.java
            )
            row = row1
            column = column1
            currentPieceArray = currentPieceArray1
            nextPieceArray = nextPieceArray1
            blockBoardArray = blockBoardArray1
            tempBlockBoardArray = tempBlockBoardArray1
            currentPiece = createPiece(currentShape, currentState)
            nextPiece = createPiece(nextShape, nextState)
            score = score1
            level = level1
        }
        tvScore!!.text = score.toString()
        tvLevel!!.text = level.toString()
        nextPieceAdapter = BlockAdapter()
        gvNextPiece!!.adapter = nextPieceAdapter
        blockBoardAdapter = BlockAdapter()
        gvBlockBoard!!.adapter = blockBoardAdapter
        uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
        nextPieceAdapter!!.setColors(nextPieceArray)
        uiHandler.sendEmptyMessage(RESUME)
        downTimer = Timer()
        downTimer!!.schedule(timerTask, timeInterval.toLong(), timeInterval.toLong())
    }

    private fun resetData() {
        govAnim!!.stop()
        llAnim!!.visibility = View.GONE
        score = 0
        level = 1
        tvScore!!.text = score.toString()
        tvLevel!!.text = level.toString()
        tvMaxScore!!.text = "0"
        val recordList = ConfigSPUtils.getString(application, RECORDLIST)
        if (!TextUtils.isEmpty(recordList)) {
            val gson = Gson()
            val (recordBeanList) = gson.fromJson(recordList, RecordListBean::class.java)
            val (name, score1) = recordBeanList!![0]
            tvUserName!!.text = name
            val lastScore = score1.toInt()
            tvMaxScore!!.text = lastScore.toString()
        }
        currentPiece = createPiece()
        currentPiece!!.getSimplePieceArray()
        row = currentPiece!!.initialRow - 1
        column = currentPiece!!.initialColumn
        currentPieceArray = currentPiece!!.getPieceArray()
        nextPiece = createPiece()
        nextPieceArray = nextPiece!!.getSimplePieceArray()
        blockBoardArray = IntArray(BOARD_ROW * BOARD_COLUMN)
        tempBlockBoardArray = IntArray(BOARD_ROW * BOARD_COLUMN)
        for (i in 0 until BOARD_ROW * BOARD_COLUMN) {
            blockBoardArray!![i] = 0
        }
        tempBlockBoardArray = (blockBoardArray!!).copyOf(BOARD_ROW * BOARD_COLUMN)
        nextPieceAdapter = BlockAdapter()
        gvNextPiece!!.adapter = nextPieceAdapter
        blockBoardAdapter = BlockAdapter()
        gvBlockBoard!!.adapter = blockBoardAdapter
        uiHandler.sendEmptyMessage(REFRESH_BLOCK_BOARD)
        nextPieceAdapter!!.setColors(nextPieceArray)
        uiHandler.sendEmptyMessage(RESUME)
        downTimer = Timer()
        downTimer!!.schedule(timerTask, timeInterval.toLong(), timeInterval.toLong())
    }

    private val timerTask: TimerTask
        get() = object : TimerTask() {
            override fun run() {
                handler!!.sendEmptyMessage(DOWN)
            }
        }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_pause) {
            if (isStart) {
                cancelSpaceTimer()
                uiHandler.sendEmptyMessage(PAUSE_RESUME)
            }
        } else if (id == R.id.btn_record_list) {
            if (isStart) {
                val intent = Intent(this, RecordListActivity::class.java)
                startActivity(intent)
            }
        } else if (id == R.id.btn_restart) {
            if (isStart) {
                handler!!.sendEmptyMessage(RESTART)
            }
        } else if (id == R.id.btn_space) {
            if (!govAnim!!.isRunning) {
                btnSpace!!.isEnabled = false
                cancelDownTimer()
                cancelSpaceTimer()
                spaceTimer = Timer()
                spaceTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        handler!!.sendEmptyMessage(DOWN)
                    }
                }, 0, 20)
            }
        } else if (id == R.id.btn_up) {
            handler!!.sendEmptyMessage(UP)
        } else if (id == R.id.btn_left) {
            handler!!.sendEmptyMessage(LEFT)
        } else if (id == R.id.btn_right) {
            handler!!.sendEmptyMessage(RIGHT)
        } else if (id == R.id.btn_down) {
            handler!!.sendEmptyMessage(DOWN)
        }
    }

    override fun onResume() {
        super.onResume()
        cancelSpaceTimer()
        cancelDownTimer()
        downTimer = Timer()
        downTimer!!.schedule(timerTask, timeInterval.toLong(), timeInterval.toLong())
        uiHandler.sendEmptyMessage(RESUME)
    }

    override fun onPause() {
        super.onPause()
        cancelSpaceTimer()
        cancelDownTimer()
        uiHandler.sendEmptyMessage(PAUSE)
    }

    override fun onStop() {
        val stateBean = StateBean()
        stateBean.row = row
        stateBean.column = column
        stateBean.currentPieceArray = currentPieceArray
        stateBean.currentShape = currentPiece!!.shape
        stateBean.currentState = currentPiece!!.state
        stateBean.nextPieceArray = nextPieceArray
        stateBean.nextShape = nextPiece!!.shape
        stateBean.nextState = nextPiece!!.state
        stateBean.blockBoardArray = blockBoardArray
        stateBean.tempBlockBoardArray = tempBlockBoardArray
        stateBean.level = level
        stateBean.score = score
        StateSPUtils.putString(application, STATEBEAN, Gson().toJson(stateBean))
        super.onStop()
    }

    override fun onDestroy() {
        cancelSpaceTimer()
        cancelDownTimer()
        handlerThread!!.quit()
        govAnim!!.stop()
        super.onDestroy()
    }

    private fun cancelDownTimer() {
        if (downTimer != null) {
            downTimer!!.cancel()
            downTimer = null
        }
    }

    private fun cancelSpaceTimer() {
        if (spaceTimer != null) {
            spaceTimer!!.cancel()
            spaceTimer = null
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        //界面的行数和列数
        private const val BOARD_ROW = 10
        const val BOARD_COLUMN = 10

        //方块片的行数和列数
        private const val PIECE_ROW = 5
        private const val PIECE_COLUMN = 5
        private const val RESTART = 1
        private const val UP = 2
        private const val LEFT = 3
        private const val RIGHT = 4
        private const val DOWN = 5
        private const val REFRESH_BLOCK_BOARD = 100
        private const val REFRESH_NEXT_PIECE = 101
        private const val PAUSE_RESUME = 102
        private const val PAUSE = 103
        private const val RESUME = 104
        private const val REFRESH_SCORE = 105
        private const val GAME_OVER = 106
        private const val RESET_DATA = 107
    }
}