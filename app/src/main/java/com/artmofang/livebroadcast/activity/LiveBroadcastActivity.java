package com.artmofang.livebroadcast.activity;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artmofang.livebroadcast.R;
import com.artmofang.livebroadcast.utils.T;
import com.artmofang.livebroadcast.utils.TCUtils;
import com.artmofang.livebroadcast.widget.BeautySettingPannel;
import com.artmofang.livebroadcast.widget.FilterSettingPannel;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * 直播
 */
public class LiveBroadcastActivity extends AppCompatActivity implements ITXLivePushListener
        , FilterSettingPannel.IOnBeautyParamsChangeListener, View.OnClickListener
        , BeautySettingPannel.IOnBeautyParamsChangeListener {


    @Bind(R.id.img_closLive)
    ImageView imgClosLive;
    @Bind(R.id.tv_verticalScreen)
    TextView tvVerticalScreen;
    @Bind(R.id.tv_fbcameral)
    TextView tvFbcameral;
    @Bind(R.id.tv_adjustFocus)
    TextView tvAdjustFocus;
    @Bind(R.id.tv_resolvingRation)
    TextView tvResolvingRation;
    @Bind(R.id.ll_setting)
    LinearLayout llSetting;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.tv_beauty)
    TextView tvBeauty;
    @Bind(R.id.btn_startLive)
    Button btnStartLive;
    @Bind(R.id.video_view)//播放视频
            TXCloudVideoView videoView;
    @Bind(R.id.layoutFaceFilter)
    FilterSettingPannel layoutFaceFilter;
    @Bind(R.id.layoutFace_eauty)
    BeautySettingPannel layoutFaceBeauty;
    @Bind(R.id.ll_Bitrate)//分辨率
            LinearLayout llBitrate;
    @Bind(R.id.rg_filter)
    RadioGroup rgFilter;
    private String liveRoom, live_push;
    private boolean mVideoPublish;//播放状态
    //    推流配置
    private TXLivePushConfig mLivePushConfig;
    private TXLivePusher mLivePusher;
    private int mBeautyLevel = 5;
    private int mWhiteningLevel = 3;
    private int mRuddyLevel = 2;
    private int mBeautyStyle = TXLiveConstants.BEAUTY_STYLE_SMOOTH;
    private static final int VIDEO_SRC_CAMERA = 0;
    private static final int VIDEO_SRC_SCREEN = 1;
    private int mVideoSrc = VIDEO_SRC_CAMERA;
    private boolean mFrontCamera = true;//前置摄像头
    private int mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;//分辨率
    // 关注系统设置项“自动旋转”的状态切换
    private RotationObserver mRotationObserver = null;
    private boolean mPortrait = true;         //手动切换，横竖屏推流
    private PhoneStateListener mPhoneListener = null;
    private boolean mTouchFocus = true;//设置
    private boolean mIsRealTime = false;
    private boolean mHWVideoEncode = true;//默认硬件加速开启
    //   直播时间计时 
    private long mSecond = 0;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcast);
        ButterKnife.bind(this);
        initData();
        checkPublishPermission();

        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
        mLivePusher.setConfig(mLivePushConfig);
        mVideoPublish = false;
        //监听结束直播
        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();

        //手机监听
        mPhoneListener = new TXPhoneStateListener(mLivePusher);
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        //    横竖屏切换    
        if (isActivityCanRotation()) {
            tvVerticalScreen.setVisibility(View.GONE);
        }
        layoutFaceFilter.setBeautyParamsChangeListener(this);
        layoutFaceBeauty.setBeautyParamsChangeListener(this);

        View view = findViewById(R.id.rl_outSideClose);
        view.setOnClickListener(this);

        /**
         * 设置像素
         */
        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                boolean oldMode = mIsRealTime;
                FixOrAdjustBitrate();
                if (oldMode != mIsRealTime && mLivePusher != null && mLivePusher.isPushing()) {
                    stopPublishRtmp();
                    startPublishRtmp();
                }
                llBitrate.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        liveRoom = getIntent().getStringExtra("liveRoom");
        live_push = getIntent().getStringExtra("live_push");//推流url
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                btnStartLive.setVisibility(View.VISIBLE);
                layoutFaceFilter.setVisibility(View.GONE);
                layoutFaceBeauty.setVisibility(View.GONE);
                llBitrate.setVisibility(View.GONE);
        }
    }

    /**
     * 关闭直播,竖屏,前后摄像头,调整焦距
     *
     * @param view
     */
    @OnClick({R.id.img_closLive, R.id.tv_verticalScreen, R.id.tv_fbcameral, R.id.tv_adjustFocus, R.id.tv_resolvingRation, R.id.tv_filter,
            R.id.tv_beauty, R.id.btn_startLive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_closLive://关闭直播
                if (liveRoom.equals("liveRoom")) {
                    this.finish();
                } else {
                    Intent intent = new Intent(LiveBroadcastActivity.this, LiveRoomActivity.class);
                    intent.putExtra("live_id", "");
                    startActivity(intent);
                    this.finish();
                }
                break;
            case R.id.tv_verticalScreen://竖屏
                T.show(LiveBroadcastActivity.this, "竖屏", 1000);
                mPortrait = !mPortrait;
                int renderRotation = 0;
                int orientation = 0;
                boolean screenCaptureLandscape = false;
                if (mPortrait) {
                    mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
//                    tvVerticalScreen.setBackgroundResource(R.drawable.landscape);
                    tvVerticalScreen.setText("横屏");
                    orientation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
                    renderRotation = 0;
                } else {
                    mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
//                    tvVerticalScreen.setBackgroundResource(R.drawable.portrait);
                    tvVerticalScreen.setText("竖屏");
                    screenCaptureLandscape = true;
                    orientation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
                    renderRotation = 90;
                }
                if (VIDEO_SRC_SCREEN == mVideoSrc) {
                    //录屏横竖屏推流的判断条件是，视频分辨率取360*640还是640*360
                    switch (mCurrentVideoResolution) {
                        case TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640:
                            if (screenCaptureLandscape)
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_640_360);
                            else
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                            break;
                        case TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960:
                            if (screenCaptureLandscape)
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_960_540);
                            else
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                            break;
                        case TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280:
                            if (screenCaptureLandscape)
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_1280_720);
                            else
                                mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280);
                            break;
                    }

                }
                if (mLivePusher.isPushing()) {
                    if (VIDEO_SRC_CAMERA == mVideoSrc) {
                        mLivePusher.setConfig(mLivePushConfig);
                    } else if (VIDEO_SRC_SCREEN == mVideoSrc) {
                        mLivePusher.setConfig(mLivePushConfig);
                        mLivePusher.stopScreenCapture();
                        mLivePusher.startScreenCapture();
                    }
                } else mLivePusher.setConfig(mLivePushConfig);
                mLivePusher.setRenderRotation(renderRotation);
                break;
            case R.id.tv_fbcameral://前后摄像头
                T.show(LiveBroadcastActivity.this, "前后摄像头", 1000);
                mFrontCamera = !mFrontCamera;

                if (mLivePusher.isPushing()) {
                    mLivePusher.switchCamera();
                }
                mLivePushConfig.setFrontCamera(mFrontCamera);
                if (mFrontCamera) {
                    tvFbcameral.setText("前置摄像头");
                } else {
                    tvFbcameral.setText("后置摄像头");
                }
                break;
            case R.id.tv_adjustFocus://调整焦距
                if (mFrontCamera) {
                    return;
                }

                mTouchFocus = !mTouchFocus;
                mLivePushConfig.setTouchFocus(mTouchFocus);
