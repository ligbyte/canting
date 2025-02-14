package com.stkj.supermarket.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.PriceUtils;
import com.stkj.supermarket.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarket.goods.data.RefreshSearchGoodsListEvent;
import com.stkj.supermarket.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarket.goods.model.GoodsInventoryListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.model.GoodsSaveStorageInfo;
import com.stkj.supermarket.goods.model.GoodsStorageListInfo;
import com.stkj.supermarket.goods.service.GoodsService;
import com.stkj.supermarket.goods.ui.adapter.GoodsInventoryListInfoViewHolder;
import com.stkj.supermarket.goods.ui.adapter.GoodsStorageListInfoViewHolder;
import com.stkj.supermarket.goods.ui.widget.GoodsAutoSearchLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品入库
 */
public class GoodsSaveStorageFragment extends BaseRecyclerFragment {
    private LinearLayout llEmptyList;
    private ShapeLinearLayout sllGoodsAdd;
    private ImageView ivGoodsTitleSelect;
    private RecyclerView rvGoodsInventoryList;
    private TextView tvSelectedCount;
    private TextView tvSelectedMoney;
    private RecyclerView rvGoodsStorageList;
    private ShapeTextView stvBack;
    private ShapeTextView stvSaveStorage;
    private CommonRecyclerAdapter goodsInventoryAdapter;
    private CommonRecyclerAdapter goodsStorageAdapter;
    private GoodsAutoSearchLayout goodsAutoSearch;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_save_storage;
    }

    @Override
    protected void initViews(View rootView) {
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsAutoSearch.hideSearchGoodsList();
            }
        });
        goodsAutoSearch = (GoodsAutoSearchLayout) findViewById(R.id.goods_auto_search);
        goodsAutoSearch.setGoodsAutoSearchListener(this, new GoodsAutoSearchListener() {

            @Override
            public void onStartGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo) {
                showLoadingDialog();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(GoodsSaleListInfo saleListInfo) {
                hideLoadingDialog();
                llEmptyList.setVisibility(View.GONE);
                goodsInventoryAdapter.addFirstData(new GoodsInventoryListInfo(saleListInfo, saleListInfo.getGoodsInitPrice(), "1", "1.0"));
                rvGoodsInventoryList.scrollToPosition(0);
            }

            @Override
            public void onErrorGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo, String msg) {
                hideLoadingDialog();
                CommonDialogUtils.showTipsDialog(mActivity, msg);
            }

            @Override
            public void onSearchGoodsList(String key, List<GoodsIdBaseListInfo> goodsIdBaseListInfoList) {

            }
        });
        llEmptyList = (LinearLayout) findViewById(R.id.ll_inventory_list_empty);
        sllGoodsAdd = (ShapeLinearLayout) findViewById(R.id.sll_goods_add);
        sllGoodsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
                addGoodsFragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            waitScanQrCode();
                        }
                    }
                });
                FragmentUtils.safeAddFragment(getParentFragmentManager(), addGoodsFragment, R.id.fl_goods_second_content);
            }
        });
        ivGoodsTitleSelect = (ImageView) findViewById(R.id.iv_goods_select_all);
        ivGoodsTitleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsInventoryAdapter.isEmptyData()) {
                    return;
                }
                //是否是在继续入库状态
                if (isContinueStorage) {
                    isContinueStorage = false;
                    goodsStorageAdapter.removeAllData();
                }
                boolean selectorAll = !ivGoodsTitleSelect.isSelected();
                List<Object> inventoryAdapterDataList = goodsInventoryAdapter.getDataList();
                if (selectorAll) {
                    List<GoodsInventoryListInfo> needSelectInventoryList = new ArrayList<>();
                    for (int i = 0; i < inventoryAdapterDataList.size(); i++) {
                        Object obj = inventoryAdapterDataList.get(i);
                        GoodsInventoryListInfo inventoryListInfo = (GoodsInventoryListInfo) obj;
                        if (!inventoryListInfo.hasGoodsCount()) {
                            CommonDialogUtils.showTipsDialog(mActivity, inventoryListInfo.getGoodsName() + "\n" + "商品入库数量不能为0");
                            return;
                        }
                        if (!inventoryListInfo.hasGoodsInitPrice()) {
                            CommonDialogUtils.showTipsDialog(mActivity, inventoryListInfo.getGoodsName() + "\n" + "商品进货价不能为0");
                            return;
                        }
                        //添加需要选中商品
                        if (!inventoryListInfo.isSelected()) {
                            needSelectInventoryList.add(inventoryListInfo);
                        }
                    }
                    //将需要选中商品添加在右侧入库列表
                    List<GoodsStorageListInfo> storageListInfoList = new ArrayList<>();
                    for (GoodsInventoryListInfo goodsInventoryListInfo : needSelectInventoryList) {
                        goodsInventoryListInfo.setSelected(true);
                        storageListInfoList.add(new GoodsStorageListInfo(goodsInventoryListInfo));
                    }
                    //左侧列表
                    goodsInventoryAdapter.notifyDataSetChanged();
                    //右侧列表
                    goodsStorageAdapter.addDataList(storageListInfoList);
                    ivGoodsTitleSelect.setSelected(true);
                } else {
                    List<Object> storageAdapterDataList = goodsStorageAdapter.getDataList();
                    List<GoodsStorageListInfo> needClearStorageList = new ArrayList<>();
                    for (int i = 0; i < inventoryAdapterDataList.size(); i++) {
                        Object obj = inventoryAdapterDataList.get(i);
                        GoodsInventoryListInfo inventoryListInfo = (GoodsInventoryListInfo) obj;
                        inventoryListInfo.setSelected(false);
                        String storageId = inventoryListInfo.getStorageId();
                        for (int j = 0; j < storageAdapterDataList.size(); j++) {
                            GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) storageAdapterDataList.get(j);
                            if (TextUtils.equals(storageId, goodsStorageListInfo.getSourceStorageId())) {
                                needClearStorageList.add(goodsStorageListInfo);
                                break;
                            }
                        }
                    }
                    //左侧列表
                    goodsInventoryAdapter.notifyDataSetChanged();
                    List<GoodsStorageListInfo> unSelectedDataList = new ArrayList<>();
                    for (int j = 0; j < storageAdapterDataList.size(); j++) {
                        GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) storageAdapterDataList.get(j);
                        if (!needClearStorageList.contains(goodsStorageListInfo)) {
                            unSelectedDataList.add(goodsStorageListInfo);
                        }
                    }
                    //右侧列表
                    goodsStorageAdapter.removeAllData();
                    if (!unSelectedDataList.isEmpty()) {
                        goodsStorageAdapter.addDataList(unSelectedDataList);
                    }
                    ivGoodsTitleSelect.setSelected(false);
                }
                refreshGoodsStorageListTitle();
            }
        });
        rvGoodsInventoryList = (RecyclerView) findViewById(R.id.rv_goods_inventory_list);
        goodsInventoryAdapter = new CommonRecyclerAdapter(false);
        goodsInventoryAdapter.addViewHolderFactory(new GoodsInventoryListInfoViewHolder.Factory());
        goodsInventoryAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsInventoryListInfoViewHolder.EVENT_CLICK) {
                    GoodsInventoryListInfo goodsInventoryListInfo = (GoodsInventoryListInfo) obj;
                    GoodsDetailFragment goodsDetailFragment = new GoodsDetailFragment();
                    goodsDetailFragment.setForbidEditGoodsInfo(true);
                    goodsDetailFragment.setGoodsId(goodsInventoryListInfo.getGoodsId());
                    FragmentUtils.safeAddFragment(getParentFragmentManager(), goodsDetailFragment, R.id.fl_goods_second_content);
                } else if (eventId == GoodsInventoryListInfoViewHolder.EVENT_SELECTOR) {
                    GoodsInventoryListInfo goodsInventoryListInfo = (GoodsInventoryListInfo) obj;
                    if (goodsInventoryListInfo.isSelected()) {
                        //是否是在继续入库状态
                        if (isContinueStorage) {
                            isContinueStorage = false;
                            goodsStorageAdapter.removeAllData();
                        }
                        goodsStorageAdapter.addFirstData(new GoodsStorageListInfo(goodsInventoryListInfo));
                        rvGoodsStorageList.scrollToPosition(0);
                    } else {
                        String storageId = goodsInventoryListInfo.getStorageId();
                        List<Object> dataList = goodsStorageAdapter.getDataList();
                        GoodsStorageListInfo needRemoveGoodsStorage = null;
                        for (int i = 0; i < dataList.size(); i++) {
                            GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) dataList.get(i);
                            if (TextUtils.equals(storageId, goodsStorageListInfo.getSourceStorageId())) {
                                needRemoveGoodsStorage = goodsStorageListInfo;
                                break;
                            }
                        }
                        if (needRemoveGoodsStorage != null) {
                            goodsStorageAdapter.removeData(needRemoveGoodsStorage);
                        }
                    }
                    boolean isAllSelected = true;
                    for (int i = 0; i < goodsInventoryAdapter.getItemCount(); i++) {
                        Object data = goodsInventoryAdapter.getData(i);
                        GoodsInventoryListInfo adapterData = (GoodsInventoryListInfo) data;
                        if (!adapterData.isSelected()) {
                            isAllSelected = false;
                            break;
                        }
                    }
                    ivGoodsTitleSelect.setSelected(isAllSelected);
                    refreshGoodsStorageListTitle();
                } else if (eventId == GoodsInventoryListInfoViewHolder.EVENT_REFRESH_PRICE) {
                    refreshStorageAdapter((GoodsInventoryListInfo) obj);
                } else if (eventId == GoodsInventoryListInfoViewHolder.EVENT_DELETE_ITEM) {
                    if (goodsInventoryAdapter.isEmptyData()) {
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        llEmptyList.setVisibility(View.GONE);
                    }
                }
            }
        });
        rvGoodsInventoryList.setAdapter(goodsInventoryAdapter);
        tvSelectedCount = (TextView) findViewById(R.id.tv_selected_count);
        tvSelectedMoney = (TextView) findViewById(R.id.tv_selected_money);
        rvGoodsStorageList = (RecyclerView) findViewById(R.id.rv_goods_storage_list);
        stvBack = (ShapeTextView) findViewById(R.id.stv_back);
        stvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGoodsSaveStorageListener != null) {
                    onGoodsSaveStorageListener.onHide();
                }
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsSaveStorageFragment.this);
            }
        });
        stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        goodsStorageAdapter = new CommonRecyclerAdapter(false);
        goodsStorageAdapter.addViewHolderFactory(new GoodsStorageListInfoViewHolder.Factory());
        goodsStorageAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsStorageListInfoViewHolder.EVENT_SET_DATE) {
                    if (obj instanceof GoodsStorageListInfo) {
                        GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                        GoodsExpireDateAlertFragment dateAlertFragment = new GoodsExpireDateAlertFragment();
                        dateAlertFragment.setOnInputExpireDaysListener(new GoodsExpireDateAlertFragment.OnInputExpireDaysListener() {
                            @Override
                            public void onInputExpireDays(String expireDate, int expireDays) {
                                if (expireDays > 0) {
                                    storageListInfo.setInputGoodsProductExpireDays(String.valueOf(expireDays));
                                } else {
                                    storageListInfo.setInputGoodsProductExpireDays("");
                                }
                                viewHolder.notifyItemDataChange();
                            }
                        });
                        FragmentUtils.safeAddFragment(getParentFragmentManager(), dateAlertFragment, R.id.fl_goods_second_content);
                    }
                } else if (eventId == GoodsStorageListInfoViewHolder.EVENT_DELETE_ITEM) {
                    if (obj instanceof GoodsStorageListInfo) {
                        GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                        String sourceStorageId = storageListInfo.getSourceStorageId();
                        goodsStorageAdapter.removeData(storageListInfo);
                        refreshGoodsStorageListTitle();
                        //同步取消选择左侧的商品
                        List<Object> inventoryAdapterDataList = goodsInventoryAdapter.getDataList();
                        for (int i = 0; i < inventoryAdapterDataList.size(); i++) {
                            Object object = inventoryAdapterDataList.get(i);
                            GoodsInventoryListInfo inventoryListInfo = (GoodsInventoryListInfo) object;
                            if (TextUtils.equals(sourceStorageId, inventoryListInfo.getStorageId())) {
                                inventoryListInfo.setSelected(false);
                                ivGoodsTitleSelect.setSelected(false);
                                goodsInventoryAdapter.notifyItemChanged(i);
                                break;
                            }
                        }
                    }
                } else if (eventId == GoodsStorageListInfoViewHolder.EVENT_CLICK) {
                    GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                    GoodsDetailFragment goodsDetailFragment = new GoodsDetailFragment();
                    goodsDetailFragment.setForbidEditGoodsInfo(true);
                    goodsDetailFragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                        @Override
                        public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                waitScanQrCode();
                            }
                        }
                    });
                    goodsDetailFragment.setGoodsId(storageListInfo.getGoodsId());
                    FragmentUtils.safeAddFragment(getParentFragmentManager(), goodsDetailFragment, R.id.fl_goods_second_content);
                }
            }
        });
        rvGoodsStorageList.setAdapter(goodsStorageAdapter);
        refreshWaitingStorageLay();
    }

    private void refreshStorageAdapter(GoodsInventoryListInfo goodsInventoryListInfo) {
        //同步刷新右侧入库的商品列表
        String storageId = goodsInventoryListInfo.getStorageId();
        List<Object> dataList = goodsStorageAdapter.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) dataList.get(i);
            if (TextUtils.equals(storageId, goodsStorageListInfo.getSourceStorageId())) {
                goodsStorageAdapter.notifyItemChanged(i);
                break;
            }
        }
        refreshGoodsStorageListTitle();
    }

    private boolean isRequestingSaveStorage;

    /**
     * 保存商品入库
     */
    private void saveGoodsToStorage() {
        if (isRequestingSaveStorage) {
            return;
        }
        List<Object> dataList = goodsStorageAdapter.getDataList();
        if (dataList.isEmpty()) {
            return;
        }
        List<GoodsSaveStorageInfo> saveStorageInfoList = new ArrayList<>();
        //检查生产日期+保质期
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof GoodsStorageListInfo) {
                GoodsSaveStorageInfo saveStorageInfo = new GoodsSaveStorageInfo();
                GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) obj;
                GoodsInventoryListInfo inventoryListInfo = goodsStorageListInfo.getInventoryListInfo();
                String goodsName = "";
                if (inventoryListInfo != null) {
                    saveStorageInfo.setGoodsId(inventoryListInfo.getGoodsId());
                    saveStorageInfo.setInUnitPrice(inventoryListInfo.getInputGoodsInitPrice());
                    saveStorageInfo.setInCount(inventoryListInfo.isWeightGoods() ? inventoryListInfo.getWeightGoodsCount() : inventoryListInfo.getStandardGoodsCount());
                    goodsName = inventoryListInfo.getGoodsName();
                }
                String goodsProductDate = goodsStorageListInfo.getInputGoodsProductDate();
                String expireDays = goodsStorageListInfo.getInputGoodsProductExpireDays();
                if (TextUtils.isEmpty(goodsProductDate) && !TextUtils.isEmpty(expireDays)) {
                    CommonDialogUtils.showTipsDialog(mActivity, "请填写\"" + goodsName + "\"的生产日期");
                    return;
                }
                if (!TextUtils.isEmpty(goodsProductDate) && TextUtils.isEmpty(expireDays)) {
                    CommonDialogUtils.showTipsDialog(mActivity, "请填写\"" + goodsName + "\"的保质期");
                    return;
                }
                saveStorageInfo.setProductDate(goodsProductDate);
                saveStorageInfo.setExpireDays(expireDays);
                saveStorageInfoList.add(saveStorageInfo);
            }
        }
        isRequestingSaveStorage = true;
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).saveGoodsStorage(saveStorageInfoList).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<BaseResponse<String>>() {
            @Override
            protected void onSuccess(BaseResponse<String> response) {
                isRequestingSaveStorage = false;
                hideLoadingDialog();
                if (response.isSuccess()) {
                    for (int i = 0; i < dataList.size(); i++) {
                        Object o = dataList.get(i);
                        if (o instanceof GoodsStorageListInfo) {
                            GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) o;
                            storageListInfo.setHasSaveStorage(true);
                            GoodsInventoryListInfo inventoryListInfo = storageListInfo.getInventoryListInfo();
                            if (inventoryListInfo != null) {
                                //实时库存增加一下
                                inventoryListInfo.refreshGoodsRealStock();
                                //同步取消左侧选中商品
                                inventoryListInfo.setSelected(false);
                            }
                        }
                    }
                    ivGoodsTitleSelect.setSelected(false);
                    //刷新左侧列表
                    goodsInventoryAdapter.notifyDataSetChanged();
                    //刷新右侧列表
                    goodsStorageAdapter.notifyDataSetChanged();
                    int finishStorageCount = goodsStorageAdapter.getItemCount();
                    goodsStorageAdapter.addFirstLayout(R.layout.item_save_storage_success);
                    rvGoodsStorageList.scrollToPosition(0);
                    refreshContinueStorageLay(finishStorageCount);
                    //刷新tab页商品列表
                    EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
                } else {
                    CommonDialogUtils.showTipsDialog(mActivity, "入库失败:" + response.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                isRequestingSaveStorage = false;
                hideLoadingDialog();
                CommonDialogUtils.showTipsDialog(mActivity, "入库失败:" + e.getMessage());
            }
        });
    }

    /**
     * 刷新右侧待入库的商品列表头部
     */
    private void refreshGoodsStorageListTitle() {
        List<Object> dataList = goodsStorageAdapter.getDataList();
        if (dataList.isEmpty()) {
            refreshWaitingStorageLay();
            return;
        }
        int fontSize = (int) mResources.getDimension(com.stkj.common.R.dimen.sp_9);
        int fontColor = mResources.getColor(R.color.color_999999);
        SpanUtils.with(tvSelectedCount).append("已选择 ").append("(" + dataList.size() + ")").setFontSize(fontSize).setForegroundColor(fontColor).create();
        double totalPrice = 0;
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof GoodsStorageListInfo) {
                GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                GoodsInventoryListInfo inventoryListInfo = storageListInfo.getInventoryListInfo();
                if (inventoryListInfo != null) {
                    double inputGoodsCount = inventoryListInfo.isWeightGoods() ? inventoryListInfo.getWeightGoodsCountWithDouble() : inventoryListInfo.getStandardGoodsCountWithInt();
                    double inputGoodsInitPrice = inventoryListInfo.getInputGoodsInitPriceWithDouble();
                    double tempPrice = BigDecimalUtils.mul(inputGoodsInitPrice, inputGoodsCount);
                    totalPrice = BigDecimalUtils.add(totalPrice, tempPrice);
                }
            }
        }
        SpanUtils.with(tvSelectedMoney).append("总计: ").setFontSize(fontSize).setForegroundColor(fontColor).append("¥ " + PriceUtils.formatPrice2(totalPrice)).create();
        stvSaveStorage.setText("确认入库");
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoodsToStorage();
            }
        });
        stvSaveStorage.setSolidColor(mResources.getColor(R.color.color_0087FA));
        stvSaveStorage.setTextColor(mResources.getColor(R.color.white));
    }

    //是否正在继续入库状态
    private boolean isContinueStorage;

    /**
     * 刷新当前为继续入库状态
     */
    private void refreshContinueStorageLay(int finishCount) {
        isContinueStorage = true;
        tvSelectedMoney.setText("");
        int fontSize = (int) mResources.getDimension(com.stkj.common.R.dimen.sp_9);
        int fontColor = mResources.getColor(R.color.color_999999);
        SpanUtils.with(tvSelectedCount).append("已完成 ").append("(" + finishCount + ")").setFontSize(fontSize).setForegroundColor(fontColor).create();
        stvSaveStorage.setText("继续入库");
        stvSaveStorage.setSolidColor(mResources.getColor(R.color.color_0087FA));
        stvSaveStorage.setTextColor(mResources.getColor(R.color.white));
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContinueStorage = false;
                goodsStorageAdapter.removeAllData();
                refreshWaitingStorageLay();
            }
        });
    }

    /**
     * 刷新当前为待入库状态
     */
    private void refreshWaitingStorageLay() {
        int fontSize = (int) mResources.getDimension(com.stkj.common.R.dimen.sp_9);
        int fontColor = mResources.getColor(R.color.color_999999);
        SpanUtils.with(tvSelectedCount).append("已选择 ").append("(0)").setFontSize(fontSize).setForegroundColor(fontColor).create();
        tvSelectedMoney.setText("");
        stvSaveStorage.setText("确认入库");
        stvSaveStorage.setOnClickListener(null);
        stvSaveStorage.setSolidColor(mResources.getColor(R.color.color_F1F2F4));
        stvSaveStorage.setTextColor(fontColor);
    }

    private OnGoodsSaveStorageListener onGoodsSaveStorageListener;

    public void setOnHideSaveStorageListener(OnGoodsSaveStorageListener onGoodsSaveStorageListener) {
        this.onGoodsSaveStorageListener = onGoodsSaveStorageListener;
    }

    public interface OnGoodsSaveStorageListener {
        void onHide();
    }

    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (!isDetached()) {
                if (goodsAutoSearch != null) {
                    goodsAutoSearch.autoSearch(data, true);
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

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        waitScanQrCode();
    }

    /**
     * 等待扫码入库商品
     */
    private void waitScanQrCode() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mScanQRCodeListener);
    }
    //二维码扫码 商品 end
}
