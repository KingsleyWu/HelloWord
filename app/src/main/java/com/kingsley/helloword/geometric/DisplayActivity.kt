package com.kingsley.helloword.geometric

import android.app.AlertDialog
import com.kingsley.helloword.data.ShapeHolder.setSize
import com.kingsley.helloword.data.ShapeHolder.getHolderList
import com.kingsley.helloword.data.ShapeHolder.add
import com.kingsley.helloword.data.ShapeProcess.setHolder
import com.kingsley.helloword.data.Shape.moveDirection
import com.kingsley.helloword.data.Shape.deflectDegree
import com.kingsley.helloword.data.ShapeProcess.rotate
import com.kingsley.helloword.data.Shape.lines
import com.kingsley.helloword.data.ShapeDB.addChairsAndDesk
import com.kingsley.helloword.data.ShapeDB.add2
import android.view.View.OnTouchListener
import android.os.Bundle
import com.kingsley.helloword.R
import android.util.DisplayMetrics
import android.graphics.PorterDuff
import android.widget.Toast
import android.widget.TextView
import android.widget.EditText
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.*
import android.widget.Button
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.data.*
import java.io.*
import java.lang.Exception
import java.util.*

class DisplayActivity : BaseActivity(), OnTouchListener, SurfaceHolder.Callback {
    /**
     * SurfaceView对象
     */
    private var surface: SurfaceView? = null

    /**
     * SurfaceHolder对象
     */
    private var sHolder: SurfaceHolder? = null

    /**
     * 画布背景
     */
    private var background = Color.WHITE

    /**
     * 画线的Paint对象
     */
    private val edgePaint = Paint()

    /**
     * 中心画笔
     */
    private val centerPaint = Paint()

    /**
     * 手机屏幕的宽高
     */
    var width = 0f
    var height = 0f

    /**
     * 模型容器
     */
    private var shapeHolder = ShapeHolder()

    /**
     * 模型容器集合批处理器
     */
    private val process = ShapeProcess(shapeHolder)

    /**
     * 判断画布是否锁定，若画布没有锁定则可以设置笔画，锁定则旋转模型
     */
    private var canDraw = false

    /**
     * 字体笔画长方体的宽高，即立体笔画粗细
     */
    private val wordWidth = 20f
    private val wordHeight = 20f

    /**
     * 字体笔画触屏的起始和终点屏幕坐标
     */
    private var wordStart = 0f
    private var wordEnd = 0f

    /**
     * 先前鼠标所处坐标x
     */
    private var preX = 0f

    /**
     * 先前鼠标所处坐标y
     */
    private var preY = 0f

    /**
     * 放大倍数
     */
    private val biggerSize = 1.1.toFloat()

    /**
     * 缩小倍数
     */
    private val smallerSize = 0.9.toFloat()

    /**
     * 保存文件名
     */
    private var fileName = "fontdata.file"

    /**
     * 锁定控件
     */
    private var lock: Button? = null

    /**
     * 添加形状的对话框
     */
    var addShapeDialog: AlertDialog? = null

