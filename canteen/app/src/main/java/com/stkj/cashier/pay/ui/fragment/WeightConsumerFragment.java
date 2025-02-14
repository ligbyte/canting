package com.stkj.cashier.pay.ui.fragment;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.CircleProgressBar;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.deviceinterface.callback.OnReadWeightListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 称重消费
 */
public class WeightConsumerFragment extends BasePayHelperFragment {

    private TextView tvWeightPrice;
    private TextView tvWeight;
    private TextView tvPreUnitPrice;
    private TextView tvNewUnitPrice;
    private TextView stvUpdateUnitPrice;
    private TextView tvConfirm;
    private LinearLayout llConfirm;
    private CircleProgressBar pbCalcWeight;
    private boolean isCalculatorEnable = true;
    private boolean forbidRefreshPrice;
    //当前称重商品单价
    private String mCurrentUnitWeightPrice;
    private int mCurrentAutoWeightCount;
    private String mLastWeightCount;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_weight_consumer;
    }

    @Override
    protected void initViews(View rootView) {
        llConfirm = (LinearLayout) findViewById(R.id.ll_confirm);
        pbCalcWeight = (CircleProgressBar) findViewById(R.id.pb_calc_weight);
        tvWeightPrice = (TextView) findViewById(R.id.tv_weight_price);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvPreUnitPrice = (TextView) findViewById(R.id.tv_pre_unit_price);
        tvNewUnitPrice = (TextView) findViewById(R.id.tv_new_unit_price);
        stvUpdateUnitPrice = (TextView) findViewById(R.id.srtv_update_unit_price);
        stvUpdateUnitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    CommonDialogUtils.showTipsDialog(mActivity, "请先取消结算");
                    return;
                }
                String currentInputText = getCurrentInputText();
                boolean checkWeightUnitPrice = checkWeightUnitPrice(currentInputText);
                if (checkWeightUnitPrice) {
                    tvNewUnitPrice.setText("");
                    tvPreUnitPrice.setText(PriceUtils.formatPrice(currentInputText) + "/kg");
                    mCurrentUnitWeightPrice = currentInputText;
                    PaymentSettingMMKV.putUnitWeightPrice(currentInputText);
                    AppToast.toastMsg("单价修改成功");
                }
            }
        });
        findViewById(R.id.stv_num0).setOnClickListener(buildNumClickListener("0"));
        findViewById(R.id.stv_num1).setOnClickListener(buildNumClickListener("1"));
        findViewById(R.id.stv_num2).setOnClickListener(buildNumClickListener("2"));
        findViewById(R.id.stv_num3).setOnClickListener(buildNumClickListener("3"));
        findViewById(R.id.stv_num4).setOnClickListener(buildNumClickListener("4"));
        findViewById(R.id.stv_num5).setOnClickListener(buildNumClickListener("5"));
        findViewById(R.id.stv_num6).setOnClickListener(buildNumClickListener("6"));
        findViewById(R.id.stv_num7).setOnClickListener(buildNumClickListener("7"));
        findViewById(R.id.stv_num8).setOnClickListener(buildNumClickListener("8"));
        findViewById(R.id.stv_num9).setOnClickListener(buildNumClickListener("9"));
        findViewById(R.id.sfl_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                deleteInput();
            }
        });
        findViewById(R.id.stv_spot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                inputSpot();
            }
        });
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    setCalcEnable(true);
                    stopWeightPay();
                    goToWeightPay("取消结算");
                } else {
                    goToWeightPay("");
                }
            }
        };
        llConfirm.setOnClickListener(confirmListener);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(confirmListener);
    }

    private boolean checkWeightUnitPrice(String unitPrice) {
        if (TextUtils.isEmpty(unitPrice)) {
            CommonDialogUtils.showTipsDialog(mActivity, "单价不能为空");
            return false;
        }
        try {
            double parseDouble = Double.parseDouble(unitPrice);
            if (parseDouble <= 0) {
                CommonDialogUtils.showTipsDialog(mActivity, "单价不能小于0");
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected boolean canConsumerCancelPay() {
        return true;
    }

    private OnReadWeightListener onReadWeightListener = new OnReadWeightListener() {
        @Override
        public void onReadWeightData(String weightCount, String unit) {
            if (forbidRefreshPrice) {
                //商品总量
                boolean switchWeightAutoCancelPay = PaymentSettingMMKV.getSwitchWeightAutoCancelPay();
                if (switchWeightAutoCancelPay) {
                    String formatWeightCount = PriceUtils.formatPrice(weightCount);
                    double parseWeightCount = 0;
                    try {
                        parseWeightCount = Double.parseDouble(formatWeightCount);
                        if (parseWeightCount <= 0) {
                            stopWeightPay();
                            goToWeightPay("取消结算");
                            LogHelper.print("onReadWeightData-forbidRefreshPrice-formatWeightCount: 0");
                            return;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            String formatWeightUnitPrice = mCurrentUnitWeightPrice;
            if (TextUtils.isEmpty(formatWeightUnitPrice) || TextUtils.isEmpty(weightCount)) {
                ConsumerManager.INSTANCE.setConsumerTips("请将物品放至秤上");
                setCalcEnable(true);
                tvWeight.setText("0.00");
                resetWeightPrice();
                return;
            }
            //商品单价
            double parseWeightUnitPrice = 0;
            try {
                parseWeightUnitPrice = Double.parseDouble(formatWeightUnitPrice);
                if (parseWeightUnitPrice <= 0) {
                    ConsumerManager.INSTANCE.setConsumerTips("请将物品放至秤上");
                    setCalcEnable(true);
                    tvWeight.setText("0.00");
                    resetWeightPrice();
                    LogHelper.print("onReadWeightData--formatWeightUnitPrice: 0");
                    return;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            //商品总量
            String formatWeightCount = PriceUtils.formatPrice(weightCount);
            double parseWeightCount = 0;
            try {
                parseWeightCount = Double.parseDouble(formatWeightCount);
                if (parseWeightCount <= 0) {
                    ConsumerManager.INSTANCE.setConsumerTips("请将物品放至秤上");
                    setCalcEnable(true);
                    tvWeight.setText("0.00");
                    resetWeightPrice();
                    LogHelper.print("onReadWeightData--formatWeightCount: 0");
                    return;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            double parseWeightPrice = BigDecimalUtils.mul(parseWeightUnitPrice, parseWeightCount);
            double weightAttachAmount = getWeightAttachAmount();
            if (weightAttachAmount > 0) {
                parseWeightPrice = BigDecimalUtils.add(parseWeightPrice, weightAttachAmount);
            }
            String weightPrice = PriceUtils.formatPrice(parseWeightPrice);
            tvWeight.setText(formatWeightCount);
            tvWeightPrice.setText(weightPrice);
            LogHelper.print("--onReadWeightData mLastWeightCount: " + mLastWeightCount + " | currentWeightCount: " + weightCount);
            //判读称重稳定次数
            if (TextUtils.equals(mLastWeightCount, weightCount)) {
                mCurrentAutoWeightCount++;
                int autoWeightCount = PaymentSettingMMKV.getAutoWeightCount();
                if (autoWeightCount > 0) {
                    int pro = (int) ((mCurrentAutoWeightCount * 1.0f / autoWeightCount) * 360);
                    setCalcWeight(pro);
                    ConsumerManager.INSTANCE.setConsumerTips("计价中", pro);
                }
                //重量已经稳定
                if (mCurrentAutoWeightCount > autoWeightCount) {
                    forbidRefreshPrice = true;
                    setCalcEnable(false);
                    //判断称重商品重量
                    String weightCountTemp = getWeightCount();
                    String formatWeightCountTemp = PriceUtils.formatPrice(weightCountTemp);
                    double parseWeightCountTemp = 0;
                    try {
                        parseWeightCountTemp = Double.parseDouble(formatWeightCountTemp);
                        if (parseWeightCountTemp <= 0) {
                            goToWeightPay("重量为零");
                            return;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        goToWeightPay("称重重量异常");
                        return;
                    }
                    int goToPay = goToPay(getWeightRealPayMoney());
                    if (goToPay == PAYING_TO_PAY) {
                        speakTTSVoice("支付中,请稍等");
                    } else if (goToPay == MONEY_ZERO_TO_PAY) {
                        goToWeightPay("支付金额为零");
                    }
                }
            } else {
                setCalcWeight(0);
                mCurrentAutoWeightCount = 0;
                mLastWeightCount = weightCount;
            }
            LogHelper.print("onReadWeightData--formatWeightCount: " + formatWeightCount + "--formatWeightUnitPrice: " + formatWeightUnitPrice + " weightPrice: " + weightPrice);
        }

        @Override
        public void onReadWeightError(String message) {
            tvWeight.setText("0.00");
            resetWeightPrice();
        }
    };

    /**
     * 称重结算
     */
    private void goToWeightPay(String voiceExtra) {
        boolean checkWeightUnitPrice = checkWeightUnitPrice(mCurrentUnitWeightPrice);
        if (checkWeightUnitPrice) {
            if (!TextUtils.isEmpty(voiceExtra)) {
                speakTTSVoice(voiceExtra + ",请将物品放至秤上");
            } else {
                speakTTSVoice("请将物品放至秤上");
            }
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            ConsumerManager.INSTANCE.setConsumerTips("请将物品放至秤上");
            DeviceManager.INSTANCE.getDeviceInterface().readWeight(onReadWeightListener);
            forbidRefreshPrice = false;
            mLastWeightCount = "";
            mCurrentAutoWeightCount = 0;
            setCalcEnable(true);
        }
    }

    /**
     * 取消称重结算
     */
    private void stopWeightPay() {
        tvWeight.setText("0.00");
        resetWeightPrice();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterReadWeightListener(onReadWeightListener);
        stopToPay();
    }

    /**
     * 设置计价中
     */
    private void setCalcWeight(int pro) {
        tvConfirm.setText("计价中");
        tvConfirm.setTextColor(Color.BLACK);
        llConfirm.setBackgroundResource(R.drawable.shape_calc_item_normal);
        if (pro > 0) {
            pbCalcWeight.setVisibility(View.VISIBLE);
            pbCalcWeight.setProgress(pro);
        } else {
            pbCalcWeight.setVisibility(View.GONE);
        }
    }

    public void setCalcEnable(boolean calcEnable) {
        isCalculatorEnable = calcEnable;
        pbCalcWeight.setVisibility(View.GONE);
        if (calcEnable) {
            tvConfirm.setText("结算");
            tvConfirm.setTextColor(Color.WHITE);
            llConfirm.setBackgroundResource(R.drawable.shape_calc_item_confirm_normal);
        } else {
            tvConfirm.setText("取消");
            tvConfirm.setTextColor(Color.BLACK);
            llConfirm.setBackgroundResource(R.drawable.shape_calc_item_normal);
        }
    }

    private View.OnClickListener buildNumClickListener(String number) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                inputNumber(number);
            }
        };
    }

    /**
     * 获取当前输入的数值
     */
    public String getCurrentInputText() {
        return tvNewUnitPrice.getText().toString();
    }

    /**
     * 输入数字
     */
    private void inputNumber(String number) {
        try {
            String inputText = getCurrentInputText();
            if (!TextUtils.isEmpty(inputText)) {
                //判断数字长度不能超多
                int pointIndex = inputText.indexOf(".");
                if (pointIndex != -1) {
                    //获取小数点位数 最多两位小数
                    if (inputText.length() - 1 >= pointIndex + 2) {
                        return;
                    }
                    //小数可以输入最大11位
                    if (inputText.length() >= 11) {
                        return;
                    }
                } else {
                    //整数可以输入最大8位
                    if (inputText.length() >= 8) {
                        return;
                    }
                    //007类似不可以输入
                    if (inputText.equals("0")) {
                        //当前如果还是0 则不可以输入
                        if (!number.equals("0")) {
                            //删除一个值 插入新数字
                            deleteInput();
                            inputFormula(number);
                        }
                        return;
                    }
                }
                //插入数字
                inputFormula(number);
            } else {
                inputFormula(number);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入数字出错了:" + e.getMessage());
        }
    }

    /**
     * 输入小数点
     */
    private void inputSpot() {
        try {
            //当前没有任何输入直接插入0.
            String currentInputText = getCurrentInputText();
            if (TextUtils.isEmpty(currentInputText)) {
                inputFormula("0.");
            } else if (!currentInputText.contains(".")) {
                inputFormula(".");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入小数点出错了:" + e.getMessage());
        }
    }

    /**
     * 删除输入
     */
    private void deleteInput() {
        try {
            StringBuilder inputBuilder = new StringBuilder(getCurrentInputText());
            if (inputBuilder.length() > 0) {
                //删除最后的一个字符
                int length = inputBuilder.length();
                inputBuilder.delete(length - 1, inputBuilder.length());
                tvNewUnitPrice.setText(inputBuilder.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "删除出错了:" + e.getMessage());
        }
    }

    private void inputFormula(String operateOrNumber) {
        String currentInputText = getCurrentInputText();
        if (!TextUtils.isEmpty(currentInputText)) {
            tvNewUnitPrice.setText(currentInputText + operateOrNumber);
        } else {
            tvNewUnitPrice.setText(operateOrNumber);
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new ConsumerRecordListFragment(), R.id.fl_consumer_list_content);
            Observable.timer(1, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    goToWeightPay("");
                }
            });
        }
        mCurrentUnitWeightPrice = PaymentSettingMMKV.getUnitWeightPrice();
        tvPreUnitPrice.setText(PriceUtils.formatPrice(mCurrentUnitWeightPrice) + "/kg");
    }

    private String getWeightRealPayMoney() {
        return tvWeightPrice.getText().toString();
    }

    private String getWeightCount() {
        return tvWeight.getText().toString();
    }

    /**
     * 重置称重价格（包含附加金额）
     */
    private void resetWeightPrice() {
        String weightAttachAmount = PaymentSettingMMKV.getWeightAttachAmount();
        double parseDouble = 0;
        try {
            parseDouble = Double.parseDouble(weightAttachAmount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (parseDouble <= 0) {
            tvWeightPrice.setText("0.00");
        } else {
            tvWeightPrice.setText(weightAttachAmount);
        }
    }

    /**
     * 称重附加金额
     */
    private double getWeightAttachAmount() {
        String weightAttachAmount = PaymentSettingMMKV.getWeightAttachAmount();
        try {
            return Double.parseDouble(weightAttachAmount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        super.onPaySuccess(payRequest, modifyBalanceResult);
        setCalcEnable(true);
    }

    @Override
    public void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg) {
        super.onPayError(responseCode, payRequest, modifyBalanceResult, msg);
        setCalcEnable(true);
    }

    @Override
    protected void onPayCancel(int payType) {
        speakTTSVoice("取消结算");
        setCalcEnable(true);
        stopWeightPay();
        Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
            @Override
            protected void onSuccess(Long aLong) {
                goToWeightPay("");
            }
        });
    }

    @Override
    protected void delayedToPayStatus() {
        goToWeightPay("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //移除称重监听
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterReadWeightListener(onReadWeightListener);
        stopToPay();
    }

}
