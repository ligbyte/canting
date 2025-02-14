package com.stkj.infocollect.card.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.card.callback.StatusSuccessConfirmListener;

/**
 * 成功状态
 */
public class SuccessStatusFragment extends BaseRecyclerFragment {

    private String mTitleTxt;
    private String mContentTxt;
    private String mConfirmTxt;
    private StatusSuccessConfirmListener successConfirmListener;
    private TextView tvStatusTitle;
    private TextView tvStatusContent;
    private ShapeTextView stvStatusConfirm;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_status_success;
    }

    @Override
    protected void initViews(View rootView) {
        tvStatusTitle = (TextView) findViewById(R.id.tv_status_title);
        tvStatusContent = (TextView) findViewById(R.id.tv_status_content);
        stvStatusConfirm = (ShapeTextView) findViewById(R.id.stv_status_confirm);
        stvStatusConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), SuccessStatusFragment.this);
                if (successConfirmListener != null) {
                    successConfirmListener.onClickConfirm();
                }
            }
        });
        if (!TextUtils.isEmpty(mTitleTxt)) {
            tvStatusTitle.setText(mTitleTxt);
        }
        if (TextUtils.isEmpty(mContentTxt)) {
            tvStatusContent.setVisibility(View.GONE);
        } else {
            tvStatusContent.setVisibility(View.VISIBLE);
            tvStatusContent.setText(mContentTxt);
        }
        if (!TextUtils.isEmpty(mConfirmTxt)) {
            stvStatusConfirm.setText(mConfirmTxt);
        }
    }

    public void setTitleTxt(String mTitleTxt) {
        this.mTitleTxt = mTitleTxt;
        if (tvStatusTitle != null) {
            tvStatusTitle.setText(mTitleTxt);
        }
    }

    public void setContentTxt(String mContentTxt) {
        this.mContentTxt = mContentTxt;
        if (tvStatusContent != null) {
            if (TextUtils.isEmpty(mContentTxt)) {
                tvStatusContent.setVisibility(View.GONE);
            } else {
                tvStatusContent.setVisibility(View.VISIBLE);
                tvStatusContent.setText(mContentTxt);
            }
        }
    }

    public void setConfirmTxt(String mConfirmTxt) {
        this.mConfirmTxt = mConfirmTxt;
        if (stvStatusConfirm != null) {
            stvStatusConfirm.setText(mConfirmTxt);
        }
    }

    public void setSuccessConfirmListener(StatusSuccessConfirmListener successConfirmListener) {
        this.successConfirmListener = successConfirmListener;
    }
}
