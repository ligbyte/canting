package com.stkj.supermarketmini.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.linelayout.LineFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.callback.QrCodeListener;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.ui.fragment.QrCodeAlertFragment;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.base.utils.PriceUtils;
import com.stkj.supermarketmini.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarketmini.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaveStorageInfo;
import com.stkj.supermarketmini.goods.model.GoodsStorageListInfo;
import com.stkj.supermarketmini.goods.model.RefreshSearchGoodsListEvent;
import com.stkj.supermarketmini.goods.service.GoodsService;
import com.stkj.supermarketmini.goods.ui.adapter.GoodsSaveStorageInfoViewHolder;
import com.stkj.supermarketmini.goods.ui.weight.GoodsAutoSearchLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品入库页面
 */
public class GoodsSaveStorageFragment extends BaseRecyclerFragment {

    private LinearLayout llInventoryListEmpty;
    private ShapeTextView stvAddGoods;
    private RecyclerView rvGoodsStorageList;
    private GoodsAutoSearchLayout gslLayout;
    private LineFrameLayout flSaveStorage;
    private ShapeTextView stvSaveStorage;
    private TextView storageAllPrice;
    private ImageView ivScan;
    private CommonRecyclerAdapter mStorageAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_storage_goods;
    }

    @Override
    protected void initViews(View rootView) {
        findViewById(R.id.iv_goods_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsSaveStorageFragment.this);
            }
        });
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        ivScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScanGoodsCode();
            }
        });
        stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoodsToStorage();
            }
        });
        llInventoryListEmpty = (LinearLayout) findViewById(R.id.ll_inventory_list_empty);
        stvAddGoods = (ShapeTextView) findViewById(R.id.stv_add_goods);
        stvAddGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
                mActivity.addContentPlaceHolderFragment(addGoodsFragment);
            }
        });
        rvGoodsStorageList = (RecyclerView) findViewById(R.id.rv_goods_storage_list);
        gslLayout = (GoodsAutoSearchLayout) findViewById(R.id.gsl_layout);
        flSaveStorage = (LineFrameLayout) findViewById(R.id.fl_save_storage);
        stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        storageAllPrice = (TextView) findViewById(R.id.storage_all_price);
        mStorageAdapter = new CommonRecyclerAdapter(false);
        mStorageAdapter.addViewHolderFactory(new GoodsSaveStorageInfoViewHolder.Factory());
        mStorageAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                if (eventId == GoodsSaveStorageInfoViewHolder.EVENT_DELETE) {
                    mStorageAdapter.removeData(storageListInfo);
                    refreshBottomGoodsStorage();
                } else if (eventId == GoodsSaveStorageInfoViewHolder.EVENT_REFRESH_PRICE) {
                    refreshBottomGoodsStorage();
                } else if (eventId == GoodsSaveStorageInfoViewHolder.EVENT_CLICK) {
                    GoodsDetailFragment goodsDetailFragment = new GoodsDetailFragment();
                    goodsDetailFragment.setGoodsId(storageListInfo.getGoodsId());
                    goodsDetailFragment.setForbidEditGoodsInfo(true);
                    mActivity.addContentPlaceHolderFragment(goodsDetailFragment);
                }
            }
        });
        rvGoodsStorageList.setAdapter(mStorageAdapter);
        gslLayout.setGoodsAutoSearchListener(this, new GoodsAutoSearchListener() {

            @Override
            public void onStartGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo) {
                showLoadingDialog();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(GoodsSaleListInfo saleListInfo) {
                hideLoadingDialog();
                mStorageAdapter.addFirstData(new GoodsStorageListInfo(saleListInfo, saleListInfo.getGoodsInitPrice(), "1", "1.0"));
                rvGoodsStorageList.scrollToPosition(0);
                refreshBottomGoodsStorage();
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
    }

    /**
     * 显示商品搜索
     */
    private void showScanGoodsCode() {
        QrCodeAlertFragment qrCodeAlertFragment = new QrCodeAlertFragment();
        qrCodeAlertFragment.setQrCodeListener(new QrCodeListener() {
            @Override
            public void onScanResult(String result) {
                qrCodeAlertFragment.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    gslLayout.autoSearch(result, true);
                }
            }
        });
        qrCodeAlertFragment.show(mActivity);
    }


    /**
     * 刷新底部待入库的商品价格
     */
    private void refreshBottomGoodsStorage() {
        List<Object> dataList = mStorageAdapter.getDataList();
        if (dataList.isEmpty()) {
            flSaveStorage.setVisibility(View.GONE);
            llInventoryListEmpty.setVisibility(View.VISIBLE);
            return;
        }
        llInventoryListEmpty.setVisibility(View.GONE);
        flSaveStorage.setVisibility(View.VISIBLE);
        double totalPrice = 0;
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof GoodsStorageListInfo) {
                GoodsStorageListInfo storageListInfo = (GoodsStorageListInfo) obj;
                double inputGoodsCount = storageListInfo.isWeightGoods() ? storageListInfo.getWeightGoodsCountWithDouble() : storageListInfo.getStandardGoodsCountWithInt();
                double inputGoodsInitPrice = storageListInfo.getInputGoodsInitPriceWithDouble();
                double tempPrice = BigDecimalUtils.mul(inputGoodsInitPrice, inputGoodsCount);
                totalPrice = BigDecimalUtils.add(totalPrice, tempPrice);
            }
        }
        int blueColor = mResources.getColor(R.color.color_0087FA);
        int fontSize = mResources.getDimensionPixelSize(com.stkj.common.R.dimen.sp_14);
        SpanUtils.with(storageAllPrice).append("总计: ¥ ").setFontSize(fontSize).setForegroundColor(blueColor).append(PriceUtils.formatPrice2(totalPrice)).create();
    }

    private boolean isRequestingSaveStorage;

    /**
     * 保存商品入库
     */
    private void saveGoodsToStorage() {
        if (isRequestingSaveStorage) {
            return;
        }
        List<Object> dataList = mStorageAdapter.getDataList();
        if (dataList.isEmpty()) {
            return;
        }
        List<GoodsSaveStorageInfo> saveStorageInfoList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof GoodsStorageListInfo) {
                GoodsStorageListInfo goodsStorageListInfo = (GoodsStorageListInfo) obj;
                String goodsName = goodsStorageListInfo.getGoodsName();
                //检查商品数量+进货价
                if (!goodsStorageListInfo.hasGoodsCount()) {
                    CommonDialogUtils.showTipsDialog(mActivity, goodsStorageListInfo.getGoodsName() + "\n" + "商品入库数量不能为0");
                    return;
                }
                if (!goodsStorageListInfo.hasGoodsInitPrice()) {
                    CommonDialogUtils.showTipsDialog(mActivity, goodsStorageListInfo.getGoodsName() + "\n" + "商品进货价不能为0");
                    return;
                }
                //检查生产日期+保质期
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
                GoodsSaveStorageInfo saveStorageInfo = new GoodsSaveStorageInfo();
                saveStorageInfo.setGoodsId(goodsStorageListInfo.getGoodsId());
                saveStorageInfo.setInUnitPrice(goodsStorageListInfo.getInputGoodsInitPrice());
                saveStorageInfo.setInCount(goodsStorageListInfo.isWeightGoods() ? goodsStorageListInfo.getWeightGoodsCount() : goodsStorageListInfo.getStandardGoodsCount());
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
                    mStorageAdapter.removeAllData();
                    rvGoodsStorageList.scrollToPosition(0);
                    //刷新tab页商品列表
                    EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
                    AppToast.toastMsg("入库成功");
                    refreshBottomGoodsStorage();
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

}
