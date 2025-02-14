package com.stkj.cashier.pay.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.stkj.cashier.R;
import com.stkj.cashier.pay.model.TakeMealListItem;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.ratiolayout.RatioImageView;

/**
 * 订单列表菜品item
 */
public class TakeMealFoodItemViewHolder extends CommonRecyclerViewHolder<TakeMealListItem.FoodList> {

    private RatioImageView rivPic;
    private TextView tvName;
    private int cornerOffset;

    public TakeMealFoodItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        rivPic = (RatioImageView) findViewById(R.id.riv_pic);
        tvName = (TextView) findViewById(R.id.tv_name);
        cornerOffset = mContext.getResources().getDimensionPixelOffset(com.stkj.common.R.dimen.dp_4);
    }

    @Override
    public void initData(TakeMealListItem.FoodList data) {
        GlideApp.with(mItemView).load(data.getImage()).placeholder(R.mipmap.icon_food_default)
                .transform(new GranularRoundedCorners(cornerOffset, cornerOffset, 0, 0)).into(rivPic);
        tvName.setText(data.getDish_name());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<TakeMealListItem.FoodList> {
        @Override
        public CommonRecyclerViewHolder<TakeMealListItem.FoodList> createViewHolder(View itemView) {
            return new TakeMealFoodItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_take_meal_food_item;
        }

        @Override
        public Class<TakeMealListItem.FoodList> getItemDataClass() {
            return TakeMealListItem.FoodList.class;
        }
    }
}