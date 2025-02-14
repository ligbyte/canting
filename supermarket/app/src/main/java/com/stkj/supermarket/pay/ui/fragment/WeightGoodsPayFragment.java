package com.stkj.supermarket.pay.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.IndexBarView;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.deviceinterface.callback.OnReadWeightListener;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.callback.SimpleTextWatcher;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.EventBusUtils;
import com.stkj.supermarket.base.utils.PriceUtils;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.service.GoodsService;
import com.stkj.supermarket.login.helper.LoginHelper;
import com.stkj.supermarket.pay.data.RefreshWeightGoodsListEvent;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.pay.model.GoodsWeightIndexInfo;
import com.stkj.supermarket.pay.model.GoodsWeightItemInfo;
import com.stkj.supermarket.pay.ui.adapter.GoodsWeightIndexViewHolder;
import com.stkj.supermarket.pay.ui.adapter.GoodsWeightItemViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.functions.Function;
import kotlin.Unit;

/**
 * 称重商品支付
 */
public class WeightGoodsPayFragment extends BaseRecyclerFragment {

    private AppBarLayout ablRecentWeight;
    private ImageView ivClose;
    private ShapeSelectTextView stvRecent;
    private IndexBarView ibvIndex;
    private RecyclerView rvContent;
    private RecyclerView rvRecentWeightGoods;
    private TextView tvWeightCount;
    private TextView tvWeightUnit;
    private TextView tvWeightPrice;
    private ShapeTextView stvRefresh;
    private ShapeTextView stvConfirm;
    private ShapeTextView stvContinue;
    private CommonRecyclerAdapter mGoodsListAdapter;
    private CommonRecyclerAdapter mRecentGoodsListAdapter;
    private GoodsWeightItemInfo mSelectedGoodsWeight;
    private OnWeightGoodsPayListener weightGoodsPayListener;
    private GoodsSaleListInfo needSelectedGoods;

    public void setWeightGoodsPayListener(OnWeightGoodsPayListener weightGoodsPayListener) {
        this.weightGoodsPayListener = weightGoodsPayListener;
    }

    public void setNeedSelectedGoods(GoodsSaleListInfo needSelectedGoods) {
        this.needSelectedGoods = needSelectedGoods;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_weight_pay;
    }

