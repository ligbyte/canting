package com.stkj.cashiermini.login.helper;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.stkj.cashiermini.R;
import com.stkj.cashiermini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashiermini.base.utils.JacksonUtils;
import com.stkj.cashiermini.home.ui.activity.MainActivity;
import com.stkj.cashiermini.login.callback.LoginCallback;
import com.stkj.cashiermini.login.model.UserInfo;
import com.stkj.cashiermini.login.ui.fragment.LoginAlertFragment;
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
    private String machineNumber;

    public String getMachineNumber() {
        if (machineNumber == null) {
            readMachineNumber();
        }
        return machineNumber;
    }

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
    private static final String MMKV_KEY_MACHINE_NUMBER = "machine_number";

    public void putMachineNumber(String machineNumber) {
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        mmkv.putString(MMKV_KEY_MACHINE_NUMBER, machineNumber);
        this.machineNumber = machineNumber;
    }

    public void readMachineNumber() {
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        machineNumber = mmkv.getString(MMKV_KEY_MACHINE_NUMBER, "");
    }

    public void saveUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        mmkv.putString(MMKV_KEY_USER, JacksonUtils.convertJsonString(userInfo));
    }

    public void readLocalUserInfo() {
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        String jsonString = mmkv.getString(MMKV_KEY_USER, "");
        userInfo = JacksonUtils.convertJsonObject(jsonString, UserInfo.class);
    }

    public void clearUserInfo() {
        this.userInfo = null;
        this.machineNumber = null;
        MMKV mmkv = MMKV.mmkvWithID(MMKV_NAME);
        mmkv.remove(MMKV_KEY_USER);
        mmkv.remove(MMKV_KEY_MACHINE_NUMBER);
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
                //清理用户信息
                LoginHelper.INSTANCE.clearUserInfo();
                MainActivity mainActivity = (MainActivity) activity;
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
    public void addMainLoginPlaceFragment(MainActivity activity, Fragment fragment) {
        FragmentUtils.safeAddFragment(activity.getSupportFragmentManager(), fragment, R.id.fl_login_placeholder);
    }

    /**
     * 替换登录相关占位布局
     */
    public void replaceMainLoginPlaceFragment(MainActivity activity, Fragment fragment) {
        FragmentUtils.safeReplaceFragment(activity.getSupportFragmentManager(), fragment, R.id.fl_login_placeholder);
    }

}