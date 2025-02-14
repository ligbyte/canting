package com.stkj.supermarketmini.setting.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.supermarketmini.BuildConfig;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.CommonExpandItem;
import com.stkj.supermarketmini.base.net.AppNetManager;
import com.stkj.supermarketmini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarketmini.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarketmini.login.callback.LoginCallback;
import com.stkj.supermarketmini.login.helper.LoginHelper;
import com.stkj.supermarketmini.setting.data.ServerSettingMMKV;
import com.stkj.supermarketmini.setting.helper.CrashLogHelper;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

/**
 * 设置页
 */
public class TabSettingFragment extends BaseRecyclerFragment {

    private FrameLayout flServerAddress;
    private ShapeEditText setServerAddress;
    private ImageView ivServerAddress;
    private ShapeTextView stvConfirmRestart;
    private TextView tvUserName;
    private ShapeSelectTextView stvChangeUser;
    private ShapeSelectTextView stvExportCrashLog;
    private ShapeSelectTextView stvClearCrashLog;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_setting;
    }

    @Override
    protected void initViews(View rootView) {
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        stvChangeUser = (ShapeSelectTextView) findViewById(R.id.stv_change_user);
        stvChangeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeUserDialog();
            }
        });
        flServerAddress = (FrameLayout) findViewById(R.id.fl_server_address);
        setServerAddress = (ShapeEditText) findViewById(R.id.set_server_address);
        ivServerAddress = (ImageView) findViewById(R.id.iv_server_address);
        stvConfirmRestart = (ShapeTextView) findViewById(R.id.stv_confirm_restart);
        List<CommonExpandItem> serverAddressExpandList = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            serverAddressExpandList.add(new CommonExpandItem(0, AppNetManager.API_TEST_URL));
        }
        serverAddressExpandList.add(new CommonExpandItem(1, AppNetManager.API_OFFICIAL_URL));
        //获取本地保存
        String serverAddress = ServerSettingMMKV.getServerAddress();
        if (!TextUtils.isEmpty(serverAddress)) {
            serverAddressExpandList.add(new CommonExpandItem(3, serverAddress));
            setServerAddress.setText(serverAddress);
        } else {
            if (BuildConfig.DEBUG) {
                setServerAddress.setText(AppNetManager.API_TEST_URL);
            } else {
                setServerAddress.setText(AppNetManager.API_OFFICIAL_URL);
            }
        }
        ivServerAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivServerAddress.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flServerAddress.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivServerAddress.setSelected(false);
                        setServerAddress.setText(commonExpandItem.getName());
                        ServerSettingMMKV.putServerAddress(commonExpandItem.getName());
                    }
                });
                commonExpandListPopWindow.setExpandItemList(serverAddressExpandList);
                commonExpandListPopWindow.showAsDropDown(flServerAddress);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivServerAddress.setSelected(false);
                    }
                });
            }
        });
        RxHelper.clickThrottle(stvConfirmRestart, this, new DefaultObserver<Unit>() {
            @Override
            protected void onSuccess(Unit unit) {
                String address = setServerAddress.getText().toString().trim();
                ServerSettingMMKV.handleChangeServerAddress(mActivity, address);
            }
        });
        stvExportCrashLog = (ShapeSelectTextView) findViewById(R.id.stv_export_crash_log);
        stvExportCrashLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashLogHelper.saveCrashLogToSDCard(mActivity);
            }
        });
        stvClearCrashLog = (ShapeSelectTextView) findViewById(R.id.stv_clear_crash_log);
        stvClearCrashLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashLogHelper.clearCrashLog(mActivity);
            }
        });
    }

    private void showChangeUserDialog() {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("切换账号会清理用户信息,请确认?")
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        AppNetManager.INSTANCE.clearAppNetCache();
                        LoginHelper.INSTANCE.clearUserInfo();
                        LoginHelper.INSTANCE.handleLoginValid(false);
                    }
                })
                .setRightNavTxt("取消")
                .show(mActivity);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        tvUserName.setText(LoginHelper.INSTANCE.getAccount());
        LoginHelper.INSTANCE.addLoginCallback(loginCallback);
    }

    private LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess() {
            if (tvUserName != null) {
                tvUserName.setText(LoginHelper.INSTANCE.getAccount());
            }
        }
    };

    @Override
    public void onDetach() {
        LoginHelper.INSTANCE.removeLoginCallback(loginCallback);
        super.onDetach();
    }

}
