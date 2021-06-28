package com.kingsley.helloword.floating;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.kingsley.base.ExKt;
import com.kingsley.base.L;
import com.kingsley.helloword.BuildConfig;
import com.kingsley.helloword.R;

import java.util.Objects;

/**
 * 语音窗口
 *
 * @author Kingsley
 * @date 2020/1/14
 */
public class FloatingPlayerView extends LinearLayout implements View.OnTouchListener {
    private static final String TAG = "FloatingPlayerView";

    private static final int ICON_SIZE = ExKt.dp(88);
    private static final int MENU_PADDING_START = ExKt.dp(56);
    private static final int MENU_HEIGHT = ExKt.dp(64);
    private static final int MENU_PADDING_END = ExKt.dp(20);
    private static final int MENU_START = ExKt.dp(32);
    private static final int MENU_WIDTH = ExKt.dp(268);

    /**
     * 點擊翻譯時，未登錄，當前需要綁定賬號
     */
    private static final int DIALOG_ACTION_TYPE_LOGIN = 1;
    /**
     * 點擊翻譯時，試用翻譯次數已經用完，需要提示購買服務
     */
    private static final int DIALOG_ACTION_TYPE_TRANTIPS = 2;
    /**
     * 菜單顯示時長
     */
    private static final int MENU_SHOW_TIME = 60 * 1000;
    /**
     * 文本顯示時長
     */
    private static final int TITLE_SHOW_TIME = 3000;
    /**
     * 提示文本顯示時長
     */
    private static final int TITLE_TIPS_SHOW_TIME = 6000;
    /**
     * 誤操作5秒，隱藏頭像
     */
    private static final int ICON_SHOW_TIME = 5000;
    /**
     * QooApp 切換前後台的時候觸發
     */
    public static final String ACTION_IN_BACKGROUND = BuildConfig.APPLICATION_ID + ".action.inBackground";
    /**
     * 播放一個語音
     */
    public static final String ACTION_CV_PLAY = BuildConfig.APPLICATION_ID + ".action.cv_play";
    /**
     * 關閉浮動ICON
     */
    public static final String ACTION_CLOSE = BuildConfig.APPLICATION_ID + ".action.floating_icon_close";
    /**
     * 开启浮動ICON
     */
    public static final String ACTION_OPEN = BuildConfig.APPLICATION_ID + ".action.floating_icon_open";
    /**
     * 默認狀態
     */
    public static final int SHOT_PERMISSION_STATUS_NORML = 0;
    /**
     * 用戶接受了授權
     */
    public static final int SHOT_PERMISSION_STATUS_RECEIVE = 1;
    /**
     * 正在請求權限
     */
    public static final int SHOT_PERMISSION_STATUS_REQUEST = 2;
    /**
     * 用戶取消了授權
     */
    public static final int SHOT_PERMISSION_STATUS_REFUSE = 3;
    // **********************************//
    protected WindowManager windowManager;

    protected WindowManager.LayoutParams floatingViewParams;

    private final SpringSystem mSpringSystem = SpringSystem.create();

    private int isOpenShotPermission = SHOT_PERMISSION_STATUS_NORML;
    ;
    /**
     * floating touch event
     */
    int lastX, lastY;
    int paramX, paramY;
    private int dx, dy;
    /**
     * 浮動ICON
     */
    private ImageView mheadIcon;
    /**
     * 菜單佈局
     */
    private ViewGroup menuLayout;
    private LinearLayout layoutVoice, layouttanl, layoutRest;
    private TextView tvVoice, tvTranl, tvRest;
    private RelativeLayout layoutMenu;
    private WindowManager.LayoutParams menuLayoutParams;
    private boolean menuAnimRunning = false;
    /**
     * 顯示彈框
     */
    private ViewGroup mDialog;
    private ConstraintLayout layoutBack, layoutParent;
    private TextView tvMsg, tvBtn, tvTips;
    private WindowManager.LayoutParams dialogParams;
    private boolean isDialogShow = false;

