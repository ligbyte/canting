package com.stkj.supermarket.coupon.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.callback.SimpleTextWatcher;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.supermarket.base.ui.widget.CommonSortImageView;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.EventBusUtils;
import com.stkj.supermarket.base.utils.JacksonUtils;
import com.stkj.supermarket.coupon.ui.adapter.GoodsCouponListInfoViewHolder;
import com.stkj.supermarket.goods.callback.OnGoodsFilterListener;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.data.RefreshSearchGoodsListEvent;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListQueryInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListResponse;
import com.stkj.supermarket.goods.service.GoodsService;
import com.stkj.supermarket.goods.ui.adapter.GoodsSaleListInfoViewHolder;
import com.stkj.supermarket.goods.ui.fragment.GoodsFilterAlertFragment;
import com.stkj.supermarket.pay.data.RefreshWeightGoodsListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * 优惠页面
 */
public class TabCouponFragment extends BaseRecyclerFragment {

    private FrameLayout flSearchEmpty;
    private ImageView ivClearSearch;
    private EditText etGoodsSearch;
    private TextView tvGoodsResetAll;
    //    private ShapeLinearLayout sllGoodsAdd;
    private ShapeLinearLayout sllGoodsFilter;
    private ShapeLinearLayout sllSetCoupon;
    private ShapeLinearLayout sllClearCoupon;
    private RecyclerView rvGoodsList;
    private CommonRecyclerAdapter goodsCouponListAdapter;
    private AppSmartRefreshLayout srlGoodsSaleList;
    private TextView tvGoodsSearch;
    private CommonSortImageView ivSortStorage;
    private CommonSortImageView ivSortExpireDate;
    private CommonSortImageView ivSortPrice;
    //网络请求查询参数
    private GoodsSaleListQueryInfo queryInfo = new GoodsSaleListQueryInfo();
    private int mLastRequestPage;

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_coupon;
    }

    private CommonSortImageView.OnSortSelectListener buildSortSelectListener(int sortField) {
        return new CommonSortImageView.OnSortSelectListener() {
            @Override
            public void onSortSelect(int sortType) {
                if (sortType == CommonSortImageView.TYPE_SORT_DEF) {
                    ivSortStorage.setDefaultSort();
                    ivSortPrice.setDefaultSort();
                    ivSortExpireDate.setDefaultSort();
                    resetRequestPage();
                    scrollGoodsListToTop();
                    queryInfo.setCurrent(1);
                    queryInfo.setCountSort("");
                    queryInfo.setExpireSort("");
                    queryInfo.setPriceSort("");
                    showLoadingDialog();
                    searchGoodsSaleList();
                } else if (sortType == CommonSortImageView.TYPE_SORT_UP) {
                    if (sortField == GoodsConstants.TYPE_SORT_STORAGE) {
                        ivSortPrice.setDefaultSort();
                        ivSortExpireDate.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("0");
                        queryInfo.setExpireSort("");
                        queryInfo.setPriceSort("");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    } else if (sortField == GoodsConstants.TYPE_SORT_EXPIRE) {
                        ivSortStorage.setDefaultSort();
                        ivSortPrice.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("");
                        queryInfo.setExpireSort("0");
                        queryInfo.setPriceSort("");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    } else if (sortField == GoodsConstants.TYPE_SORT_PRICE) {
                        ivSortStorage.setDefaultSort();
                        ivSortExpireDate.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("");
                        queryInfo.setExpireSort("");
                        queryInfo.setPriceSort("0");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    }
                } else if (sortType == CommonSortImageView.TYPE_SORT_DOWN) {
                    if (sortField == GoodsConstants.TYPE_SORT_STORAGE) {
                        ivSortPrice.setDefaultSort();
                        ivSortExpireDate.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("1");
                        queryInfo.setExpireSort("");
                        queryInfo.setPriceSort("");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    } else if (sortField == GoodsConstants.TYPE_SORT_EXPIRE) {
                        ivSortStorage.setDefaultSort();
                        ivSortPrice.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("");
                        queryInfo.setExpireSort("1");
                        queryInfo.setPriceSort("");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    } else if (sortField == GoodsConstants.TYPE_SORT_PRICE) {
                        ivSortStorage.setDefaultSort();
                        ivSortExpireDate.setDefaultSort();
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setCountSort("");
                        queryInfo.setExpireSort("");
                        queryInfo.setPriceSort("1");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    }
                }

            }
        };
    }

    @Override
    protected void initViews(View rootView) {
        flSearchEmpty = (FrameLayout) findViewById(R.id.fl_search_empty);
        tvGoodsResetAll = (TextView) findViewById(R.id.tv_goods_reset_all);
        RxView.clicks(tvGoodsResetAll)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        //获取全部商品
                        etGoodsSearch.setText("");
                        onClickSearch();
                    }
                });
        ivSortStorage = (CommonSortImageView) findViewById(R.id.iv_sort_storage);
        TextView tvSortStorage = (TextView) findViewById(R.id.tv_sort_storage);
        tvSortStorage.setOnClickListener(ivSortStorage.getSortClickListener());
        ivSortStorage.setOnSortSelectListener(buildSortSelectListener(GoodsConstants.TYPE_SORT_STORAGE));
        ivSortExpireDate = (CommonSortImageView) findViewById(R.id.iv_sort_expire_date);
        TextView tvSortExpireDate = (TextView) findViewById(R.id.tv_sort_expire_date);
        tvSortExpireDate.setOnClickListener(ivSortExpireDate.getSortClickListener());
        ivSortExpireDate.setOnSortSelectListener(buildSortSelectListener(GoodsConstants.TYPE_SORT_EXPIRE));
        ivSortPrice = (CommonSortImageView) findViewById(R.id.iv_sort_price);
        TextView tvSortPrice = (TextView) findViewById(R.id.tv_sort_price);
        tvSortPrice.setOnClickListener(ivSortPrice.getSortClickListener());
        ivSortPrice.setOnSortSelectListener(buildSortSelectListener(GoodsConstants.TYPE_SORT_PRICE));
        tvGoodsSearch = (TextView) findViewById(R.id.tv_goods_search);
        tvGoodsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearch();
            }
        });
        srlGoodsSaleList = (AppSmartRefreshLayout) findViewById(R.id.srl_goods_sale_list);
        ivClearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        ivClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoodsSearch.setText("");
            }
        });
        etGoodsSearch = (EditText) findViewById(R.id.et_goods_search);
        etGoodsSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    ivClearSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        etGoodsSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClickSearch();
                }
                return false;
            }
        });
