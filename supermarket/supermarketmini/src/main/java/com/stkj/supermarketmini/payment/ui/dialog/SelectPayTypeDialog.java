package com.stkj.supermarketmini.payment.ui.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarketmini.payment.data.PayConstants;

public class SelectPayTypeDialog extends CommonAlertFragment {

    private LinearLayout llWexinPay;
    private ImageView ivWexinPaySelect;
    private LinearLayout llQrcodePay;
    private ImageView ivQrcodePaySelect;
    private LinearLayout llCashPay;
    private ImageView ivCashPaySelect;
    private int payType = PayConstants.PAY_TYPE_THIRD;

    @Override
    protected int getAlertContentLayResId() {
        return R.layout.dialog_select_pay_type;
    }

    @Override
    protected void initAlertContentView(View contentView) {
        setCloseClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), SelectPayTypeDialog.this);
            }
        });
        llWexinPay = (LinearLayout) contentView.findViewById(R.id.ll_wexin_pay);
        llWexinPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivWexinPaySelect.setSelected(true);
                ivQrcodePaySelect.setSelected(false);
                ivCashPaySelect.setSelected(false);
                payType = PayConstants.PAY_TYPE_THIRD;
            }
        });
        ivWexinPaySelect = (ImageView) contentView.findViewById(R.id.iv_wexin_pay_select);
        llQrcodePay = (LinearLayout) contentView.findViewById(R.id.ll_qrcode_pay);
        llQrcodePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivQrcodePaySelect.setSelected(true);
                ivWexinPaySelect.setSelected(false);
                ivCashPaySelect.setSelected(false);
                payType = PayConstants.PAY_TYPE_QRCODE;
            }
        });
        ivQrcodePaySelect = (ImageView) contentView.findViewById(R.id.iv_qrcode_pay_select);
        llCashPay = (LinearLayout) findViewById(R.id.ll_cash_pay);
        ivCashPaySelect = (ImageView) findViewById(R.id.iv_cash_pay_select);
        llCashPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCashPaySelect.setSelected(true);
                ivQrcodePaySelect.setSelected(false);
                ivWexinPaySelect.setSelected(false);
                payType = PayConstants.PAY_TYPE_CASH;
            }
        });
        ivWexinPaySelect.setSelected(true);
    }

    public int getPayType() {
        return payType;
    }

    @Override
    protected String getAlertTitle() {
        return "支付方式";
    }

    @Override
    protected String getLeftNavText() {
        return "去支付";
    }

    @Override
    protected String getRightNavText() {
        return "取消";
    }
}
