package com.stkj.supermarketmini.goods.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.goods.model.GoodsIdBaseListInfo;

/**
 * 商品自动搜索列表
 */
public class GoodsAutoSearchInfoViewHolder extends CommonRecyclerViewHolder<GoodsIdBaseListInfo> {

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsQrcode;

    public GoodsAutoSearchInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsQrcode = (TextView) findViewById(R.id.tv_goods_qrcode);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(GoodsIdBaseListInfo data) {
        String goodsImg = data.getGoodsImg();
        String[] split = goodsImg.split(",");
        if (split.length > 0) {
            String picUrl = split[0];
            if (!TextUtils.isEmpty(picUrl)) {
                GlideApp.with(mContext).load(picUrl).placeholder(R.mipmap.icon_goods_default).into(ivGoodsPic);
            } else {
                ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
            }
        } else {
            ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
        }
        tvGoodsName.setText(data.getGoodsName());
        tvGoodsQrcode.setText("条码: " + data.getGoodsCode());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsIdBaseListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsIdBaseListInfo> createViewHolder(View itemView) {
            return new GoodsAutoSearchInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_auto_search_info;
        }

        @Override
        public Class<GoodsIdBaseListInfo> getItemDataClass() {
            return GoodsIdBaseListInfo.class;
        }
    }
}