//        sllGoodsAdd = (ShapeLinearLayout) findViewById(R.id.sll_goods_add);
        sllGoodsFilter = (ShapeLinearLayout) findViewById(R.id.sll_goods_filter);
        sllSetCoupon = (ShapeLinearLayout) findViewById(R.id.sll_set_coupon);
        sllClearCoupon = (ShapeLinearLayout) findViewById(R.id.sll_clear_coupon);
        rvGoodsList = (RecyclerView) findViewById(R.id.rv_goods_sale_list);
        goodsCouponListAdapter = new CommonRecyclerAdapter(false);
        goodsCouponListAdapter.addViewHolderFactory(new GoodsCouponListInfoViewHolder.Factory());
        goodsCouponListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsSaleListInfoViewHolder.EVENT_CLICK) {

                } else if (eventId == GoodsSaleListInfoViewHolder.EVENT_LONG_CLICK) {

                }
            }
        });
        rvGoodsList.setAdapter(goodsCouponListAdapter);
        //筛选商品
        sllGoodsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsFilterAlertFragment goodsFilterAlertFragment = new GoodsFilterAlertFragment();
                goodsFilterAlertFragment.setGoodsFilterListener(new OnGoodsFilterListener() {
                    @Override
                    public void onFilter(String goodsType, String discountType, String minPrice, String maxPrice, String minStock, String maxStock, String minExpire, String maxExpire, boolean isSelectExpire) {
                        FragmentUtils.safeRemoveFragment(getChildFragmentManager(), goodsFilterAlertFragment);
                        resetRequestPage();
                        scrollGoodsListToTop();
                        queryInfo.setCurrent(1);
                        queryInfo.setSearchType(GoodsConstants.DIALOG_SEARCH_TYPE);
                        queryInfo.setGoodsType(goodsType);
                        queryInfo.setDiscountType(discountType);
                        queryInfo.setGoodsUnitPriceMin(minPrice);
                        queryInfo.setGoodsUnitPriceMax(maxPrice);
                        queryInfo.setGoodsRealStockMin(minStock);
                        queryInfo.setGoodsRealStockMax(maxStock);
                        queryInfo.setExpireMin(minExpire);
                        queryInfo.setExpireMax(maxExpire);
                        queryInfo.setExpired(isSelectExpire ? "1" : "");
                        showLoadingDialog();
                        searchGoodsSaleList();
                    }
                });
                FragmentUtils.safeReplaceFragment(getChildFragmentManager(), goodsFilterAlertFragment, R.id.fl_coupon_second_content);
            }
        });
        //商品入库
        sllClearCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        srlGoodsSaleList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                searchGoodsSaleList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                resetRequestPage();
                resetQueryParams();
                searchGoodsSaleList();
            }
        });
    }

    private void scrollGoodsListToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvGoodsList.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);
        if (isFirstOnResume) {
            srlGoodsSaleList.autoRefresh();
        }
        waitScanQrCode();
    }

    /**
     * 处理刷新数据
     */
    private void handleNeedRefreshSearchGoodsList() {
        LogHelper.print("--EventBusUtils-handleNeedRefreshSearchGoodsList");
        if (needRefreshSearchGoodsList) {
            needRefreshSearchGoodsList = false;
            //获取全部商品
            etGoodsSearch.setText("");
            onClickSearch();
        }
    }

    //是否需要重置刷新列表数据
    private boolean needRefreshSearchGoodsList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshTabSearchGoodsList(RefreshSearchGoodsListEvent eventBus) {
        LogHelper.print("--EventBusUtils-onRefreshTabSearchGoodsList");
        needRefreshSearchGoodsList = true;
    }

    @Override
    public void onDetach() {
        EventBusUtils.unRegisterEventBus(this);
        super.onDetach();
    }

    private void onClickSearch() {
        KeyBoardUtils.hideSoftKeyboard(mActivity, etGoodsSearch);
        etGoodsSearch.clearFocus();
        String searchKey = etGoodsSearch.getText().toString();
        scrollGoodsListToTop();
        resetRequestPage();
        resetQueryParams();
        queryInfo.setSearchKey(searchKey);
        showLoadingDialog();
        searchGoodsSaleList();
    }

    public void resetQueryParams() {
        ivSortStorage.setDefaultSort();
        ivSortPrice.setDefaultSort();
        ivSortExpireDate.setDefaultSort();
        queryInfo.resetDefaultData();
    }

    /**
     * 搜索销售商品列表
     */
    private void searchGoodsSaleList() {
        int currentPage = mLastRequestPage + 1;
        queryInfo.setCurrent(currentPage);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .queryGoodsSaleList(JacksonUtils.convertObjectToMap(queryInfo))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<GoodsSaleListResponse>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GoodsSaleListResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        srlGoodsSaleList.finishRefresh();
                        srlGoodsSaleList.finishLoadMore();
                        //第一页清空旧数据
                        if (currentPage == 1) {
                            goodsCouponListAdapter.removeAllData();
                        }
                        GoodsSaleListResponse responseData = responseBaseResponse.getData();
                        if (responseBaseResponse.isSuccess() && responseData != null) {
                            List<GoodsSaleListInfo> dataRecords = responseData.getRecords();
                            if (dataRecords != null && !dataRecords.isEmpty()) {
                                if (currentPage == 1) {
                                    flSearchEmpty.setVisibility(View.GONE);
                                }
                                mLastRequestPage = currentPage;
                                goodsCouponListAdapter.addDataList(dataRecords);
                            } else {
                                if (currentPage == 1) {
                                    showNoSearchResult();
                                }
                                AppToast.toastMsg("没有更多数据了");
                            }
                        } else {
                            if (currentPage == 1) {
                                showNoSearchResult();
                            }
                            AppToast.toastMsg("没有更多数据了");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlGoodsSaleList.finishRefresh();
                        srlGoodsSaleList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

    /**
     * 显示无搜索结果
     */
    private void showNoSearchResult() {
        flSearchEmpty.setVisibility(View.VISIBLE);
        flSearchEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flSearchEmpty.setVisibility(View.GONE);
                //获取全部商品
                etGoodsSearch.setText("");
                onClickSearch();
            }
        });
    }


    private void deleteGoods(GoodsSaleListInfo goodsSaleListInfo) {
        showLoadingDialog();
        Map<String, String> params = new HashMap<>();
        params.put("id", goodsSaleListInfo.getId());
        List<Map<String, String>> goodsIdList = new ArrayList<>();
        goodsIdList.add(params);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .deleteGoods(goodsIdList)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        hideLoadingDialog();
                        if (response.isSuccess()) {
                            AppToast.toastMsg("删除商品成功");
                            goodsCouponListAdapter.removeData(goodsSaleListInfo);
                            //称重商品
                            if (goodsSaleListInfo.isWeightGoods()) {
                                EventBus.getDefault().post(new RefreshWeightGoodsListEvent());
                            }
                        } else {
                            CommonDialogUtils.showTipsDialog(mActivity, "删除商品失败!" + response.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "删除商品失败!" + e.getMessage());
                    }
                });
    }

    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (!isDetached()) {
                if (etGoodsSearch != null) {
                    etGoodsSearch.setText(data);
                    onClickSearch();
                }
            }
        }

        @Override
        public void onScanQRCodeError(String message) {
            if (!isDetached()) {
                CommonDialogUtils.showTipsDialog(mActivity, message);
            }
        }
    };

    //二维码扫码 商品 start
    @Override
    public void onPause() {
        super.onPause();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mScanQRCodeListener);
    }

    /**
     * 等待扫码入库商品
     */
    private void waitScanQrCode() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mScanQRCodeListener);
    }
    //二维码扫码 商品 end

}
