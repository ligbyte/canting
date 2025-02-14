package com.stkj.cashier.pay.ui.fragment;

import android.view.View;

import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.pay.callback.OnCalculateListener;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.ui.weight.SimpleCalculator;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.utils.FragmentUtils;

import java.util.Map;
import java.util.logging.Handler;

/**
 * 金额模式
 */
public class AmountConsumerFragment extends BasePayHelperFragment implements OnPayListener, OnConsumerConfirmListener {

    private SimpleCalculator scCalc;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_amount_consumer;
    }

    @Override
    protected void initViews(View rootView) {
        scCalc = (SimpleCalculator) findViewById(R.id.sc_calc);
        scCalc.setOnCalculateListener(new OnCalculateListener() {
            @Override
            public void onConfirmMoney(String payMoney) {
                goToAmountPay();
            }

            @Override
            public void onClickDisableConfirm() {
                stopAmountPay();
            }
        });
    }

    @Override
    public void onCancelCardNumber(String cardNumber) {
        super.onCancelCardNumber(cardNumber);
        scCalc.setCalcEnable(true);
        scCalc.setConfirmTxt("结算");
    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        super.onCancelFacePass(passPeopleInfo);
        scCalc.setCalcEnable(true);
        scCalc.setConfirmTxt("结算");
    }

    /**
     * 获取最终结算价格
     */
    private String getAmountRealPayMoney() {
        String currentInputText = scCalc.getCurrentInputText();
        return PriceUtils.formatPrice(currentInputText);
    }

    /**
     * 金额结算
     */
    private void goToAmountPay() {
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        String realPayMoney = getAmountRealPayMoney();
        int goToPay = goToPay(realPayMoney);
        if (goToPay == NORMAL_TO_PAY) {
            scCalc.setCalcEnable(false);
            scCalc.setConfirmTxt("取消结算");
        } else {
            if (PaymentSettingMMKV.getConsumerMode() != PayConstants.CONSUMER_QUOTA_MODE) {
                CommonDialogUtils.showTipsDialog(mActivity, getGoToPayStatus(goToPay));
            }
        }
    }

    /**
     * 取消金额结算
     */
    private void stopAmountPay() {
        speakTTSVoice("取消结算");
        stopToPay();
        scCalc.setCalcEnable(true);
        scCalc.setConfirmTxt("结算");
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new ConsumerRecordListFragment(), R.id.fl_consumer_list_content);
        }
    }

    @Override
    public void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg) {
        super.onPayError(responseCode, payRequest, modifyBalanceResult, msg);
        if (PaymentSettingMMKV.getConsumerMode() == PayConstants.CONSUMER_QUOTA_MODE){

            scCalc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToAmountPay();
                }
            }, 1000);

            return;
        }
        scCalc.setCalcEnable(true);
        scCalc.setConfirmTxt("结算");
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        super.onPaySuccess(payRequest, modifyBalanceResult);
        if (PaymentSettingMMKV.getConsumerMode() == PayConstants.CONSUMER_QUOTA_MODE){

            scCalc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToAmountPay();
                }
            }, 1000);

            return;
        }
        scCalc.setCalcEnable(true);
        scCalc.setConfirmTxt("结算");
        scCalc.clearCalcData();
    }

    @Override
    protected void onPayCancel(int payType) {
        if (PaymentSettingMMKV.getConsumerMode() == PayConstants.CONSUMER_QUOTA_MODE){
            speakTTSVoice("取消结算");
            scCalc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToAmountPay();
                }
            }, 1000);

            return;
        }
        stopAmountPay();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopToPay();
    }
}