    @Override
    protected void initViews(View rootView) {
        tvWeightUnit = (TextView) findViewById(R.id.tv_weight_unit);
        ablRecentWeight = (AppBarLayout) findViewById(R.id.abl_recent_weight);
        ablRecentWeight.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (stvRecent.isShapeSelect()) {
                        stvRecent.setShapeSelect(false);
                    }
                } else {
                    if (!stvRecent.isShapeSelect()) {
                        stvRecent.setShapeSelect(true);
                    }
                }
            }
        });
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeDetachFragment(getParentFragmentManager(), WeightGoodsPayFragment.this);
            }
        });
        stvRecent = (ShapeSelectTextView) findViewById(R.id.stv_recent);
        stvRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ablRecentWeight.setExpanded(true, false);
                GridLayoutManager layoutManager = (GridLayoutManager) rvContent.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        });
        ibvIndex = (IndexBarView) findViewById(R.id.ibv_index);
        ibvIndex.setOnTouchIndexBarListener(new IndexBarView.OnTouchIndexBarListener() {
            @Override
            public void onTouchIndexBar(int index, String content) {
                int itemDataIndex = mGoodsListAdapter.findItemDataIndex(new GoodsWeightIndexInfo(content));
                if (itemDataIndex != -1) {
                    ablRecentWeight.setExpanded(false, false);
                    GridLayoutManager layoutManager = (GridLayoutManager) rvContent.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.scrollToPositionWithOffset(itemDataIndex, 0);
                    }
                }
            }
        });
        rvContent = (RecyclerView) findViewById(R.id.rv_weight_goods_content);
        rvRecentWeightGoods = (RecyclerView) findViewById(R.id.rv_recent_weight_goods);
        mGoodsListAdapter = new CommonRecyclerAdapter(false);
        tvWeightCount = (TextView) findViewById(R.id.tv_weight_count);
        tvWeightCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有修改权限
                if (!LoginHelper.INSTANCE.hasPermissionChangePrice()) {
                    return;
                }
                boolean isSupportReadWeight = DeviceManager.INSTANCE.getDeviceInterface().isSupportReadWeight();
                //不支持称重则可以手动输入
                if (isSupportReadWeight) {
                    return;
                }
                CommonInputDialogFragment.build()
                        .setTitle("请输入重量")
                        .setInputContent(tvWeightCount.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL)
                        .setNeedLimitNumber(true)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                tvWeightCount.setText(input);
                            }
                        }).show(mActivity);
            }
        });
        tvWeightCount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                refreshWeightGoodsPrice();
            }
        });
        tvWeightPrice = (TextView) findViewById(R.id.tv_weight_price);
        tvWeightPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有修改权限
                if (!LoginHelper.INSTANCE.hasPermissionChangePrice()) {
                    return;
                }
                double weightGoodsCount = getWeightGoodsCount();
                if (weightGoodsCount <= 0) {
                    return;
                }
                boolean isSupportReadWeight = DeviceManager.INSTANCE.getDeviceInterface().isSupportReadWeight();
                //不支持称重则可以手动输入
                if (isSupportReadWeight) {
                    return;
                }
                CommonInputDialogFragment.build()
                        .setTitle("请输入价格")
                        .setInputContent(tvWeightPrice.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL)
                        .setNeedLimitNumber(true)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                tvWeightPrice.setText(input);
                            }
                        }).show(mActivity);
            }
        });
        stvRefresh = (ShapeTextView) findViewById(R.id.stv_refresh);
        RxView.clicks(stvRefresh)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        //重置一些秤机器数据
                        getAllWeightGoodsList();
                    }
                });
        stvConfirm = (ShapeTextView) findViewById(R.id.stv_confirm);
        RxView.clicks(stvConfirm)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        checkAddSelectWeightGoods(true);
                    }
                });
        stvContinue = (ShapeTextView) findViewById(R.id.stv_continue);
        RxView.clicks(stvContinue)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        checkAddSelectWeightGoods(false);
                    }
                });
        GridLayoutManager layoutManager = (GridLayoutManager) rvContent.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object data = mGoodsListAdapter.getData(position);
                if (data instanceof GoodsWeightIndexInfo) {
                    return 5;
                }
                return 1;
            }
        });
        mRecentGoodsListAdapter = new CommonRecyclerAdapter(false);
        mRecentGoodsListAdapter.addViewHolderFactory(new GoodsWeightItemViewHolder.Factory());
        mRecentGoodsListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsWeightItemViewHolder.EVENT_SELECTOR_ITEM) {
                    weightGoodsSelector((GoodsWeightItemInfo) obj);
                }
            }
        });
        rvRecentWeightGoods.setAdapter(mRecentGoodsListAdapter);
        mGoodsListAdapter = new CommonRecyclerAdapter(false);
        mGoodsListAdapter.addViewHolderFactory(new GoodsWeightIndexViewHolder.Factory());
        mGoodsListAdapter.addViewHolderFactory(new GoodsWeightItemViewHolder.Factory());
        mGoodsListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsWeightItemViewHolder.EVENT_SELECTOR_ITEM) {
                    weightGoodsSelector((GoodsWeightItemInfo) obj);
                }
            }
        });
        int offset = mResources.getDimensionPixelOffset(com.stkj.common.R.dimen.dp_10);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(offset, 0, 0, offset);
            }
        };
        rvRecentWeightGoods.addItemDecoration(itemDecoration);
        rvContent.addItemDecoration(itemDecoration);
        rvContent.setAdapter(mGoodsListAdapter);
    }

    private void checkAddSelectWeightGoods(boolean isFinished) {
        //当前有选中商品
        if (mSelectedGoodsWeight != null && mSelectedGoodsWeight.isSelect()) {
            String weightCount = tvWeightCount.getText().toString();
            double weightCountDouble = 0;
            try {
                weightCountDouble = Double.parseDouble(weightCount);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (weightCountDouble <= 0) {
                return;
            }
            String totalPrice = tvWeightPrice.getText().toString();
            String goodsPayInfo = "名称: " + mSelectedGoodsWeight.getGoodsName() +
                    "\n重量: " + weightCount + "kg" +
                    "\n价格: " + totalPrice + "元";
            CommonAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("确定添加以下商品到订单列表?\n" + goodsPayInfo)
                    .setLeftNavTxt("确定")
                    .setRightNavTxt("取消")
                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            if (mSelectedGoodsWeight == null) {
                                return;
                            }
                            GoodsOrderListInfo goodsOrderListInfo = new GoodsOrderListInfo(mSelectedGoodsWeight.getSaleListInfo(), weightCount, "", totalPrice);
                            if (weightGoodsPayListener != null) {
                                weightGoodsPayListener.onAddWeightGoodsOrderInfo(goodsOrderListInfo);
                            }
                            AppToast.toastMsg(mSelectedGoodsWeight.getGoodsName() + "已添加至订单列表");
                            clearWeightGoods(true);
                            if (isFinished) {
                                FragmentUtils.safeDetachFragment(getParentFragmentManager(), WeightGoodsPayFragment.this);
                            }
                        }
                    })
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            if (isFinished) {
                                FragmentUtils.safeDetachFragment(getParentFragmentManager(), WeightGoodsPayFragment.this);
                            }
                        }
                    }).show(mActivity);
        } else {
            if (isFinished) {
                FragmentUtils.safeDetachFragment(getParentFragmentManager(), WeightGoodsPayFragment.this);
            }
        }
    }

    /**
     * 清理已选中的称重商品
     */
    private void clearWeightGoods(boolean needNotify) {
        if (mSelectedGoodsWeight != null && needNotify) {
            mSelectedGoodsWeight.setSelect(false);
            mGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
            mRecentGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
        }
        mSelectedGoodsWeight = null;
        tvWeightCount.setText("");
        tvWeightPrice.setText("");
    }

    private void weightGoodsSelector(GoodsWeightItemInfo goodsWeightItemInfo) {
        //移除之前的选中的商品
        if (mSelectedGoodsWeight != null && mSelectedGoodsWeight != goodsWeightItemInfo) {
            mSelectedGoodsWeight.setSelect(false);
            mGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
            mRecentGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
            mSelectedGoodsWeight = null;
        }
        boolean select = !goodsWeightItemInfo.isSelect();
        goodsWeightItemInfo.setSelect(select);
        if (select) {
            mSelectedGoodsWeight = goodsWeightItemInfo;
            mGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
            //添加到最近商品
            List<Object> dataList = mRecentGoodsListAdapter.getDataList();
            if (!dataList.contains(mSelectedGoodsWeight)) {
                int size = dataList.size();
                //超过十个移除最后一个
                if (size >= 10) {
                    mRecentGoodsListAdapter.removeLastData();
                    mRecentGoodsListAdapter.addFirstData(mSelectedGoodsWeight);
                } else {
                    mRecentGoodsListAdapter.addFirstData(mSelectedGoodsWeight);
                }
            } else {
                mRecentGoodsListAdapter.notifyChangeItemData(mSelectedGoodsWeight);
            }
        } else {
            mGoodsListAdapter.notifyChangeItemData(goodsWeightItemInfo);
            mRecentGoodsListAdapter.notifyChangeItemData(goodsWeightItemInfo);
            mSelectedGoodsWeight = null;
        }
        refreshWeightGoodsPrice();
    }

    /**
     * 获取称重商品重量
     */
    private double getWeightGoodsCount() {
        String weightCount = tvWeightCount.getText().toString();
        double weightCountDouble = 0;
        try {
            weightCountDouble = Double.parseDouble(weightCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return weightCountDouble;
    }

    /**
     * 刷新称重商品价格
     */
    private void refreshWeightGoodsPrice() {
        if (mSelectedGoodsWeight != null && mSelectedGoodsWeight.isSelect()) {
            double weightCountDouble = getWeightGoodsCount();
            double tempTotalPrice = BigDecimalUtils.mul(mSelectedGoodsWeight.getGoodsDiscountPrice(), weightCountDouble);
            String totalPrice = PriceUtils.formatPrice(String.valueOf(tempTotalPrice));
            tvWeightPrice.setText(totalPrice);
        } else {
            tvWeightPrice.setText("");
        }
    }

    /**
     * 设置需要直接选中的商品
     */
    private void checkNeedsSelectGoods() {
        if (rvContent != null && !mGoodsListAdapter.isEmptyData()) {
            if (needSelectedGoods != null) {
                //当前商品已经选中 则不需要反选
                if (mSelectedGoodsWeight != null && TextUtils.equals(mSelectedGoodsWeight.getGoodsId(), needSelectedGoods.getId())) {
                    needSelectedGoods = null;
                    return;
                }
                GoodsWeightItemInfo needSelectWeightItemInfo = null;
                List<Object> dataList = mGoodsListAdapter.getDataList();
                for (int i = 0; i < dataList.size(); i++) {
                    Object o = dataList.get(i);
                    if (o instanceof GoodsWeightItemInfo) {
                        GoodsWeightItemInfo goodsWeightItemInfo = (GoodsWeightItemInfo) o;
                        if (TextUtils.equals(goodsWeightItemInfo.getGoodsId(), needSelectedGoods.getId())) {
                            needSelectWeightItemInfo = goodsWeightItemInfo;
                            break;
                        }
                    }
                }
                needSelectedGoods = null;
                if (needSelectWeightItemInfo != null) {
                    GoodsWeightItemInfo finalNeedSelectWeightItemInfo = needSelectWeightItemInfo;
                    rvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int itemDataIndex = mGoodsListAdapter.findItemDataIndex(finalNeedSelectWeightItemInfo);
                            GridLayoutManager layoutManager = (GridLayoutManager) rvContent.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.scrollToPositionWithOffset(itemDataIndex, 0);
                            }
//                            GridLayoutManager layoutManager = (GridLayoutManager) rvContent.getLayoutManager();
//                            if (layoutManager != null) {
//                                layoutManager.scrollToPositionWithOffset(0, 0);
//                            }
//                            ablRecentWeight.setExpanded(true, false);
                            weightGoodsSelector(finalNeedSelectWeightItemInfo);
                        }
                    }, 60);
                }
            }
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (needRefreshWeightGoodsList) {
            getAllWeightGoodsList();
            needRefreshWeightGoodsList = false;
        } else {
            if (isFirstOnResume) {
                getAllWeightGoodsList();
            } else {
                checkNeedsSelectGoods();
            }
        }
        EventBusUtils.registerEventBus(this);
        boolean isSupportReadWeight = DeviceManager.INSTANCE.getDeviceInterface().isSupportReadWeight();
        //支持称重
        if (isSupportReadWeight) {
            DeviceManager.INSTANCE.getDeviceInterface().readWeight(mReadWeightListener);
        }
    }

    @Override
    public void onPause() {
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterReadWeightListener(mReadWeightListener);
        super.onPause();
    }

    @Override
    public void onDetach() {
        EventBusUtils.unRegisterEventBus(this);
        super.onDetach();
    }

    private OnReadWeightListener mReadWeightListener = new OnReadWeightListener() {
        @Override
        public void onReadWeightData(String data, String unit) {
            if (tvWeightCount != null && data != null) {
                String formatData = PriceUtils.formatPrice(data);
                tvWeightCount.setText(formatData);
            }
        }

        @Override
        public void onReadWeightError(String message) {

        }
    };

    private boolean needRefreshWeightGoodsList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshWeightGoodsList(RefreshWeightGoodsListEvent eventBus) {
        LogHelper.print("--EventBusUtils-onRefreshWeightGoodsList");
        needRefreshWeightGoodsList = true;
    }

    private void getAllWeightGoodsList() {
        showLoadingDialog();
        //测试代码 start
//        Observable.just(30)
//                .map(new Function<Integer, BaseResponse<List<GoodsSaleListInfo>>>() {
//                    @Override
//                    public BaseResponse<List<GoodsSaleListInfo>> apply(Integer integer) throws Throwable {
//                        BaseResponse<List<GoodsSaleListInfo>> baseResponse = new BaseResponse<>();
//                        baseResponse.setCode("200");
//                        List<GoodsSaleListInfo> saleListInfoList = new ArrayList<>();
//                        for (int i = 0; i < integer; i++) {
//                            GoodsSaleListInfo saleListInfo = GoodsConstants.randomWeightGoods();
//                            saleListInfoList.add(saleListInfo);
//                        }
//                        baseResponse.setData(saleListInfoList);
//                        return baseResponse;
//                    }
//                })
        //测试代码 end
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .getAllGoodsList(String.valueOf(GoodsConstants.TYPE_GOODS_WEIGHT))
                .map(new Function<BaseResponse<List<GoodsSaleListInfo>>, List<Object>>() {
                    @Override
                    public List<Object> apply(BaseResponse<List<GoodsSaleListInfo>> response) throws Throwable {
                        List<GoodsSaleListInfo> saleListInfoList = response.getData();
                        //最终数据集合
                        List dataList = new ArrayList();
                        if (!response.isSuccess() || saleListInfoList == null) {
                            return dataList;
                        }
                        int size = saleListInfoList.size();
                        //拼音排序
                        saleListInfoList.sort(new Comparator<GoodsSaleListInfo>() {
                            @Override
                            public int compare(GoodsSaleListInfo o1, GoodsSaleListInfo o2) {
                                return o1.getGoodsFirstChar().toUpperCase().compareTo(o2.getGoodsFirstChar().toUpperCase());
                            }
                        });
                        //#无首字母
                        List nullPinYinList = new ArrayList();
                        nullPinYinList.add(new GoodsWeightIndexInfo("#"));
                        String letterName = "";
                        for (int i = 0; i < size; i++) {
                            GoodsSaleListInfo saleListInfo = saleListInfoList.get(i);
                            String pinYinName = saleListInfo.getGoodsFirstChar();
                            if (TextUtils.isEmpty(pinYinName)) {
                                nullPinYinList.add(new GoodsWeightItemInfo(saleListInfo));
                            } else {
                                String firstLetterName = pinYinName.substring(0, 1).toUpperCase();
                                if (!TextUtils.equals(letterName, firstLetterName)) {
                                    dataList.add(new GoodsWeightIndexInfo(firstLetterName));
                                    letterName = firstLetterName;
                                }
                                dataList.add(new GoodsWeightItemInfo(saleListInfo));
                            }
                        }
                        if (nullPinYinList.size() > 1) {
                            dataList.addAll(nullPinYinList);
                        }
                        return dataList;
                    }
                })
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<List<Object>>() {
                    @Override
                    protected void onSuccess(List<Object> objects) {
                        hideLoadingDialog();
                        clearWeightGoods(false);
                        mRecentGoodsListAdapter.removeAllData();
                        mGoodsListAdapter.removeAllData();
                        rvContent.scrollToPosition(0);
                        ablRecentWeight.setExpanded(false, false);
                        if (objects.isEmpty()) {
                            CommonDialogUtils.showTipsDialog(mActivity, "称重数据为空,请尝试刷新");
                        } else {
                            mGoodsListAdapter.addDataList(objects);
                            checkNeedsSelectGoods();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "获取数据失败: " + e.getMessage());
                    }
                });
    }

    public interface OnWeightGoodsPayListener {
        void onAddWeightGoodsOrderInfo(GoodsOrderListInfo orderListInfo);
    }

}
