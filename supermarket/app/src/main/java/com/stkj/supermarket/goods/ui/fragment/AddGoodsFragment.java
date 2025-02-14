package com.stkj.supermarket.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.goods.callback.OnGoodsCateSpecListener;
import com.stkj.supermarket.goods.callback.OnRequestQrCodeDetailListener;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.data.RefreshSearchGoodsListEvent;
import com.stkj.supermarket.goods.helper.GoodsCateSpecHelper;
import com.stkj.supermarket.goods.helper.GoodsQrCodeHelper;
import com.stkj.supermarket.goods.model.GoodsBaseInfo;
import com.stkj.supermarket.goods.model.GoodsCate;
import com.stkj.supermarket.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarket.goods.model.GoodsSpec;
import com.stkj.supermarket.goods.service.GoodsService;
import com.stkj.supermarket.goods.ui.widget.GoodsDetailInfoLayout;
import com.stkj.supermarket.pay.data.RefreshWeightGoodsListEvent;

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
        svContent = (ScrollView) findViewById(R.id.sv_content);
        goodsDetailLay = (GoodsDetailInfoLayout) findViewById(R.id.goods_detail_lay);
        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AddGoodsFragment.this);
            }
        };
        ShapeTextView stvCancelAdd = (ShapeTextView) findViewById(R.id.stv_cancel_add);
        stvCancelAdd.setOnClickListener(backClickListener);
        findViewById(R.id.iv_add_goods_back).setOnClickListener(backClickListener);
        ShapeTextView stvContinueAdd = (ShapeTextView) findViewById(R.id.stv_continue_add);
        stvContinueAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGoodsCateSpec();
                svContent.scrollTo(0, 0);
                goodsDetailLay.resetAllLayoutAndData();
            }
        });
        ShapeTextView stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoodsStorage();
            }
        });
    }


    //二维码扫码 商品 start
    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (!isDetached()) {
                requestQrCodeDetail(data);
            }
        }

        @Override
        public void onScanQRCodeError(String message) {
            if (!isDetached()) {
                CommonDialogUtils.showTipsDialog(mActivity, message);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mScanQRCodeListener);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            requestGoodsCateSpec();
        }
        waitScanQrCode();
    }

    /**
     * 等待扫码新增商品
     */
    private void waitScanQrCode() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mScanQRCodeListener);
    }
    //二维码扫码 商品 end

    /**
     * 请求条码详细信息
     */
    private void requestQrCodeDetail(String qrcode) {
        showLoadingDialog();
        GoodsQrCodeHelper.requestCodeDetail(qrcode, this, new OnRequestQrCodeDetailListener() {
            @Override
            public void onRequestDetailSuccess(GoodsQrCodeDetail data) {
                if (!isDetached()) {
                    hideLoadingDialog();
                    goodsDetailLay.setGoodsName(data.getGoodsName());
                    goodsDetailLay.setGoodsCode(data.getCode());
                    goodsDetailLay.setGoodsPic(data.getImg());
                    goodsDetailLay.setGoodsComment(data.getSpec());
                }
            }

            @Override
            public void onRequestDetailError(String qrcode, String msg) {
                if (!isDetached()) {
                    hideLoadingDialog();
                    goodsDetailLay.setGoodsName("");
                    goodsDetailLay.setGoodsCode(qrcode);
                    goodsDetailLay.setGoodsPic("");
                    goodsDetailLay.setGoodsComment("");
                }
            }
        });
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
                goodsDetailFragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            waitScanQrCode();
                        }
                    }
                });
                goodsDetailFragment.setGoodsId(goodsId);
                FragmentUtils.safeAddFragment(getParentFragmentManager(), goodsDetailFragment, R.id.fl_goods_second_content);
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
        FragmentUtils.safeAddFragment(getParentFragmentManager(), storageAlertFragment, R.id.fl_goods_second_content);
        //刷新tab页商品列表
        EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
        //称重商品
        if (goodsBaseInfo.isWeightGoods()) {
            EventBus.getDefault().post(new RefreshWeightGoodsListEvent());
        }
    }

    private void onSaveStorageError(String msg) {
        CommonDialogUtils.showTipsDialog(mActivity, "保存失败!" + msg);
    }
}