    /**
     * 添加形状数据对话框
     */
    var addShapeDetailDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        //获取屏幕宽高
        val dm = resources.displayMetrics
        width = (dm.widthPixels / 2).toFloat()
        height = (dm.heightPixels / 2).toFloat()
        //初始化控件
        surface = findViewById<View>(R.id.surfaceview) as SurfaceView
        lock = findViewById<View>(R.id.lock) as Button
        sHolder = surface!!.holder
        sHolder.addCallback(this) //生命周期
        surface!!.setOnTouchListener(this)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.bigger -> {
                shapeHolder.setSize(biggerSize)
                //在锁定情况下放大会导致无法消除跟踪路径，所以另需两步
                if (canDraw) {
                    canDraw = !canDraw
                    drawDetail(0f, 0f)
                    canDraw = !canDraw
                } else {
                    drawDetail(0f, 0f)
                }
            }
            R.id.smaller -> {
                shapeHolder.setSize(smallerSize)
                //在锁定情况下缩小会导致无法消除跟踪路径，所以另需两步
                if (canDraw) {
                    canDraw = !canDraw
                    drawDetail(0f, 0f)
                    canDraw = !canDraw
                } else {
                    drawDetail(0f, 0f)
                }
            }
            R.id.add -> if (addShapeDialog != null) {
                addShapeDialog!!.show()
            } else {
                createAddShapeDialog()
            }
            R.id.clear -> {
                shapeHolder.getHolderList().clear()
                val canvas = sHolder!!.lockCanvas()
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                canvas.drawColor(background)
                sHolder!!.unlockCanvasAndPost(canvas)
            }
            R.id.lock -> {
                if (lock!!.text == "锁定") {
                    lock!!.text = "解锁"
                    findViewById<View>(R.id.read).visibility = View.INVISIBLE
                    findViewById<View>(R.id.save).visibility = View.INVISIBLE
                } else {
                    lock!!.text = "锁定"
                    findViewById<View>(R.id.read).visibility = View.VISIBLE
                    findViewById<View>(R.id.save).visibility = View.VISIBLE
                }
                canDraw = !canDraw
            }
            R.id.save -> saveFile(fileName) //保存
            R.id.read -> readFile(fileName) //读取
            R.id.random -> {
                //清除
                shapeHolder.getHolderList().clear()
                shapeHolder.add(createRandomShape()!!)
                drawDetail(0f, 0f)
            }
            R.id.back -> finish()
            else -> {
            }
        }
    }

    /**
     * 创建一个随机模型
     */
    private fun createRandomShape(): Shape? {
        var randomShape: Shape? = null
        val random = Random()
        when (random.nextInt(8)) {
            0 -> randomShape = Cuboid(
                (random.nextInt(500) + 200).toFloat(),
                (random.nextInt(500) + 200).toFloat(),
                (random.nextInt(500) + 200).toFloat()
            )
            1 -> randomShape = Prism(3, (random.nextInt(500) + 200).toFloat(), (random.nextInt(500) + 200).toFloat())
            2, 3 -> randomShape = Prism(
                random.nextInt(6) + 5,
                (random.nextInt(300) + 200).toFloat(),
                (random.nextInt(500) + 200).toFloat()
            )
            4 -> randomShape = Pyramid(3, (random.nextInt(500) + 200).toFloat(), (random.nextInt(500) + 200).toFloat())
            5, 6 -> randomShape = Pyramid(
                random.nextInt(6) + 5,
                (random.nextInt(300) + 200).toFloat(),
                (random.nextInt(500) + 200).toFloat()
            )
            7 -> {
                val length = (random.nextInt(500) + 200).toFloat()
                randomShape = Cuboid(length, length, length)
            }
            else -> {
            }
        }
        return randomShape
    }

    /**
     * 保存的字体文件
     *
     * @param fileName 文件名
     */
    private fun saveFile(fileName: String) {
        try {
            val file = File(this@DisplayActivity.filesDir.toString() + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val wirte: ObjectOutput = ObjectOutputStream(FileOutputStream(file))
            wirte.writeObject(shapeHolder)
            wirte.flush()
            wirte.close()
            Toast.makeText(this@DisplayActivity, "保存成功：$fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this@DisplayActivity, "写入错误：" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 读取保存的字体文件
     *
     * @param fileName 文件名
     */
    private fun readFile(fileName: String) {
        try {
            val read: ObjectInput =
                ObjectInputStream(FileInputStream(this@DisplayActivity.filesDir.toString() + fileName))
            val readHolder = read.readObject() as ShapeHolder
            read.close()
            shapeHolder = readHolder
            process.setHolder(shapeHolder)
            drawDetail(0f, 0f)
            Toast.makeText(this@DisplayActivity, "读取成功:$fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this@DisplayActivity, "读取错误：" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 创建添加模型对话框
     */
    private fun createAddShapeDialog() {
        val builder = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_shape, null)
        //点击各个模型类别分别将进行不同处理
        val listener = View.OnClickListener { v -> createAddShapeDetailDialog(v) }
        view.findViewById<View>(R.id.add_shape_cube).setOnClickListener(listener)
        view.findViewById<View>(R.id.add_shape_cuboid).setOnClickListener(listener)
        view.findViewById<View>(R.id.add_shape_prism).setOnClickListener(listener)
        view.findViewById<View>(R.id.add_shape_pyramid).setOnClickListener(listener)
        //关闭对话框
        view.findViewById<View>(R.id.add_shape_exit).setOnClickListener { addShapeDialog!!.dismiss() }
        builder.setView(view)
        addShapeDialog = builder.show()
    }

    /**
     * 添加模型数据对话框
     *
     * @param v 点击控件
     */
    private fun createAddShapeDetailDialog(v: View) {
        val builder = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_shape_detail, null)
        val title = view.findViewById<View>(R.id.add_shape_detail_title) as TextView
        val oneText = view.findViewById<View>(R.id.add_shape_detail_onetext) as TextView
        val twoText = view.findViewById<View>(R.id.add_shape_detail_twotext) as TextView
        val threeText = view.findViewById<View>(R.id.add_shape_detail_threetext) as TextView
        val oneEdit = view.findViewById<View>(R.id.add_shape_detail_oneedit) as EditText
        val twoEdit = view.findViewById<View>(R.id.add_shape_detail_twoedit) as EditText
        val threeEdit = view.findViewById<View>(R.id.add_shape_detail_threeedit) as EditText
        val movex = view.findViewById<View>(R.id.add_shape_detail_movex) as EditText
        val movey = view.findViewById<View>(R.id.add_shape_detail_movey) as EditText
        val movez = view.findViewById<View>(R.id.add_shape_detail_movez) as EditText
        val submit = view.findViewById<View>(R.id.add_shape_submit) as Button
        //取消按钮
        view.findViewById<View>(R.id.add_shape_back).setOnClickListener { addShapeDetailDialog!!.dismiss() }
        when (v.id) {
            R.id.add_shape_cube -> {
                title.text = "正方体(单位:px)"
                oneText.text = "边长"
                twoText.visibility = View.INVISIBLE
                twoEdit.visibility = View.INVISIBLE
                threeText.visibility = View.INVISIBLE
                threeEdit.visibility = View.INVISIBLE
            }
            R.id.add_shape_prism -> {
                title.text = "多棱柱(单位:px)"
                oneText.text = "棱数"
                twoText.text = "边长"
            }
            R.id.add_shape_pyramid -> {
                title.text = "多棱锥(单位:px)"
                oneText.text = "棱数"
                twoText.text = "边长"
            }
            else -> {
            }
        }
        //执行提交按钮
        submit.setOnClickListener(View.OnClickListener {
            var one = 0f
            var two = 0f
            var three = 0f
            var x = 0f
            var y = 0f
            var z = 0f
            try {
                one = java.lang.Float.valueOf(oneEdit.text.toString())
                //当选择模型为正方体时，two和three控件不显示，需要跳过
                if (v.id != R.id.add_shape_cube) {
                    two = java.lang.Float.valueOf(twoEdit.text.toString())
                    three = java.lang.Float.valueOf(threeEdit.text.toString())
                }
                x = java.lang.Float.valueOf(movex.text.toString())
                y = java.lang.Float.valueOf(movey.text.toString())
                z = java.lang.Float.valueOf(movez.text.toString())
            } catch (e: Exception) {
                Toast.makeText(this@DisplayActivity, "请输入正确的数据：" + e.message, Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            when (v.id) {
                R.id.add_shape_cube -> shapeHolder.add(Cuboid(one, one, one).moveDirection(x, y, z))
                R.id.add_shape_cuboid -> shapeHolder.add(Cuboid(one, two, three).moveDirection(x, y, z))
                R.id.add_shape_prism -> {
                    if (one > 20) {
                        Toast.makeText(this@DisplayActivity, "输入棱数过多  棱数范围必须小于等于20", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    shapeHolder.add(Prism(one.toInt(), two, three).moveDirection(x, y, z))
                }
                R.id.add_shape_pyramid -> {
                    if (one > 20) {
                        Toast.makeText(this@DisplayActivity, "输入棱数过多  棱数范围必须小于等于20", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                    shapeHolder.add(Pyramid(one.toInt(), two, three).moveDirection(x, y, z))
                }
                else -> {
                }
            }
            //数据没有出错，则显示
            addShapeDialog!!.dismiss()
            addShapeDetailDialog!!.dismiss()
            drawDetail(0f, 0f)
        })
        builder.setView(view)
        addShapeDetailDialog = builder.show()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (canDraw) {
                wordStart = event.x
                wordEnd = event.y
            }
            MotionEvent.ACTION_UP -> if (canDraw) {
                drawFont(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> if (!canDraw) {
                drawDetail(event.x - preX, event.y - preY)
            } else {
                drawDetail(0f, 0f)
            }
            else -> {
            }
        }
        preX = event.x //鼠标开始的位置，或者是下次触发的先前位置
        preY = event.y
        return true
    }

    /**
     * 画字体笔画
     *
     * @param curX 鼠标松开位置的X坐标
     * @param curY 鼠标松开位置的Y坐标
     */
    private fun drawFont(curX: Float, curY: Float) {
        val drawX = curX - wordStart
        val drawY = curY - wordEnd
        val degree = Math.atan2(drawX.toDouble(), drawY.toDouble()).toFloat() //求出左视图旋转角度
        val wordLine: Shape = Cuboid(
            wordWidth, wordHeight,
            Math.sqrt((drawX * drawX + drawY * drawY).toDouble()).toFloat()
        ) //长度
        //两次旋转和一次位移达到目的位置
        wordLine.deflectDegree(0f, -degree) //左视图先旋转
        wordLine.deflectDegree(Math.PI.toFloat() / 2, 0f) //俯视图旋转
        wordLine.moveDirection(
            (wordStart + curX) / 2 - width, 0f,  //x轴移动方向
            height - (wordEnd + curY) / 2
        ) //z轴移动方向
        shapeHolder.add(wordLine)
        canDraw = !canDraw //消除跟踪路径
        drawDetail(0f, 0f)
        canDraw = !canDraw //重新设置为true
    }

    /**
     * 逐帧画图
     *
     * @param moveX 鼠标横向移动距离
     * @param moveY 鼠标纵向移动距离
     */
    private fun drawDetail(moveX: Float, moveY: Float) {
        val canvas = sHolder!!.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR) //清除画布
        canvas.drawColor(background) //背景
        canvas.drawPoint(width, height, centerPaint) //中心
        if (canDraw) //若画布锁定，需要画出触屏过程中的跟踪路径
        {
            canvas.drawLine(wordStart, wordEnd, moveX + preX, moveY + preY, edgePaint)
        }
        //画所有形状的正视图，只需显示正视图的投影(x,z)
        val afterRotate = process.rotate(moveX, moveY) //批处理
        for (shape in afterRotate!!.getHolderList()) { //批画
            for (l in shape.lines) {
                canvas.drawLine(
                    width + l.start.x,
                    height - l.start.z,  //开始坐标(x,z)
                    width + l.end.x,
                    height - l.end.z,  //结束坐标(x,z)
                    edgePaint
                )
            }
        }
        sHolder!!.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val intent = intent
        val startActivity = intent.getIntExtra("startActivity", 1)
        when (startActivity) {
            1 -> {
                shapeHolder.add(Cuboid(400, 400, 400))
                lock!!.visibility = View.GONE
                findViewById<View>(R.id.add).visibility = View.GONE
                findViewById<View>(R.id.save).visibility = View.GONE
                findViewById<View>(R.id.read).visibility = View.GONE
                findViewById<View>(R.id.clear).visibility = View.GONE
            }
            2 -> {
                fileName = "simpledate.file"
                findViewById<View>(R.id.random).visibility = View.GONE
                lock!!.visibility = View.GONE
            }
            3 -> {
                fileName = "fontdata.file"
                findViewById<View>(R.id.add).visibility = View.GONE
                findViewById<View>(R.id.random).visibility = View.GONE
            }
            4 -> {
                when (intent.getIntExtra("choiceShape", 0)) {
                    0 -> addChairsAndDesk(shapeHolder)
                    1 -> add2(shapeHolder)
                    else -> {
                    }
                }
                lock!!.visibility = View.GONE
                findViewById<View>(R.id.add).visibility = View.GONE
                findViewById<View>(R.id.save).visibility = View.GONE
                findViewById<View>(R.id.read).visibility = View.GONE
                findViewById<View>(R.id.clear).visibility = View.GONE
                findViewById<View>(R.id.random).visibility = View.GONE
            }
            else -> {
            }
        }
        //获取个性化数据并赋值
        val styleData = intent.getIntArrayExtra("styleData")
        background = styleData!![0]
        edgePaint.color = styleData[1]
        edgePaint.strokeWidth = (styleData[2] * 3).toFloat()
        centerPaint.strokeWidth = (styleData[2] * 3).toFloat()
        centerPaint.color = if (styleData[4] == 1) styleData[3] else Color.TRANSPARENT
        drawDetail(0f, 0f)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}