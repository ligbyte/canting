package com.stkj.cashier.pay.ui.fragment;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.home.ui.activity.MainActivity;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.PayHelper;
import com.stkj.cashier.pay.model.ConsumerSuccessEvent;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultDisposeObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 支付帮助基类
 */
public abstract class BasePayHelperFragment extends IdentityVerificationFragment implements OnPayListener, OnConsumerConfirmListener {
    //去支付--正常支付
    protected static final int NORMAL_TO_PAY = 0;
    //去支付--正在支付中
    protected static final int PAYING_TO_PAY = 1;
    //去支付--支付金额为零
    protected static final int MONEY_ZERO_TO_PAY = 2;

    protected String getGoToPayStatus(int goToPay) {
        if (goToPay == PAYING_TO_PAY) {
            return "支付中";
        } else if (goToPay == MONEY_ZERO_TO_PAY) {
            return "支付金额为零";
        }
        return "";
    }

    private String mPayMoney = "";

    /**
     * 消费金额
     */
    private String getPayMoney() {
        if (TextUtils.isEmpty(mPayMoney)) {
            return "0.00";
        }
        return mPayMoney;
    }


    /**
     * 10-固定模式 40-菜品模式 50-时段模式 60-单价模式 80-菜品订餐 150-订餐模式 160-水控实时模式 161-水控预扣费模务
     * 扣费模式
     */
    protected int getDeductionType() {
        return PayConstants.DEDUCTION_TYPE_AMOUNT;
    }

