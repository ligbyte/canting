package com.stkj.cashiermini.base.ui.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stkj.cashiermini.R;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

/**
 * 公用弹窗fragment
 */
public class CommonAlertFragment extends BaseRecyclerFragment {

    private ImageView ivClose;
    private TextView tvTitle;
    private FrameLayout flAlertContent;
    private ShapeTextView stvLeftBt;
    private ShapeTextView stvRightBt;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_common_alert;
    }

    @Override
    protected void initViews(View rootView) {
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloseClickListener != null) {
                    mCloseClickListener.onClick();
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.tv_title);
        flAlertContent = (FrameLayout) findViewById(R.id.fl_alert_content);
        stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
        stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
        int layoutResId = getAlertContentLayResId();
        if (layoutResId != 0) {
            View alertContentView = LayoutInflater.from(mActivity).inflate(layoutResId, flAlertContent, false);
            flAlertContent.addView(alertContentView);
            initAlertContentView(alertContentView);
        } else {
            View contentLayoutView = getAlertContentLayView();
            if (contentLayoutView != null) {
                flAlertContent.addView(contentLayoutView);
                initAlertContentView(contentLayoutView);
            }
        }
        String leftNavText = getLeftNavText();
        if (!TextUtils.isEmpty(leftNavText)) {
            stvLeftBt.setText(leftNavText);
        }
        stvLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftNavClickListener != null) {
                    mLeftNavClickListener.onClick();
                }
            }
        });
        String rightNavText = getRightNavText();
        if (!TextUtils.isEmpty(rightNavText)) {
            stvRightBt.setText(rightNavText);
        }
        stvRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightNavClickListener != null) {
                    mRightNavClickListener.onClick();
                }
            }
        });
        String alertTitle = getAlertTitle();
        if (!TextUtils.isEmpty(alertTitle)) {
            tvTitle.setText(alertTitle);
        }
    }

    protected void initAlertContentView(View contentView) {

    }

    protected int getAlertContentLayResId() {
        return 0;
    }

    protected View getAlertContentLayView() {
        return null;
    }

    protected String getLeftNavText() {
        return "确定";
    }

    protected String getRightNavText() {
        return "取消";
    }

    protected String getAlertTitle() {
        return "";
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;
    private OnSweetClickListener mCloseClickListener;

    public void setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
    }

    public void setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
    }

    public void setCloseClickListener(OnSweetClickListener listener) {
        mCloseClickListener = listener;
    }

    public interface OnSweetClickListener {
        void onClick();
    }

}
