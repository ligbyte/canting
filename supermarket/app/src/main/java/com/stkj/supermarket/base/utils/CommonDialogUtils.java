package com.stkj.supermarket.base.utils;

import android.content.Context;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.net.AppNetManager;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.login.helper.LoginHelper;
import com.stkj.supermarket.setting.helper.FacePassHelper;

public class CommonDialogUtils {

    public static void showTipsDialog(Context context, String msg) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .show(context);
    }

    public static void showTipsDialog(Context context, String msg, String confirmText, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setLeftNavTxt(confirmText)
                .setLeftNavClickListener(confirmClickListener)
                .show(context);
    }

    public static void showAppResetDialog(Context context, String msg, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setNeedHandleDismiss(true)
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.setAlertContentTxt("数据清理中,请稍等...")
                                .setLeftNavTxt("清理中")
                                .setRightNavTxt("")
                                .setLeftNavClickListener(null);
                        //确认回调
                        if (confirmClickListener != null) {
                            confirmClickListener.onClick(alertDialogFragment);
                        }
                        //释放设备接口
                        DeviceManager.INSTANCE.getDeviceInterface().release();
                        //清理网络缓存
                        AppNetManager.INSTANCE.clearAppNetCache();
                        //清理用户登录信息
                        LoginHelper.INSTANCE.clearUserInfo();
                        //清理本地人脸数据库
                        FacePassHelper facePassHelper = ActivityHolderFactory.get(FacePassHelper.class, context);
                        if (facePassHelper != null) {
                            facePassHelper.deleteAllFaceGroup(new FacePassHelper.OnDeleteAllFaceListener() {
                                @Override
                                public void onDeleteAllFace() {
                                    alertDialogFragment.setAlertContentTxt("清理完成,App将自动重启(若失败,请手动重启App)")
                                            .setLeftNavTxt("重启中")
                                            .setRightNavTxt("");
                                    AndroidUtils.restartApp();
                                }

                                @Override
                                public void onDeleteAllFaceError(String msg) {
                                    alertDialogFragment.setAlertContentTxt("清理完成,App将自动重启(若失败,请手动重启App)")
                                            .setLeftNavTxt("重启中")
                                            .setRightNavTxt("");
                                    AndroidUtils.restartApp();
                                }
                            });
                        }
                    }
                })
                .setRightNavTxt("取消")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.dismiss();
                    }
                });
        commonAlertDialogFragment.show(context);
    }

}
