package com.stkj.supermarketmini.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarketmini.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.base.utils.JacksonUtils;
import com.stkj.supermarketmini.goods.callback.GoodsDetailViewController;
import com.stkj.supermarketmini.goods.callback.OnGoodsCateSpecListener;
import com.stkj.supermarketmini.goods.callback.OnRequestQrCodeDetailListener;
import com.stkj.supermarketmini.goods.callback.OnScanQRCodeListener;
import com.stkj.supermarketmini.goods.helper.GoodsCateSpecHelper;
import com.stkj.supermarketmini.goods.helper.GoodsQrCodeHelper;
import com.stkj.supermarketmini.goods.model.GoodsBatchListResponse;
import com.stkj.supermarketmini.goods.model.GoodsEditBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsIdRequestPageParams;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.model.RefreshSearchGoodsListEvent;
import com.stkj.supermarketmini.goods.service.GoodsService;
import com.stkj.supermarketmini.goods.ui.adapter.GoodsDetailBatchInfoViewHolder;
import com.stkj.supermarketmini.goods.ui.adapter.GoodsDetailViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * 商品详情页面
 */
public class GoodsDetailFragment extends BaseRecyclerFragment {
    private AppSmartRefreshLayout srlGoodsDetailList;
    private RecyclerView rvGoodsDetail;
    private ShapeTextView stvEditGoods;
    private String goodsId;
    private CommonRecyclerAdapter mAdapter;
    private GoodsIdRequestPageParams batchPageParams = new GoodsIdRequestPageParams();
    private int mLastRequestPage;
    //禁止编辑商品信息
    private boolean forbidEditGoodsInfo;

