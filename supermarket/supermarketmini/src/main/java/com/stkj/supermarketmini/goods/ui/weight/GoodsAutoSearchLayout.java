package com.stkj.supermarketmini.goods.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.ui.widget.CommonActionDoneEditText;
import com.stkj.supermarketmini.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarketmini.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.service.GoodsService;
import com.stkj.supermarketmini.goods.ui.adapter.GoodsAutoSearchInfoViewHolder;

import java.util.List;

public class GoodsAutoSearchLayout extends FrameLayout {
    private ShapeLinearLayout sllSearch;
    private RecyclerView rvSearchContent;
    private View searchContentPlaceholder;
    private LinearLayout llSearchContent;
    private CommonActionDoneEditText etGoodsSearch;
    private CommonRecyclerAdapter commonRecyclerAdapter;
    private LifecycleOwner lifecycleOwner;
    private GoodsAutoSearchListener goodsAutoSearchListener;
    private String mLastSearchKey;
    private ImageView ivClearSearch;

    public GoodsAutoSearchLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        int searchRightMargin = 0;
        int searchLeftMargin = 0;
        if (attributeSet != null) {
            int defMargin = context.getResources().getDimensionPixelSize(com.stkj.common.R.dimen.dp_11);
            TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.GoodsAutoSearchLayout);
            searchRightMargin = array.getDimensionPixelSize(R.styleable.GoodsAutoSearchLayout_gasl_search_right_margin, defMargin);
            searchLeftMargin = array.getDimensionPixelSize(R.styleable.GoodsAutoSearchLayout_gasl_search_left_margin, defMargin);
            array.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.include_goods_auto_search, this);
        sllSearch = (ShapeLinearLayout) findViewById(R.id.sll_search);
        searchContentPlaceholder = (View) findViewById(R.id.search_content_placeholder);
        searchContentPlaceholder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchGoodsList();
            }
        });
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
                    llSearchContent.setVisibility(GONE);
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
                        llSearchContent.setVisibility(VISIBLE);
                    } else {
                        llSearchContent.setVisibility(GONE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
//                    hideSearchGoodsList();
                }
            }
        });
        llSearchContent = findViewById(R.id.ll_search_content);
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
        if (searchRightMargin != 0 || searchLeftMargin != 0) {
            setSearchMargin(searchLeftMargin, searchRightMargin);
        }
    }

    public void setGoodsAutoSearchListener(LifecycleOwner lifecycleOwner, GoodsAutoSearchListener autoSearchItemListener) {
        this.lifecycleOwner = lifecycleOwner;
        this.goodsAutoSearchListener = autoSearchItemListener;
    }

    public void setSearchMargin(int searchLeftMargin, int searchRightMargin) {
        if (sllSearch != null) {
            ViewGroup.LayoutParams layoutParams = sllSearch.getLayoutParams();
            if (layoutParams instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                marginLayoutParams.leftMargin = searchLeftMargin;
                marginLayoutParams.rightMargin = searchRightMargin;
                sllSearch.setLayoutParams(marginLayoutParams);
            }
        }
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
        if (autoGetGoodsDetail) {
            etGoodsSearch.setText(key);
        }
        mLastSearchKey = key;
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).searchGoodsList(key).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(lifecycleOwner)).subscribe(new DefaultObserver<BaseResponse<List<GoodsIdBaseListInfo>>>() {
            @Override
            protected void onSuccess(BaseResponse<List<GoodsIdBaseListInfo>> listBaseResponse) {
                if (!autoGetGoodsDetail && TextUtils.isEmpty(mLastSearchKey)) {
                    llSearchContent.setVisibility(GONE);
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
        llSearchContent.setVisibility(VISIBLE);
        commonRecyclerAdapter.removeAllData();
        if (dataList == null || dataList.isEmpty()) {
            commonRecyclerAdapter.addData(new PlaceModel(R.layout.item_goods_auto_search_empty, true));
        } else {
            commonRecyclerAdapter.addDataList(dataList);
        }
    }

    public void hideSearchGoodsList() {
        llSearchContent.setVisibility(VISIBLE);
        etGoodsSearch.clearFocus();
    }

    public void clearSearchKey() {
        etGoodsSearch.setText("");
        etGoodsSearch.clearFocus();
    }

}
