package com.kuanquan.playdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.kuanquan.playdemo.widget.media.AndroidMediaController;
import com.kuanquan.playdemo.widget.media.IjkVideoView;
import com.kuanquan.playdemo.widget.media.MeasureHelper;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 真正的视频播放Activity
 */
public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private FrameLayout mFrameLayout;
    private String mVideoPath;   // 播放路径
    private AndroidMediaController mMediaController;   // 播放控制器
    private IjkVideoView mVideoView;     // 播放器类（封装SurfaceView和TexTrueView）
//    private TextView mToastTextView;     // 显示提示信息改在播放器上面的
//    private TableLayout mHudView;        // 设置显示到屏幕中间网速、缓存的布局
    private boolean mBackPressed;        // 是否退出 true退出

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mFrameLayout = findViewById(R.id.drawer_layout);
        mVideoPath = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";

        // init UI
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        mMediaController = new AndroidMediaController(this, true);
//        mMediaController.hide();
//        mMediaController.setSupportActionBar(actionBar);  // 设置顶部标题栏

//        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
//        mHudView = (TableLayout) findViewById(R.id.hud_view);

        // init player  初始化播放器
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.clearFocus();
        mFrameLayout.setFocusable(true);
        mFrameLayout.setFocusableInTouchMode(true);
//        mVideoView.setEnabled(true);
        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"点击播放器布局" + index++);
            }
        });

        Log.e(TAG,"视屏高度 = " + mVideoView.getHeight());
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);
//        mVideoView.setHudView(mHudView); // 设置显示到屏幕中间网速、缓存的布局
        // 设置播放路径
        if (mVideoPath != null) {
            mVideoView.setVideoPath(mVideoPath);
        }else {
            finish();
            return;
        }
        mVideoView.start();  // 开始播放
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();  // 停止播放
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * 竖屏的时候播放器充满全屏
     */
    public void onOptionsItemSelected() {
        int aspectRatio = mVideoView.toggleAspectRatio();
        String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
//        mToastTextView.setText(aspectRatioText);
//        mMediaController.showOnce(mToastTextView);
    }

}