    /**
     * 字幕佈局
     */
    private ViewGroup titleLayout;
    /**
     * 顯示字幕
     */
    private WindowManager.LayoutParams titleLayoutParams;
    private TextView titleText;
    /**
     * 標記字幕顯示左邊，false為顯示左邊
     */
    private boolean leftShowTitle = false;

    private int sWidth;
    private int sHeight;
    private int statusHeight;
    private int navBarHeight;
    /**
     * 浮動ICON Y坐標的位置
     */
    private float floatingViewY;

    private boolean titleShowing = false;

    private boolean show;

    /**
     * 頭像是否因為沒操作而隱藏
     */
    private boolean isBlur = false;

    /**
     * 浮動窗口類型 ===>> WindowManager.LayoutParams.type
     */
    private int type;
    private ObjectAnimator titleHideAnimation;

    /**
     * 正在顯示角色預覽
     */
    private boolean previewing;

    private boolean initialized;

    private int mTopBoundary, mBottomBoundary;

    /**
     * 正在翻譯中，禁止拖動,點擊,
     */
    private boolean isTransing = false;

    /**
     * 當前是橫屏還是豎屏
     *
     * @param context
     */
    private boolean isPortrait = true;

    public FloatingPlayerView(Context context) {
        this(context, null);
    }

