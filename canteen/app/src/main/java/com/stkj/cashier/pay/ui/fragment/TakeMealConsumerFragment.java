package com.stkj.cashier.pay.ui.fragment;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.pay.callback.OnGetCanteenTimeInfoListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.CanteenCurrentTimeInfo;
import com.stkj.cashier.pay.model.ConsumerSuccessEvent;
import com.stkj.cashier.pay.model.TakeMealListItem;
import com.stkj.cashier.pay.model.TakeMealListResult;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.pay.ui.adapter.TakeMealViewHolder;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.helper.FacePassHelper;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 取餐模式
 */
public class TakeMealConsumerFragment extends IdentityVerificationFragment implements OnConsumerConfirmListener {

    private TextView tvMealFeeType;
    private TextView tvMealStartTime;
    private TextView tvMealEndTime;
    private TextView tvMealOrderCount;
    private TextView tvMealPickCount;
    private RecyclerView rvTakeMealList;
    private View flListEmpty;
    private CommonRecyclerAdapter mTakeMealListAdapter = new CommonRecyclerAdapter(false);

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_take_meal_consumer;
    }

    @Override
    protected void initViews(View rootView) {
        tvMealFeeType = (TextView) findViewById(R.id.tv_meal_feeType);
        tvMealStartTime = (TextView) findViewById(R.id.tv_meal_start_time);
        tvMealEndTime = (TextView) findViewById(R.id.tv_meal_end_time);
        tvMealOrderCount = (TextView) findViewById(R.id.tv_meal_order_count);
        tvMealPickCount = (TextView) findViewById(R.id.tv_meal_pick_count);
        rvTakeMealList = (RecyclerView) findViewById(R.id.rv_take_meal_list);
        flListEmpty = findViewById(R.id.fl_list_empty);
        int rightMargin = getResources().getDimensionPixelSize(com.stkj.common.R.dimen.dp_12);
        rvTakeMealList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, 0, rightMargin, 0);
            }
        });
        mTakeMealListAdapter = new CommonRecyclerAdapter(false);
        mTakeMealListAdapter.addViewHolderFactory(new TakeMealViewHolder.Factory());
        mTakeMealListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == TakeMealViewHolder.EVENT_CLICK_TAKE_MEAL) {
                    removeTakeMealList((TakeMealListItem) obj);
                }
            }
        });
        rvTakeMealList.setAdapter(mTakeMealListAdapter);
    }

    private OnGetCanteenTimeInfoListener canteenTimeInfoListener = new OnGetCanteenTimeInfoListener() {

        @Override
        public void onGetCanteenTimeInfo(CanteenCurrentTimeInfo canteenCurrentTimeInfo) {
            if (tvMealFeeType != null) {
                tvMealFeeType.setText("当前餐别：" + PayConstants.getFeeTypeStr(canteenCurrentTimeInfo.getFeeType()));
                tvMealStartTime.setText("供应时间：" + canteenCurrentTimeInfo.getBegin());
                tvMealEndTime.setText("截止订餐时间：" + canteenCurrentTimeInfo.getEndOrder());
                tvMealOrderCount.setText("订餐人数：" + canteenCurrentTimeInfo.getTotal());
                tvMealPickCount.setText("已取餐人数：" + canteenCurrentTimeInfo.getTakeMeal());
            }
        }
    };

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            Observable.timer(1, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    goToAuthGetMealOrder("");
                }
            });
            ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
            consumerModeHelper.addGetCanteenTimeInfoListener(canteenTimeInfoListener);
            consumerModeHelper.requestCanteenCurrentTimeInfo();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        ConsumerManager.INSTANCE.setFacePassConfirmListener(null);
        stopAllAuth();
        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        consumerModeHelper.removeGetCanteenTimeInfoListener(canteenTimeInfoListener);
    }

    private void goToAuthGetMealOrder(String extraTips) {
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        ConsumerManager.INSTANCE.setConsumerTakeMealWay();
        ConsumerManager.INSTANCE.setFacePassConfirmListener(this);
        goToAllAuth(extraTips);
    }

    @Override
    protected void handleReadICCardError(String message) {
        ttsVoiceAndConsumerTips("读卡失败，请重试");
    }

    @Override
    protected void handleReadICCardSuccess(FacePassPeopleInfo facePassPeopleInfo) {
        ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, false, PayConstants.PAY_TYPE_IC_CARD);
        ttsVoiceAndConsumerTips("读卡成功,正在获取订单");
    }

    @Override
    protected void handleReadICCardSuccess(String cardNumber) {
        ConsumerManager.INSTANCE.setConsumerConfirmCardInfo(cardNumber, false);
        ttsVoiceAndConsumerTips("读卡成功,正在获取订单");
    }

    @Override
    protected void handleScanQRCodeError(String message) {
        ttsVoiceAndConsumerTips("扫码失败，请重试");
    }

    @Override
    protected void handleScanQRCodeSuccess(String qrcodeResult) {
        ttsVoiceAndConsumerTips("扫码成功,正在获取订单");
        getMealOrderList(qrcodeResult, PayConstants.PAY_TYPE_QRCODE);
    }

    @Override
    protected void handleFacePassError(boolean canSpeakFacePassFail, int recognizeState) {
        if (canSpeakFacePassFail) {
            ttsVoiceAndConsumerTips("识别失败,正在重试");
        }
    }

    @Override
    protected void handleFacePassSuccess(FacePassPeopleInfo facePassPeopleInfo) {
        ttsVoiceAndConsumerTips("识别成功,正在获取订单");
        ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, false, PayConstants.PAY_TYPE_FACE);
    }

    /**
     * 获取取餐订单(通过cardNumber)
     */
    private void getMealOrderList(String cardNumber, int consumptionType) {
        showLoadingDialog("获取订单中");
        TreeMap<String, String> takeMealsListParams = ParamsUtils.newSortParamsMapWithMode("Take_Meals_List");
        takeMealsListParams.put("cardNumber", cardNumber);
        takeMealsListParams.put("consumption_type", String.valueOf(consumptionType));
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(PayService.class).takeMealList(ParamsUtils.signSortParamsMap(takeMealsListParams)).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<TakeMealListResult>() {
            @Override
            protected void onSuccess(TakeMealListResult takeMealListResult) {
                hideLoadingDialog();
                handleGetMealSuccess(takeMealListResult, consumptionType);
            }

            @Override
            public void onError(Throwable e) {
                hideLoadingDialog();
                delayToGetMealOrder("获取订单失败");
            }
        });
    }

    /**
     * 获取取餐订单(通过takeCode)
     */
    private void queryMealOrderList(String takeCode, int consumptionType) {
        showLoadingDialog("获取订单中");
        TreeMap<String, String> takeMealsListParams = ParamsUtils.newSortParamsMapWithMode("Take_Code_Query");
        takeMealsListParams.put("takeCode", takeCode);
        takeMealsListParams.put("consumption_type", String.valueOf(consumptionType));
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(PayService.class).takeCodeQuery(ParamsUtils.signSortParamsMap(takeMealsListParams)).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<TakeMealListResult>() {
            @Override
            protected void onSuccess(TakeMealListResult takeMealListResult) {
                hideLoadingDialog();
                handleGetMealSuccess(takeMealListResult, consumptionType);
            }

            @Override
            public void onError(Throwable e) {
                hideLoadingDialog();
                delayToGetMealOrder("获取订单失败");
            }
        });
    }

    /**
     * 获取订单成功
     */
    private void handleGetMealSuccess(TakeMealListResult takeMealListResult, int consumptionType) {
        if (takeMealListResult.isSuccess() && takeMealListResult.getData() != null && !takeMealListResult.getData().isEmpty()) {
            List<TakeMealListItem> takeMealListItems = takeMealListResult.getData();
            for (TakeMealListItem item : takeMealListItems) {
                item.setFull_Name(takeMealListResult.getFull_Name());
                item.setUser_Tel(takeMealListResult.getUser_Tel());
                item.setUser_Face(takeMealListResult.getUser_Face());
                item.setTakeType(consumptionType);
                item.setCard_Number(takeMealListResult.getCard_Number());
            }
            addTakeMealList(takeMealListItems, takeMealListResult.getCard_Number());
            delayToGetMealOrder("获取订单成功");
            //消费成功刷新相关数据
            EventBus.getDefault().post(new ConsumerSuccessEvent());
        } else {
            if (!takeMealListResult.isSuccess() && !TextUtils.isEmpty(takeMealListResult.getMessage())) {
                delayToGetMealOrder(takeMealListResult.getMessage());
            } else {
                delayToGetMealOrder("未查询到订单");
            }
        }
    }

    /**
     * 延迟提示继续去认证取餐
     */
    private void delayToGetMealOrder(String tips) {
        Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
            @Override
            protected void onSuccess(Long aLong) {
                goToAuthGetMealOrder(tips);
            }
        });
    }

    /**
     * 添加取餐的订单
     */
    private void addTakeMealList(List<TakeMealListItem> takeMealListItemList, String newQueueCardNumber) {
        LogHelper.print("TakeMealConsumerFragment---addTakeMealList queue newQueueCardNumber:" + newQueueCardNumber);
        //订单内餐品数量叠加
        for (int i = 0; i < takeMealListItemList.size(); i++) {
            TakeMealListItem takeMealListItem = takeMealListItemList.get(i);
            List<TakeMealListItem.FoodList> foodList = takeMealListItem.getFoodList();
            if (foodList != null && !foodList.isEmpty()) {
                //重新修改餐品列表
                List<TakeMealListItem.FoodList> newFoodList = new ArrayList<>();
                for (int j = 0; j < foodList.size(); j++) {
                    TakeMealListItem.FoodList foodItem = foodList.get(j);
                    String number = foodItem.getNumber();
                    int foodCount = 0;
                    try {
                        foodCount = Integer.parseInt(number);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (foodCount > 0) {
                        for (int k = 0; k < foodCount; k++) {
                            newFoodList.add(foodItem);
                        }
                    }
                }
                takeMealListItem.setFoodList(newFoodList);
            }
        }
        //获取排队人数限制
        int takeMealQueueLimit = PaymentSettingMMKV.getTakeMealQueueLimit();
        //当前排队人数
        List<Object> dataList = mTakeMealListAdapter.getDataList();
        Set<String> cardNumberSet = new HashSet<>();
        String firstCardNumber = "";
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof TakeMealListItem) {
                TakeMealListItem takeMealListItem = (TakeMealListItem) obj;
                String cardNumber = takeMealListItem.getCard_Number();
                if (TextUtils.isEmpty(firstCardNumber)) {
                    //第一个排队的人
                    firstCardNumber = cardNumber;
                }
                cardNumberSet.add(cardNumber);
                LogHelper.print("TakeMealConsumerFragment---addTakeMealList queue forEach cardNumber:" + cardNumber + " queueCount:" + cardNumberSet.size());
            }
        }
        //添加新来的订单人
        cardNumberSet.add(newQueueCardNumber);
        //超出限制，移除第一个排队的人的订单
        if (cardNumberSet.size() > takeMealQueueLimit && !TextUtils.isEmpty(firstCardNumber)) {
            LogHelper.print("TakeMealConsumerFragment---addTakeMealList queue remove firstCardNumber:" + firstCardNumber);
            AppToast.toastMsg("超出排队限制,已移除第一个人的订单!");
            //移除第一个排队的人后的排队订单
            List<Object> newDataList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                Object obj = dataList.get(i);
                if (obj instanceof TakeMealListItem) {
                    TakeMealListItem takeMealListItem = (TakeMealListItem) obj;
                    if (!TextUtils.equals(firstCardNumber, takeMealListItem.getCard_Number())) {
                        newDataList.add(obj);
                    }
                }
            }
            //添加新获取的排队订单
            newDataList.addAll(takeMealListItemList);
            mTakeMealListAdapter.removeAllData();
            AppToast.toastMsg("新增订单成功");
            mTakeMealListAdapter.addDataList(newDataList);
        } else {
            //未超出限制直接添加到队列尾部
            AppToast.toastMsg("新增订单成功");
            mTakeMealListAdapter.addDataList(takeMealListItemList);
        }
        flListEmpty.setVisibility(View.GONE);
        rvTakeMealList.setVisibility(View.VISIBLE);
    }

    /**
     * 点击出餐
     */
    private void removeTakeMealList(TakeMealListItem takeMealListItem) {
        mTakeMealListAdapter.removeData(takeMealListItem);
        speakTTSVoice("餐号" + takeMealListItem.getTakeCode() + "出餐成功");
        if (mTakeMealListAdapter.isEmptyData()) {
            rvTakeMealList.setVisibility(View.GONE);
            flListEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放语音和副屏提示
     */
    private void ttsVoiceAndConsumerTips(String tips) {
        speakTTSVoice(tips);
        ConsumerManager.INSTANCE.setConsumerTips(tips);
    }

    /**
     * start 副屏用户确认逻辑 start
     */
    @Override
    public void onConfirmFacePass(FacePassPeopleInfo passPeopleInfo) {
        ConsumerManager.INSTANCE.setConsumerTips("获取订单中");
        getMealOrderList(passPeopleInfo.getCard_Number(), PayConstants.PAY_TYPE_IC_CARD);
    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        speakTTSVoice("用户已取消");
        delayToGetMealOrder("");
    }

    @Override
    public void onConfirmCardNumber(String cardNumber) {
        ConsumerManager.INSTANCE.setConsumerTips("获取订单中");
        getMealOrderList(cardNumber, PayConstants.PAY_TYPE_IC_CARD);
    }

    @Override
    public void onCancelCardNumber(String cardNumber) {
        speakTTSVoice("用户已取消");
        delayToGetMealOrder("");
    }

    @Override
    public void onConsumerCancelPay() {
        speakTTSVoice("用户已取消");
        delayToGetMealOrder("");
    }

    @Override
    public void onConfirmTakeMealCode(String takeCode) {
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        ConsumerManager.INSTANCE.setConsumerTips("获取订单中");
        queryMealOrderList(takeCode, PayConstants.PAY_TYPE_IC_CARD);
    }

    @Override
    public void onConfirmPhone(String phone) {
        mActivity.getWeakRefHolder(FacePassHelper.class).searchFacePassByPhone(phone, new FacePassHelper.OnHandlePhoneListener() {
            @Override
            public void onHandleLocalPhone(String phone, FacePassPeopleInfo facePassPeopleInfo) {
                ttsVoiceAndConsumerTips("识别成功,正在获取订单");
                ConsumerManager.INSTANCE.setConsumerConfirmFaceInfo(facePassPeopleInfo, false, PayConstants.PAY_TYPE_FACE);
            }

            @Override
            public void onHandleLocalPhoneError(String phone) {
                delayToGetMealOrder("未查询到订单");
            }
        });
    }

    @Override
    public void onShowSimpleInputNumber(boolean show) {
        if (show) {
            stopAllAuth();
        } else {
            goToAuthGetMealOrder("");
        }
    }

    /**
     * end 副屏用户确认逻辑 end
     */

}
