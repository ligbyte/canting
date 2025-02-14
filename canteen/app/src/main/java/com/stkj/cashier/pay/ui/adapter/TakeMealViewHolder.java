package com.stkj.cashier.pay.ui.adapter;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.cashier.R;
import com.stkj.cashier.pay.model.TakeMealListItem;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

/**
 * 取餐订单列表
 */
public class TakeMealViewHolder extends CommonRecyclerViewHolder<TakeMealListItem> {

    public static final int EVENT_CLICK_TAKE_MEAL = 1;

    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvTakeCode;
    private RecyclerView rvFoodList;
    private ShapeTextView stvTakeMeal;
    private CommonRecyclerAdapter foodsAdapter;

    public TakeMealViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivUserAvatar = (ImageView) findViewById(R.id.iv_user_avatar);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        tvTakeCode = (TextView) findViewById(R.id.tv_take_code);
        rvFoodList = (RecyclerView) findViewById(R.id.rv_food_list);
        int leftOffset = mContext.getResources().getDimensionPixelOffset(com.stkj.common.R.dimen.dp_5);
        int bottomOffset = mContext.getResources().getDimensionPixelOffset(com.stkj.common.R.dimen.dp_10);
        rvFoodList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(leftOffset, 0, leftOffset, bottomOffset);
            }
        });
        foodsAdapter = new CommonRecyclerAdapter(false);
        foodsAdapter.addViewHolderFactory(new TakeMealFoodItemViewHolder.Factory());
        rvFoodList.setAdapter(foodsAdapter);
        stvTakeMeal = (ShapeTextView) findViewById(R.id.stv_take_meal);
        stvTakeMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(TakeMealViewHolder.this, EVENT_CLICK_TAKE_MEAL, mData);
            }
        });
    }

    @Override
    public void initData(TakeMealListItem data) {
        GlideApp.with(mContext).load(data.getUser_Face()).placeholder(R.mipmap.icon_take_meal_person)
                .circleCrop().into(ivUserAvatar);
        tvUserName.setText(data.getFull_Name());
        tvUserPhone.setText(data.getUser_Tel());
        tvTakeCode.setText("取餐号(" + data.getTakeCode() + ")");
        foodsAdapter.removeAllData();
        foodsAdapter.addDataList(data.getFoodList());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<TakeMealListItem> {
        @Override
        public CommonRecyclerViewHolder<TakeMealListItem> createViewHolder(View itemView) {
            return new TakeMealViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_take_meal;
        }

        @Override
        public Class<TakeMealListItem> getItemDataClass() {
            return TakeMealListItem.class;
        }
    }
}