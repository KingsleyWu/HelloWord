package com.kingsley.helloword.floating

import android.animation.Animator
import com.kingsley.base.dp
import com.kingsley.base.getNavBarHeight
import com.kingsley.base.getStatusBarHeight
import com.kingsley.base.getScreenWidth
import com.kingsley.base.getScreenHeight
import com.kingsley.common.L.d
import com.kingsley.common.L.e
import kotlin.jvm.JvmOverloads
import android.widget.LinearLayout
import android.view.View.OnTouchListener
import com.facebook.rebound.SpringSystem
import com.kingsley.helloword.floating.FloatingPlayerView
import android.widget.TextView
import android.widget.RelativeLayout
import android.animation.ObjectAnimator
import android.content.Intent
import com.kingsley.common.L
import kotlin.Throws
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.graphics.PixelFormat
import com.kingsley.helloword.R
import android.text.TextUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SimpleSpringListener
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.rebound.Spring
import com.kingsley.helloword.BuildConfig
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import kotlin.math.abs

/**
 * 语音窗口
 *
 * @author Kingsley
 * @date 2020/1/14
 */
open class FloatingPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), OnTouchListener {
    // **********************************//
    private lateinit var windowManager: WindowManager
    private var floatingViewParams: WindowManager.LayoutParams? = null
    private val mSpringSystem = SpringSystem.create()
    private var isOpenShotPermission = SHOT_PERMISSION_STATUS_NORML

    /**
     * floating touch event
     */
    var lastX = 0
    var lastY = 0
    var paramX = 0
    var paramY = 0
    private var dx = 0
    private var dy = 0

    /**
     * 浮動ICON
     */
    private var mheadIcon: ImageView? = null

    /**
     * 菜單佈局
     */
    private var menuLayout: ViewGroup? = null
    private lateinit var layoutVoice: LinearLayout
    private lateinit var layouttanl: LinearLayout
    private lateinit var layoutRest: LinearLayout
    private lateinit var tvVoice: TextView
    private lateinit var tvTranl: TextView
    private lateinit var tvRest: TextView
    private lateinit var layoutMenu: RelativeLayout
    private var menuLayoutParams: WindowManager.LayoutParams? = null
    private val menuAnimRunning = false

    /**
     * 顯示彈框
     */
    private val mDialog: ViewGroup? = null
    private val layoutBack: ConstraintLayout? = null
    private val layoutParent: ConstraintLayout? = null
    private val tvMsg: TextView? = null
    private val tvBtn: TextView? = null
    private val tvTips: TextView? = null
    private val dialogParams: WindowManager.LayoutParams? = null
    private val isDialogShow = false

    /**
     * 字幕佈局
     */
    private var titleLayout: ViewGroup? = null

    /**
     * 顯示字幕
     */
    private var titleLayoutParams: WindowManager.LayoutParams? = null
    private var titleText: TextView? = null

    /**
     * 標記字幕顯示左邊，false為顯示左邊
     */
    private var leftShowTitle = false
    private var sWidth: Int
    private var sHeight: Int
    private var statusHeight: Int
    private var navBarHeight: Int

    /**
     * 浮動ICON Y坐標的位置
     */
    private var floatingViewY = 0f
    private var titleShowing = false
    private var show = false

    /**
     * 頭像是否因為沒操作而隱藏
     */
    private var isBlur = false

    /**
     * 浮動窗口類型 ===>> WindowManager.LayoutParams.type
     */
    private var type = 0
    private var titleHideAnimation: ObjectAnimator? = null

    /**
     * 正在顯示角色預覽
     */
    private var previewing = false
    private var initialized = false
    private var mTopBoundary: Int
    private var mBottomBoundary: Int

    /**
     * 正在翻譯中，禁止拖動,點擊,
     */
    private val isTransing = false

    /**
     * 當前是橫屏還是豎屏
     *
     * @param context
     */
    private var isPortrait = true
    fun handleIntent(intent: Intent) {
        d(TAG, "wwc initialized = " + intent.extras.toString())
        if (!initialized) {
            init()
        }
        if (initialized) {
            val reset = intent.getBooleanExtra("reset", false)
            //重置浮動角色頭像
            if (reset) {
                //顯示選擇的角色頭像，如果沒有選擇，則隱藏不顯示浮動角色頭像以及台詞
                previewing = false
                if (false) {
                    d("mCVReceiver init stopSelf")
                    stopSelf()
                } else {
                    active(null, null)
                }
                //有設置默認頭像角色，再次隱藏台詞
                hideTitle(false)
                titleShowing = false
            } else {
                val title = intent.getStringExtra("title")
                val uri = intent.getParcelableExtra<Uri>("iconUri")
                if (!previewing) {
                    previewing = intent.getBooleanExtra("previewing", false)
                    active(title, uri)
                } else {
                    setTitle(title)
                }
            }
        }
    }

