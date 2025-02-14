package com.stkj.cashier.pay.ui.fragment;

import android.view.Choreographer;
import android.view.View;

import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;

/**
 * 支付提示弹窗
 */
public class PayOrderTipsDialog extends CommonAlertDialogFragment {

    private boolean needCancelPay;

    @Override
    protected void initViews(View rootView) {
        super.initViews(rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        super.onFragmentResume(isFirstOnResume);
        if (needCancelPay) {
            needCancelPay = false;
            Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    dismiss();
                }
            });
        }
    }

    public boolean isNeedCancelPay() {
        return needCancelPay;
    }

    public void setNeedCancelPay(boolean needCancelPay) {
        this.needCancelPay = needCancelPay;
    }

    public static PayOrderTipsDialog build() {
        PayOrderTipsDialog payOrderTipsDialog = new PayOrderTipsDialog();
        payOrderTipsDialog.setLeftNavTxt("隐藏弹窗")
                .setLeftNavClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {

                    }
                });
        return payOrderTipsDialog;
    }

    public PayOrderTipsDialog setPayTips(String payTips) {
        setAlertContentTxt(payTips);
        return this;
    }
}
