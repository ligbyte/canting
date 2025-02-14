package com.stkj.supermarket.pay.helper;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.pay.callback.OnPayListener;
import com.stkj.supermarket.pay.data.PayConstants;
import com.stkj.supermarket.pay.model.AddOrderRequest;
import com.stkj.supermarket.pay.model.AddOrderResult;
import com.stkj.supermarket.pay.model.ConsumeOrderRequest;
import com.stkj.supermarket.pay.model.ConsumeOrderResult;
import com.stkj.supermarket.pay.service.PayService;
import com.stkj.supermarket.setting.data.PaymentSettingMMKV;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 支付帮助类
 */
public class PayHelper extends ActivityWeakRefHolder {

    private OnPayListener onPayListener;
    private boolean isPaying;

    public PayHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public boolean isPaying() {
        return isPaying;
    }

    public void pay(AddOrderRequest addOrderRequest, String thirdAuthCode) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (isPaying) {
            AppToast.toastMsg("正在支付中,请稍等");
            return;
        }
        if (onPayListener != null) {
            onPayListener.onStartPay(addOrderRequest);
        }
        isPaying = true;
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(PayService.class).addOrder(addOrderRequest).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck)).subscribe(new DefaultObserver<BaseResponse<AddOrderResult>>() {
            @Override
            protected void onSuccess(BaseResponse<AddOrderResult> response) {
                if (response.isSuccess()) {
                    AddOrderResult responseData = response.getData();
                    if (responseData != null && !TextUtils.isEmpty(responseData.getId())) {
                        consumeOrder(addOrderRequest, responseData, thirdAuthCode);
                    } else {
                        isPaying = false;
                        if (onPayListener != null) {
                            onPayListener.onPayError(response.getCode(), addOrderRequest, null, "订单id为空");
                        }
                    }
                } else {
                    isPaying = false;
                    if (onPayListener != null) {
                        onPayListener.onPayError(response.getCode(), addOrderRequest, null, response.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                isPaying = false;
                if (onPayListener != null) {
                    onPayListener.onPayError("-1", addOrderRequest, null, e.getMessage());
                }
            }
        });
    }

    private void consumeOrder(AddOrderRequest addOrderRequest, AddOrderResult addOrderResult, String thirdAuthCode) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        ConsumeOrderRequest consumeOrderRequest = new ConsumeOrderRequest();
        //职工卡号
        consumeOrderRequest.setCardNumber(addOrderRequest.getCustomerNo());
        //支付费用
        consumeOrderRequest.setMoney(addOrderRequest.getRealPayPrice());
        //消费方式
        consumeOrderRequest.setConsumptionType(String.valueOf(addOrderRequest.getPayType()));
        //三方付款码
        consumeOrderRequest.setAuthCode(thirdAuthCode);
        //订单id
        consumeOrderRequest.setOrderId(addOrderResult.getId());
        //通联支付
        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
        consumeOrderRequest.setPayType(switchTongLianPay ? "1" : "");
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(PayService.class).consumeOrder(consumeOrderRequest).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck)).subscribe(new DefaultObserver<BaseResponse<ConsumeOrderResult>>() {
            @Override
            protected void onSuccess(BaseResponse<ConsumeOrderResult> response) {
                ConsumeOrderResult data = response.getData();
                if (data != null) {
                    String payStatus = data.getPayStatus();
                    if (TextUtils.equals(PayConstants.ORDER_SUCCESS_STATUS, payStatus)) {
                        isPaying = false;
                        if (onPayListener != null) {
                            onPayListener.onPaySuccess(addOrderRequest, addOrderResult, consumeOrderRequest);
                        }
                    } else if (TextUtils.equals(PayConstants.ORDER_UNKNOWN_STATUS, payStatus)) {
                        requestOrderStatus(addOrderRequest, addOrderResult, consumeOrderRequest, data);
                    } else {
                        isPaying = false;
                        if (onPayListener != null) {
                            onPayListener.onPayError(response.getCode(), addOrderRequest, addOrderResult, "支付失败");
                        }
                    }
                } else {
                    isPaying = false;
                    if (onPayListener != null) {
                        onPayListener.onPayError(response.getCode(), addOrderRequest, addOrderResult, response.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                isPaying = false;
                if (onPayListener != null) {
                    onPayListener.onPayError("-1", addOrderRequest, addOrderResult, e.getMessage());
                }
            }
        });
    }

    /**
     * 请求订单状态
     */
    private void requestOrderStatus(AddOrderRequest addOrderRequest, AddOrderResult addOrderResult, ConsumeOrderRequest consumeOrderRequest, ConsumeOrderResult consumeOrderResult) {
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
                        //通联支付
                        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
                        RetrofitManager.INSTANCE.getDefaultRetrofit()
                                .create(PayService.class)
                                .orderStatus(consumeOrderResult.getPayNo(), switchTongLianPay ? "1" : "")
                                .compose(RxTransformerUtils.mainSchedulers())
                                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                                    @Override
                                    protected void onSuccess(BaseResponse<String> response) {
                                        String responseData = response.getData();
                                        if (TextUtils.equals(PayConstants.ORDER_SUCCESS_STATUS, responseData)) {
                                            isPaying = false;
                                            if (onPayListener != null) {
                                                onPayListener.onPaySuccess(addOrderRequest, addOrderResult, consumeOrderRequest);
                                            }
                                        } else if (TextUtils.equals(PayConstants.ORDER_UNKNOWN_STATUS, responseData)) {
                                            //继续查询
                                            requestOrderStatus(addOrderRequest, addOrderResult, consumeOrderRequest, consumeOrderResult);
                                        } else {
                                            isPaying = false;
                                            if (onPayListener != null) {
                                                onPayListener.onPayError(response.getCode(), addOrderRequest, addOrderResult, "支付失败");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        isPaying = false;
                                        if (onPayListener != null) {
                                            onPayListener.onPayError("-1", addOrderRequest, addOrderResult, e.getMessage());
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void onClear() {

    }
}