    private fun stopSelf() {
        destroy()
    }

    @Throws(RuntimeException::class)
    fun init() {
        windowManager = context.getSystemService(
            Context.WINDOW_SERVICE
        ) as WindowManager
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            WindowManager.LayoutParams.TYPE_TOAST
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        try {
            createMenuView()
            createFloatingView()
            createTitleView()
            val intentFilter = IntentFilter(ACTION_IN_BACKGROUND)
            intentFilter.addAction(ACTION_CV_PLAY)
            intentFilter.addAction(ACTION_CLOSE)
            LocalBroadcastManager.getInstance(context).registerReceiver(
                broadcastReceiver!!, intentFilter
            )
            initialized = true
        } catch (e: Exception) {
            initialized = false
            stopSelf()
        }
    }

    fun onCompleted() {
        //播放成功完成
        hideTitleAtDelayed()
        //播放完成后恢复默认角色头像
        setDefaultIcon()
    }

    private fun hideTitleAtDelayed() {
        internalHandler.removeMessages(1)
        internalHandler.sendEmptyMessageDelayed(1, TITLE_SHOW_TIME.toLong())
    }

    private fun hideTitleAtDelayed(time: Int) {
        internalHandler.removeMessages(1)
        internalHandler.sendEmptyMessageDelayed(1, time.toLong())
    }

