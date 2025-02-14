package com.stkj.supermarketmini.goods.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.goods.callback.GoodsDetailViewController;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.model.GoodsCate;
import com.stkj.supermarketmini.goods.model.GoodsEditBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSpec;
import com.stkj.supermarketmini.goods.ui.weight.GoodsDetailInfoLayout;

public class GoodsDetailViewHolder extends CommonRecyclerViewHolder<GoodsSaleListInfo> implements GoodsDetailViewController {

    public static final String VIEW_CONTROLLER = "view_controller";

    private GoodsDetailInfoLayout goodsDetailLay;
    private TextView tvGoodsBatchTitle;

    public GoodsDetailViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        goodsDetailLay = (GoodsDetailInfoLayout) findViewById(R.id.goods_detail_info_layout);
        tvGoodsBatchTitle = (TextView) findViewById(R.id.tv_goods_batch_title);
    }

    @Override
    public void initData(GoodsSaleListInfo data) {
        mDataAdapter.putAdapterPrivateData(VIEW_CONTROLLER, this);
        goodsDetailLay.loadGoodsBaseInfo(data);
        String builder = "平均进货价：¥" +
                data.getInAvgPrice() +
                " 丨当前库存：" +
                data.getGoodsRealStock() +
                "\n总销量：" +
                data.getSaleSumCount() +
                "丨 总销售额：¥" +
                data.getSaleSumPrice();
        tvGoodsBatchTitle.setText(builder);
    }

    @Override
    public GoodsEditBaseInfo getGoodsDetailEditInfo() {
        //商品名称
        String goodsName = goodsDetailLay.getGoodsName();
        if (TextUtils.isEmpty(goodsName)) {
            AppToast.toastMsg("商品名称不能为空!");
            return null;
        }
        //商品分类
        GoodsCate goodsCate = goodsDetailLay.getGoodsCate();
        if (TextUtils.isEmpty(goodsCate.getName())) {
            AppToast.toastMsg("请选择商品分类");
            return null;
        }
        //商品名称
        int goodsType = goodsDetailLay.getGoodsType();
        //条形码
        String goodsCode = goodsDetailLay.getGoodsCode();
        if (TextUtils.isEmpty(goodsCode) || goodsCode.length() < 7) {
            AppToast.toastMsg("商品条码最小为7位!");
            return null;
        }
        //零售价
        String goodsUnitPrice = goodsDetailLay.getGoodsUnitPrice();
        if (TextUtils.isEmpty(goodsUnitPrice)) {
            AppToast.toastMsg("商品零售价不能为空!");
            return null;
        }
        //商品图片
        String uploadPicUrl = goodsDetailLay.getUploadPicUrl();
        //商品规格
        GoodsSpec goodsSpec = goodsDetailLay.getGoodsSpec();
        if (goodsType != GoodsConstants.TYPE_GOODS_WEIGHT && TextUtils.isEmpty(goodsSpec.getName())) {
            AppToast.toastMsg("请选择商品规格");
            return null;
        }
        //备注
        String goodsNote = goodsDetailLay.getGoodsNote();
        GoodsEditBaseInfo goodsEditBaseInfo = new GoodsEditBaseInfo();
        goodsEditBaseInfo.setGoodsName(goodsName);
        //商品分类
        goodsEditBaseInfo.setGoodsCategory(goodsCate.getId());
        goodsEditBaseInfo.setGoodsCategoryStr(goodsCate.getName());
        goodsEditBaseInfo.setGoodsType(goodsType);
        goodsEditBaseInfo.setGoodsCode(goodsCode);
        goodsEditBaseInfo.setGoodsUnitPrice(goodsUnitPrice);
        goodsEditBaseInfo.setGoodsImg(uploadPicUrl);
        //商品规格
        if (!TextUtils.isEmpty(goodsSpec.getId())) {
            goodsEditBaseInfo.setGoodsSpec(goodsSpec.getId());
            goodsEditBaseInfo.setGoodsSpecStr(goodsSpec.getName());
        }
        goodsEditBaseInfo.setGoodsMinStock(goodsDetailLay.getGoodsMinInventory());
        goodsEditBaseInfo.setGoodsLossRate(goodsDetailLay.getGoodsLossRate());
        goodsEditBaseInfo.setGoodsNote(goodsNote);
        return goodsEditBaseInfo;
    }

    @Override
    public void setGoodsDetailEditMode() {
        goodsDetailLay.setGoodsLayoutType(GoodsDetailInfoLayout.LAYOUT_TYPE_DETAIL_EDIT);
    }

    @Override
    public void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo) {
        mData.setGoodsName(goodsQrCodeInfo.getGoodsName());
        mData.setGoodsCode(goodsQrCodeInfo.getCode());
        mData.setGoodsImg(goodsQrCodeInfo.getImg());
        mData.setGoodsNote(goodsQrCodeInfo.getSpec());
        goodsDetailLay.setGoodsName(goodsQrCodeInfo.getGoodsName());
        goodsDetailLay.setGoodsCode(goodsQrCodeInfo.getCode());
        goodsDetailLay.setGoodsPic(goodsQrCodeInfo.getImg());
        goodsDetailLay.setGoodsComment(goodsQrCodeInfo.getSpec());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsSaleListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsSaleListInfo> createViewHolder(View itemView) {
            return new GoodsDetailViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_detail_info_title;
        }

        @Override
        public Class<GoodsSaleListInfo> getItemDataClass() {
            return GoodsSaleListInfo.class;
        }
    }
}
