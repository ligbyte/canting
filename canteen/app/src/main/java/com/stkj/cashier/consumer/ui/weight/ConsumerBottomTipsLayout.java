package com.stkj.cashier.consumer.ui.weight;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.callback.OnGetCanteenTimeInfoListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.CanteenCurrentTimeInfo;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;

/**
 * 消费者底部提示布局
 */
public class ConsumerBottomTipsLayout extends FrameLayout implements OnGetCanteenTimeInfoListener {

    private TextView tvMealFeeType;
    private TextView tvMealStartTime;

    public ConsumerBottomTipsLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ConsumerBottomTipsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConsumerBottomTipsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int consumeLayRes = 0;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ConsumerBottomTipsLayout);
            consumeLayRes = array.getInteger(R.styleable.ConsumerBottomTipsLayout_cbtl_consume_lay_res, 0);
        }
        if (consumeLayRes == 1) {
            LayoutInflater.from(context).inflate(R.layout.include_consumer_bottom_tip_s1, this);
        } else if (consumeLayRes == 2) {
            LayoutInflater.from(context).inflate(R.layout.include_consumer_bottom_tip_s2, this);
        } else {
            LayoutInflater.from(context).inflate(R.layout.include_consumer_bottom_tip, this);
        }
        tvMealFeeType = (TextView) findViewById(R.id.tv_meal_feeType);
        tvMealStartTime = (TextView) findViewById(R.id.tv_meal_start_time);
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
            if (consumerModeHelper != null) {
                consumerModeHelper.addGetCanteenTimeInfoListener(this);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
            if (consumerModeHelper != null) {
                consumerModeHelper.removeGetCanteenTimeInfoListener(this);
            }
        }
    }

    @Override
    public void onGetCanteenTimeInfo(CanteenCurrentTimeInfo canteenCurrentTimeInfo) {
        if (tvMealFeeType != null) {
            tvMealFeeType.setText("当前餐别：" + PayConstants.getFeeTypeStr(canteenCurrentTimeInfo.getFeeType()));
            tvMealStartTime.setText("供应时间：" + canteenCurrentTimeInfo.getBegin() + " - " + canteenCurrentTimeInfo.getEnd());
        }
    }
}
