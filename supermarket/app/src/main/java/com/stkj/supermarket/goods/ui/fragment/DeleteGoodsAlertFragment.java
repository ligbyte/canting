package com.stkj.supermarket.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsBaseInfo;

/**
 * 删除商品弹窗
 */
public class DeleteGoodsAlertFragment extends CommonAlertFragment {

    private GoodsBaseInfo goodsBaseInfo;
    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsQrcode;
    private TextView tvGoodsSpecs;

    @Override
    protected void initAlertContentView(View contentView) {
        setRightNavClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), DeleteGoodsAlertFragment.this);
            }
        });
        setCloseClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), DeleteGoodsAlertFragment.this);
            }
        });
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsQrcode = (TextView) findViewById(R.id.tv_goods_qrcode);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        if (goodsBaseInfo != null) {
            String goodsImg = goodsBaseInfo.getGoodsImg();
            String[] split = goodsImg.split(",");
            if (split.length > 0) {
                String picUrl = split[0];
                if (!TextUtils.isEmpty(picUrl)) {
                    GlideApp.with(this).load(picUrl).placeholder(R.mipmap.icon_goods_default).into(ivGoodsPic);
                } else {
                    ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
                }
            } else {
                ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
            }
            tvGoodsName.setText(goodsBaseInfo.getGoodsName());
            tvGoodsSpecs.setText("规格: " + goodsBaseInfo.getGoodsSpecStr());
            tvGoodsQrcode.setText(goodsBaseInfo.getGoodsCode());
        }
    }

    public void setGoodsBaseInfo(GoodsBaseInfo goodsBaseInfo) {
        this.goodsBaseInfo = goodsBaseInfo;
    }

    @Override
    protected int getAlertContentLayResId() {
        return R.layout.fragment_alert_delete_goods;
    }

    @Override
    protected String getAlertTitle() {
        return "删除商品";
    }
}