    /**
     * 去支付
     */
    protected int goToPay(String realPayMoney) {
        PayHelper payHelper = getPayHelper();
        if (payHelper.isPaying()) {
            AppToast.toastMsg("支付中,请稍等~");
            return PAYING_TO_PAY;
        }
        LogHelper.print("--BasePayHelperFragment--goToPay");
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).clearMainFocus();
        }
        int deductionType = getDeductionType();
        double parsePrice = PriceUtils.parsePrice(realPayMoney);
        if (parsePrice <= 0 && deductionType != PayConstants.DEDUCTION_TYPE_NUMBER) {
            AppToast.toastMsg("支付金额为0");
            return MONEY_ZERO_TO_PAY;
        }
        //停止之前的resetToPay
        stopDelayResetPay();
        //确定金额
        mPayMoney = realPayMoney;
        String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
        //语音消费者屏幕提示
        speakTTSVoice(payTypeVoice);
        if (deductionType == PayConstants.DEDUCTION_TYPE_NUMBER) {
            if (!TextUtils.isEmpty(mPayMoney)) {
                ConsumerManager.INSTANCE.setPayPrice(mPayMoney, canConsumerCancelPay());
            } else {
                ConsumerManager.INSTANCE.setPayPrice("按次消费", canConsumerCancelPay());
            }
            LogHelper.print("--BasePayHelperFragment--goToPay--按次消费");
        } else {
            ConsumerManager.INSTANCE.setPayPrice(mPayMoney, canConsumerCancelPay());
            LogHelper.print("--BasePayHelperFragment--goToPay--其他消费");
        }
        //刷脸、刷卡或扫码认证
        goToAllAuth();
        ConsumerManager.INSTANCE.setFacePassConfirmListener(this);
        return NORMAL_TO_PAY;
    }

    protected boolean canConsumerCancelPay() {
        return false;
    }

    protected void stopToPay() {
        //停止刷脸、刷卡或扫码认证
        stopAllAuth();
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        ConsumerManager.INSTANCE.setFacePassConfirmListener(null);
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(null);
    }

    @Override
    protected void handleReadICCardError(String message) {
        //读卡失败
        speakTTSVoice("读卡失败,请重试!");
        ConsumerManager.INSTANCE.setConsumerTips("读卡失败,请重试!");
        LogHelper.print("--BasePayHelperFragment--handleReadICCardError message: " + message);
    }

    @Override
    protected void handleReadICCardSuccess(FacePassPeopleInfo facePassPeopleInfo) {
        //读卡成功(有人脸信息)
        boolean switchConsumerConfirm = PaymentSettingMMKV.getSwitchConsumerConfirm();
        if (switchConsumerConfirm) {
            speakTTSVoice("读卡成功,请确认支付");
            ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, true, PayConstants.PAY_TYPE_IC_CARD);
        } else {
            speakTTSVoice("读卡成功,支付中");
            ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, false, PayConstants.PAY_TYPE_IC_CARD);
        }
        LogHelper.print("--BasePayHelperFragment--handleReadICCardSuccess confirmFaceInfo");
    }

    @Override
    protected void handleReadICCardSuccess(String cardNumber) {
        //读卡成功(无人脸信息)
        boolean switchConsumerConfirm = PaymentSettingMMKV.getSwitchConsumerConfirm();
        if (switchConsumerConfirm) {
            speakTTSVoice("读卡成功,请确认支付");
            ConsumerManager.INSTANCE.setConsumerConfirmCardInfo(cardNumber, true);
        } else {
            speakTTSVoice("读卡成功,支付中");
            ConsumerManager.INSTANCE.setConsumerConfirmCardInfo(cardNumber, false);
        }
        LogHelper.print("--BasePayHelperFragment--handleReadICCardSuccess confirmCardInfo");
    }

    @Override
    protected void handleScanQRCodeError(String message) {
        //扫码失败
        speakTTSVoice("扫码失败,请重试!");
        ConsumerManager.INSTANCE.setConsumerTips("扫码失败,请重试!");
        LogHelper.print("--BasePayHelperFragment--handleScanQRCodeError message: " + message);
    }

    @Override
    protected void handleScanQRCodeSuccess(String qrcodeResult) {
        //扫码成功
//        boolean switchConsumerConfirm = PaymentSettingMMKV.getSwitchConsumerConfirm();
//        if (switchConsumerConfirm) {
//            speakTTSVoice("扫码成功,请确认支付");
//            ConsumerManager.INSTANCE.setConsumerConfirmScanInfo(qrcodeResult, true);
//        } else {
        speakTTSVoice("扫码成功,支付中");
        ConsumerManager.INSTANCE.setConsumerConfirmScanInfo(qrcodeResult, false);
//        }
        LogHelper.print("--BasePayHelperFragment--handleScanQRCodeSuccess qrcodeResult: " + qrcodeResult);
    }

    @Override
    protected void handleFacePassError(boolean canSpeakFacePassFail, int recognizeState) {
        //人脸识别失败 canSpeakFacePassFail (5s后置为true)
        if (canSpeakFacePassFail) {
            speakTTSVoice("识别失败,正在重试");
            ConsumerManager.INSTANCE.setConsumerTips("识别失败,正在重试");
        }
    }

    @Override
    protected void handleFacePassSuccess(FacePassPeopleInfo facePassPeopleInfo) {
        //人脸识别成功
        boolean switchConsumerConfirm = PaymentSettingMMKV.getSwitchConsumerConfirm();
        if (switchConsumerConfirm) {
            speakTTSVoice("识别成功,请确认支付");
            ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, true, PayConstants.PAY_TYPE_FACE);
        } else {
            speakTTSVoice("识别成功,支付中");
            ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, false, PayConstants.PAY_TYPE_FACE);
        }
        LogHelper.print("--BasePayHelperFragment--handleFacePassResult  confirmFaceInfo");
    }

    /**
     * start 消费者页面处理 start
     */
    @Override
    public void onConfirmFacePass(FacePassPeopleInfo passPeopleInfo) {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(this);
        payHelper.goToPay(PayConstants.PAY_TYPE_FACE, getDeductionType(), getPayMoney(), passPeopleInfo.getCard_Number());
        LogHelper.print("--BasePayHelperFragment--onConfirmFacePass");
    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        onPayCancel(PayConstants.PAY_TYPE_FACE);
        LogHelper.print("--BasePayHelperFragment--onCancelFacePass");
    }

    /**
     * 餐卡支付确认
     */
    @Override
    public void onConfirmCardNumber(String cardNumber) {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(this);
        payHelper.goToPay(PayConstants.PAY_TYPE_IC_CARD, getDeductionType(), getPayMoney(), cardNumber);
        LogHelper.print("--BasePayHelperFragment--onConfirmCardNumber");
    }

    @Override
    public void onCancelCardNumber(String cardNumber) {
        onPayCancel(PayConstants.PAY_TYPE_IC_CARD);
        LogHelper.print("--BasePayHelperFragment--onCancelCardNumber");
    }

    @Override
    public void onConfirmScanData(String scanData) {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(BasePayHelperFragment.this);
        payHelper.goToPay(PayConstants.PAY_TYPE_QRCODE, getDeductionType(), getPayMoney(), scanData);
        LogHelper.print("--BasePayHelperFragment--onConfirmScanData");
    }

    @Override
    public void onCancelScanData(String scanData) {
        onPayCancel(PayConstants.PAY_TYPE_QRCODE);
        LogHelper.print("--BasePayHelperFragment--onCancelScanData");
    }

    @Override
    public void onConsumerCancelPay() {
        onPayCancel(-1);
        LogHelper.print("--BasePayHelperFragment--onConsumerCancelPay");
    }

    /**
     * end 消费者页面处理 end
     */

    /**
     * start 支付接口处理 start
     */
    @Override
    public void onStartPay(Map<String, String> payRequest) {
        //重置支付价格
        mPayMoney = "";
        String statusTips = "支付中,请稍等";
        ConsumerManager.INSTANCE.setConsumerTips(statusTips);
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        String successPayTips = "消费成功,谢谢惠顾";
        String consumeSuccessVoice = TTSSettingMMKV.getConsumeSuccessVoice();
        if (!TextUtils.isEmpty(consumeSuccessVoice)) {
            speakTTSVoice(consumeSuccessVoice);
        }
        //设置实际消费金额
        ConsumerManager.INSTANCE.setPayPrice(modifyBalanceResult.getConsumption_Mone(), false);
        ConsumerManager.INSTANCE.setConsumerTips(successPayTips);
        //消费成功刷新相关数据
        EventBus.getDefault().post(new ConsumerSuccessEvent());
        goToDelayResetPay();
    }

    @Override
    public void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg) {
        ConsumerManager.INSTANCE.setCanCancelPay(false);
        if (TextUtils.equals("401", responseCode)) {
            //token失效
            speakTTSVoice("支付失败,请联系管理人员");
            ConsumerManager.INSTANCE.setConsumerTips("支付失败,请联系管理人员");
        } else if (TextUtils.equals(PayHelper.ERROR_CONNECT, responseCode)) {
            speakTTSVoice("网络不给力");
            ConsumerManager.INSTANCE.setConsumerTips("支付失败,网络异常");
        } else if (TextUtils.equals("-1", responseCode)) {
            speakTTSVoice("系统异常");
            ConsumerManager.INSTANCE.setConsumerTips("支付失败,系统异常");
        } else {
            if (!TextUtils.isEmpty(msg)) {
                speakTTSVoice(msg);
            } else {
                speakTTSVoice("支付失败,未知异常");
            }
            ConsumerManager.INSTANCE.setConsumerTips("支付失败");
        }
        goToDelayResetPay();
    }

    /**
     * 取消支付
     */
    protected void onPayCancel(int payType) {
        stopToPay();
        LogHelper.print("--BasePayHelperFragment--onPayCancel payType:" + payType);
    }

    private DefaultDisposeObserver<Long> resetPayStatusObserver;

    /**
     * 延时三秒重置支付状态
     */
    private void goToDelayResetPay() {
        if (resetPayStatusObserver != null) {
            resetPayStatusObserver.dispose();
        }
        long delayStatusTime = PaymentSettingMMKV.getPaySuccessDelay();
        if (delayStatusTime <= 0) {
            LogHelper.print("goToDelayResetPay: 0");
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            delayedToPayStatus();
        } else {
            resetPayStatusObserver = new DefaultDisposeObserver<Long>() {
                @Override
                protected void onSuccess(Long integer) {
                    LogHelper.print("goToDelayResetPay: " + integer);
                    ConsumerManager.INSTANCE.resetFaceConsumerLayout();
                    delayedToPayStatus();
                }
            };
            Observable.timer(delayStatusTime, TimeUnit.MILLISECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(resetPayStatusObserver);
        }
    }

    /**
     * 停止重置到支付状态
     */
    protected void stopDelayResetPay() {
        if (resetPayStatusObserver != null) {
            resetPayStatusObserver.dispose();
            resetPayStatusObserver = null;
        }
    }

    /**
     * end 支付接口处理 end
     */

    protected PayHelper getPayHelper() {
        return mActivity.getWeakRefHolder(PayHelper.class);
    }

    /**
     * 恢复支付状态延迟回调
     */
    protected void delayedToPayStatus() {

    }
}
