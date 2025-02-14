package com.stkj.cashiermini.base.utils;

import android.content.Context;

import com.stkj.cashiermini.base.net.AppNetManager;
import com.stkj.cashiermini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashiermini.login.helper.LoginHelper;
import com.stkj.common.utils.AndroidUtils;

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
                        if (confirmClickListener != null) {
                            confirmClickListener.onClick(alertDialogFragment);
                        }
                        alertDialogFragment.setAlertContentTxt("数据清理中,请稍等...")
                                .setLeftNavTxt("清理中")
                                .setRightNavTxt("")
                                .setLeftNavClickListener(null);
                        //清理网络缓存
                        AppNetManager.INSTANCE.clearAppNetCache();
                        //清理用户登录信息
                        LoginHelper.INSTANCE.clearUserInfo();
                        alertDialogFragment.setAlertContentTxt("清理完成,App将自动重启(若失败,请手动重启App)")
                                .setLeftNavTxt("重启中")
                                .setRightNavTxt("");
                        AndroidUtils.restartApp();
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
