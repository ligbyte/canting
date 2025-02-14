package com.stkj.infocollect.home.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.data.CBGFacePassConfigMMKV;
import com.stkj.cbgfacepass.model.CBGFacePassConfig;
import com.stkj.cbgfacepass.permission.CBGPermissionRequest;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shadowlayout.ShadowFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.infocollect.BuildConfig;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.callback.AppNetCallback;
import com.stkj.infocollect.base.net.AppNetManager;
import com.stkj.infocollect.base.permission.AppPermissionHelper;
import com.stkj.infocollect.base.tts.TTSVoiceHelper;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.card.callback.FacePassVerifyListener;
import com.stkj.infocollect.card.ui.fragment.FacePassVerificationFragment;
import com.stkj.infocollect.card.ui.fragment.OpenCardFragment;
import com.stkj.infocollect.home.helper.HeartBeatHelper;
import com.stkj.infocollect.home.helper.ScreenProtectHelper;
import com.stkj.infocollect.home.helper.SystemEventWatcherHelper;
import com.stkj.infocollect.home.ui.widget.HomeTitleLayout;
import com.stkj.infocollect.login.callback.LoginCallback;
import com.stkj.infocollect.login.helper.LoginHelper;
import com.stkj.infocollect.setting.data.FacePassDateBaseMMKV;
import com.stkj.infocollect.setting.data.ServerSettingMMKV;
import com.stkj.infocollect.setting.helper.AppUpgradeHelper;
import com.stkj.infocollect.setting.model.FacePassPeopleInfo;
import com.stkj.infocollect.setting.ui.fragment.TabSettingFragment;

public class MainActivity extends BaseActivity implements AppNetCallback {

    private FrameLayout flScreenProtect;
    private LinearLayout llMainMenu;
    private ShadowFrameLayout sflOpenCard;
    private ShadowFrameLayout sflSupplyCard;
    private ShadowFrameLayout sflCancelSupplyCard;
    private ShapeLinearLayout sllSetting;

