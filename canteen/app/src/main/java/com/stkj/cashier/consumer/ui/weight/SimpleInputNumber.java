package com.stkj.cashier.consumer.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.consumer.callback.OnInputNumberListener;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单输入数字布局
 */
public class SimpleInputNumber extends FrameLayout {

    private static final String NUMBER_PLACE_HOLDER = "—";
    private List<String> mInputNumberList = new ArrayList<>();
    private int mInputNumberCount;
    private OnInputNumberListener inputNumberListener;
    private ShapeTextView stvInputNumber;
    private TextView tvNumber0;
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvNumber3;
    private TextView tvNumber4;
    private TextView tvBack;
    private TextView tvDel;
    private TextView tvNumber5;
    private TextView tvNumber6;
    private TextView tvNumber7;
    private TextView tvNumber8;
    private TextView tvNumber9;
    private ShapeTextView stvConfirm;

    public SimpleInputNumber(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SimpleInputNumber(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimpleInputNumber(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int consumeLayRes = 0;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleInputNumber);
            mInputNumberCount = array.getInteger(R.styleable.SimpleInputNumber_sin_number_count, 0);
            consumeLayRes = array.getInteger(R.styleable.SimpleInputNumber_sin_consume_lay_res, 0);
        }
        if (consumeLayRes == 1) {
            LayoutInflater.from(context).inflate(R.layout.include_simple_input_number_s1, this);
        } else if (consumeLayRes == 2) {
            LayoutInflater.from(context).inflate(R.layout.include_simple_input_number_s2, this);
        } else {
            LayoutInflater.from(context).inflate(R.layout.include_simple_input_number, this);
        }
        for (int i = 0; i < mInputNumberCount; i++) {
            mInputNumberList.add(NUMBER_PLACE_HOLDER);
        }
        stvInputNumber = (ShapeTextView) findViewById(R.id.stv_input_number);
        tvNumber0 = (TextView) findViewById(R.id.tv_number0);
        tvNumber1 = (TextView) findViewById(R.id.tv_number1);
        tvNumber2 = (TextView) findViewById(R.id.tv_number2);
        tvNumber3 = (TextView) findViewById(R.id.tv_number3);
        tvNumber4 = (TextView) findViewById(R.id.tv_number4);
        tvNumber5 = (TextView) findViewById(R.id.tv_number5);
        tvNumber6 = (TextView) findViewById(R.id.tv_number6);
        tvNumber7 = (TextView) findViewById(R.id.tv_number7);
        tvNumber8 = (TextView) findViewById(R.id.tv_number8);
        tvNumber9 = (TextView) findViewById(R.id.tv_number9);
        tvNumber0.setOnClickListener(buildNumberClickListener("0"));
        tvNumber1.setOnClickListener(buildNumberClickListener("1"));
        tvNumber2.setOnClickListener(buildNumberClickListener("2"));
        tvNumber3.setOnClickListener(buildNumberClickListener("3"));
        tvNumber4.setOnClickListener(buildNumberClickListener("4"));
        tvNumber5.setOnClickListener(buildNumberClickListener("5"));
        tvNumber6.setOnClickListener(buildNumberClickListener("6"));
        tvNumber7.setOnClickListener(buildNumberClickListener("7"));
        tvNumber8.setOnClickListener(buildNumberClickListener("8"));
        tvNumber9.setOnClickListener(buildNumberClickListener("9"));
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputNumberListener != null) {
                    inputNumberListener.onClickBack();
                }
            }
        });
        tvDel = (TextView) findViewById(R.id.tv_del);
        tvDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNumber();
            }
        });
        stvConfirm = (ShapeTextView) findViewById(R.id.stv_confirm);
        stvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNumber();
            }
        });
        refreshNumber();
        setFixInputNumberParams();
    }


    private OnClickListener buildNumberClickListener(String number) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNumber(number);
            }
        };
    }

    public void setInputNumberListener(OnInputNumberListener inputNumberListener) {
        this.inputNumberListener = inputNumberListener;
    }

    /**
     * 设置输入number个数
     */
    public void setInputNumberCount(int numberCount) {
        if (numberCount > 0) {
            if (mInputNumberCount != numberCount) {
                mInputNumberCount = numberCount;
                mInputNumberList = new ArrayList<>();
                for (int i = 0; i < numberCount; i++) {
                    mInputNumberList.add(NUMBER_PLACE_HOLDER);
                }
                refreshNumber();
                setFixInputNumberParams();
            }
        } else {
            clearNumber();
        }
    }

    /**
     * 设置输入宽度固定
     */
    private void setFixInputNumberParams() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (stvInputNumber != null) {
                    ViewGroup.LayoutParams layoutParams = stvInputNumber.getLayoutParams();
                    float measureText = stvInputNumber.getPaint().measureText(NUMBER_PLACE_HOLDER);
                    int paddingLeft = stvInputNumber.getPaddingLeft();
                    int paddingRight = stvInputNumber.getPaddingRight();
                    layoutParams.width = (int) (mInputNumberCount * measureText) + paddingLeft + paddingRight;
                    stvInputNumber.setLayoutParams(layoutParams);
                }
            }
        });
    }

    /**
     * 输入数字
     */
    private void inputNumber(String number) {
        for (int i = 0; i < mInputNumberList.size(); i++) {
            String itemNumber = mInputNumberList.get(i);
            if (TextUtils.equals(itemNumber, NUMBER_PLACE_HOLDER)) {
                mInputNumberList.set(i, number);
                break;
            }
        }
        refreshNumber();
    }

    /**
     * 删除数字
     */
    private void deleteNumber() {
        for (int i = mInputNumberList.size() - 1; i >= 0; i--) {
            String itemNumber = mInputNumberList.get(i);
            if (!TextUtils.equals(itemNumber, NUMBER_PLACE_HOLDER)) {
                mInputNumberList.set(i, NUMBER_PLACE_HOLDER);
                break;
            }
        }
        refreshNumber();
    }

    /**
     * 清空数字
     */
    private void clearNumber() {
        for (int i = 0; i < mInputNumberList.size(); i++) {
            mInputNumberList.set(i, NUMBER_PLACE_HOLDER);
        }
        refreshNumber();
    }

    /**
     * 刷新数字
     */
    private void refreshNumber() {
        stvInputNumber.setText(getCurrentInputNumber());
    }

    /**
     * 获取当前输入的数字
     */
    public String getCurrentInputNumber() {
        StringBuilder builder = new StringBuilder();
        for (String number : mInputNumberList) {
            builder.append(number);
        }
        return builder.toString();
    }

    public int getInputNumberCount() {
        return mInputNumberCount;
    }

    /**
     * 确认数字
     */
    private void confirmNumber() {
        boolean hasInputFinish = true;
        boolean hasInputNumber = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mInputNumberList.size(); i++) {
            String inputNumber = mInputNumberList.get(i);
            builder.append(inputNumber);
            if (TextUtils.equals(inputNumber, NUMBER_PLACE_HOLDER)) {
                hasInputFinish = false;
                break;
            } else {
                hasInputNumber = true;
            }
        }
        if (hasInputFinish) {
            clearNumber();
            if (inputNumberListener != null) {
                inputNumberListener.onConfirmNumber(builder.toString());
            }
        } else {
            if (inputNumberListener != null) {
                inputNumberListener.onConfirmError(hasInputNumber);
            }
        }
    }

}
