package com.stkj.infocollect.base.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.infocollect.R;
import com.stkj.infocollect.base.model.CommonExpandItem;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.linelayout.LineLinearLayout;

/**
 * 通用下拉选择 item
 */
public class CommonExpandItemViewHolder extends CommonRecyclerViewHolder<CommonExpandItem> {

    public static final int EVENT_CLICK = 1;
    public static final int EVENT_EDIT = 2;
    public static final int EVENT_DEL = 3;

    private ImageView ivEdit;
    private TextView tvName;
    private ImageView ivDel;
    private LineLinearLayout lineLinearLayout;

    public CommonExpandItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        lineLinearLayout = (LineLinearLayout) itemView;
        ivEdit = (ImageView) findViewById(R.id.iv_edit);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivDel = (ImageView) findViewById(R.id.iv_del);
    }

    @Override
    public void initData(CommonExpandItem data) {
        lineLinearLayout.setLineBottom(getDataPosition() != (mDataAdapter.getItemCount() - 1));
        tvName.setText(data.getName());
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(CommonExpandItemViewHolder.this, EVENT_CLICK, mData);
            }
        });
        if (data.isAllowEdit()) {
            ivEdit.setVisibility(View.VISIBLE);
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataAdapter.notifyCustomItemEventListener(CommonExpandItemViewHolder.this, EVENT_EDIT, mData);
                }
            });
        } else {
            ivEdit.setVisibility(View.GONE);
        }
        if (data.isAllowDel()) {
            ivDel.setVisibility(View.VISIBLE);
            ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataAdapter.notifyCustomItemEventListener(CommonExpandItemViewHolder.this, EVENT_DEL, mData);
                }
            });
        } else {
            ivDel.setVisibility(View.GONE);
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<CommonExpandItem> {
        @Override
        public CommonRecyclerViewHolder<CommonExpandItem> createViewHolder(View itemView) {
            return new CommonExpandItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return com.stkj.infocollect.R.layout.item_common_expand_list;
        }

        @Override
        public Class<CommonExpandItem> getItemDataClass() {
            return CommonExpandItem.class;
        }
    }


}
