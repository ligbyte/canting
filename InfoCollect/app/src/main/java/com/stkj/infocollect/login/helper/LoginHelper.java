package com.stkj.infocollect.login.helper;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.stkj.infocollect.R;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.utils.JacksonUtils;
import com.stkj.infocollect.home.helper.HeartBeatHelper;
import com.stkj.infocollect.home.helper.SystemEventWatcherHelper;
import com.stkj.infocollect.home.ui.activity.MainActivity;
import com.stkj.infocollect.login.callback.LoginCallback;
import com.stkj.infocollect.login.model.UserInfo;
import com.stkj.infocollect.login.ui.fragment.LoginAlertFragment;
import com.stkj.common.core.AppManager;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.FragmentUtils;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public enum LoginHelper {
    INSTANCE;
    private UserInfo userInfo;

    private Set<LoginCallback> loginCallbackSet = new HashSet<>();

    public boolean isLoginSuccess() {
        if (userInfo == null) {
            readLocalUserInfo();
        }
        return userInfo != null;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getToken() {
        if (userInfo != null) {
            return userInfo.getToken();
        }
        return "";
    }

    public String getAccount() {
        if (userInfo != null && userInfo.getUserInfo() != null) {
            return userInfo.getUserInfo().getAccount();
        }
        return "";
    }

    public String getFaceImg() {
        if (userInfo != null && userInfo.getUserInfo() != null) {
            return userInfo.getUserInfo().getFaceImg();
        }
        return "";
    }

    private static final String MMKV_NAME = "login";
    private static final String MMKV_KEY_USER = "user";

    public void saveUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        mmkv.putString(MMKV_KEY_USER, JacksonUtils.convertJsonString(userInfo));
        Activity activity = AppManager.INSTANCE.getMainActivity();
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            //继续心跳检测、网络检查接口
            mainActivity.getWeakRefHolder(HeartBeatHelper.class).setForbidHeatBeat(false);
            mainActivity.getWeakRefHolder(SystemEventWatcherHelper.class).setForbidHealthCheck(false);
        }
    }

    public void readLocalUserInfo() {
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        String jsonString = mmkv.getString(MMKV_KEY_USER, "");
        userInfo = JacksonUtils.convertJsonObject(jsonString, UserInfo.class);
    }

    public void clearUserInfo() {
        Activity activity = AppManager.INSTANCE.getMainActivity();
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            //停止心跳检测、网络检查接口
            mainActivity.getWeakRefHolder(HeartBeatHelper.class).setForbidHeatBeat(true);
            mainActivity.getWeakRefHolder(SystemEventWatcherHelper.class).setForbidHealthCheck(true);
        }
        this.userInfo = null;
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        mmkv.remove(MMKV_KEY_USER);
    }

    public void addLoginCallback(LoginCallback loginCallback) {
        loginCallbackSet.add(loginCallback);
    }

    public void removeLoginCallback(LoginCallback loginCallback) {
        loginCallbackSet.remove(loginCallback);
    }

    public void onLoginSuccess() {
        isHandleLoginValid.set(false);
        for (LoginCallback callback : loginCallbackSet) {
            callback.onLoginSuccess();
        }
    }

    public void onLoginError(String error) {
        for (LoginCallback callback : loginCallbackSet) {
            callback.onLoginError(error);
        }
    }

    /**
     * 是否有更改价格的权限
     */
    public boolean hasPermissionChangePrice() {
        if (userInfo != null && userInfo.getUserInfo() != null && userInfo.getUserInfo().getMobileButtonCodeList() != null) {
            List<String> mobileButtonCodeList = userInfo.getUserInfo().getMobileButtonCodeList();
            return mobileButtonCodeList.contains(UserInfo.AccountInfo.ACCOUNT_PERMISSION_CHANGE_PRICE);
        }
        return false;
    }

    //是否正在处理登录等状态
    private AtomicBoolean isHandleLoginValid = new AtomicBoolean();
    private LoginAlertFragment loginAlertFragment;
    private LoginCallback handleLoginValidCallback;

    public boolean isHandleLoginValid() {
        return isHandleLoginValid.get();
    }

    public void setNeedHandleLoginValid() {
        isHandleLoginValid.set(true);
    }

    /**
     * 处理登录过期问题或者未登录
     */
    public void handleLoginValid(boolean needShowLoginValidTips) {
        Activity activity = AppManager.INSTANCE.getMainActivity();
        if (!ActivityUtils.isActivityFinished(activity)) {
            if (activity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) activity;
                //清理用户信息
                LoginHelper.INSTANCE.clearUserInfo();
                if (needShowLoginValidTips) {
                    CommonAlertDialogFragment loginValidAlertDialog = CommonAlertDialogFragment.build()
                            .setAlertTitleTxt("提示")
                            .setAlertContentTxt("登录状态已过期,请重新登录")
                            .setLeftNavTxt("去登录")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    //1.重启app方式
//                                  DeviceManager.INSTANCE.getDeviceInterface().release();
//                                  AndroidUtils.restartApp();
                                    //2.直接打开登录页方式
                                    showLoginFragment(mainActivity);
                                }
                            });
                    addMainLoginPlaceFragment(mainActivity, loginValidAlertDialog);
                } else {
                    showLoginFragment(mainActivity);
                }
            }
        }
    }

    private void showLoginFragment(MainActivity mainActivity) {
        loginAlertFragment = new LoginAlertFragment();
        if (handleLoginValidCallback == null) {
            handleLoginValidCallback = new LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    if (loginAlertFragment != null) {
                        loginAlertFragment.dismiss();
                        loginAlertFragment = null;
                    }
                }

                @Override
                public void onLoginError(String msg) {
                    Activity activity = AppManager.INSTANCE.getMainActivity();
                    if (activity instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) activity;
                        if (!ActivityUtils.isActivityFinished(mainActivity)) {
                            CommonAlertDialogFragment alertDialogFragment = CommonAlertDialogFragment.build()
                                    .setAlertTitleTxt("提示")
                                    .setAlertContentTxt(msg);
                            addMainLoginPlaceFragment(mainActivity, alertDialogFragment);
                        }
                    }
                }
            };
        }
        LoginHelper.INSTANCE.addLoginCallback(handleLoginValidCallback);
        replaceMainLoginPlaceFragment(mainActivity, loginAlertFragment);
    }

    /**
     * 添加登录相关占位布局
     */
    private void addMainLoginPlaceFragment(MainActivity activity, Fragment fragment) {
        FragmentUtils.safeAddFragment(activity.getSupportFragmentManager(), fragment, com.stkj.infocollect.R.id.fl_login_placeholder);
    }

    /**
     * 替换登录相关占位布局
     */
    private void replaceMainLoginPlaceFragment(MainActivity activity, Fragment fragment) {
        FragmentUtils.safeReplaceFragment(activity.getSupportFragmentManager(), fragment, R.id.fl_login_placeholder);
    }

}