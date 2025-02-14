package com.stkj.cashier.pay.helper;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 支付帮助类
 */
public class PayHelper extends ActivityWeakRefHolder {

    public static final String ERROR_CONNECT = "-2";

    /**
     * 正在支付
     */
    private boolean isPaying;
    private OnPayListener onPayListener;

    public PayHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public boolean isPaying() {
        return isPaying;
    }

    /**
     * 去支付
     */
    public void goToPay(int payType, int deductionType, String money, String cardNumber) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (isPaying) {
            AppToast.toastMsg("正在支付中,请稍等");
            return;
        }
        TreeMap<String, String> modifyBalanceParams = ParamsUtils.newSortParamsMapWithMode("ModifyBalance");
        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
        modifyBalanceParams.put("payType", switchTongLianPay ? "1" : "");
        modifyBalanceParams.put("cardNumber", cardNumber);
        modifyBalanceParams.put("consumption_type", String.valueOf(payType));
        modifyBalanceParams.put("deduction_Type", String.valueOf(deductionType));
        modifyBalanceParams.put("online_Order_number", PayConstants.createOrderNumber());
        modifyBalanceParams.put("money", money);
        if (onPayListener != null) {
            onPayListener.onStartPay(modifyBalanceParams);
        }
        isPaying = true;
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .goToPay(ParamsUtils.signSortParamsMap(modifyBalanceParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<ModifyBalanceResult>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<ModifyBalanceResult> baseNetResponse) {
                        ModifyBalanceResult modifyBalanceResult = baseNetResponse.getData();
                        if (modifyBalanceResult != null) {
                            if (baseNetResponse.isSuccess()) {
                                isPaying = false;
                                if (onPayListener != null) {
                                    onPayListener.onPaySuccess(modifyBalanceParams, modifyBalanceResult);
                                }
                            } else if (TextUtils.equals(PayConstants.PAY_PROCESSING_STATUS, baseNetResponse.getCode())) {
                                requestPayStatus(modifyBalanceParams, modifyBalanceResult);
                            } else {
                                isPaying = false;
                                if (onPayListener != null) {
                                    onPayListener.onPayError(baseNetResponse.getCode(), modifyBalanceParams, modifyBalanceResult, baseNetResponse.getMessage());
                                }
                            }
                        } else {
                            isPaying = false;
                            if (onPayListener != null) {
                                onPayListener.onPayError(baseNetResponse.getCode(), modifyBalanceParams, null, baseNetResponse.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isPaying = false;
                        if (onPayListener != null) {
                            if (e instanceof java.net.ConnectException) {
                                onPayListener.onPayError(ERROR_CONNECT, modifyBalanceParams, null, e.getMessage());
                            } else {
                                onPayListener.onPayError("-1", modifyBalanceParams, null, e.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * 请求支付状态
     */
    private void requestPayStatus(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<Long>() {
                    @Override
                    protected void onSuccess(Long aLong) {
                        TreeMap<String, String> payStatusParams = ParamsUtils.newSortParamsMapWithMode("PayStatus");
                        payStatusParams.put("payNo", modifyBalanceResult.getPayNo());
                        //通联支付
                        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
                        payStatusParams.put("payType", switchTongLianPay ? "1" : "");
                        RetrofitManager.INSTANCE.getDefaultRetrofit()
                                .create(PayService.class)
                                .getPayStatus(ParamsUtils.signSortParamsMap(payStatusParams))
                                .compose(RxTransformerUtils.mainSchedulers())
                                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                                .subscribe(new DefaultObserver<BaseNetResponse<ModifyBalanceResult>>() {
                                    @Override
                                    protected void onSuccess(BaseNetResponse<ModifyBalanceResult> baseNetResponse) {
                                        ModifyBalanceResult modifyBalanceResult = baseNetResponse.getData();
                                        if (modifyBalanceResult != null) {
                                            if (baseNetResponse.isSuccess()) {
                                                isPaying = false;
                                                if (onPayListener != null) {
                                                    onPayListener.onPaySuccess(payRequest, modifyBalanceResult);
                                                }
                                            } else if (TextUtils.equals(PayConstants.PAY_PROCESSING_STATUS, baseNetResponse.getCode())) {
                                                requestPayStatus(payRequest, modifyBalanceResult);
                                            } else {
                                                isPaying = false;
                                                if (onPayListener != null) {
                                                    onPayListener.onPayError(baseNetResponse.getCode(), payRequest, modifyBalanceResult, baseNetResponse.getMessage());
                                                }
                                            }
                                        } else {
                                            isPaying = false;
                                            if (onPayListener != null) {
                                                onPayListener.onPayError(baseNetResponse.getCode(), payRequest, null, baseNetResponse.getMessage());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        isPaying = false;
                                        if (onPayListener != null) {
                                            if (e instanceof java.net.ConnectException) {
                                                onPayListener.onPayError(ERROR_CONNECT, payRequest, modifyBalanceResult, e.getMessage());
                                            } else {
                                                onPayListener.onPayError("-1", payRequest, modifyBalanceResult, e.getMessage());
                                            }
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void onClear() {
        onPayListener = null;
    }
}