//                v.setBackgroundResource(mTouchFocus ? R.drawable.automatic : R.drawable.manual);
                tvAdjustFocus.setText(mTouchFocus ? "自动对焦" : "手动对焦");
                if (mLivePusher.isPushing()) {
                    mLivePusher.stopCameraPreview(false);
                    mLivePusher.startCameraPreview(videoView);
                }

                T.show(LiveBroadcastActivity.this, mTouchFocus ? "已开启手动对焦" : "已开启自动对焦", 1000);
                break;
            case R.id.tv_resolvingRation://分辨率
                T.show(LiveBroadcastActivity.this, "分辨率", 1000);
                llBitrate.setVisibility(llBitrate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                btnStartLive.setVisibility(llBitrate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                layoutFaceBeauty.setVisibility(View.GONE);
                break;
            case R.id.tv_filter://滤镜
                layoutFaceFilter.setVisibility(layoutFaceFilter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                btnStartLive.setVisibility(layoutFaceFilter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                layoutFaceBeauty.setVisibility(View.GONE);
                llBitrate.setVisibility(View.GONE);
                break;
            case R.id.tv_beauty://美颜开
//                T.show(LiveBroadcastActivity.this, "美颜开", 1000);
                layoutFaceBeauty.setVisibility(layoutFaceBeauty.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                btnStartLive.setVisibility(layoutFaceBeauty.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                layoutFaceFilter.setVisibility(View.GONE);
                llBitrate.setVisibility(View.GONE);

                break;
            case R.id.btn_startLive://开始直播
                T.show(LiveBroadcastActivity.this, "开始直播", 1000);
                if (mVideoPublish) {
                    stopPublishRtmp();
                    //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                    stopRecordAnimation();
                } else {
                    if (mVideoSrc == VIDEO_SRC_CAMERA) {
                        FixOrAdjustBitrate();  //根据设置确定是“固定”还是“自动”码率
                    } else {
                        //录屏横竖屏采用两种分辨率，和摄像头推流逻辑不一样
                    }
                    mVideoPublish = startPublishRtmp();
                }

                break;

        }
    }


    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            //Log.i(TAG, "timeTask ");
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    btnStartLive.setText("结束直播 " + TCUtils.formattedTime(mSecond));
                }
            });
//            if (MySelfInfo.getInstance().getIdStatus() == TCConstants.HOST)
//                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
//            Log.e("mSecond","结束直播 " + mSecond+"");
        }
    }

    /**
     * 关闭计时
     */
    private void stopRecordAnimation() {
        
        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
        Log.e("sssssssssssssssss","结束直播 " + TCUtils.formattedTime(mSecond));
    }

    /**
     * 开始直播启动推流
     *
     * @return
     */
    private boolean startPublishRtmp() {
        String rtmpUrl = "";
        if (!TextUtils.isEmpty(live_push)) {
            String url[] = live_push.split("###");
            if (url.length > 0) {
                rtmpUrl = url[0];
            }
        }

        if (TextUtils.isEmpty(rtmpUrl) || (!rtmpUrl.trim().toLowerCase().startsWith("rtmp://"))) {
            Toast.makeText(getApplicationContext(), "推流地址不合法，目前支持rtmp推流!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mVideoSrc != VIDEO_SRC_SCREEN) {
            videoView.setVisibility(View.VISIBLE);
        }
        int customModeType = 0;
        if (isActivityCanRotation()) {
            onActivityRotation();
        }
        mLivePushConfig.setCustomModeType(customModeType);
        mLivePusher.setPushListener(this);
        mLivePushConfig.setPauseImg(300, 5);
        Bitmap bitmap = decodeResource(getResources(), R.mipmap.pause_publish);//主播不在显示的界面
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
        if (mVideoSrc != VIDEO_SRC_SCREEN) {
            mLivePushConfig.setFrontCamera(mFrontCamera);
            mLivePushConfig.setBeautyFilter(mBeautyLevel, mWhiteningLevel, mRuddyLevel);
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.startCameraPreview(videoView);
        } else {
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.startScreenCapture();
        }

        mLivePusher.startPusher(rtmpUrl.trim());//推流


        btnStartLive.setBackgroundResource(R.mipmap.livepage_btn_finish);
//        btnStartLive.setText("结束直播 1:51:02");
        //直播时间
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
        return true;
    }


    /**
     * 停止直播
     */

    private void stopPublishRtmp() {
        mVideoPublish = false;
        mLivePusher.stopBGM();
        mLivePusher.stopCameraPreview(true);
        mLivePusher.stopScreenCapture();
        mLivePusher.setPushListener(null);
        mLivePusher.stopPusher();
        videoView.setVisibility(View.GONE);
        //启动关闭硬件加速
//        if(mBtnHWEncode != null) {
//            //mHWVideoEncode = true;
//            mLivePushConfig.setHardwareAcceleration(mHWVideoEncode ? TXLiveConstants.ENCODE_VIDEO_HARDWARE : TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
//            mBtnHWEncode.setBackgroundResource(R.drawable.quick);
//            mBtnHWEncode.getBackground().setAlpha(mHWVideoEncode ? 255 : 100);
//        }
//
//        enableQRCodeBtn(true);
//        mBtnPlay.setBackgroundResource(R.drawable.play_start);
        btnStartLive.setText("开始直播");

        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
    }


    /**
     * 判断Activity是否可旋转。只有在满足以下条件的时候，Activity才是可根据重力感应自动旋转的。
     * 系统“自动旋转”设置项打开；
     *
     * @return false---Activity可根据重力感应自动旋转
     */
    protected boolean isActivityCanRotation() {
        // 判断自动旋转是否打开
        int flag = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        if (flag == 0) {
            return false;
        }
        return true;
    }

    protected void onActivityRotation() {
        // 自动旋转打开，Activity随手机方向旋转之后，需要改变推流方向
        int mobileRotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
        boolean screenCaptureLandscape = false;
        switch (mobileRotation) {
            case Surface.ROTATION_0:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
                break;
            case Surface.ROTATION_90:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
                screenCaptureLandscape = true;
                break;
            case Surface.ROTATION_270:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_LEFT;
                screenCaptureLandscape = true;
                break;
            default:
                break;
        }
        mLivePusher.setRenderRotation(0); //因为activity也旋转了，本地渲染相对正方向的角度为0。
        mLivePushConfig.setHomeOrientation(pushRotation);
        if (mLivePusher.isPushing()) {
            if (VIDEO_SRC_CAMERA == mVideoSrc) {
                mLivePusher.setConfig(mLivePushConfig);
                mLivePusher.stopCameraPreview(true);
                mLivePusher.startCameraPreview(videoView);
            } else if (VIDEO_SRC_SCREEN == mVideoSrc) {
                //录屏横竖屏推流的判断条件是，视频分辨率取360*640还是640*360
                switch (mCurrentVideoResolution) {
                    case TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640:
                        if (screenCaptureLandscape)
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_640_360);
                        else
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                        break;
                    case TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960:
                        if (screenCaptureLandscape)
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_960_540);
                        else
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                        break;
                    case TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280:
                        if (screenCaptureLandscape)
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_1280_720);
                        else
                            mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280);
                        break;
                }
                mLivePusher.setConfig(mLivePushConfig);
                mLivePusher.stopScreenCapture();
                mLivePusher.startScreenCapture();
            }
        }
    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void onBeautyParamsChange(FilterSettingPannel.BeautyParams params, int key) {
        switch (key) {
            case FilterSettingPannel.BEAUTYPARAM_EXPOSURE:
                if (mLivePusher != null) {
                    mLivePusher.setExposureCompensation(params.mExposure);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_BEAUTY:
                mBeautyLevel = params.mBeautyLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_WHITE:
                mWhiteningLevel = params.mWhiteLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_BIG_EYE:
                if (mLivePusher != null) {
                    mLivePusher.setEyeScaleLevel(params.mBigEyeLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_FACE_LIFT:
                if (mLivePusher != null) {
                    mLivePusher.setFaceSlimLevel(params.mFaceSlimLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_FILTER:
                if (mLivePusher != null) {
                    mLivePusher.setFilter(params.mFilterBmp);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_GREEN:
                if (mLivePusher != null) {
                    mLivePusher.setGreenScreenFile(params.mGreenFile);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_MOTION_TMPL:
                if (mLivePusher != null) {
                    mLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_RUDDY:
                mRuddyLevel = params.mRuddyLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_BEAUTY_STYLE:
                mBeautyStyle = params.mBeautyStyle;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_FACEV:
                if (mLivePusher != null) {
                    mLivePusher.setFaceVLevel(params.mFaceVLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_FACESHORT:
                if (mLivePusher != null) {
                    mLivePusher.setFaceShortLevel(params.mFaceShortLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_CHINSLIME:
                if (mLivePusher != null) {
                    mLivePusher.setChinLevel(params.mChinSlimLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_NOSESCALE:
                if (mLivePusher != null) {
                    mLivePusher.setNoseSlimLevel(params.mNoseScaleLevel);
                }
                break;
            case FilterSettingPannel.BEAUTYPARAM_FILTER_MIX_LEVEL:
                if (mLivePusher != null) {
                    mLivePusher.setSpecialRatio(params.mFilterMixLevel / 10.f);
                }
                break;
//            case BeautySettingPannel.BEAUTYPARAM_CAPTURE_MODE:
//                if (mLivePusher != null) {
//                    boolean bEnable = ( 0 == params.mCaptureMode ? false : true);
//                    mLivePusher.enableHighResolutionCapture(bEnable);
//                }
//                break;
//            case BeautySettingPannel.BEAUTYPARAM_SHARPEN:
//                if (mLivePusher != null) {
//                    mLivePusher.setSharpenLevel(params.mSharpenLevel);
//                }
//                break;
        }
    }

    @Override
    public void onBeautyParamsChange(BeautySettingPannel.BeautyParams params, int key) {
        switch (key) {
            case BeautySettingPannel.BEAUTYPARAM_EXPOSURE:
                if (mLivePusher != null) {
                    mLivePusher.setExposureCompensation(params.mExposure);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BEAUTY:
                mBeautyLevel = params.mBeautyLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_WHITE:
                mWhiteningLevel = params.mWhiteLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BIG_EYE:
                if (mLivePusher != null) {
                    mLivePusher.setEyeScaleLevel(params.mBigEyeLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACE_LIFT:
                if (mLivePusher != null) {
                    mLivePusher.setFaceSlimLevel(params.mFaceSlimLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FILTER:
                if (mLivePusher != null) {
                    mLivePusher.setFilter(params.mFilterBmp);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_GREEN:
                if (mLivePusher != null) {
                    mLivePusher.setGreenScreenFile(params.mGreenFile);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_MOTION_TMPL:
                if (mLivePusher != null) {
                    mLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_RUDDY:
                mRuddyLevel = params.mRuddyLevel;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BEAUTY_STYLE:
                mBeautyStyle = params.mBeautyStyle;
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACEV:
                if (mLivePusher != null) {
                    mLivePusher.setFaceVLevel(params.mFaceVLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACESHORT:
                if (mLivePusher != null) {
                    mLivePusher.setFaceShortLevel(params.mFaceShortLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_CHINSLIME:
                if (mLivePusher != null) {
                    mLivePusher.setChinLevel(params.mChinSlimLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_NOSESCALE:
                if (mLivePusher != null) {
                    mLivePusher.setNoseSlimLevel(params.mNoseScaleLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FILTER_MIX_LEVEL:
                if (mLivePusher != null) {
                    mLivePusher.setSpecialRatio(params.mFilterMixLevel / 10.f);
                }
                break;
        }
    }


    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = LiveBroadcastActivity.this.getContentResolver();
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //更新按钮状态
            if (isActivityCanRotation()) {
                tvVerticalScreen.setVisibility(View.GONE);
                onActivityRotation();
            } else {
                tvVerticalScreen.setVisibility(View.VISIBLE);
                mPortrait = true;
                mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
                tvVerticalScreen.setText("横屏");
                mLivePusher.setRenderRotation(0);
                mLivePusher.setConfig(mLivePushConfig);
            }

        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }


    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePusher> mPusher;

        public TXPhoneStateListener(TXLivePusher pusher) {
            mPusher = new WeakReference<TXLivePusher>(pusher);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePusher pusher = mPusher.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pusher != null) pusher.resumePusher();
                    break;
            }
        }
    }

    ;

    /**
     * 设置分别率
     */
    public void FixOrAdjustBitrate() {
        if (rgFilter == null || mLivePushConfig == null || mLivePusher == null) {
            return;
        }

        RadioButton rb = (RadioButton) findViewById(rgFilter.getCheckedRadioButtonId());
        int mode = Integer.parseInt((String) rb.getTag());
        mIsRealTime = false;
        switch (mode) {
            case 5: /*720p*/
                if (mLivePusher != null) {
                    mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION, false, false);
                    mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280;
                    //超清默认开启硬件加速
                    if (Build.VERSION.SDK_INT >= 18) {
                        mHWVideoEncode = true;
                    }
//                    mBtnHWEncode.getBackground().setAlpha(255);
                }
                break;
            case 4: /*540p*/
                if (mLivePusher != null) {
                    mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
                    mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960;
                    mHWVideoEncode = false;
//                    mBtnHWEncode.getBackground().setAlpha(100);
                }
                break;
            case 3: /*360p*/
                if (mLivePusher != null) {
                    mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION, true, false);
                    mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                    //标清默认开启了码率自适应，需要关闭码率自适应
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(700);
                    mLivePusher.setConfig(mLivePushConfig);
                    //标清默认关闭硬件加速
                    mHWVideoEncode = false;
//                    mBtnHWEncode.getBackground().setAlpha(100);
                }
                break;

            case 2: /*自动*/
                if (mLivePusher != null) {
                    mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION, true, true);
                    mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                    //标清默认关闭硬件加速
                    mHWVideoEncode = false;
//                    mBtnHWEncode.getBackground().setAlpha(100);
                }
                break;
            case 1: /*实时*/
                if (mLivePusher != null) {
                    mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_REALTIEM_VIDEOCHAT, true, false);
                    mCurrentVideoResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                    //超清默认开启硬件加速
                    if (Build.VERSION.SDK_INT >= 18) {
                        mHWVideoEncode = true;
//                        mBtnHWEncode.getBackground().setAlpha(255);
                    }
                    mIsRealTime = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (videoView != null) {
            videoView.onResume();
        }

        if (mVideoPublish && mLivePusher != null && mVideoSrc == VIDEO_SRC_CAMERA) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.onPause();
        }

        if (mVideoPublish && mLivePusher != null && mVideoSrc == VIDEO_SRC_CAMERA) {
            mLivePusher.pausePusher();
            mLivePusher.pauseBGM();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPublishRtmp();
        if (videoView != null) {
            videoView.onDestroy();
        }

        mRotationObserver.stopObserver();
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);

    }


    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }

        return true;
    }
}
