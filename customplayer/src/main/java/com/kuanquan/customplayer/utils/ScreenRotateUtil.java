package com.kuanquan.customplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import android.view.OrientationEventListener;
import android.view.WindowManager;

/**
 * Created by fei.wang on 2018/6/21.
 * 屏幕旋转工具类
 */
public class ScreenRotateUtil extends OrientationEventListener {

    private boolean isBack;  // true 表示在横屏的状态下，按下返回键了，false 表示这时候已经是竖屏了
    private static final int ORIENTATION_PORTRAIT = 1;      // 竖屏
    private static final int ORIENTATION_LANDSCAPE = 2;     // 横屏
    private static final int ORIENTATION_REVERSE_LANDSCAPE = 3; // 反向横屏
    private int currentOreation = ORIENTATION_PORTRAIT; //当前重力感应方向
    private Activity mContext;
    private final WindowManager.LayoutParams attrs;

    public ScreenRotateUtil(Activity context, ScreenRotateListener mScreenRotateListener) {
        super(context);
        mContext = context;
        this.mScreenRotateListener = mScreenRotateListener;

        attrs = mContext.getWindow().getAttributes();  // 是否全屏
    }

    private void fullScreen(){
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mContext.getWindow().setAttributes(attrs);
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void noFullScreen(){
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext.getWindow().setAttributes(attrs);
        mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    // 开启监听  建议activity的onCreate方法中调用
    @Override
    public void enable() {
        super.enable();
    }

    // 关闭监听  建议activity的onDestroy方法中调用
    @Override
    public void disable() {
        super.disable();
    }

    // 在横屏的状态下点击了返回键
    public void setBack(boolean b) {
        isBack = b;
    }

    /**
     * 是否是横屏 ,true为横屏<br/>
     * Configuration.ORIENTATION_LANDSCAPE-2横屏<br/>
     * Configuration.ORIENTATION_PORTRAIT-1竖屏
     * context = Application.getAppContext()
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 检查当前用户是否系统锁定了屏幕
     * true :　可以转
     * false :  不可以转
     */
    private boolean checkIsSystemOrientationEnabled() {
        boolean isOrientationEnabled;
        try {
//            isOrientationEnabled = Settings.System.getInt(QuApplication.getAppContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1;
            isOrientationEnabled = Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Settings.SettingNotFoundException e) {
            isOrientationEnabled = false;
        }
        return isOrientationEnabled;
    }

    /**
     * 手动切换横屏的时候调用的时候
     */
    public void manualSwitchingLandscape() {
        currentOreation = ORIENTATION_LANDSCAPE;
        mScreenRotateListener.onLandscape();
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullScreen();
//        ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * 手动要切换到竖屏的时候调用的时候
     */
    public void manualSwitchingPortrait() {
        currentOreation = ORIENTATION_PORTRAIT;
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        noFullScreen();
        mScreenRotateListener.onPortrait();
//        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        int screenOrientation = mContext.getResources().getConfiguration().orientation;
        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) { // 设置竖屏
            if (currentOreation != ORIENTATION_PORTRAIT) {
                if (checkIsSystemOrientationEnabled()) {
                    if (!isBack) {
                        currentOreation = ORIENTATION_PORTRAIT;
                        LogUtil.e("ScreenRotateUtil", "设置竖屏screenOrientation = " + screenOrientation);
                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        // 显示状态栏
                        noFullScreen();
//                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        mScreenRotateListener.onPortrait();
                    }
                }
            }
            isBack = false;
        } else if (orientation > 225 && orientation < 315) { // 设置横屏
            if (currentOreation != ORIENTATION_LANDSCAPE) {
                if (checkIsSystemOrientationEnabled()) {
                    if (!isBack) {
                        currentOreation = ORIENTATION_LANDSCAPE;
                        LogUtil.e("ScreenRotateUtil", "设置横屏screenOrientation = " + screenOrientation);
                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        // 隐藏状态栏
                        fullScreen();
//                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        mScreenRotateListener.onLandscape();
                    }
                }
            }
        } else if (orientation > 45 && orientation < 135) {  // 设置反向横屏
            if (currentOreation != ORIENTATION_REVERSE_LANDSCAPE) {
                if (checkIsSystemOrientationEnabled()) {
                    if (!isBack) {
                        currentOreation = ORIENTATION_REVERSE_LANDSCAPE;
                        LogUtil.e("ScreenRotateUtil", "设置反向横屏screenOrientation = " + screenOrientation);
                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        // 隐藏状态栏
                        fullScreen();
//                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        mScreenRotateListener.onLandscape();
                    }
                }
            }
        }else{

        }
    }

    private ScreenRotateListener mScreenRotateListener;

    public interface ScreenRotateListener {   // 屏幕监听的接口
        void onPortrait();           // 竖屏

        void onLandscape();          // 横屏

//        void onReverseLandscape();   // 反向横屏
    }
}