    /**
     * 播放完成自動隱藏字幕
     *
     * @param fadeOut
     */
    private fun hideTitle(fadeOut: Boolean) {
        if (titleLayout == null) {
            return
        }
        if (fadeOut && titleLayout!!.visibility == VISIBLE) {
            if (titleHideAnimation == null) {
                titleHideAnimation = ObjectAnimator.ofFloat(titleLayout!!, "alpha", 0.0f)
                titleHideAnimation!!.duration = 600
                titleHideAnimation!!.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        d(TAG, "titleShowing onAnimationEnd>$titleShowing, titleLayout = $titleLayout")
                        if (titleLayout != null) {
                            titleLayout!!.visibility = GONE
                            titleLayout!!.alpha = 1.0f
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
            titleHideAnimation!!.start()
        } else {
            //titleShowing = false;
            titleLayout!!.visibility = GONE
        }
    }

    private fun addViewToWindow(view: View, params: WindowManager.LayoutParams) {
        if (windowManager != null) {
            if (view.parent != null) {
                windowManager!!.removeView(view)
            }
            windowManager!!.addView(view, params)
        }
    }

    private fun active(title: String?, uri: Uri?) {
        show(title, uri)
        if (!previewing) {
            show = true
        }
    }

    private fun unactive() {
        hide()
        if (!previewing) {
            show = false
        }
    }

    /**
     * 創建浮動視圖
     */
    private fun createFloatingView() {
        // LayoutInflater.from(getContext()).inflate(R.layout.global_floating_view, this);
        visibility = GONE
        orientation = VERTICAL
        removeAllViews()
        mheadIcon = ImageView(context)
        addView(mheadIcon, LayoutParams(ICON_SIZE, ICON_SIZE))
        floatingViewParams = WindowManager.LayoutParams()

        // 设置window type
        floatingViewParams!!.type = type
        floatingViewParams!!.format = PixelFormat.RGBA_8888 // 设置图片格式，效果为背景透明

        // 设置Window flag
        floatingViewParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        // 设置悬浮窗的长宽
        floatingViewParams!!.width = ICON_SIZE //ConvertUtils.dp2px(this, 120);
        floatingViewParams!!.height = ICON_SIZE //ConvertUtils.dp2px(this, 120);

        // 設置初始顯示位置
        floatingViewParams!!.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        // 设置悬浮窗的Touch监听
        setOnTouchListener(this)
        addViewToWindow(this, floatingViewParams!!)
    }

    private fun createMenuView() {
        menuLayout = LayoutInflater.from(context).inflate(R.layout.layout_floating_menu, null) as ViewGroup
        menuLayout!!.visibility = GONE
        layoutMenu = menuLayout!!.findViewById(R.id.layout_menu)
        layoutVoice = menuLayout!!.findViewById(R.id.layout_voice)
        layouttanl = menuLayout!!.findViewById(R.id.layout_tranl)
        layoutRest = menuLayout!!.findViewById(R.id.layout_rest)
        tvVoice = menuLayout!!.findViewById(R.id.tv_voice)
        tvTranl = menuLayout!!.findViewById(R.id.tv_tranl)
        tvRest = menuLayout!!.findViewById(R.id.tv_rest)
        menuLayoutParams = WindowManager.LayoutParams()
        menuLayoutParams!!.type = type
        menuLayoutParams!!.format = PixelFormat.RGBA_8888 // 设置图片格式，效果为背景透明

        // 设置Window flag
        menuLayoutParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        menuLayoutParams!!.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        menuLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        menuLayoutParams!!.height = ICON_SIZE
        layoutVoice.setOnClickListener {
            hideMenu(true)
            if (titleText != null && !TextUtils.isEmpty(titleText!!.text)) {
                showTitle()
            }
        }
        layoutRest.setOnClickListener {
            hide()
            stopSelf()
        }
        layouttanl.setOnClickListener { }
        addViewToWindow(menuLayout!!, menuLayoutParams!!)
    }

    private fun createTitleView() {
        titleLayout = LayoutInflater.from(context).inflate(R.layout.layout_floating_title, null) as ViewGroup
        titleLayout!!.visibility = GONE
        titleText = titleLayout!!.findViewById(R.id.titleText)
        titleLayoutParams = WindowManager.LayoutParams()
        titleLayoutParams!!.type = type
        titleLayoutParams!!.format = PixelFormat.RGBA_8888 // 设置图片格式，效果为背景透明
        titleLayoutParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        titleLayoutParams!!.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        titleLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        titleLayoutParams!!.height = ICON_SIZE
        val observer = titleLayout!!.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            if (titleLayout != null && "1" == titleLayout!!.tag) {
                showTitle()
                titleLayout!!.tag = "0"
            }
        }
        addViewToWindow(titleLayout!!, titleLayoutParams!!)
    }

    private fun getFlingDirection(startX: Int, startY: Int, lastX: Int, lastY: Int): Int {
        if (startX < lastX && startY > lastY) {
            return 1
        }
        if (startX < lastX && startY < lastY) {
            return 2
        }
        if (startX > lastX && startY < lastY) {
            return 3
        }
        return if (startX > lastX && startY > lastY) {
            4
        } else 0
    }

    /**
     * @param startX
     * @param startY
     * @param direction 屏幕四个角的方向
     */
    private fun fling(startX: Int, startY: Int, direction: Int) {
        var distanceX = 0
        var distanceY = 0 //滑动距离
        when (direction) {
            1 -> {
                distanceX = startX
                distanceY = startY
            }
            2 -> {
            }
            3 -> {
            }
            4 -> {
            }
            else -> {
            }
        }
        val degress = Math.atan2(distanceX.toDouble(), distanceY.toDouble())
    }

    private fun show(title: String?, uri: Uri?) {
        d("wwc show title = $title uri = $uri")
        if (title == null && titleShowing) {
            //未顯示完成的title
            showTitle()
        } else {
            setTitle(title)
        }
        showFloatingView(uri)
    }

    /**
     * 從前台切換到後台時，直接隱藏浮動ICON 和字幕顯示
     */
    private fun hide() {
        hideFloatingView()
        //字幕氣泡
        hideTitle(false)
        //
        hideMenu(false)
    }

    private fun hideFloatingView() {
        //頭像
        visibility = GONE
        // isAddFloatView = false;
        d(TAG, "hideFloatingView")
    }

    private fun showFloatingView(uri: Uri?) {
        d(TAG, "showFloatingView uri = $uri")
        if (windowManager != null) {
            if (uri != null) {
                //設置顯示頭像
                //mheadIcon.setImageURI(uri);
            } else {
                if (!setDefaultIcon()) {
                    //沒有成功設置默認頭像，不顯示浮動角色
                    hideFloatingView()
                    hideTitle(false)
                    return
                }
            }
            animate().cancel()
            rotation = 0f
            visibility = VISIBLE
            animateIcon()
        }
    }
    /**
     * 多久沒有操作，把圖標隱藏到邊緣,暫時沒用到
     */
    //    Runnable iconRunning = new Runnable() {
    //        @Override
    //        public void run() {
    //            if(!AppForegroundStateManager.getInstance().isAppInForeground()) {
    //                blurIcon();
    //            }
    //        }
    //    };
    /**
     * 發送虛化定時
     */
    //    private void sendBlurRequest(){
    //        internalHandler.removeCallbacks(iconRunning);
    //        internalHandler.postDelayed(iconRunning,ICON_SHOW_TIME);
    //    }
    /**
     * 虛化頭像
     */
    private fun blurIcon() {
        alpha = 0.3f
        if (floatingViewParams != null && isAttachedToWindow) {
            // 设置Window flag
            floatingViewParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            if (leftShowTitle) {
                floatingViewParams!!.x = sWidth - width / 2
            } else {
                floatingViewParams!!.x = -width / 2
            }
            windowManager!!.updateViewLayout(this, floatingViewParams)
        }
        isBlur = true
    }

    private fun cancelBlur() {
        alpha = 1.0f
        if (floatingViewParams != null && isAttachedToWindow) {
            floatingViewParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            if (leftShowTitle) {
                floatingViewParams!!.x = sWidth - width
            } else {
                floatingViewParams!!.x = 0
            }
            windowManager!!.updateViewLayout(this, floatingViewParams)
        }
        isBlur = false
    }

    /**
     * 設置字幕
     *
     * @param title
     */
    private fun setTitle(title: String?) {
        // 調整字幕顯示位置(區分在ICON的左或右的位置)
        if (titleLayout == null) {
            return
        }
        if (!TextUtils.isEmpty(title)) {
            d("wwc show title = $title")
            titleLayout!!.tag = "1"
            titleText!!.text = title!!.trim { it <= ' ' }
            //cancel timer
            internalHandler.removeMessages(1)
            titleLayout!!.visibility = VISIBLE
        } else {
            hideTitle(false)
        }
    }

    /**
     * 設置字幕文本
     *
     * @param title
     */
    private fun setTitleText(title: String) {
        // 調整字幕顯示位置(區分在ICON的左或右的位置)
        if (titleLayout == null || titleText == null) {
            return
        }
        if (!TextUtils.isEmpty(title)) {
            titleLayout!!.tag = "1"
            titleText!!.text = title.trim { it <= ' ' }
            showTitle()
        }
    }

    /**
     * 顯示字幕動畫
     */
    private fun showTitle() {
        if (titleLayout == null) {
            return
        }
        hideMenu(false)
        if (titleHideAnimation != null && titleHideAnimation!!.isRunning) {
            titleHideAnimation!!.cancel()
        }
        titleLayout!!.visibility = VISIBLE
        d(TAG, "showTitle>ICONSIZE: " + ICON_SIZE + " , " + titleText!!.text)
        d(TAG, "showTitle>VIEW WIDTH: " + width + " - " + titleLayout!!.width)
        val x = if (leftShowTitle) sWidth - (if (width == 0) ICON_SIZE else width) - titleLayout!!.width else ICON_SIZE
        val y = floatingViewY.toInt()
        d(TAG, "showTitle>move y: $floatingViewY")
        d(TAG, "showTitle>move X: $x")
        titleLayoutParams!!.x = x
        titleLayoutParams!!.y = y
        windowManager!!.updateViewLayout(titleLayout, titleLayoutParams)
        var paddingLeft = 16.dp
        var paddingRight = 8.dp
        val paddingBottom = 13.dp
        val paddingTop = 12.dp
        if (leftShowTitle) {
            titleText!!.setBackgroundResource(R.drawable.ic_bubble_right)
            paddingLeft = 8.dp
            paddingRight = 16.dp
        } else {
            titleText!!.setBackgroundResource(R.drawable.ic_bubble_left)
        }

        //titleText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        animateTitle()
        titleShowing = true
        d(TAG, "titleShowing>true")
    }

    /*
      默认头像图片，用B触发点第一张图片
     */
    private fun setDefaultIcon(): Boolean {
        //角色默認的頭像, 默認頭像打包在apk，不同角色的默認頭像命名是  ic_[角色id].png
        if (previewing) {
            return true
        } else {
            val type = "CVUtils.getDefaultCV()"
            d(TAG, "showFloatingView setDefaultIcon type = $type")
            if (type != null) {
                /*int defaultIcon = QooApplication.getInstance().getResources().getIdentifier("ic_" + type, "drawable", BuildConfig.APPLICATION_ID);
                mheadIcon.setImageResource(defaultIcon);*/
                //File defaultPictureFile = QooFiles.get().getCVIconFileWithType("default", -1, type);
                //L.INSTANCE.d(TAG, "showFloatingView setDefaultIcon defaultPictureFile = " + defaultPictureFile);
                mheadIcon!!.setImageResource(R.drawable.ic_launcher_background)
                return true
            }
        }
        return false
    }

    private fun animateIcon() {
        // Add a spring to the system.
        val spring = mSpringSystem.createSpring()
        spring.springConfig = SpringConfig.fromOrigamiTensionAndFriction(80.0, 8.0)
        // Add a listener to observe the motion of the spring.
        spring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                val value = spring.currentValue.toFloat()
                scaleX = value
                scaleY = value
                //LogUtils.d(TAG, "onSpringUpdate: " + value);
            }
        })
        spring.endValue = 1.0
    }

    private fun animateTitle() {
// Add a spring to the system.
        val spring = mSpringSystem.createSpring()
        spring.springConfig = SpringConfig.fromOrigamiTensionAndFriction(80.0, 8.0)
        // Add a listener to observe the motion of the spring.
        spring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                val value = spring.currentValue.toFloat()
                val scale = 1f - value * 0.5f
                if (titleLayout != null) {
                    titleLayout!!.scaleX = value
                    titleLayout!!.scaleY = value
                }

                //LogUtils.d(TAG, "onSpringUpdate: " + value);
                //ViewHelper.setTranslationX(mFloatingView, value);
                //ViewHelper.setTranslationY(mFloatingView, value);
            }
        })
        spring.endValue = 1.0
    }

    /**
     * 執行菜單彈出動畫，需要根據在屏幕左邊還是右邊來判斷執行動畫的方向
     */
    private fun animateMenu() {
//        if (leftShowTitle) {
//            int menuWidth = menuLayout.getWidth();
//            menuWidth = menuWidth == 0 ? MENU_WIDTH : menuWidth;
//            menuLayout.setScaleX(0);
//            menuLayout.setScaleY(1.0f);
//            menuLayout.setPivotX(menuWidth);
//        } else {
//            menuLayout.setScaleX(0);
//            menuLayout.setScaleY(1.0f);
//            menuLayout.setPivotX(0);
//        }
//        menuLayout.setPivotY(0.5f);
//        menuLayout.animate().setDuration(100).scaleX(1.0f).scaleY(1.0f).withStartAction(() -> {
//            menuAnimRunning = true;
//        }).setInterpolator(new AccelerateInterpolator()).withEndAction(() -> {
//            menuAnimRunning = false;
//            internalHandler.removeCallbacks(mCloseMenuRunnable);
//            internalHandler.postDelayed(mCloseMenuRunnable, MENU_SHOW_TIME);
//        }).start();
    }

    /**
     * 執行菜單隱藏動畫，需要根據在屏幕左邊還是右邊來判斷執行動畫的方向
     */
    private fun animateHideMenu() {
//        if (leftShowTitle) {
//            menuLayout.setScaleX(1.0f);
//            menuLayout.setScaleY(1.0f);
//            menuLayout.setPivotX(menuLayout.getWidth());
//            menuLayout.setPivotY(menuLayout.getHeight() / 2);
//        } else {
//            menuLayout.setScaleX(1.0f);
//            menuLayout.setScaleY(1.0f);
//            menuLayout.setPivotX(0);
//            menuLayout.setPivotY(menuLayout.getHeight() / 2);
//        }
//        menuLayout.animate().setDuration(100).scaleX(0.0f).scaleY(1.0f).withStartAction(() -> {
//            menuAnimRunning = true;
//        }).setInterpolator(new AccelerateInterpolator()).withEndAction(() -> {
//            menuAnimRunning = false;
        menuLayout?.visibility = GONE
        //        }).start();
    }

    /**
     * 從有到無
     */
    private fun animHideScale(isHide: Boolean, isShow: Boolean) {
        pivotX = (width / 2).toFloat()
        pivotY = (height / 2).toFloat()
        scaleX = 1.0f
        scaleY = 1.0f
        animate()
            .setDuration(49)
            .scaleX(0.0f)
            .scaleY(0.0f)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                if (isHide) {
                    hide()
                } else if (isShow) {
                    animScale(false)
                }
            }.start()
    }

    /**
     * 從無到有
     */
    private fun animScale(isRpeat: Boolean) {
        pivotX = (width / 2).toFloat()
        pivotY = (height / 2).toFloat()
        scaleX = 0f
        scaleY = 0f
        animate()
            .setDuration(150)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                if (isRpeat) {
                    animHead()
                }
            }.start()
    }

    /**
     * 自身旋轉
     */
    private fun animHead() {
        pivotX = (width / 2).toFloat()
        pivotY = (height / 2).toFloat()
        rotation = 0f
        isClearAnim = false
        animate()
            .setDuration(800)
            .rotation(360f)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                if (!isClearAnim) {
                    animHead()
                } else {
                    rotation = 0f
                }
            }.start()
    }

    private var isClearAnim = false

    /**
     * 關閉自身旋轉
     */
    private fun closeAnimHead() {
        isClearAnim = true
        animate().cancel()
        rotation = 0f
        animHideScale(false, true)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        var moved = false
        val moveX: Float
        val moveY: Float
        if (isTransing) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                paramX = floatingViewParams!!.x
                paramY = floatingViewParams!!.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isBlur) {
                    moveX = event.rawX - lastX
                    moveY = event.rawY - lastY
                    moved = Math.abs(moveX) >= 8 || Math.abs(moveY) >= 8
                    d("wwc moved = $moved")
                    if (moved) {
                        hideMenu(false)
                        dx = event.rawX.toInt() - lastX
                        dy = event.rawY.toInt() - lastY
                        val x = paramX + dx
                        var y = paramY + dy
                        // 增加边界判断
                        if (isPortrait) {
                            if (y < mTopBoundary) {
                                y = mTopBoundary
                            } else if (y > mBottomBoundary) {
                                y = mBottomBoundary
                            }
                        } else {
                            if (y <= -sHeight / 2 + ICON_SIZE / 2) {
                                y = -sHeight / 2 + ICON_SIZE / 2
                            } else if (y >= sHeight / 2 - ICON_SIZE / 2) {
                                y = sHeight / 2 - ICON_SIZE / 2
                            }
                        }
                        floatingViewY = y.toFloat()
                        floatingViewParams!!.x = x
                        floatingViewParams!!.y = y

                        // 更新悬浮窗位置
                        windowManager!!.updateViewLayout(this, floatingViewParams)
                        //移動中，隱藏字幕氣泡
                        hideTitle(false)
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                d(TAG, "CLICK X  " + event.rawX)
                d(TAG, "CLICK lastX  $lastX")
                d(TAG, "CLICK event.getAction()  " + event.action)
                moveX = event.rawX - lastX
                moveY = event.rawY - lastY
                moved = abs(moveX) >= 5 || abs(moveY) >= 5
                if (!moved) {
                    clickIcon()
                } else {
                    if (!isBlur) {
                        attractSide(event.rawX)
                    }
                }
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 自動關閉菜單
     */
    var mCloseMenuRunnable = Runnable { hideMenu(true) }

    /**
     * 頭像點擊事件
     */
    private fun clickIcon() {
        //如果當前位於僕人預覽詳情頁，則發送廣播播放置頂語音
        if (isBlur) {
            cancelBlur()
        }
        showMenu()
    }

    /**
     * 顯示菜單
     */
    private fun showMenu() {
        //正在執行動畫，不要再次觸發
        if (menuAnimRunning) {
            return
        }
        if (titleLayoutParams != null) {
            hideTitle(false)
        }
        if (menuLayout != null && menuLayoutParams != null) {
            if (menuLayout!!.visibility == VISIBLE) {
                menuLayout!!.visibility = GONE
                return
            }
            menuLayoutParams!!.y = floatingViewY.toInt()
            //需要根據僕人顯示在左邊還是右邊， 來調整menu中菜單的位置
            if (leftShowTitle) {
                var menuWidth = menuLayout!!.width
                menuWidth = if (menuWidth == 0) MENU_WIDTH else menuWidth
                menuLayoutParams!!.x = sWidth - menuWidth - MENU_START
                layoutMenu.setPadding(MENU_PADDING_END, 0, MENU_PADDING_START, 0)
                val rParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                rParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutRest.layoutParams = rParams
                val tParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                tParams.addRule(RelativeLayout.END_OF, R.id.layout_rest)
                layouttanl.layoutParams = tParams
                val vParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                vParams.addRule(RelativeLayout.END_OF, R.id.layout_tranl)
                layoutVoice.layoutParams = vParams
            } else {
                menuLayoutParams!!.x = MENU_START
                layoutMenu.setPadding(MENU_PADDING_START, 0, MENU_PADDING_END, 0)
                val vParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                vParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutVoice.layoutParams = vParams
                val tParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                tParams.addRule(RelativeLayout.END_OF, R.id.layout_voice)
                layouttanl.layoutParams = tParams
                val rParams = RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT)
                rParams.addRule(RelativeLayout.END_OF, R.id.layout_tranl)
                layoutRest.layoutParams = rParams
            }
            windowManager!!.updateViewLayout(menuLayout, menuLayoutParams)
            menuLayout!!.visibility = VISIBLE
            animateMenu()
        }
    }

    /**
     * 隱藏菜單
     */
    private fun hideMenu(isAnim: Boolean) {
        if (menuAnimRunning) {
            return
        }
        internalHandler.removeCallbacks(mCloseMenuRunnable)
        if (menuLayout != null && menuLayout!!.visibility != GONE) {
            if (isAnim) {
                animateHideMenu()
            } else {
                menuLayout!!.visibility = GONE
            }
        }
    }

    /**
     * 移動浮動ICON 后，自動貼在屏幕的左右邊緣
     *
     * @param rawX
     */
    private fun attractSide(rawX: Float) {
        val startValue: Int
        val endValue: Int
        val leftSide = sWidth / 2f >= rawX
        if (leftSide) {
            startValue = (rawX - width).toInt()
            endValue = 0
            leftShowTitle = false
        } else {
            leftShowTitle = true
            startValue = rawX.toInt()
            endValue = sWidth - width
        }
        val valueAnimator = ValueAnimator.ofInt(startValue, endValue)
        valueAnimator.duration = 100
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Int
            floatingViewParams?.x = value
            try {
                windowManager.updateViewLayout(this@FloatingPlayerView, floatingViewParams)
                if (value == endValue && titleShowing) {
                    //繼續未顯示完成的字幕
                    d(TAG, "繼續未顯示完成的字幕")
                    showTitle()
                    //hideTitleAtDelayed();
                }
            } catch (e: IllegalArgumentException) {
                e(TAG, e.message ?: "")
            }
        }
        valueAnimator.start()
    }

    private fun attractSideXY(rawX: Float) {
        if (floatingViewParams != null) {
            val endValue: Int
            val leftSide = sWidth / 2f >= rawX
            if (leftSide) {
                endValue = 0
                leftShowTitle = false
            } else {
                leftShowTitle = true
                endValue = sWidth - width
            }
            floatingViewParams!!.x = endValue
            if (floatingViewParams!!.y <= -sHeight / 2 + ICON_SIZE / 2) {
                floatingViewParams!!.y = -sHeight / 2 + ICON_SIZE / 2
            } else if (floatingViewParams!!.y >= sHeight / 2 - ICON_SIZE / 2) {
                floatingViewParams!!.y = sHeight / 2 - ICON_SIZE / 2
            }
            floatingViewY = floatingViewParams!!.y.toFloat()
        }
        windowManager.updateViewLayout(this@FloatingPlayerView, floatingViewParams)
    }

    /**
     * 播放聲優語音后接受
     */
    var broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent != null) {
                if (ACTION_CV_PLAY == intent.action) {
                    val status = intent.getIntExtra("status", -1)
                    if (status == 0) {
                        // 播放
                        val title = intent.getStringExtra("title")
                        if (!TextUtils.isEmpty(title)) {
                            d(TAG, " setTitle 播放 ")
                            setTitle(title)
                        }
                    } else if (status == 1) {
                        //播放成功完成
                        hideTitleAtDelayed()
                    }
                } else if (ACTION_IN_BACKGROUND == intent.action) {
                    val isForground = intent.getBooleanExtra("isForground", false)
                    val activity = BuildConfig.APPLICATION_ID + ".BrowserActivity"
                    //截圖翻譯完成
                } else if (ACTION_CLOSE == intent.action) {
                    stopSelf()
                }
            }
        }
    }
    var internalHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                titleShowing = false
                hideTitle(true)
            }
        }
    }

    fun removeAllView() {
        if (windowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow) {
                    rotation = 0f
                    windowManager.removeViewImmediate(this)
                }
            }
            if (menuLayout != null && menuLayout!!.isAttachedToWindow) {
                windowManager.removeViewImmediate(menuLayout)
            }
            if (titleLayout != null && titleLayout!!.isAttachedToWindow) {
                windowManager.removeViewImmediate(titleLayout)
            }
        }
    }

    /**
     * 清除数据
     */
    fun destroy() {
        d("wwc destroy initialized = $initialized")
        stopService()
        if (initialized) {
            if (broadcastReceiver != null) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver!!)
            }
            internalHandler.removeCallbacks(mCloseMenuRunnable)
            removeAllView()
            // 标识为未初始化
            initialized = false
            // 标识为右边显示
            leftShowTitle = false
            // 标识为未显示中
            titleShowing = false
            // 防止View错位
            floatingViewY = 0f
        }
    }

    private fun stopService() {
        isOpenShotPermission = SHOT_PERMISSION_STATUS_NORML
        //Intent intent = new Intent(getContext(), ShotService.class);
        //getContext().stopService(intent);
    }

    /**
     * 監聽橫豎屏切換，
     * 橫豎屏切換后，寬高發生了變化，需要重新獲取，且需要對顯示區域重新計算
     *
     * @param newConfig
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        sWidth = context.getScreenWidth()
        sHeight = context.getScreenHeight()
        navBarHeight = context.getNavBarHeight()
        statusHeight = context.getStatusBarHeight()
        mTopBoundary = -sHeight / 2 + statusHeight
        mBottomBoundary = sHeight / 2 - navBarHeight
        //為了避免錯誤，隱藏了title和menu
        hideMenu(false)
        hideTitle(false)
        //對頭像的顯示做處理
        if (floatingViewParams != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                isPortrait = true
                floatingViewParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                menuLayoutParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            } else {
                //豎屏時，一般為遊戲界面，沒有stateBar，所以允許頭像顯示在頂部。 不考慮這種橫屏有狀態欄的情況了
                isPortrait = false
                floatingViewParams!!.flags =
                    (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    floatingViewParams!!.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                menuLayoutParams!!.flags =
                    (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    menuLayoutParams!!.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
            attractSideXY(floatingViewParams!!.x.toFloat())
        }
    }

    companion object {
        private const val TAG = "FloatingPlayerView"
        private val ICON_SIZE = 88.dp
        private val MENU_PADDING_START = 56.dp
        private val MENU_HEIGHT = 64.dp
        private val MENU_PADDING_END = 20.dp
        private val MENU_START = 32.dp
        private val MENU_WIDTH = 268.dp

        /**
         * 點擊翻譯時，未登錄，當前需要綁定賬號
         */
        private const val DIALOG_ACTION_TYPE_LOGIN = 1

        /**
         * 點擊翻譯時，試用翻譯次數已經用完，需要提示購買服務
         */
        private const val DIALOG_ACTION_TYPE_TRANTIPS = 2

        /**
         * 菜單顯示時長
         */
        private const val MENU_SHOW_TIME = 60 * 1000

        /**
         * 文本顯示時長
         */
        private const val TITLE_SHOW_TIME = 3000

        /**
         * 提示文本顯示時長
         */
        private const val TITLE_TIPS_SHOW_TIME = 6000

        /**
         * 誤操作5秒，隱藏頭像
         */
        private const val ICON_SHOW_TIME = 5000

        /**
         * QooApp 切換前後台的時候觸發
         */
        const val ACTION_IN_BACKGROUND = BuildConfig.APPLICATION_ID + ".action.inBackground"

        /**
         * 播放一個語音
         */
        const val ACTION_CV_PLAY = BuildConfig.APPLICATION_ID + ".action.cv_play"

        /**
         * 關閉浮動ICON
         */
        const val ACTION_CLOSE = BuildConfig.APPLICATION_ID + ".action.floating_icon_close"

        /**
         * 开启浮動ICON
         */
        const val ACTION_OPEN = BuildConfig.APPLICATION_ID + ".action.floating_icon_open"

        /**
         * 默認狀態
         */
        const val SHOT_PERMISSION_STATUS_NORML = 0

        /**
         * 用戶接受了授權
         */
        const val SHOT_PERMISSION_STATUS_RECEIVE = 1

        /**
         * 正在請求權限
         */
        const val SHOT_PERMISSION_STATUS_REQUEST = 2

        /**
         * 用戶取消了授權
         */
        const val SHOT_PERMISSION_STATUS_REFUSE = 3
    }

    init {
        navBarHeight = context.getNavBarHeight()
        statusHeight = context.getStatusBarHeight()
        sWidth = context.getScreenWidth()
        sHeight = context.getScreenHeight()
        mTopBoundary = -sHeight / 2 + statusHeight
        mBottomBoundary = sHeight / 2 - navBarHeight
    }
}