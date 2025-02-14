package com.stkj.supermarket.goods.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceModel;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceRecyclerViewHolder;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.ui.widget.CommonActionDoneEditText;
import com.stkj.supermarket.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarket.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.service.GoodsService;
import com.stkj.supermarket.goods.ui.adapter.GoodsAutoSearchInfoViewHolder;

import java.util.List;

public class GoodsAutoSearchLayout extends FrameLayout {
    private RecyclerView rvSearchContent;
    private CommonActionDoneEditText etGoodsSearch;
    private CommonRecyclerAdapter commonRecyclerAdapter;
    private LifecycleOwner lifecycleOwner;
    private GoodsAutoSearchListener goodsAutoSearchListener;
    private String mLastSearchKey;
    private ImageView ivClearSearch;

    public GoodsAutoSearchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_goods_auto_search, this);
        ivClearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        ivClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoodsSearch.setText("");
            }
        });
        etGoodsSearch = (CommonActionDoneEditText) findViewById(R.id.et_goods_search);
        etGoodsSearch.setOnEditWatchListener(new CommonActionDoneEditText.OnEditWatchListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    ivClearSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAfterTextChanged(String input) {
                if (TextUtils.isEmpty(input)) {
                    mLastSearchKey = "";
                    rvSearchContent.setVisibility(GONE);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoSearch(input, false);
                        }
                    }, 100);
                }
            }

            @Override
            public void onActionDone() {
                KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
            }

            @Override
            public void onFocusChange(boolean hasFocus) {
                if (hasFocus) {
                    String currentTxt = etGoodsSearch.getText().toString();
                    if (!TextUtils.isEmpty(currentTxt)) {
                        rvSearchContent.setVisibility(VISIBLE);
                    } else {
                        rvSearchContent.setVisibility(GONE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
//                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent = (RecyclerView) findViewById(R.id.rv_search_content);
        commonRecyclerAdapter = new CommonRecyclerAdapter(false);
        commonRecyclerAdapter.addViewHolderFactory(new GoodsAutoSearchInfoViewHolder.Factory());
        commonRecyclerAdapter.addViewHolderFactory(new PlaceRecyclerViewHolder.Factory(R.layout.item_goods_auto_search_empty));
        commonRecyclerAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                if (obj instanceof GoodsIdBaseListInfo) {
                    GoodsIdBaseListInfo goodsIdBaseListInfo = (GoodsIdBaseListInfo) obj;
                    getAutoSearchGoodsDetail(goodsIdBaseListInfo);
                    KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
                    hideSearchGoodsList();
                } else if (obj instanceof PlaceModel) {
                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent.setAdapter(commonRecyclerAdapter);
    }

    public void setGoodsAutoSearchListener(LifecycleOwner lifecycleOwner, GoodsAutoSearchListener autoSearchItemListener) {
        this.lifecycleOwner = lifecycleOwner;
        this.goodsAutoSearchListener = autoSearchItemListener;
    }

    /**
     * @param key                搜索 key
     * @param autoGetGoodsDetail 自动调用获取商品详细信息
     */
    public void autoSearch(String key, boolean autoGetGoodsDetail) {
        if (lifecycleOwner == null) {
            return;
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        //已经在搜索了
        if (!autoGetGoodsDetail && TextUtils.equals(key, mLastSearchKey)) {
            return;
        }
        //注释掉扫码枪扫商品条码不填充数据
//        if (autoGetGoodsDetail) {
//            etGoodsSearch.setText(key);
//        }
        mLastSearchKey = key;
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).searchGoodsList(key).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(lifecycleOwner)).subscribe(new DefaultObserver<BaseResponse<List<GoodsIdBaseListInfo>>>() {
            @Override
            protected void onSuccess(BaseResponse<List<GoodsIdBaseListInfo>> listBaseResponse) {
                if (!autoGetGoodsDetail && TextUtils.isEmpty(mLastSearchKey)) {
                    rvSearchContent.setVisibility(GONE);
                    return;
                }
                if (!autoGetGoodsDetail && !TextUtils.equals(key, mLastSearchKey)) {
                    return;
                }
                if (listBaseResponse.isSuccess()) {
                    List<GoodsIdBaseListInfo> goodsIdBaseListInfoList = listBaseResponse.getData();
                    if (autoGetGoodsDetail && goodsIdBaseListInfoList.size() == 1) {
                        hideSearchGoodsList();
                        getAutoSearchGoodsDetail(goodsIdBaseListInfoList.get(0));
                    } else {
                        showSearchGoodsList(goodsIdBaseListInfoList);
                    }
                    if (goodsAutoSearchListener != null) {
                        goodsAutoSearchListener.onSearchGoodsList(key, goodsIdBaseListInfoList);
                    }
                } else {
                    showSearchGoodsList(null);
                    if (goodsAutoSearchListener != null) {
                        goodsAutoSearchListener.onSearchGoodsList(key, null);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                showSearchGoodsList(null);
                if (goodsAutoSearchListener != null) {
                    goodsAutoSearchListener.onSearchGoodsList(key, null);
                }
            }
        });
    }

    /**
     * 搜索商品详细信息
     */
    private void getAutoSearchGoodsDetail(GoodsIdBaseListInfo goodsIdBaseListInfo) {
        if (goodsAutoSearchListener != null) {
            goodsAutoSearchListener.onStartGetGoodsItemDetail(goodsIdBaseListInfo);
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).getGoodsDetail(goodsIdBaseListInfo.getId()).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(lifecycleOwner)).subscribe(new DefaultObserver<BaseResponse<GoodsSaleListInfo>>() {
            @Override
            protected void onSuccess(BaseResponse<GoodsSaleListInfo> response) {
                GoodsSaleListInfo data = response.getData();
                if (response.isSuccess() && data != null) {
                    if (goodsAutoSearchListener != null) {
                        clearSearchKey();
                        goodsAutoSearchListener.onSuccessGetGoodsItemDetail(data);
                    }
                } else {
                    if (goodsAutoSearchListener != null) {
                        goodsAutoSearchListener.onErrorGetGoodsItemDetail(goodsIdBaseListInfo, "获取商品信息失败");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (goodsAutoSearchListener != null) {
                    goodsAutoSearchListener.onErrorGetGoodsItemDetail(goodsIdBaseListInfo, "获取商品信息失败:" + e.getMessage());
                }
            }
        });
    }

    private void showSearchGoodsList(List<GoodsIdBaseListInfo> dataList) {
        rvSearchContent.setVisibility(VISIBLE);
        commonRecyclerAdapter.removeAllData();
        if (dataList == null || dataList.isEmpty()) {
            commonRecyclerAdapter.addData(new PlaceModel(R.layout.item_goods_auto_search_empty, true));
        } else {
            commonRecyclerAdapter.addDataList(dataList);
        }
    }

    public void hideSearchGoodsList() {
        rvSearchContent.setVisibility(GONE);
        etGoodsSearch.clearFocus();
    }

    public void clearSearchKey() {
        etGoodsSearch.setText("");
        etGoodsSearch.clearFocus();
        rvSearchContent.setVisibility(GONE);
    }

}
