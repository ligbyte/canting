package com.stkj.supermarketmini.home.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.ImmerseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.callback.AppNetCallback;
import com.stkj.supermarketmini.base.net.AppNetManager;
import com.stkj.supermarketmini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarketmini.home.model.HomeMenuInfo;
import com.stkj.supermarketmini.home.model.HomeTabInfo;
import com.stkj.supermarketmini.home.ui.adapter.HomeTabPageAdapter;
import com.stkj.supermarketmini.home.ui.widget.HomeTabLayout;
import com.stkj.supermarketmini.login.callback.LoginCallback;
import com.stkj.supermarketmini.login.helper.LoginHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends ImmerseActivity implements AppNetCallback {

    private View scanHolderView;
    private HomeTabLayout htlLeftNav;
    private ViewPager2 vp2Content;
    private HomeTabPageAdapter homeTabPageAdapter;
    private boolean isSoftKeyboardShow;

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
        loadingDialogHelper.setStyleId(1);
        AppManager.INSTANCE.setMainActivity(this);
        readSaveInstanceState(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initApp();
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
        scanHolderView = (View) findViewById(R.id.scan_holder_view);
        htlLeftNav = (HomeTabLayout) findViewById(R.id.htl_left_nav);
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
        vp2Content = (ViewPager2) findViewById(R.id.vp2_content);
    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_main_content;
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
        checkAppNetInit();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---MainActivity--dispatchKeyEvent--activity event: " + event);
        //判断扫码枪是否连接
        return super.dispatchKeyEvent(event);
    }

    /**
     * 检查 app 网络是否初始化
     */
    private void checkAppNetInit() {
        String machineNumber = LoginHelper.INSTANCE.getMachineNumber();
        //判断是否绑定了设备编号
        if (TextUtils.isEmpty(machineNumber)) {
            LoginHelper.INSTANCE.addLoginCallback(new LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    initHomeContent();
                }
            });
            LoginHelper.INSTANCE.handleLoginValid(false);
        } else {
            //判断 app 是否初始化完成
            String deviceDomain = AppNetManager.INSTANCE.getDeviceDomain();
            if (!TextUtils.isEmpty(deviceDomain)) {
                initHomeContent();
            } else {
                boolean requestingDeviceDomain = AppNetManager.INSTANCE.isRequestingDeviceDomain();
                if (requestingDeviceDomain) {
                    showLoadingDialog();
                } else {
                    AppNetManager.INSTANCE.initAppNet(LoginHelper.INSTANCE.getMachineNumber(), new AppNetCallback() {
                        @Override
                        public void onNetInitSuccess(String machineNumber) {
                            hideLoadingDialog();
                            initHomeContent();
                        }

                        @Override
                        public void onNetInitError(String message) {
                            hideLoadingDialog();
                            showAppNetInitErrorDialog(message);
                        }
                    });
                }
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
                        initApp();
                    }
                }).show(this);
    }

    /**
     * 加载主页内容
     */
    private void initHomeContent() {
        //读取本地用户信息
        LoginHelper.INSTANCE.readLocalUserInfo();
        //添加左侧tab列表
        List<HomeTabInfo<HomeMenuInfo>> homeTabInfoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                HomeMenuInfo menuInfo = new HomeMenuInfo(HomeTabPageAdapter.TAB_PAYMENT_TAG);
                HomeTabInfo<HomeMenuInfo> paymentTabInfo = new HomeTabInfo<>();
                paymentTabInfo.setExtraInfo(menuInfo);
                paymentTabInfo.setSelectRes(R.mipmap.icon_payment);
                paymentTabInfo.setUnSelectRes(R.mipmap.icon_payment);
                homeTabInfoList.add(paymentTabInfo);
            } else if (i == 1) {

                HomeMenuInfo menuInfo = new HomeMenuInfo(HomeTabPageAdapter.TAB_GOODS_TAG);
                HomeTabInfo<HomeMenuInfo> goodsTabInfo = new HomeTabInfo<>();
                goodsTabInfo.setExtraInfo(menuInfo);
                goodsTabInfo.setSelectRes(R.mipmap.icon_goods);
                goodsTabInfo.setUnSelectRes(R.mipmap.icon_goods);
                homeTabInfoList.add(goodsTabInfo);
            } else {
                //设置
                HomeMenuInfo menuInfo = new HomeMenuInfo(HomeTabPageAdapter.TAB_SETTING_TAG);
                HomeTabInfo<HomeMenuInfo> settingTabInfo = new HomeTabInfo<>();
                settingTabInfo.setExtraInfo(menuInfo);
                settingTabInfo.setSelectRes(R.mipmap.icon_setting);
                settingTabInfo.setUnSelectRes(R.mipmap.icon_setting);
                homeTabInfoList.add(settingTabInfo);
            }
        }
        htlLeftNav.addTabList(homeTabInfoList);
        htlLeftNav.setOnTabChangeListener(new HomeTabLayout.OnTabChangeListener() {
            @Override
            public void onTabSelected(int tabIndex) {
                clearMainFocus();
                vp2Content.setCurrentItem(tabIndex, false);
            }
        });
        //添加右侧内容页面
        homeTabPageAdapter = new HomeTabPageAdapter(this, homeTabInfoList);
        //禁止viewPager左右滑动切换tab页
        vp2Content.setUserInputEnabled(false);
        vp2Content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                HomeTabInfo homeTabInfo = htlLeftNav.getHomeTabInfo(position);
                Object extraInfo = homeTabInfo.getExtraInfo();
                if (extraInfo instanceof HomeMenuInfo) {
                    HomeMenuInfo menu = (HomeMenuInfo) extraInfo;

                }
                htlLeftNav.setCurrentTab(position);
            }
        });
        vp2Content.setAdapter(homeTabPageAdapter);
        vp2Content.setOffscreenPageLimit(2);
        htlLeftNav.setCurrentTab(saveStateCurrentTabPage);
        htlLeftNav.setEnableTabClick(true);
    }

    //当前TAB界面
    private static final String TAB_CURRENT_PAGE = "currentTabPage";
    private int saveStateCurrentTabPage;

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_CURRENT_PAGE, vp2Content.getCurrentItem());
    }

    private void readSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            saveStateCurrentTabPage = savedInstanceState.getInt(TAB_CURRENT_PAGE, 0);
        }
    }

    private long lastBackClickTime = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //fragment碎片
        if (!placeHolderFragmentSet.isEmpty()) {
            Fragment poppedFragment = placeHolderFragmentSet.pop();
            if (poppedFragment != null) {
                LogHelper.print("--addContentPlaceHolderFragment--onBackPressed remove fragment " + poppedFragment);
                FragmentUtils.safeRemoveFragment(getSupportFragmentManager(), poppedFragment);
                return;
            }
        }
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastBackClickTime) > 2000) {
            AppToast.toastMsg("再按一次退出程序");
            lastBackClickTime = currentTime;
        } else {
            //杀掉进程
            AndroidUtils.killApp(this);
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.INSTANCE.clearMainActivity();
        super.onDestroy();
    }

    private Stack<Fragment> placeHolderFragmentSet = new Stack<>();

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        //登录过期弹窗弹出，不处理其他弹窗
        if (LoginHelper.INSTANCE.isHandleLoginValid()) {
            return;
        }
        int contentPlaceHolderId = getContentPlaceHolderId();
        if (contentPlaceHolderId != 0) {
            try {
                fragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            LogHelper.print("--addContentPlaceHolderFragment--onStateChanged remove fragment " + fragment);
                            placeHolderFragmentSet.remove(fragment);
                        }
                    }
                });
                getSupportFragmentManager().beginTransaction()
                        .add(getContentPlaceHolderId(), fragment)
                        .commitNowAllowingStateLoss();
                //添加堆栈里面
                LogHelper.print("--addContentPlaceHolderFragment--add fragment " + fragment);
                placeHolderFragmentSet.push(fragment);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            AppToast.toastMsg("添加内容失败");
        }
    }
}