    private boolean isSoftKeyboardShow;
    private boolean hasInitData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        // 判断当前activity是不是所在任务栈的根
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            //1.避免从桌面启动程序后，会重新实例化入口类的activity , 判断当前activity是不是所在任务栈的根
            if (!isTaskRoot()) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
            //2.经过路由跳转的，判断当前应用是否已经初始化过，首页是否存在并且未销毁
            if (Intent.ACTION_VIEW.equals(action)) {
                Activity homeActivity = AppManager.INSTANCE.getMainActivity();
                if (!ActivityUtils.isActivityFinished(homeActivity)) {
                    finish();
                    return;
                }
            }
        }
        AppManager.INSTANCE.setMainActivity(this);
        setContentView(com.stkj.infocollect.R.layout.activity_main);
        findViews();
        initApp();
        LogHelper.print("-main--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_place_holder_content;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //打开屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 初始化app
     */
    private void initApp() {
        //初始化人脸识别
        CBGFacePassHandlerHelper facePassHelper = getWeakRefHolder(CBGFacePassHandlerHelper.class);
        facePassHelper.setReadAssetCert(true);
        facePassHelper.setOnInitFacePassListener(new CBGFacePassHandlerHelper.OnInitFacePassListener() {
            @Override
            public void onInitSuccess() {
                initData();
            }

            @Override
            public void onInitError(String msg) {
                hideLoadingDialog();
                CommonDialogUtils.showTipsDialog(MainActivity.this, msg, "知道了", new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        initData();
                    }
                });
            }
        });
        AppPermissionHelper.with(this)
                .requestPermission(new CBGPermissionRequest(), new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        showLoadingDialog();
                        //设备识别距离阈值
                        int defaultFaceMinThreshold = 512 - FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL50;
                        CBGFacePassConfigMMKV.setDefDetectFaceMinThreshold(defaultFaceMinThreshold);
                        CBGFacePassConfig facePassConfig = CBGFacePassConfigMMKV.getFacePassConfig(false);
                        CBGFacePassHandlerHelper facePassHelper = getWeakRefHolder(CBGFacePassHandlerHelper.class);
                        facePassHelper.initAndAuthSdk(facePassConfig);
                    }

                    @Override
                    public void onCancel() {
                        CommonDialogUtils.showTipsDialog(MainActivity.this, "人脸识别功能请求系统权限失败", "知道了", new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                initData();
                            }
                        });
                    }
                });
    }

    /**
     * 清理焦点
     */
    public void clearMainFocus() {
        //清理焦点信息
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private void findViews() {
        HomeTitleLayout htlMain = (HomeTitleLayout) findViewById(R.id.htl_main);
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(htlMain);
        }
        llMainMenu = (LinearLayout) findViewById(R.id.ll_main_menu);
        sflOpenCard = (ShadowFrameLayout) findViewById(R.id.sfl_open_card);
        sflOpenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContentPlaceHolderFragment(new OpenCardFragment());
            }
        });
        sflSupplyCard = (ShadowFrameLayout) findViewById(R.id.sfl_supply_card);
        sflSupplyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacePassVerificationFragment facePassVerificationFragment  = new FacePassVerificationFragment();
                facePassVerificationFragment.setOperateType(FacePassVerificationFragment.APPLY_LOSS_REPLACE_CARD);
                facePassVerificationFragment.setFacePassVerifyListener(new FacePassVerifyListener() {
                    @Override
                    public void onFacePassVerify(FacePassPeopleInfo passPeopleInfo) {

                    }
                });
                addContentPlaceHolderFragment(facePassVerificationFragment);
            }
        });
        sflCancelSupplyCard = (ShadowFrameLayout) findViewById(R.id.sfl_cancel_supply_card);
        sflCancelSupplyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacePassVerificationFragment facePassVerificationFragment  = new FacePassVerificationFragment();
                facePassVerificationFragment.setOperateType(FacePassVerificationFragment.SEARCH_LOSS_REPLACE_CARD);
                facePassVerificationFragment.setFacePassVerifyListener(new FacePassVerifyListener() {
                    @Override
                    public void onFacePassVerify(FacePassPeopleInfo passPeopleInfo) {

                    }
                });
                addContentPlaceHolderFragment(facePassVerificationFragment);
            }
        });
        sllSetting = (ShapeLinearLayout) findViewById(R.id.sll_setting);
        sllSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContentPlaceHolderFragment(new TabSettingFragment());
            }
        });
        flScreenProtect = (FrameLayout) findViewById(R.id.fl_screen_protect);
        flScreenProtect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flScreenProtect.setVisibility(View.GONE);
                ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
                screenProtectHelper.startScreenProtect();
            }
        });
        View rootPlaceHolderView = findViewById(R.id.root_view_placeholder);
        rootPlaceHolderView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //获取占位view高度
                        int placeViewHeight = rootPlaceHolderView.getHeight();
                        if (placeViewHeight <= 0) {
                            return;
                        }
                        Rect rect = new Rect();
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                        //获取被遮挡高度
                        int keyBoardHeight = placeViewHeight - rect.height();
                        LogHelper.print("keyboard onGlobalLayout: placeViewHeight = " + placeViewHeight + " rect.height = " + rect.height());
                        //软键盘显示或者隐藏
                        boolean needTouchHideKeyboard = keyBoardHeight >= 200;
                        if (needTouchHideKeyboard) {
                            isSoftKeyboardShow = true;
                            LogHelper.print("show keyboard--offset = " + keyBoardHeight);
                            rootPlaceHolderView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    KeyBoardUtils.hideSoftKeyboard(MainActivity.this, rootPlaceHolderView);
                                    rootPlaceHolderView.setOnClickListener(null);
                                    rootPlaceHolderView.setClickable(false);
                                    clearMainFocus();
                                }
                            });
                        } else {
                            isSoftKeyboardShow = false;
                            LogHelper.print("hide keyboard--offset = " + keyBoardHeight);
                            rootPlaceHolderView.setOnClickListener(null);
                            rootPlaceHolderView.setClickable(false);
                        }
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.stopScreenProtect();
        } else if (action == MotionEvent.ACTION_UP) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.startScreenProtect();
        }
        return super.dispatchTouchEvent(event);
    }

    private void initData() {
        //登录状态回调
        LoginHelper.INSTANCE.addLoginCallback(new LoginCallback() {
            @Override
            public void onLoginSuccess() {
                if (!hasInitData) {
                    checkAppNetInit();
                }
            }
        });
        //检查 app 网络是否初始化
        checkAppNetInit();
    }

    @Override
    public void onNetInitSuccess() {
        hideLoadingDialog();
        checkLogin();
    }

    @Override
    public void onNetInitError(String message) {
        hideLoadingDialog();
        showAppNetInitErrorDialog(message);
    }

    /**
     * 检查 app 网络是否初始化
     */
    private void checkAppNetInit() {
        //判断 app 是否初始化完成
        String deviceDomain = AppNetManager.INSTANCE.getDeviceDomain();
        if (!TextUtils.isEmpty(deviceDomain)) {
            checkLogin();
        } else {
            boolean requestingDeviceDomain = AppNetManager.INSTANCE.isRequestingDeviceDomain();
            if (requestingDeviceDomain) {
                showLoadingDialog();
            } else {
                AppNetManager.INSTANCE.addNetCallback(this);
                AppNetManager.INSTANCE.initAppNet();
            }
        }
    }

    /**
     * 展示 app 初始化失败弹窗
     */
    private void showAppNetInitErrorDialog(String errorMsg) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("初始化失败,错误原因:\n" + errorMsg)
                .setLeftNavTxt("重试")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showLoadingDialog();
                        AppNetManager.INSTANCE.initAppNet();
                    }
                })
                .setRightNavTxt("修改地址")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showInputServerAddressDialog();
                    }
                }).show(this);
    }

    /**
     * 显示修改服务器地址
     */
    private void showInputServerAddressDialog() {
        CommonInputDialogFragment.build()
                .setTitle("修改服务器地址")
                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        ServerSettingMMKV.handleChangeServerAddress(MainActivity.this, input);
                    }
                }).show(this);
    }

    /**
     * 检查登录状态
     */
    private void checkLogin() {
        //判断是否登录
        if (!LoginHelper.INSTANCE.isLoginSuccess()) {
            LoginHelper.INSTANCE.handleLoginValid(false);
        } else {
            initHomeContent();
        }
    }

    /**
     * 加载主页内容
     */
    private void initHomeContent() {
        sllSetting.setVisibility(View.VISIBLE);
        llMainMenu.setVisibility(View.VISIBLE);
        //每秒回调helper
        CountDownHelper countDownHelper = getWeakRefHolder(CountDownHelper.class);
        countDownHelper.startCountDown();
        //开始心跳设置
        HeartBeatHelper heartBeatHelper = getWeakRefHolder(HeartBeatHelper.class);
        heartBeatHelper.requestHeartBeat();
        countDownHelper.addCountDownListener(heartBeatHelper);
        //初始化语音
        TTSVoiceHelper ttsVoiceHelper = getWeakRefHolder(TTSVoiceHelper.class);
        ttsVoiceHelper.initTTSVoice(null);
        //屏幕保护程序
        ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
        screenProtectHelper.setOnScreenProtectListener(new ScreenProtectHelper.OnScreenProtectListener() {
            @Override
            public void onNeedScreenProtect() {
                if (!isSoftKeyboardShow) {
                    flScreenProtect.setVisibility(View.VISIBLE);
                }
            }
        });
        countDownHelper.addCountDownListener(screenProtectHelper);
        //网络状态回调
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        countDownHelper.addCountDownListener(systemEventWatcherHelper);
        //自动升级
        AppUpgradeHelper appUpgradeHelper = getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.checkAppVersion();
        hasInitData = true;
    }

//    private long lastBackClickTime = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        long currentTime = System.currentTimeMillis();
//        if ((currentTime - lastBackClickTime) > 2000) {
//            AppToast.toastMsg("再按一次退出程序");
//            lastBackClickTime = currentTime;
//        } else {
//            //杀掉进程
//            AndroidUtils.killApp(this);
//        }
    }

    @Override
    protected void onDestroy() {
        AppManager.INSTANCE.clearMainActivity();
        super.onDestroy();
    }

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        //登录过期弹窗弹出，不处理其他弹窗
        if (LoginHelper.INSTANCE.isHandleLoginValid()) {
            return;
        }
        super.addContentPlaceHolderFragment(fragment);
    }
}