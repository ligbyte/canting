package com.stkj.supermarketmini.login.ui.fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.supermarketmini.BuildConfig;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.callback.AppNetCallback;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.net.AppNetManager;
import com.stkj.supermarketmini.base.net.ParamsUtils;
import com.stkj.supermarketmini.base.utils.DESUtil;
import com.stkj.supermarketmini.login.helper.LoginHelper;
import com.stkj.supermarketmini.login.model.UserInfo;
import com.stkj.supermarketmini.login.service.LoginService;

import java.util.HashMap;

/**
 * 用户登录页面
 */
public class LoginAlertFragment extends BaseDialogFragment implements AppNetCallback {

    private ShapeEditText setDeviceId;
    private ShapeEditText setUserName;
    private ShapeEditText setPassword;
    private FrameLayout flLoading;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_login_alert_dialog;
    }

    @Override
    protected void initViews(View rootView) {
        flLoading = (FrameLayout) findViewById(R.id.fl_loading);
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("版本信息:" + AndroidUtils.getAppVersionName());
        setDeviceId = (ShapeEditText) findViewById(R.id.set_device_id);
        if (BuildConfig.DEBUG) {
            setDeviceId.setText("shopandroidtest1");
        }
        setUserName = (ShapeEditText) findViewById(R.id.set_userName);
        setPassword = (ShapeEditText) findViewById(R.id.set_password);
        setPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyBoardUtils.hideSoftKeyboard(mActivity, v);
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.stv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAppNetInit();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            setDeviceId.requestFocus();
            KeyBoardUtils.showSoftKeyboard(mActivity, setDeviceId);
        }
    }

    public void showLoading() {
        flLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        flLoading.setVisibility(View.GONE);
    }

    private void checkAppNetInit() {
        KeyBoardUtils.hideSoftKeyboard(mActivity, setPassword);
        String deviceId = setDeviceId.getText().toString();
        if (TextUtils.isEmpty(deviceId)) {
            AppToast.toastMsg("设备编号不能为空!");
            return;
        }
        String userName = setUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            AppToast.toastMsg("用户名不能为空!");
            return;
        }
        String password = setPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            AppToast.toastMsg("密码不能为空!");
            return;
        }
        showLoading();
        AppNetManager.INSTANCE.initAppNet(deviceId, new AppNetCallback() {
            @Override
            public void onNetInitSuccess(String machineNumber) {
                String userName = setUserName.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    hideLoading();
                    AppToast.toastMsg("用户名不能为空!");
                    return;
                }
                String password = setPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    hideLoading();
                    AppToast.toastMsg("密码不能为空!");
                    return;
                }
                loginSystem(machineNumber, userName, password);
            }

            @Override
            public void onNetInitError(String message) {
                hideLoading();
                AppNetManager.INSTANCE.clearAppNetCache();
                LoginHelper.INSTANCE.onLoginError("登录失败:" + message);
            }
        });
    }

    /**
     * 登录系统
     */
    private void loginSystem(String machineNumber, String userName, String password) {
        showLoading();
        HashMap<String, String> paramsMap = new HashMap<>();
        String encryptUserName = DESUtil.encrypt(userName, ParamsUtils.DES_PUBLIC_KEY);
        String encryptPassword = DESUtil.encrypt(password, ParamsUtils.DES_PUBLIC_KEY);
        paramsMap.put("phonenumber", encryptUserName);
        paramsMap.put("password", encryptPassword);
        paramsMap.put("device", "MINI");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(LoginService.class)
                .login(paramsMap)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<UserInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<UserInfo> userInfoBaseResponse) {
                        UserInfo userInfo = userInfoBaseResponse.getData();
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getToken())) {
                            //登录成功
                            LoginHelper.INSTANCE.putMachineNumber(machineNumber);
                            LoginHelper.INSTANCE.saveUserInfo(userInfo);
                            LoginHelper.INSTANCE.onLoginSuccess();
                        } else {
                            hideLoading();
                            //登录失败
                            LoginHelper.INSTANCE.onLoginError("登录失败: " + userInfoBaseResponse.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        //登录失败
                        LoginHelper.INSTANCE.onLoginError("登录失败:" + e.getMessage());
                    }
                });
    }

}
