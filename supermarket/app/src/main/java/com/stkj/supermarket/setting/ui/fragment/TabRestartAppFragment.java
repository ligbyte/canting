package com.stkj.supermarket.setting.ui.fragment;

import android.view.View;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;

/**
 * 重启app
 */
public class TabRestartAppFragment extends BaseRecyclerFragment {

    private ShapeTextView stvDeviceRestart;
    private ShapeTextView stvDeviceShutdown;
    private ShapeTextView stvAppRestart;
    private ShapeTextView stvShowNav;
    private ShapeTextView stvHideNav;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_restart_app;
    }

    @Override
    protected void initViews(View rootView) {
        stvDeviceRestart = (ShapeTextView) findViewById(R.id.stv_device_restart);
        stvDeviceRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确认重启设备吗?")
                        .setLeftNavTxt("确定")
                        .setRightNavTxt("取消")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                boolean rebootDevice = DeviceManager.INSTANCE.getDeviceInterface().rebootDevice();
                                if (!rebootDevice) {
                                    AppToast.toastMsg("设备暂不支持该功能");
                                }
                            }
                        }).show(mActivity);
            }
        });
        stvDeviceShutdown = (ShapeTextView) findViewById(R.id.stv_device_shutdown);
        stvDeviceShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确认关闭设备吗?")
                        .setLeftNavTxt("确定")
                        .setRightNavTxt("取消")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                boolean shutDownDevice = DeviceManager.INSTANCE.getDeviceInterface().shutDownDevice();
                                if (!shutDownDevice) {
                                    AppToast.toastMsg("设备暂不支持该功能");
                                }
                            }
                        }).show(mActivity);
            }
        });
        stvAppRestart = (ShapeTextView) findViewById(R.id.stv_app_restart);
        stvAppRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("App将自动重启(若失败,请手动重启App)")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                DeviceManager.INSTANCE.getDeviceInterface().release();
                                AndroidUtils.restartApp();
                            }
                        })
                        .setRightNavTxt("取消").show(mActivity);
            }
        });
        stvShowNav = (ShapeTextView) findViewById(R.id.stv_show_nav);
        stvShowNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceManager.INSTANCE.getDeviceInterface().showOrHideSysNavBar(true);
            }
        });
        stvHideNav = (ShapeTextView) findViewById(R.id.stv_hide_nav);
        stvHideNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceManager.INSTANCE.getDeviceInterface().showOrHideSysNavBar(false);
            }
        });
    }
}
