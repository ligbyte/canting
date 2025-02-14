package com.stkj.cashier.login.ui.fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.model.BaseResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.utils.DESUtil;
import com.stkj.cashier.login.helper.LoginHelper;
import com.stkj.cashier.login.model.UserInfo;
import com.stkj.cashier.login.service.LoginService;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.KeyBoardUtils;

import java.util.HashMap;

/**
 * 用户登录页面
 */
public class LoginAlertFragment extends BaseDialogFragment {

    private ShapeEditText setUserName;
    private ShapeEditText setPassword;
    private FrameLayout flLoading;

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_login_alert_dialog;
    }

    @Override
    protected void initViews(View rootView) {
        flLoading = (FrameLayout) findViewById(R.id.fl_loading);
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("版本信息:" + AndroidUtils.getAppVersionName());
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
        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSystem();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            setUserName.requestFocus();
            KeyBoardUtils.showSoftKeyboard(mActivity, setUserName);
        }
    }


    public void showLoading() {
        flLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        flLoading.setVisibility(View.GONE);
    }

    /**
     * 登录系统
     */
    private void loginSystem() {
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
        KeyBoardUtils.hideSoftKeyboard(mActivity, setPassword);
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
                        hideLoading();
                        UserInfo userInfo = userInfoBaseResponse.getData();
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getToken())) {
                            //登录成功
                            LoginHelper.INSTANCE.saveUserInfo(userInfo);
                            LoginHelper.INSTANCE.onLoginSuccess();
                        } else {
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
