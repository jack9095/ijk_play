package com.kuanquan.customplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.kuanquan.customplayer.utils.MediaUtils;
import com.kuanquan.customplayer.utils.ScreenRotateUtil;
import com.kuanquan.customplayer.utils.ScreenUtils;
import com.kuanquan.customplayer.utils.ScreenWakeLockUtil;
import com.kuanquan.customplayer.utils.UrlUtil;
import com.kuanquan.customplayer.view.CommonPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CommonVideoActivity extends AppCompatActivity implements ScreenRotateUtil.ScreenRotateListener, CommonPlayer.PlayerListener{

    private CommonPlayer mCommonPlayer;
    private int index;
        private ScreenRotateUtil mScreenRotateUtil;
    private ScreenWakeLockUtil mScreenWakeLockUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_video);
        mCommonPlayer = findViewById(R.id.common_player);
        mScreenRotateUtil = new ScreenRotateUtil(this, this);
        mScreenRotateUtil.enable();
        mCommonPlayer.setStartIjkMediaPlayer();
        mScreenWakeLockUtil = new ScreenWakeLockUtil(this);
        mScreenWakeLockUtil.onCreate();
        mCommonPlayer.setPlayerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        mCommonPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PlayActivity", "播放器点击事件 = " + index++);
                mCommonPlayer.hideShowViewAll();
            }
        });
        mCommonPlayer.setVideoPath(UrlUtil.url4);
        mCommonPlayer.initSurfaceView();
    }

    @Override
    public void onPortrait() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCommonPlayer.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(this) / 3;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        mCommonPlayer.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mCommonPlayer.mPlayerBottomView.getLineView().setVisibility(View.GONE);

    }

    @Override
    public void onLandscape() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCommonPlayer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        mCommonPlayer.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mCommonPlayer.mPlayerBottomView.getLineView().setVisibility(View.VISIBLE);
    }

    @Override
    public void goBack() {
        if (ScreenRotateUtil.isLandscape(this)) {
            mScreenRotateUtil.setBack(true);
            mScreenRotateUtil.manualSwitchingPortrait();
        } else {
            mScreenWakeLockUtil.finish();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    public void screen(int type) {
        if (type == 1) { // 到横屏
            mScreenRotateUtil.manualSwitchingLandscape();
        } else {
            mScreenRotateUtil.manualSwitchingPortrait();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            mCommonPlayer.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCommonPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenRotateUtil.disable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaUtils.muteAudioFocus(this, true); // 恢复系统其它媒体的状态
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaUtils.muteAudioFocus(this, false);  // 暂停系统其它媒体的状态
        mScreenWakeLockUtil.onResume();
    }
}