    public void setForbidEditGoodsInfo(boolean forbidEditGoodsInfo) {
        this.forbidEditGoodsInfo = forbidEditGoodsInfo;
    }

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_detail;
    }

    @Override
    protected void initViews(View rootView) {
        srlGoodsDetailList = (AppSmartRefreshLayout) findViewById(R.id.srl_goods_detail_list);
        srlGoodsDetailList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getGoodsBatchInfo();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                resetRequestPage();
                showLoadingDialog();
                getGoodsDetail();
            }
        });
        findViewById(R.id.iv_goods_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsDetailFragment.this);
            }
        });
        stvEditGoods = (ShapeTextView) findViewById(R.id.stv_edit_goods);
        if (forbidEditGoodsInfo) {
            stvEditGoods.setVisibility(View.GONE);
        } else {
            stvEditGoods.setVisibility(View.VISIBLE);
            setEditButtonGoodsMode(0);
        }
        rvGoodsDetail = (RecyclerView) findViewById(R.id.rv_goods_detail);
        mAdapter = new CommonRecyclerAdapter(false);
        mAdapter.addViewHolderFactory(new GoodsDetailViewHolder.Factory());
        mAdapter.addViewHolderFactory(new GoodsDetailBatchInfoViewHolder.Factory());
        rvGoodsDetail.setAdapter(mAdapter);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            showLoadingDialog();
            getGoodsDetail();
        }
    }

    /**
     * 获取商品详情
     */
    private void getGoodsDetail() {
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .getGoodsDetail(goodsId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<GoodsSaleListInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GoodsSaleListInfo> response) {
                        hideLoadingDialog();
                        srlGoodsDetailList.finishRefresh();
                        srlGoodsDetailList.finishLoadMore();
                        GoodsSaleListInfo data = response.getData();
                        if (response.isSuccess() && data != null) {
                            mAdapter.removeAllData();
                            mAdapter.addData(data);
                            getGoodsBatchInfo();
                        } else {
                            showGetDetailError(response.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlGoodsDetailList.finishRefresh();
                        srlGoodsDetailList.finishLoadMore();
                        showGetDetailError(e.getMessage());
                    }
                });
    }

    /**
     * 获取商品批次信息
     */
    private void getGoodsBatchInfo() {
        int currentPage = mLastRequestPage + 1;
        batchPageParams.setCurrent(currentPage);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .getGoodsBatchList(JacksonUtils.convertObjectToMap(batchPageParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<GoodsBatchListResponse>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GoodsBatchListResponse> goodsBatchListResponse) {
                        srlGoodsDetailList.finishRefresh();
                        srlGoodsDetailList.finishLoadMore();
                        if (goodsBatchListResponse.isSuccess()) {
                            GoodsBatchListResponse data = goodsBatchListResponse.getData();
                            if (data != null && data.records != null) {
                                if (!data.records.isEmpty()) {
                                    mLastRequestPage = currentPage;
                                    if (currentPage == 1) {
                                        mAdapter.putAdapterPrivateData(GoodsDetailBatchInfoViewHolder.GOODS_BATCH_TOTAL_COUNT, data.getTotal());
                                    }
                                    mAdapter.addDataList(data.records);
                                } else {
                                    if (currentPage != 1) {
                                        AppToast.toastMsg("没有更多数据了");
                                    }
                                }
                            } else {
                                if (currentPage != 1) {
                                    AppToast.toastMsg("没有更多数据了");
                                }
                            }
                        } else {
                            AppToast.toastMsg("商品批次信息获取失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        srlGoodsDetailList.finishRefresh();
                        srlGoodsDetailList.finishLoadMore();
                        AppToast.toastMsg("商品批次信息获取失败：" + e.getMessage());
                    }
                });
    }

    private void showGetDetailError(String msg) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("获取详情失败!" + msg)
                .setLeftNavTxt("重试")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        getGoodsDetail();
                    }
                })
                .setRightNavTxt("返回")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsDetailFragment.this);
                    }
                }).show(mActivity);
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
        this.batchPageParams.setGoodsId(goodsId);
    }

    private GoodsDetailViewController getGoodsDetailViewController() {
        Object adapterPrivateData = mAdapter.getAdapterPrivateData(GoodsDetailViewHolder.VIEW_CONTROLLER);
        if (adapterPrivateData instanceof GoodsDetailViewController) {
            return (GoodsDetailViewController) adapterPrivateData;
        }
        return null;
    }

    private void saveEditGoodsInfo() {
        GoodsDetailViewController detailViewController = getGoodsDetailViewController();
        if (detailViewController != null) {
            GoodsEditBaseInfo goodsEditBaseInfo = detailViewController.getGoodsDetailEditInfo();
            if (goodsEditBaseInfo == null) {
                return;
            }
            goodsEditBaseInfo.setId(goodsId);
            showLoadingDialog();
            RetrofitManager.INSTANCE.getDefaultRetrofit()
                    .create(GoodsService.class)
                    .editGoods(goodsEditBaseInfo)
                    .compose(RxTransformerUtils.mainSchedulers())
                    .to(AutoDisposeUtils.onDestroyDispose(this))
                    .subscribe(new DefaultObserver<BaseResponse<String>>() {
                        @Override
                        protected void onSuccess(BaseResponse<String> response) {
                            if (response.isSuccess()) {
                                onSaveEditGoodsSuccess(goodsEditBaseInfo);
                            } else {
                                hideLoadingDialog();
                                onSaveEditGoodsError(response.getMsg());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideLoadingDialog();
                            onSaveEditGoodsError(e.getMessage());
                        }
                    });
        }
    }

    /**
     * 设置右上角编辑按钮状态
     *
     * @param mode 0: 编辑信息 1: 保存
     */
    private void setEditButtonGoodsMode(int mode) {
        if (mode == 1) {
            stvEditGoods.setText("保存");
            stvEditGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //保存信息
                    saveEditGoodsInfo();
                }
            });
            requestGoodsCateSpec();
        } else {
            stvEditGoods.setText("编辑");
            stvEditGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoodsDetailViewController detailViewController = getGoodsDetailViewController();
                    if (detailViewController != null) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) rvGoodsDetail.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0, 0);
                        detailViewController.setGoodsDetailEditMode();
                    }
                    setEditButtonGoodsMode(1);
                }
            });
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
     * 保存编辑商品成功
     */
    private void onSaveEditGoodsSuccess(GoodsEditBaseInfo goodsEditBaseInfo) {
        AppToast.toastMsg("保存成功!");
        setEditButtonGoodsMode(0);
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvGoodsDetail.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
        resetRequestPage();
        getGoodsDetail();
        //刷新tab页商品列表
        EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
    }

    private void onSaveEditGoodsError(String msg) {
        CommonDialogUtils.showTipsDialog(mActivity, "保存失败!" + msg);
    }


    //二维码扫码 商品 start
    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (!isDetached()) {
                String editGoodsTxt = stvEditGoods.getText().toString();
                if (TextUtils.equals(editGoodsTxt, "保存")) {
                    requestQrCodeDetail(data);
                }
            }
        }

        @Override
        public void onScanQRCodeError(String message) {
            if (!isDetached()) {
                String editGoodsTxt = stvEditGoods.getText().toString();
                if (TextUtils.equals(editGoodsTxt, "保存")) {
                    CommonDialogUtils.showTipsDialog(mActivity, message);
                }
            }
        }
    };

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
                    Object obj = mAdapter.getData(0);
                    if (obj instanceof GoodsSaleListInfo) {
                        GoodsDetailViewController detailViewController = getGoodsDetailViewController();
                        if (detailViewController != null) {
                            detailViewController.setGoodsQrCodeInfo(data);
                        }
                    }
                }
            }

            @Override
            public void onRequestDetailError(String qrcode, String msg) {
                if (!isDetached()) {
                    hideLoadingDialog();
                    Object obj = mAdapter.getData(0);
                    if (obj instanceof GoodsSaleListInfo) {
                        GoodsDetailViewController detailViewController = getGoodsDetailViewController();
                        if (detailViewController != null) {
                            GoodsQrCodeDetail goodsQrCodeDetail = new GoodsQrCodeDetail();
                            goodsQrCodeDetail.setCode(qrcode);
                            detailViewController.setGoodsQrCodeInfo(goodsQrCodeDetail);
                        }
                    }
                }
            }
        });
    }
}
