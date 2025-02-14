package com.stkj.cashier.pay.ui.fragment;

import com.stkj.cashier.R;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.FragmentUtils;

/**
 * 收银页面
 */
public class TabPayFragment extends BaseRecyclerFragment implements OnConsumerModeListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_payment;
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        if (isFirstOnResume) {
            changeConsumerMode(consumerModeHelper.getCurrentConsumerMode());
        }
        consumerModeHelper.addConsumerModeListener(this);
    }

    /**
     * 切换餐厅模式
     */
    private void changeConsumerMode(int mode) {
        if (mode == PayConstants.CONSUMER_AMOUNT_MODE) {
            //默认金额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AmountConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_NUMBER_MODE) {
            //按次模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new NumberConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_TAKE_MODE) {
            //取餐模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TakeMealConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_SEND_MODE) {
            //送餐模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TakeMealConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_WEIGHT_MODE) {
            //称重模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new WeightConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_QUOTA_MODE) {
            //定额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AmountConsumerFragment(), R.id.fl_pay_second_content);
        }else {
            //默认金额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AmountConsumerFragment(), R.id.fl_pay_second_content);
        }
    }

    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
        changeConsumerMode(consumerMode);
    }
}
