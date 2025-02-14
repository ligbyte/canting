package com.stkj.cashier.pay.helper;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.model.CommonSelectItem;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.ui.dialog.CommonSelectDialogFragment;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.callback.OnGetCanteenTimeInfoListener;
import com.stkj.cashier.pay.callback.OnGetIntervalCardTypeListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.CanteenCurrentTimeInfo;
import com.stkj.cashier.pay.model.IntervalCardType;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 消费模式帮助类
 */
public class ConsumerModeHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    private Set<OnConsumerModeListener> onConsumerModeListenerSet = new HashSet<>();
    private Set<OnGetCanteenTimeInfoListener> onCanteenTimeInfoListenerSet = new HashSet<>();
    private Set<OnGetIntervalCardTypeListener> onGetIntervalCardTypeListenerSet = new HashSet<>();
    private int currentConsumerMode;
    //按次消费的金额信息
    private IntervalCardType mIntervalCardType;
    //餐厅时段信息
    private CanteenCurrentTimeInfo mCanteenCurrentTimeInfo;
    //餐厅时段信息（一分钟）
    private int totalRequestTimeInfoSecond;

    public ConsumerModeHelper(@NonNull Activity activity) {
        super(activity);
        currentConsumerMode = PaymentSettingMMKV.getConsumerMode();
    }

    /**
     * 获取按次消费金额
     */
    public String getNumberConsumerPayMoney() {
        if (mCanteenCurrentTimeInfo != null && mIntervalCardType != null) {
            String feeType = mCanteenCurrentTimeInfo.getFeeType();
            if (TextUtils.equals(PayConstants.FEE_TYPE_BREAKFAST, feeType)) {
                return mIntervalCardType.getF_OneMoney();
            } else if (TextUtils.equals(PayConstants.FEE_TYPE_LUNCH, feeType)) {
                return mIntervalCardType.getF_TwoMoney();
            } else if (TextUtils.equals(PayConstants.FEE_TYPE_DINNER, feeType)) {
                return mIntervalCardType.getF_ThreeMoney();
            }
        }
        return "";
    }

    /**
     * 按次消费金额信息
     */
    public void requestIntervalCardType() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        TreeMap<String, String> intervalCardTypeParams = ParamsUtils.newSortParamsMapWithMode("GetIntervalCardType");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .getIntervalCardType(ParamsUtils.signSortParamsMap(intervalCardTypeParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<List<IntervalCardType>>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<List<IntervalCardType>> baseNetResponse) {
                        if(baseNetResponse.isSuccess()){
                            List<IntervalCardType> intervalCardTypes = baseNetResponse.getData();
                            if (intervalCardTypes != null && !intervalCardTypes.isEmpty()) {
                                mIntervalCardType = intervalCardTypes.get(0);
                                for (OnGetIntervalCardTypeListener listener : onGetIntervalCardTypeListenerSet) {
                                    listener.onGetIntervalCardType(mIntervalCardType);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 获取餐厅时段信息
     */
    public void requestCanteenCurrentTimeInfo() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        TreeMap<String, String> currentTimeInfoParams = ParamsUtils.newSortParamsMapWithMode("Current_Time_Info");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .getCanteenTimeInfo(ParamsUtils.signSortParamsMap(currentTimeInfoParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<CanteenCurrentTimeInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<CanteenCurrentTimeInfo> baseNetResponse) {
                        CanteenCurrentTimeInfo data = baseNetResponse.getData();
                        if (data != null) {
                            mCanteenCurrentTimeInfo = data;
                            for (OnGetCanteenTimeInfoListener listener : onCanteenTimeInfoListenerSet) {
                                listener.onGetCanteenTimeInfo(mCanteenCurrentTimeInfo);
                            }
                        }
                    }
                });
    }

    /**
     * 改变消费模式
     */
    public void changeConsumerMode(int consumerMode) {
        if (currentConsumerMode == consumerMode) {
            AppToast.toastMsg("当前已在" + PayConstants.getConsumerModeStr(consumerMode));
            return;
        }
        for (OnConsumerModeListener consumerModeListener : onConsumerModeListenerSet) {
            consumerModeListener.onChangeConsumerMode(consumerMode, currentConsumerMode);
        }
        currentConsumerMode = consumerMode;
        PaymentSettingMMKV.putConsumerMode(consumerMode);
    }

    public int getCurrentConsumerMode() {
        return currentConsumerMode;
    }

    public void addConsumerModeListener(OnConsumerModeListener consumerModeListener) {
        onConsumerModeListenerSet.add(consumerModeListener);
    }

    public void removeConsumerModeListener(OnConsumerModeListener consumerModeListener) {
        onConsumerModeListenerSet.remove(consumerModeListener);
    }

    public void addGetCanteenTimeInfoListener(OnGetCanteenTimeInfoListener listener) {
        onCanteenTimeInfoListenerSet.add(listener);
    }

    public void removeGetCanteenTimeInfoListener(OnGetCanteenTimeInfoListener listener) {
        onCanteenTimeInfoListenerSet.remove(listener);
    }

    public void addGetIntervalCardTypeListener(OnGetIntervalCardTypeListener listener) {
        onGetIntervalCardTypeListenerSet.add(listener);
    }

    public void removeGetIntervalCardTypeListener(OnGetIntervalCardTypeListener listener) {
        onGetIntervalCardTypeListenerSet.remove(listener);
    }

    @Override
    public void onClear() {
        onConsumerModeListenerSet.clear();
        onGetIntervalCardTypeListenerSet.clear();
    }

    public void showSelectConsumerModeDialog() {
        List<CommonSelectItem> selectItemList = new ArrayList<>();
        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_AMOUNT_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_AMOUNT_MODE)));
        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_NUMBER_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_NUMBER_MODE)));
        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_TAKE_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_TAKE_MODE)));
//        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_SEND_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_SEND_MODE)));
        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_WEIGHT_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_WEIGHT_MODE)));
        selectItemList.add(new CommonSelectItem(PayConstants.CONSUMER_QUOTA_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_QUOTA_MODE)));
        for (CommonSelectItem item : selectItemList) {
            if (currentConsumerMode == item.getTypeInt()) {
                item.setSelect(true);
                break;
            }
        }
        CommonSelectDialogFragment.build()
                .setTitle("请选择消费模式")
                .setSelectListData(selectItemList)
                .setOnSelectListener(new CommonSelectDialogFragment.OnSelectListener() {
                    @Override
                    public void onConfirmSelectItem(CommonSelectItem commonSelectItem) {
                        changeConsumerMode(commonSelectItem.getTypeInt());
                    }
                }).show(getHolderActivity());
    }

    @Override
    public void onCountDown() {
        totalRequestTimeInfoSecond++;
        //一分钟请求一次 餐厅时段信息
        if (totalRequestTimeInfoSecond >= 60) {
            totalRequestTimeInfoSecond = 0;
            requestCanteenCurrentTimeInfo();
            //按次消费获取消费时段金额
            if (currentConsumerMode == PayConstants.CONSUMER_NUMBER_MODE) {
                requestIntervalCardType();
            }
        }
    }
}
