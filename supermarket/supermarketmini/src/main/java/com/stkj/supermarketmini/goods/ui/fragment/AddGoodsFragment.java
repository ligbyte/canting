package com.stkj.supermarketmini.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.goods.callback.OnGoodsCateSpecListener;
import com.stkj.supermarketmini.goods.callback.OnRequestQrCodeDetailListener;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.helper.GoodsCateSpecHelper;
import com.stkj.supermarketmini.goods.helper.GoodsQrCodeHelper;
import com.stkj.supermarketmini.goods.model.GoodsBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsCate;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarketmini.goods.model.GoodsSpec;
import com.stkj.supermarketmini.goods.model.RefreshSearchGoodsListEvent;
import com.stkj.supermarketmini.goods.service.GoodsService;
import com.stkj.supermarketmini.goods.ui.weight.GoodsDetailInfoLayout;

import org.greenrobot.eventbus.EventBus;

/**
 * 增加商品页面
 */
public class AddGoodsFragment extends BaseRecyclerFragment {
    private ScrollView svContent;
    private GoodsDetailInfoLayout goodsDetailLay;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add_goods;
    }

    @Override
    protected void initViews(View rootView) {
        findViewById(R.id.iv_goods_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AddGoodsFragment.this);
            }
        });
        svContent = (ScrollView) findViewById(R.id.sv_content);
        goodsDetailLay = (GoodsDetailInfoLayout) findViewById(R.id.goods_detail_lay);
        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AddGoodsFragment.this);
            }
        };
        ShapeTextView stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoodsStorage();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            requestGoodsCateSpec();
        }
    }

    /**
     * 请求商品分类规格
     */
    private void requestGoodsCateSpec() {
        showLoadingDialog();
        GoodsCateSpecHelper goodsCateSpecHelper = mActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
        goodsCateSpecHelper.requestAllList(new OnGoodsCateSpecListener() {
            @Override
            public void onGetCateSpecListEnd() {
                hideLoadingDialog();
            }

            @Override
            public void onGetCateSpecError(String msg) {
                hideLoadingDialog();
            }
        });
    }

    /**
     * 保存商品入库
     */
    public void saveGoodsStorage() {
        //商品名称
        String goodsName = goodsDetailLay.getGoodsName();
        if (TextUtils.isEmpty(goodsName)) {
            AppToast.toastMsg("商品名称不能为空!");
            return;
        }
        //商品分类
        GoodsCate goodsCate = goodsDetailLay.getGoodsCate();
        if (TextUtils.isEmpty(goodsCate.getName())) {
            AppToast.toastMsg("请选择商品分类");
            return;
        }
        //商品名称
        int goodsType = goodsDetailLay.getGoodsType();
        //条形码
        String goodsCode = goodsDetailLay.getGoodsCode();
        if (TextUtils.isEmpty(goodsCode) || goodsCode.length() < 7) {
            AppToast.toastMsg("商品条码最小为7位!");
            return;
        }
        //零售价
        String goodsUnitPrice = goodsDetailLay.getGoodsUnitPrice();
        if (TextUtils.isEmpty(goodsUnitPrice)) {
            AppToast.toastMsg("商品零售价不能为空!");
            return;
        }
        //商品图片
        String uploadPicUrl = goodsDetailLay.getUploadPicUrl();
        //商品规格
        GoodsSpec goodsSpec = goodsDetailLay.getGoodsSpec();
        if (goodsType != GoodsConstants.TYPE_GOODS_WEIGHT && TextUtils.isEmpty(goodsSpec.getName())) {
            AppToast.toastMsg("请选择商品规格");
            return;
        }
        //初始库存
        String goodsInitStock = goodsDetailLay.getGoodsInitStock();
        //进货价格
        String goodsWholesalePrice = goodsDetailLay.getGoodsWholesalePrice();
        if (!TextUtils.isEmpty(goodsInitStock) && TextUtils.isEmpty(goodsWholesalePrice)) {
            CommonDialogUtils.showTipsDialog(mActivity, "请填写进货价格");
            return;
        }
        if (TextUtils.isEmpty(goodsInitStock) && !TextUtils.isEmpty(goodsWholesalePrice)) {
            CommonDialogUtils.showTipsDialog(mActivity, "请填写初始库存");
            return;
        }
        //生产日期
        String goodsProductDate = goodsDetailLay.getGoodsProductDate();
        //保质期
        String expireDays = goodsDetailLay.getExpireDays();
        if (!TextUtils.isEmpty(goodsProductDate)) {
            if (TextUtils.isEmpty(goodsInitStock) || TextUtils.isEmpty(goodsWholesalePrice)) {
                CommonDialogUtils.showTipsDialog(mActivity, "请填写进货价格和初始库存");
                return;
            }
            if (TextUtils.isEmpty(expireDays)) {
                CommonDialogUtils.showTipsDialog(mActivity, "请填写保质期");
                return;
            }
        }
        if (!TextUtils.isEmpty(expireDays)) {
            if (TextUtils.isEmpty(goodsInitStock) || TextUtils.isEmpty(goodsWholesalePrice)) {
                CommonDialogUtils.showTipsDialog(mActivity, "请填写进货价格和初始库存");
                return;
            }
            if (TextUtils.isEmpty(goodsProductDate)) {
                CommonDialogUtils.showTipsDialog(mActivity, "请填写生产日期");
                return;
            }
        }
        //备注
        String goodsNote = goodsDetailLay.getGoodsNote();
        GoodsBaseInfo goodsBaseInfo = new GoodsBaseInfo();
        goodsBaseInfo.setGoodsName(goodsName);
        //商品分类
        goodsBaseInfo.setGoodsCategory(goodsCate.getId());
        goodsBaseInfo.setGoodsCategoryStr(goodsCate.getName());
        goodsBaseInfo.setGoodsType(goodsType);
        goodsBaseInfo.setGoodsCode(goodsCode);
        goodsBaseInfo.setGoodsUnitPrice(goodsUnitPrice);
        goodsBaseInfo.setGoodsImg(uploadPicUrl);
        //商品规格
        if (!TextUtils.isEmpty(goodsSpec.getId())) {
            goodsBaseInfo.setGoodsSpec(goodsSpec.getId());
            goodsBaseInfo.setGoodsSpecStr(goodsSpec.getName());
        }
        goodsBaseInfo.setGoodsInitStock(goodsInitStock);
        goodsBaseInfo.setProductDate(goodsProductDate);
        goodsBaseInfo.setGoodsInitPrice(goodsWholesalePrice);
        goodsBaseInfo.setExpireDays(expireDays);
        goodsBaseInfo.setGoodsMinStock(goodsDetailLay.getGoodsMinInventory());
        goodsBaseInfo.setGoodsLossRate(goodsDetailLay.getGoodsLossRate());
        goodsBaseInfo.setGoodsNote(goodsNote);
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .addGoods(goodsBaseInfo)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        hideLoadingDialog();
                        if (response.isSuccess()) {
                            onSaveStorageSuccess(response.getData(), goodsBaseInfo);
                        } else {
                            onSaveStorageError(response.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        onSaveStorageError(e.getMessage());
                    }
                });
    }

    /**
     * 增加商品成功
     */
    private void onSaveStorageSuccess(String goodsId, GoodsBaseInfo goodsBaseInfo) {
        GoodsStorageAlertFragment storageAlertFragment = new GoodsStorageAlertFragment();
        storageAlertFragment.setExtraCloseListener(new CommonAlertFragment.OnSweetClickListener() {
            @Override
            public void onClick() {
                //返回到其他页
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AddGoodsFragment.this);
            }
        });
        storageAlertFragment.setRightNavClickListener(new CommonAlertFragment.OnSweetClickListener() {
            @Override
            public void onClick() {
                //继续添加
                svContent.scrollTo(0, 0);
                goodsDetailLay.resetAllLayoutAndData();
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), storageAlertFragment);
                //查看详情
                GoodsDetailFragment goodsDetailFragment = new GoodsDetailFragment();
                goodsDetailFragment.setGoodsId(goodsId);
                mActivity.addContentPlaceHolderFragment(goodsDetailFragment);
            }
        });
        storageAlertFragment.setLeftNavClickListener(new CommonAlertFragment.OnSweetClickListener() {
            @Override
            public void onClick() {
                //继续添加
                svContent.scrollTo(0, 0);
                goodsDetailLay.resetAllLayoutAndData();
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), storageAlertFragment);
            }
        });
        mActivity.addContentPlaceHolderFragment(storageAlertFragment);
        //刷新tab页商品列表
        EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
    }

    private void onSaveStorageError(String msg) {
        CommonDialogUtils.showTipsDialog(mActivity, "保存失败!" + msg);
    }
}
