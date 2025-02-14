package com.stkj.supermarket.pay.ui.fragment;

import android.view.Choreographer;

import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;

/**
 * 支付提示弹窗
 */
public class PayOrderTipsDialog extends CommonAlertDialogFragment {

    private boolean needCancelPay;

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
}
