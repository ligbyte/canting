package com.stkj.cashier.pay.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.pay.callback.OnCalculateListener;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 简易计算器
 */
public class SimpleCalculator extends FrameLayout {

    private static final BigDecimal MAX_VALUE = new BigDecimal(99999999);
    private static final BigDecimal MIN_VALUE = new BigDecimal(0);
    private static final String SPLITE_STRING = " ";
    private static final String PLUS_OPERATE = "+";
    private static final String MINUS_OPERATE = "-";
    private static final String[] OPERATE_ALL = {PLUS_OPERATE, MINUS_OPERATE};
    private TextView tvConfirm;
    private TextView tvConsume;
    private TextView tvOperateNumber;
    private HorizontalScrollView hsvConsume;
    private OnCalculateListener onCalculateListener;
    private boolean isCalculatorEnable = true;

    public SimpleCalculator(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SimpleCalculator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimpleCalculator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        String confirmTxt = "确认金额";
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, com.stkj.cashier.R.styleable.SimpleCalculator);
            confirmTxt = array.getString(R.styleable.SimpleCalculator_sc_confirm_txt);
        }
        LayoutInflater.from(context).inflate(R.layout.include_fast_pay_calculator, this);
        tvConsume = (TextView) findViewById(R.id.tv_consume);
        hsvConsume = (HorizontalScrollView) findViewById(R.id.hsv_consume);
        tvOperateNumber = (TextView) findViewById(R.id.tv_operate_number);
        tvOperateNumber.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvOperateNumber.setHorizontallyScrolling(true);
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
        findViewById(R.id.sfl_del).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                deleteInput();
            }
        });
        findViewById(R.id.sfl_plus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                inputOperate(PLUS_OPERATE);
            }
        });
        findViewById(R.id.sfl_minus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                inputOperate(MINUS_OPERATE);
            }
        });
        findViewById(R.id.stv_spot).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                inputSpot();
            }
        });
        tvConfirm = findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            tvConfirm.setText(confirmTxt);
        }
        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    if (onCalculateListener != null) {
                        onCalculateListener.onClickDisableConfirm();
                    }
                    return;
                }
                confirmOperateNumberResult();
            }
        });
        findViewById(R.id.iv_clear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCalculatorEnable) {
                    AppToast.toastMsg("不可操作,请稍等~");
                    return;
                }
                clearCalcData();
            }
        });
        tvConsume.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int hsvConsumeWidth = hsvConsume.getWidth();
                int tvConsumeWidth = right - left;
                if (hsvConsumeWidth > 0 && tvConsumeWidth > hsvConsumeWidth) {
                    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                        @Override
                        public void doFrame(long frameTimeNanos) {
                            hsvConsume.fullScroll(FOCUS_RIGHT);
                        }
                    });
                }
                LogHelper.print("---onLayoutChange---left: " + left + " right: " + right + " scrollWidth: " + hsvConsume.getWidth());
            }
        });
    }

    private OnClickListener buildNumClickListener(String number) {
        return new OnClickListener() {
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
        return tvConsume.getText().toString();
    }

    private void confirmOperateNumberResult() {
        String currentInputText = getCurrentInputText();
        if (TextUtils.isEmpty(currentInputText)) {
            return;
        }
        //分割最后的输入的数据
        String[] splitInput = currentInputText.split(SPLITE_STRING);
        if (splitInput.length > 2) {
            //获取最后一个公式
            String lastOperateNumber = splitInput[splitInput.length - 1];
            //如果最后是操作符则不进行运算
            int needOperateLength = 0;
            if (isOperate(lastOperateNumber)) {
                needOperateLength = splitInput.length - 1;
            } else {
                needOperateLength = splitInput.length;
            }
            StringBuilder lastFormulaBuilder = new StringBuilder();
            String tempLastResult = "";
            String tempLastOperate = "";
            for (int i = 0; i < needOperateLength; i++) {
                String s = splitInput[i];
                int pointIndex = s.indexOf(".");
                if (pointIndex != -1 && pointIndex + 1 == s.length()) {
                    lastFormulaBuilder.append(s.substring(0, pointIndex)).append(SPLITE_STRING);
                } else {
                    lastFormulaBuilder.append(s).append(SPLITE_STRING);
                }
                if (!isOperate(s)) {
                    if (TextUtils.isEmpty(tempLastResult)) {
                        //第一次赋值result
                        tempLastResult = s;
                        continue;
                    }
                    if (!TextUtils.isEmpty(tempLastOperate)) {
                        //有操作符去计算结果
                        try {
                            double numberResult = 0;
                            BigDecimal lastNumberValue = new BigDecimal(tempLastResult);
                            BigDecimal currentNumberValue = new BigDecimal(s);
                            switch (tempLastOperate) {
                                case PLUS_OPERATE:
                                    numberResult = lastNumberValue.add(currentNumberValue).doubleValue();
                                    break;
                                case MINUS_OPERATE:
                                    numberResult = lastNumberValue.subtract(currentNumberValue).doubleValue();
                                    break;
                            }
                            LogHelper.print("---SimpleCalculator---lastNumberValue: " + lastNumberValue + "--currentNumberValue: " + currentNumberValue + "--numberResult: " + numberResult);
                            tempLastResult = String.valueOf(numberResult);
                            tempLastOperate = "";
                        } catch (Throwable e) {
                            e.printStackTrace();
                            tempLastResult = "0";
                            tempLastOperate = "";
                            CommonDialogUtils.showTipsDialog(getContext(), "计算出错了:" + e.getMessage());
                        }
                    }
                } else {
                    tempLastOperate = s;
                }
            }
            BigDecimal result = new BigDecimal(tempLastResult);
            BigDecimal max = result.max(MAX_VALUE);
            if (Objects.equals(max, result)) {
                CommonDialogUtils.showTipsDialog(getContext(), "超出最大数99999999");
                return;
            }
            BigDecimal min = result.min(MIN_VALUE);
            if (Objects.equals(min, result)) {
                CommonDialogUtils.showTipsDialog(getContext(), "低于最小数0");
                return;
            }
            String mLastResult = result.stripTrailingZeros().toPlainString();
            tvConsume.setText(mLastResult);
            tvOperateNumber.setText(lastFormulaBuilder.append("=").toString());
            if (onCalculateListener != null) {
                onCalculateListener.onConfirmMoney(mLastResult);
            }
        } else {
            if (splitInput.length >= 1) {
                String mLastResult = splitInput[0];
                tvConsume.setText(mLastResult);
                if (onCalculateListener != null) {
                    onCalculateListener.onConfirmMoney(mLastResult);
                }
            }
        }
    }

    /**
     * 获取最后的操作符或者数字
     */
    private String getLastOperateOrNumber() {
        String currentInputText = getCurrentInputText();
        if (TextUtils.isEmpty(currentInputText)) {
            LogHelper.print("---SimpleCalculator-getLastOperateOrNumber currentInputText is null");
            return "";
        }
        String[] split = currentInputText.split(SPLITE_STRING);
        if (split.length > 0) {
            String endOperateOrNumber = split[split.length - 1];
            LogHelper.print("---SimpleCalculator-getLastOperateOrNumber split endOperateOrNumber = " + endOperateOrNumber);
            return endOperateOrNumber;
        }
        LogHelper.print("---SimpleCalculator-getLastOperateOrNumber split is null");
        return "";
    }

    /**
     * 是否是操作度
     */
    private boolean isOperate(String operateOrNumber) {
        for (String operate : OPERATE_ALL) {
            if (TextUtils.equals(operateOrNumber, operate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 输入小数点
     */
    private void inputSpot() {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber) && !isOperate(lastOperateOrNumber)) {
                if (!lastOperateOrNumber.contains(".")) {
                    inputFormula(".");
                }
            } else {
                //当前没有任何输入直接插入0.
                String currentInputText = getCurrentInputText();
                if (TextUtils.isEmpty(currentInputText)) {
                    inputFormula("0.");
                } else {
                    inputFormula(SPLITE_STRING + "0.");
                }
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
                //删除最后字符,判断最后一个字符为空格则直接删除
                int afterLength = inputBuilder.length();
                if (afterLength > 0) {
                    String lastString = inputBuilder.substring(afterLength - 1);
                    if (SPLITE_STRING.equals(lastString)) {
                        inputBuilder.delete(afterLength - 1, inputBuilder.length());
                    }
                }
                tvConsume.setText(inputBuilder.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "删除出错了:" + e.getMessage());
        }
    }

    private void inputFormula(String operateOrNumber) {
        String currentInputText = getCurrentInputText();
        if (!TextUtils.isEmpty(currentInputText)) {
            tvConsume.setText(currentInputText + operateOrNumber);
        } else {
            tvConsume.setText(operateOrNumber);
        }
    }

    /**
     * 输入操作符
     */
    private void inputOperate(String operate) {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //当前输入框没有数字则不能输入
                //当前输入的是操作符，先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    //判断如果最后是操作符 则替换成当前输入的操作符
                    //删除最后的操作符
                    deleteInput();
                    inputFormula(SPLITE_STRING + operate);
                } else {
                    //如果结尾是小数点 则先删除小数点
                    if (lastOperateOrNumber.endsWith(".")) {
                        deleteInput();
                        inputFormula(SPLITE_STRING + operate);
                    } else {
                        inputFormula(SPLITE_STRING + operate);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入操作符出错了:" + e.getMessage());
        }
    }

    /**
     * 输入数字
     */
    private void inputNumber(String number) {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //如果最后是操作符,先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    inputFormula(SPLITE_STRING + number);
                } else {
                    //判断数字长度不能超多
                    int pointIndex = lastOperateOrNumber.indexOf(".");
                    if (pointIndex != -1) {
                        //获取小数点位数 最多两位小数
                        if (lastOperateOrNumber.length() - 1 >= pointIndex + 2) {
                            return;
                        }
                        //小数可以输入最大11位
                        if (lastOperateOrNumber.length() >= 11) {
                            return;
                        }
                    } else {
                        //整数可以输入最大8位
                        if (lastOperateOrNumber.length() >= 8) {
                            return;
                        }
                        //007类似不可以输入
                        if (lastOperateOrNumber.equals("0")) {
                            //当前如果还是0 则不可以输入
                            if (!number.equals("0")) {
                                //删除一个值 插入新数字
                                deleteInput();
                                //当前没有任何输入直接插入数字
                                String currentInputText = getCurrentInputText();
                                if (TextUtils.isEmpty(currentInputText)) {
                                    //插入数字
                                    inputFormula(number);
                                } else {
                                    inputFormula(SPLITE_STRING + number);
                                }
                            }
                            return;
                        }
                    }
                    //插入数字
                    inputFormula(number);
                }
            } else {
                inputFormula(number);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入数字出错了:" + e.getMessage());
        }
    }

    /**
     * 清空重置数据
     */
    public void clearCalcData() {
        tvOperateNumber.setText("");
        tvConsume.setText("");
    }

    public void setCalcEnable(boolean calcEnable) {
        isCalculatorEnable = calcEnable;
        if (calcEnable) {
            tvConfirm.setTextColor(getResources().getColor(R.color.selector_calc_item_confirm));
            tvConfirm.setBackgroundResource(R.drawable.selector_item_calc_confirm);
        } else {
            tvConfirm.setTextColor(getResources().getColor(R.color.black));
            tvConfirm.setBackgroundResource(R.drawable.shape_calc_item_confirm_disable);
        }
    }

    public void setConfirmTxt(String confirmTxt) {
        tvConfirm.setText(confirmTxt);
    }

    public void setOnCalculateListener(OnCalculateListener onCalculateListener) {
        this.onCalculateListener = onCalculateListener;
    }
}