    public FloatingPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        navBarHeight = ExKt.getNavBarHeight();
        statusHeight = ExKt.getStatusBarHeight();
        sWidth = ExKt.getScreenWidth(context);
        sHeight = ExKt.getScreenHeight(context);
        mTopBoundary = (-sHeight / 2) + statusHeight;
        mBottomBoundary = sHeight / 2 - navBarHeight;
    }

    public void handleIntent(Intent intent) {
        L.INSTANCE.d(TAG, "wwc initialized = " + intent.getExtras().toString());
        if (!initialized) {
            init();
        }

        if (initialized) {
            final boolean reset = intent.getBooleanExtra("reset", false);
            //重置浮動角色頭像
            if (reset) {
                //顯示選擇的角色頭像，如果沒有選擇，則隱藏不顯示浮動角色頭像以及台詞
                previewing = false;
                if (false) {
                    L.INSTANCE.d("mCVReceiver init stopSelf");
                    stopSelf();
                } else {
                    active(null, null);
                }
                //有設置默認頭像角色，再次隱藏台詞
                hideTitle(false);
                titleShowing = false;

            } else {
                final String title = intent.getStringExtra("title");
                final Uri uri = intent.getParcelableExtra("iconUri");
                if (!previewing) {
                    previewing = intent.getBooleanExtra("previewing", false);
                    active(title, uri);
                } else {
                    setTitle(title);
                }
            }
        }
    }

    private void stopSelf() {
        destroy();
    }

    @SuppressWarnings("deprecation")
    public void init() throws RuntimeException {
        windowManager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        try {
            createMenuView();
            createFloatingView();
            createTitleView();

            IntentFilter intentFilter = new IntentFilter(ACTION_IN_BACKGROUND);
            intentFilter.addAction(ACTION_CV_PLAY);
            intentFilter.addAction(ACTION_CLOSE);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                    broadcastReceiver, intentFilter);

            initialized = true;
        } catch (Exception e) {
            initialized = false;
            stopSelf();
        }
    }

    public void onCompleted() {
        //播放成功完成
        hideTitleAtDelayed();
        //播放完成后恢复默认角色头像
        setDefaultIcon();
    }

    private void hideTitleAtDelayed() {
        internalHandler.removeMessages(1);
        internalHandler.sendEmptyMessageDelayed(1, TITLE_SHOW_TIME);
    }

    private void hideTitleAtDelayed(int time) {
        internalHandler.removeMessages(1);
        internalHandler.sendEmptyMessageDelayed(1, time);
    }


    /**
     * 播放完成自動隱藏字幕
     *
     * @param fadeOut
     */
    private void hideTitle(boolean fadeOut) {
        if (titleLayout == null) {
            return;
        }
        if (fadeOut && titleLayout.getVisibility() == View.VISIBLE) {
            if (titleHideAnimation == null) {
                titleHideAnimation = ObjectAnimator.ofFloat(titleLayout, "alpha", 0.0f);
                titleHideAnimation.setDuration(600);
                titleHideAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        L.INSTANCE.d(TAG, "titleShowing onAnimationEnd>" + titleShowing + ", titleLayout = " + titleLayout);
                        if (titleLayout != null) {
                            titleLayout.setVisibility(View.GONE);
                            titleLayout.setAlpha(1.0f);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
            titleHideAnimation.start();

        } else {
            //titleShowing = false;
            titleLayout.setVisibility(View.GONE);
        }
    }

    private void addViewToWindow(@NonNull View view, @NonNull WindowManager.LayoutParams params) {
        if (windowManager != null && view != null) {
            if (view.getParent() != null) {
                windowManager.removeView(view);
            }
            windowManager.addView(view, params);
        }
    }

    private void active(String title, Uri uri) {
        show(title, uri);
        if (!previewing) {
            show = true;
        }
    }

    private void unactive() {
        hide();
        if (!previewing) {
            show = false;
        }
    }

    /**
     * 創建浮動視圖
     */
    private void createFloatingView() {
        // LayoutInflater.from(getContext()).inflate(R.layout.global_floating_view, this);
        setVisibility(View.GONE);
        setOrientation(VERTICAL);
        removeAllViews();
        mheadIcon = new ImageView(getContext());
        addView(mheadIcon, new LayoutParams(ICON_SIZE, ICON_SIZE));

        floatingViewParams = new WindowManager.LayoutParams();

        // 设置window type
        floatingViewParams.type = type;

        floatingViewParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        floatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 设置悬浮窗的长宽
        floatingViewParams.width = ICON_SIZE; //ConvertUtils.dp2px(this, 120);
        floatingViewParams.height = ICON_SIZE;//ConvertUtils.dp2px(this, 120);

        // 設置初始顯示位置
        floatingViewParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

        // 设置悬浮窗的Touch监听
        setOnTouchListener(this);

        addViewToWindow(this, floatingViewParams);
    }

    private void createMenuView() {
        menuLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_floating_menu, null);
        menuLayout.setVisibility(View.GONE);
        layoutMenu = menuLayout.findViewById(R.id.layout_menu);
        layoutVoice = menuLayout.findViewById(R.id.layout_voice);
        layouttanl = menuLayout.findViewById(R.id.layout_tranl);
        layoutRest = menuLayout.findViewById(R.id.layout_rest);
        tvVoice = menuLayout.findViewById(R.id.tv_voice);
        tvTranl = menuLayout.findViewById(R.id.tv_tranl);
        tvRest = menuLayout.findViewById(R.id.tv_rest);

        menuLayoutParams = new WindowManager.LayoutParams();
        menuLayoutParams.type = type;

        menuLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        menuLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuLayoutParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        menuLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuLayoutParams.height = ICON_SIZE;

        layoutVoice.setOnClickListener(v -> {
            hideMenu(true);
            if (titleText != null && !TextUtils.isEmpty(titleText.getText())) {
                showTitle();
            }
        });

        layoutRest.setOnClickListener(v -> {
            hide();
            stopSelf();
        });

        layouttanl.setOnClickListener(v -> {

        });

        addViewToWindow(menuLayout, menuLayoutParams);
    }

    private void createTitleView() {
        titleLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_floating_title, null);
        titleLayout.setVisibility(View.GONE);
        titleText = titleLayout.findViewById(R.id.titleText);

        titleLayoutParams = new WindowManager.LayoutParams();
        titleLayoutParams.type = type;

        titleLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        titleLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        titleLayoutParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        titleLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        titleLayoutParams.height = ICON_SIZE;
        ViewTreeObserver observer = titleLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            if (titleLayout != null && "1".equals(titleLayout.getTag())) {
                showTitle();
                titleLayout.setTag("0");
            }
        });

        addViewToWindow(titleLayout, titleLayoutParams);
    }


    private int getFlingDirection(final int startX, final int startY, final int lastX, final int lastY) {
        if (startX < lastX && startY > lastY) {
            return 1;
        }
        if (startX < lastX && startY < lastY) {
            return 2;
        }
        if (startX > lastX && startY < lastY) {
            return 3;
        }
        if (startX > lastX && startY > lastY) {
            return 4;
        }
        return 0;
    }

    /**
     * @param startX
     * @param startY
     * @param direction 屏幕四个角的方向
     */
    private void fling(final int startX, final int startY, final int direction) {
        int distanceX = 0, distanceY = 0; //滑动距离
        switch (direction) {
            case 1:
                distanceX = startX;
                distanceY = startY;
                break;
            case 2:

                break;
            case 3:
                break;
            case 4:
                break;
            default:
        }
        double degress = Math.atan2(distanceX, distanceY);
    }

    private void show(final String title, Uri uri) {
        L.INSTANCE.d("wwc show title = " + title + " uri = " + uri);
        if (title == null && titleShowing) {
            //未顯示完成的title
            showTitle();
        } else {
            setTitle(title);
        }
        showFloatingView(uri);
    }

    /**
     * 從前台切換到後台時，直接隱藏浮動ICON 和字幕顯示
     */
    private void hide() {
        hideFloatingView();
        //字幕氣泡
        hideTitle(false);
        //
        hideMenu(false);
    }

    private void hideFloatingView() {
        //頭像
        setVisibility(View.GONE);
        // isAddFloatView = false;
        L.INSTANCE.d(TAG, "hideFloatingView");
    }

    private void showFloatingView(Uri uri) {
        L.INSTANCE.d(TAG, "showFloatingView uri = " + uri);
        if (windowManager != null) {
            if (uri != null) {
                //設置顯示頭像
                //mheadIcon.setImageURI(uri);
            } else {
                if (!setDefaultIcon()) {
                    //沒有成功設置默認頭像，不顯示浮動角色
                    hideFloatingView();
                    hideTitle(false);
                    return;
                }
            }
            animate().cancel();
            setRotation(0);
            setVisibility(View.VISIBLE);
            animateIcon();
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
    private void blurIcon() {
        setAlpha(0.3f);
        if (floatingViewParams != null && isAttachedToWindow()) {
            // 设置Window flag
            floatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            if (leftShowTitle) {
                floatingViewParams.x = sWidth - getWidth() / 2;
            } else {
                floatingViewParams.x = -getWidth() / 2;
            }
            windowManager.updateViewLayout(this, floatingViewParams);
        }
        isBlur = true;
    }

    private void cancelBlur() {
        setAlpha(1.0f);
        if (floatingViewParams != null && isAttachedToWindow()) {
            floatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (leftShowTitle) {
                floatingViewParams.x = sWidth - getWidth();
            } else {
                floatingViewParams.x = 0;
            }
            windowManager.updateViewLayout(this, floatingViewParams);
        }
        isBlur = false;
    }

    /**
     * 設置字幕
     *
     * @param title
     */
    private void setTitle(final String title) {
        // 調整字幕顯示位置(區分在ICON的左或右的位置)
        if (titleLayout == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            L.INSTANCE.d("wwc show title = " + title);
            titleLayout.setTag("1");
            titleText.setText(title.trim());
            //cancel timer
            internalHandler.removeMessages(1);
            titleLayout.setVisibility(View.VISIBLE);
        } else {
            hideTitle(false);
        }

    }

    /**
     * 設置字幕文本
     *
     * @param title
     */
    private void setTitleText(final String title) {
        // 調整字幕顯示位置(區分在ICON的左或右的位置)
        if (titleLayout == null || titleText == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            titleLayout.setTag("1");
            titleText.setText(title.trim());
            showTitle();
        }

    }

    /**
     * 顯示字幕動畫
     */
    private void showTitle() {
        if (titleLayout == null) {
            return;
        }
        hideMenu(false);
        if (titleHideAnimation != null && titleHideAnimation.isRunning()) {
            titleHideAnimation.cancel();
        }
        titleLayout.setVisibility(View.VISIBLE);
        L.INSTANCE.d(TAG, "showTitle>ICONSIZE: " + ICON_SIZE + " , " + titleText.getText());
        L.INSTANCE.d(TAG, "showTitle>VIEW WIDTH: " + getWidth() + " - " + titleLayout.getWidth());
        int x = leftShowTitle ? sWidth - (getWidth() == 0 ? ICON_SIZE : getWidth()) - titleLayout.getWidth() : ICON_SIZE;
        int y = (int) floatingViewY;
        L.INSTANCE.d(TAG, "showTitle>move y: " + floatingViewY);
        L.INSTANCE.d(TAG, "showTitle>move X: " + x);
        titleLayoutParams.x = x;
        titleLayoutParams.y = y;
        windowManager.updateViewLayout(titleLayout, titleLayoutParams);

        int paddingLeft = ExKt.dp(16);
        int paddingRight = ExKt.dp(8);
        final int paddingBottom = ExKt.dp(13);
        final int paddingTop = ExKt.dp(12);
        if (leftShowTitle) {
            titleText.setBackgroundResource(R.drawable.ic_bubble_right);
            paddingLeft = ExKt.dp(8);
            paddingRight = ExKt.dp(16);
        } else {
            titleText.setBackgroundResource(R.drawable.ic_bubble_left);
        }

        //titleText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        animateTitle();
        titleShowing = true;
        L.INSTANCE.d(TAG, "titleShowing>true");
    }


    /*
      默认头像图片，用B触发点第一张图片
     */
    private boolean setDefaultIcon() {
        //角色默認的頭像, 默認頭像打包在apk，不同角色的默認頭像命名是  ic_[角色id].png
        if (previewing) {
            return true;
        } else {
            final String type = "CVUtils.getDefaultCV()";
            L.INSTANCE.d(TAG, "showFloatingView setDefaultIcon type = " + type);
            if (type != null) {
                /*int defaultIcon = QooApplication.getInstance().getResources().getIdentifier("ic_" + type, "drawable", BuildConfig.APPLICATION_ID);
                mheadIcon.setImageResource(defaultIcon);*/
                //File defaultPictureFile = QooFiles.get().getCVIconFileWithType("default", -1, type);
                //L.INSTANCE.d(TAG, "showFloatingView setDefaultIcon defaultPictureFile = " + defaultPictureFile);
                mheadIcon.setImageResource(R.drawable.ic_launcher_background);
                return true;
            }
        }
        return false;
    }

    private void animateIcon() {
        // Add a spring to the system.
        Spring spring = mSpringSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 8));
        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                setScaleX(value);
                setScaleY(value);
                //LogUtils.d(TAG, "onSpringUpdate: " + value);
            }
        });
        spring.setEndValue(1);
    }

    private void animateTitle() {
// Add a spring to the system.
        Spring spring = mSpringSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 8));
        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);

                if (titleLayout != null) {
                    titleLayout.setScaleX(value);
                    titleLayout.setScaleY(value);
                }

                //LogUtils.d(TAG, "onSpringUpdate: " + value);
                //ViewHelper.setTranslationX(mFloatingView, value);
                //ViewHelper.setTranslationY(mFloatingView, value);
            }
        });
        spring.setEndValue(1);
    }

    /**
     * 執行菜單彈出動畫，需要根據在屏幕左邊還是右邊來判斷執行動畫的方向
     */
    private void animateMenu() {
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
    private void animateHideMenu() {
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
        menuLayout.setVisibility(View.GONE);
//        }).start();

    }

    /**
     * 從有到無
     */
    private void animHideScale(boolean isHide, boolean isShow) {
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        setScaleX(1.0f);
        setScaleY(1.0f);
        animate().setDuration(49).scaleX(0.0f).scaleY(0.0f).setInterpolator(new LinearInterpolator()).withEndAction(() -> {
            if (isHide) {
                hide();
            } else if (isShow) {
                animScale(false);
            }
        }).start();
    }

    /**
     * 從無到有
     */
    private void animScale(boolean isRpeat) {
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        setScaleX(0);
        setScaleY(0);
        animate().setDuration(150).scaleX(1.0f).scaleY(1.0f).setInterpolator(new LinearInterpolator()).withEndAction(() -> {
            if (isRpeat) {
                animHead();
            }
        }).start();
    }

    /**
     * 自身旋轉
     */
    private void animHead() {
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        setRotation(0);
        isClearAnim = false;
        animate().setDuration(800).rotation(360f).setInterpolator(new LinearInterpolator()).withEndAction(() -> {
            if (!isClearAnim) {
                animHead();
            } else {
                setRotation(0);
            }
        }).start();
    }

    private boolean isClearAnim;

    /**
     * 關閉自身旋轉
     */
    private void closeAnimHead() {
        isClearAnim = true;
        animate().cancel();
        setRotation(0);
        animHideScale(false, true);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean moved = false;
        float moveX, moveY;
        if (isTransing) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                paramX = floatingViewParams.x;
                paramY = floatingViewParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isBlur) {
                    break;
                }
                moveX = event.getRawX() - lastX;
                moveY = event.getRawY() - lastY;
                moved = Math.abs(moveX) >= 8 || Math.abs(moveY) >= 8;
                L.INSTANCE.d("wwc moved = " + moved);
                if (moved) {
                    hideMenu(false);
                    dx = (int) event.getRawX() - lastX;
                    dy = (int) event.getRawY() - lastY;
                    int x = paramX + dx;
                    int y = paramY + dy;
                    // 增加边界判断
                    if (isPortrait) {
                        if (y < mTopBoundary) {
                            y = mTopBoundary;
                        } else if (y > mBottomBoundary) {
                            y = mBottomBoundary;
                        }
                    } else {
                        if (y <= -sHeight / 2 + ICON_SIZE / 2) {
                            y = -sHeight / 2 + ICON_SIZE / 2;
                        } else if (y >= sHeight / 2 - ICON_SIZE / 2) {
                            y = sHeight / 2 - ICON_SIZE / 2;
                        }
                    }
                    floatingViewY = y;
                    floatingViewParams.x = x;
                    floatingViewParams.y = y;

                    // 更新悬浮窗位置
                    windowManager.updateViewLayout(this, floatingViewParams);
                    //移動中，隱藏字幕氣泡
                    hideTitle(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                L.INSTANCE.d(TAG, "CLICK X  " + event.getRawX());
                L.INSTANCE.d(TAG, "CLICK lastX  " + lastX);
                L.INSTANCE.d(TAG, "CLICK event.getAction()  " + event.getAction());
                moveX = event.getRawX() - lastX;
                moveY = event.getRawY() - lastY;
                moved = Math.abs(moveX) >= 5 || Math.abs(moveY) >= 5;
                if (!moved) {
                    clickIcon();
                } else {
                    if (!isBlur) {
                        attractSide(event.getRawX());
                    }
                }
                break;
            default:
        }
        return true;
    }

    /**
     * 自動關閉菜單
     */
    Runnable mCloseMenuRunnable = new Runnable() {
        @Override
        public void run() {
            hideMenu(true);
        }
    };

    /**
     * 頭像點擊事件
     */
    private void clickIcon() {
        //如果當前位於僕人預覽詳情頁，則發送廣播播放置頂語音
        if (isBlur) {
            cancelBlur();
        }
        showMenu();
    }

    /**
     * 顯示菜單
     */
    private void showMenu() {
        //正在執行動畫，不要再次觸發
        if (menuAnimRunning) {
            return;
        }

        if (titleLayoutParams != null) {
            hideTitle(false);
        }

        if (menuLayout != null && menuLayoutParams != null) {
            if (menuLayout.getVisibility() == View.VISIBLE) {
                menuLayout.setVisibility(View.GONE);
                return;
            }

            menuLayoutParams.y = (int) floatingViewY;
            //需要根據僕人顯示在左邊還是右邊， 來調整menu中菜單的位置
            if (leftShowTitle) {
                int menuWidth = menuLayout.getWidth();
                menuWidth = menuWidth == 0 ? MENU_WIDTH : menuWidth;
                menuLayoutParams.x = sWidth - menuWidth - MENU_START;
                layoutMenu.setPadding(MENU_PADDING_END, 0, MENU_PADDING_START, 0);

                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutRest.setLayoutParams(rParams);

                RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                tParams.addRule(RelativeLayout.END_OF, R.id.layout_rest);
                layouttanl.setLayoutParams(tParams);

                RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                vParams.addRule(RelativeLayout.END_OF, R.id.layout_tranl);
                layoutVoice.setLayoutParams(vParams);
            } else {
                menuLayoutParams.x = MENU_START;
                layoutMenu.setPadding(MENU_PADDING_START, 0, MENU_PADDING_END, 0);

                RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                vParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutVoice.setLayoutParams(vParams);

                RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                tParams.addRule(RelativeLayout.END_OF, R.id.layout_voice);
                layouttanl.setLayoutParams(tParams);

                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MENU_HEIGHT, LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.END_OF, R.id.layout_tranl);
                layoutRest.setLayoutParams(rParams);
            }
            windowManager.updateViewLayout(menuLayout, menuLayoutParams);

            menuLayout.setVisibility(View.VISIBLE);
            animateMenu();
        }
    }

    /**
     * 隱藏菜單
     */
    private void hideMenu(boolean isAnim) {
        if (menuAnimRunning) {
            return;
        }
        internalHandler.removeCallbacks(mCloseMenuRunnable);
        if (menuLayout != null && menuLayout.getVisibility() != GONE) {
            if (isAnim) {
                animateHideMenu();
            } else {
                menuLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 移動浮動ICON 后，自動貼在屏幕的左右邊緣
     *
     * @param rawX
     */
    private void attractSide(final float rawX) {
        final int startValue, endValue;
        final boolean leftSide = sWidth / 2f >= rawX;
        if (leftSide) {
            startValue = (int) (rawX - getWidth());
            endValue = 0;
            leftShowTitle = false;
        } else {
            leftShowTitle = true;
            startValue = (int) (rawX);
            endValue = sWidth - getWidth();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endValue);
        valueAnimator.setDuration(100);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            floatingViewParams.x = value;
            try {
                windowManager.updateViewLayout(FloatingPlayerView.this, floatingViewParams);
                if (value == endValue && titleShowing) {
                    //繼續未顯示完成的字幕
                    L.INSTANCE.d(TAG, "繼續未顯示完成的字幕");
                    showTitle();
                    //hideTitleAtDelayed();
                }
            } catch (IllegalArgumentException e) {
                L.INSTANCE.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        });
        valueAnimator.start();
    }

    private void attractSideXY(float rawX) {
        if (floatingViewParams != null) {
            int endValue;
            boolean leftSide = sWidth / 2f >= rawX;
            if (leftSide) {
                endValue = 0;
                leftShowTitle = false;
            } else {
                leftShowTitle = true;
                endValue = sWidth - getWidth();
            }
            floatingViewParams.x = endValue;

            if (floatingViewParams.y <= -sHeight / 2 + ICON_SIZE / 2) {
                floatingViewParams.y = -sHeight / 2 + ICON_SIZE / 2;
            } else if (floatingViewParams.y >= sHeight / 2 - ICON_SIZE / 2) {
                floatingViewParams.y = sHeight / 2 - ICON_SIZE / 2;
            }
            floatingViewY = floatingViewParams.y;
        }
        windowManager.updateViewLayout(FloatingPlayerView.this, floatingViewParams);
    }

    /**
     * 播放聲優語音后接受
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (ACTION_CV_PLAY.equals(intent.getAction())) {
                    int status = intent.getIntExtra("status", -1);
                    if (status == 0) {
                        // 播放
                        String title = intent.getStringExtra("title");
                        if (!TextUtils.isEmpty(title)) {
                            L.INSTANCE.d(TAG, " setTitle 播放 ");
                            setTitle(title);
                        }
                    } else if (status == 1) {
                        //播放成功完成
                        hideTitleAtDelayed();
                    }
                } else if (ACTION_IN_BACKGROUND.equals(intent.getAction())) {
                    boolean isForground = intent.getBooleanExtra("isForground", false);
                    final String activity = BuildConfig.APPLICATION_ID + ".BrowserActivity";
                    //截圖翻譯完成
                } else if (ACTION_CLOSE.equals(intent.getAction())) {
                    stopSelf();
                }
            }
        }
    };

    Handler internalHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                titleShowing = false;
                hideTitle(true);
            }
        }
    };

    public void removeAllView() {
        if (windowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    setRotation(0);
                    windowManager.removeViewImmediate(this);
                }
            }
            if (menuLayout != null && menuLayout.isAttachedToWindow()) {
                windowManager.removeViewImmediate(menuLayout);
            }
            if (titleLayout != null && titleLayout.isAttachedToWindow()) {
                windowManager.removeViewImmediate(titleLayout);
            }
        }
    }


    /**
     * 清除数据
     */
    public void destroy() {
        L.INSTANCE.d("wwc destroy initialized = " + initialized);
        stopService();
        if (initialized) {
            if (broadcastReceiver != null) {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
            }

            internalHandler.removeCallbacks(mCloseMenuRunnable);
            removeAllView();
            // 标识为未初始化
            initialized = false;
            // 标识为右边显示
            leftShowTitle = false;
            // 标识为未显示中
            titleShowing = false;
            // 防止View错位
            floatingViewY = 0;

        }
    }

    private void stopService() {
        isOpenShotPermission = SHOT_PERMISSION_STATUS_NORML;
        //Intent intent = new Intent(getContext(), ShotService.class);
        //getContext().stopService(intent);
    }

    /**
     * 監聽橫豎屏切換，
     * 橫豎屏切換后，寬高發生了變化，需要重新獲取，且需要對顯示區域重新計算
     *
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sWidth = ExKt.getScreenWidth(getContext());
        sHeight = ExKt.getScreenHeight(getContext());
        navBarHeight = ExKt.getNavBarHeight();
        statusHeight = ExKt.getStatusBarHeight();
        mTopBoundary = (-sHeight / 2) + statusHeight;
        mBottomBoundary = sHeight / 2 - navBarHeight;
        //為了避免錯誤，隱藏了title和menu
        hideMenu(false);
        hideTitle(false);
        //對頭像的顯示做處理
        if (floatingViewParams != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                isPortrait = true;
                floatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                menuLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            } else {
                //豎屏時，一般為遊戲界面，沒有stateBar，所以允許頭像顯示在頂部。 不考慮這種橫屏有狀態欄的情況了
                isPortrait = false;
                floatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    floatingViewParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
                menuLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    menuLayoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
            }
            attractSideXY(floatingViewParams.x);
        }
    }
}