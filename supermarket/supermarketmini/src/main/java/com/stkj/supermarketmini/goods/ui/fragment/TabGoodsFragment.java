package com.stkj.supermarketmini.goods.ui.fragment;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.picker.FilePickerHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.linelayout.LineTextView;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.callback.QrCodeListener;
import com.stkj.supermarketmini.base.callback.SimpleTextWatcher;
import com.stkj.supermarketmini.base.excel.Excel;
import com.stkj.supermarketmini.base.excel.service.IParseListener;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarketmini.base.ui.fragment.QrCodeAlertFragment;
import com.stkj.supermarketmini.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.supermarketmini.base.ui.widget.CommonSortImageView;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.base.utils.JacksonUtils;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.helper.GoodsCheckHelper;
import com.stkj.supermarketmini.goods.model.AddGoodsCateParams;
import com.stkj.supermarketmini.goods.model.AddGoodsResultWrapper;
import com.stkj.supermarketmini.goods.model.AddGoodsSpecParams;
import com.stkj.supermarketmini.goods.model.ErrorCheckGoodsBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsCate;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaleListQueryInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaleListResponse;
import com.stkj.supermarketmini.goods.model.GoodsSpec;
import com.stkj.supermarketmini.goods.service.GoodsService;
import com.stkj.supermarketmini.goods.ui.adapter.GoodsSaleListInfoViewHolder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import kotlin.Unit;
import retrofit2.Response;

/**
 * 商品管理页
 */
public class TabGoodsFragment extends BaseRecyclerFragment {

    private ImageView ivExportExcel;
    private RecyclerView rvGoodsList;
    private EditText etGoodsSearch;
    private ImageView ivClearSearch;
    private TextView tvGoodsSearch;
    private TextView tvGoodsResetAll;
    private AppSmartRefreshLayout srlGoodsSaleList;
    private FrameLayout flSearchEmpty;
    private CommonSortImageView ivSortStorage;
    private CommonSortImageView ivSortExpireDate;
    private CommonSortImageView ivSortPrice;
    private LineTextView ltvAddGoods;
    private LineTextView ltvExportExcel;
    private LineTextView ltvStorageGoods;
    private TextView ltvScanAddGoods;
    private ShapeLinearLayout sllAddGoods;
    private CommonRecyclerAdapter goodsSaleListAdapter;
    //网络请求查询参数
    private GoodsSaleListQueryInfo queryInfo = new GoodsSaleListQueryInfo();
    private int mLastRequestPage;

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_goods;
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
                    rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
                        rvGoodsList.scrollToPosition(0);
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
        srlGoodsSaleList = (AppSmartRefreshLayout) findViewById(R.id.srl_goods_sale_list);
        flSearchEmpty = (FrameLayout) findViewById(R.id.fl_search_empty);
        ivExportExcel = (ImageView) findViewById(R.id.iv_add_goods);
        ivExportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sllAddGoods.getVisibility() == View.GONE) {
                    sllAddGoods.setVisibility(View.VISIBLE);
                } else {
                    sllAddGoods.setVisibility(View.GONE);
                }
            }
        });
        rvGoodsList = (RecyclerView) findViewById(R.id.rv_content);
        etGoodsSearch = (EditText) findViewById(R.id.et_goods_search);
        ivClearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        tvGoodsSearch = (TextView) findViewById(R.id.tv_goods_search);
        tvGoodsResetAll = (TextView) findViewById(R.id.tv_goods_reset_all);
        ivClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoodsSearch.setText("");
            }
        });
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
        tvGoodsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearch();
            }
        });
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
        srlGoodsSaleList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                searchGoodsSaleList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                resetRequestPage();
                queryInfo.resetDefaultData();
                searchGoodsSaleList();
            }
        });
        goodsSaleListAdapter = new CommonRecyclerAdapter(false);
        goodsSaleListAdapter.addViewHolderFactory(new GoodsSaleListInfoViewHolder.Factory());
        goodsSaleListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                sllAddGoods.setVisibility(View.GONE);
                GoodsSaleListInfo goodsSaleListInfo = (GoodsSaleListInfo) obj;
                if (eventId == GoodsSaleListInfoViewHolder.EVENT_CLICK) {
                    GoodsDetailFragment goodsDetailFragment = new GoodsDetailFragment();
                    goodsDetailFragment.setGoodsId(goodsSaleListInfo.getId());
                    mActivity.addContentPlaceHolderFragment(goodsDetailFragment);
                } else if (eventId == GoodsSaleListInfoViewHolder.EVENT_LONG_CLICK) {
                    CommonAlertDialogFragment.build()
                            .setAlertTitleTxt("提示")
                            .setAlertContentTxt("确认删除以下商品吗?\n" + goodsSaleListInfo.getGoodsCode() + "\n" + goodsSaleListInfo.getGoodsName())
                            .setLeftNavTxt("确定")
                            .setRightNavTxt("取消")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    deleteGoods(goodsSaleListInfo);
                                }
                            }).show(mActivity);
                }
            }
        });
        rvGoodsList.setAdapter(goodsSaleListAdapter);
        sllAddGoods = (ShapeLinearLayout) findViewById(R.id.sll_add_goods);
        ltvAddGoods = (LineTextView) findViewById(R.id.ltv_add_goods);
        ltvExportExcel = (LineTextView) findViewById(R.id.ltv_export_excel);
        ltvScanAddGoods = (TextView) findViewById(R.id.ltv_scan_add_goods);
        ltvStorageGoods = (LineTextView) findViewById(R.id.ltv_storage_goods);
        ltvStorageGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sllAddGoods.setVisibility(View.GONE);
                GoodsSaveStorageFragment saveStorageFragment = new GoodsSaveStorageFragment();
                mActivity.addContentPlaceHolderFragment(saveStorageFragment);
            }
        });
        ltvScanAddGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sllAddGoods.setVisibility(View.GONE);
                showScanGoodsCode();
            }
        });
        ltvAddGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sllAddGoods.setVisibility(View.GONE);
                AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
                mActivity.addContentPlaceHolderFragment(addGoodsFragment);
            }
        });
        ltvExportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sllAddGoods.setVisibility(View.GONE);
                FilePickerHelper filePicker = getFilePicker();
                filePicker.setAppPickFileListener(new FilePickerHelper.OnPickFileListener() {
                    @Override
                    public void onPickFile(@Nullable Uri[] uri, int pickType) {
                        if (uri != null && uri.length > 0) {
                            processExcelFile(uri[0]);
                        }
                    }
                });
                filePicker.startPickFile(false);
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
                    etGoodsSearch.setText(result);
                    onClickSearch();
                }
            }
        });
        qrCodeAlertFragment.show(mActivity);
    }

    private void onClickSearch() {
        KeyBoardUtils.hideSoftKeyboard(mActivity, etGoodsSearch);
        etGoodsSearch.clearFocus();
        String searchKey = etGoodsSearch.getText().toString();
        rvGoodsList.scrollToPosition(0);
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

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            srlGoodsSaleList.autoRefresh();
        }
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
                            goodsSaleListAdapter.removeAllData();
                        }
                        GoodsSaleListResponse responseData = responseBaseResponse.getData();
                        if (responseBaseResponse.isSuccess() && responseData != null) {
                            List<GoodsSaleListInfo> dataRecords = responseData.getRecords();
                            if (dataRecords != null && !dataRecords.isEmpty()) {
                                if (currentPage == 1) {
                                    flSearchEmpty.setVisibility(View.GONE);
                                }
                                mLastRequestPage = currentPage;
                                goodsSaleListAdapter.addDataList(dataRecords);
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
                            goodsSaleListAdapter.removeData(goodsSaleListInfo);
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

    /**
     * 解析excel文件
     */
    private void processExcelFile(Uri excelFileUri) {
        ContentResolver contentResolver = mActivity.getContentResolver();
        try {
            String fileRealNameFromUri = AndroidUtils.getFileRealNameFromUri(excelFileUri);
            boolean isXLSL;
            if (fileRealNameFromUri.endsWith(".xlsx")) {
                isXLSL = true;
            } else if (fileRealNameFromUri.endsWith(".xls")) {
                isXLSL = false;
            } else {
                CommonDialogUtils.showTipsDialog(mActivity, "该文件不是excel文件");
                return;
            }
            List<GoodsBaseInfo> parseDataList = new ArrayList<>();
            Excel excel = Excel.get().readWith(contentResolver.openInputStream(excelFileUri));
            IParseListener<GoodsBaseInfo> parseListener = new IParseListener<GoodsBaseInfo>() {

                @Override
                public void onStartParse() {
                    showLoadingDialog("解析中");
                    LogHelper.print("--processExcelFile---onStartParse");
                }

                @Override
                public void onParse(GoodsBaseInfo goodsBaseInfo, JSONArray jsonObject) {
                    parseDataList.add(goodsBaseInfo);
                    LogHelper.print("--processExcelFile---onParse: " + goodsBaseInfo);
                }

                @Override
                public void onParseError(Exception e) {
                    hideLoadingDialog();
                    CommonDialogUtils.showTipsDialog(mActivity, "解析失败: " + e.getMessage());
                    LogHelper.print("--processExcelFile---onParseError: " + e.getMessage());
                }

                @Override
                public void onEndParse() {
                    checkParseData(parseDataList);
                    LogHelper.print("--processExcelFile---onEndParse size: " + parseDataList.size());
                }
            };
            if (isXLSL) {
                excel.doReadXLSX(parseListener, GoodsBaseInfo.class);
            } else {
                excel.doReadXLS(parseListener, GoodsBaseInfo.class);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(mActivity, "解析失败: " + e.getMessage());
        }
    }

    /**
     * 校验数据
     */
    private void checkParseData(List<GoodsBaseInfo> goodsBaseInfoList) {
        showLoadingDialog("校验数据中");
        List<ErrorCheckGoodsBaseInfo> errorGoodsList = new ArrayList<>();
        List<GoodsBaseInfo> successGoodsList = new ArrayList<>();
        for (GoodsBaseInfo goodsBaseInfo : goodsBaseInfoList) {
            //条形码
            String goodsCode = goodsBaseInfo.getGoodsCode();
            //替换条形码空格
            goodsCode = goodsCode.replaceAll(" ", "");
            goodsBaseInfo.setGoodsCode(goodsCode);
            if (TextUtils.isEmpty(goodsCode)) {
                continue;
            }
            if (goodsCode.length() < 7) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "商品条码最小为7位!"));
                continue;
            }
            //商品名称
            String goodsName = goodsBaseInfo.getGoodsName();
            //替换商品名称空格
            goodsName = goodsName.replaceAll(" ", "");
            goodsBaseInfo.setGoodsName(goodsName);
            if (TextUtils.isEmpty(goodsName)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "商品名称不能为空!"));
                continue;
            }
            //商品分类
            String goodsCate = goodsBaseInfo.getGoodsCategoryStr();
            //替换商品分类空格
            goodsCate = goodsCate.replaceAll(" ", "");
            goodsBaseInfo.setGoodsCategoryStr(goodsCate);
            if (TextUtils.isEmpty(goodsCate)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请设置商品分类"));
                continue;
            }

            //零售价
            String goodsUnitPrice = goodsBaseInfo.getGoodsUnitPrice();
            //替换零售价空格
            goodsUnitPrice = goodsUnitPrice.replaceAll(" ", "");
            goodsBaseInfo.setGoodsUnitPrice(goodsUnitPrice);
            if (TextUtils.isEmpty(goodsUnitPrice)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "商品零售价不能为空!"));
                continue;
            } else {
                boolean validDecimalNumber = GoodsCheckHelper.isValidDecimalNumber(goodsUnitPrice);
                if (!validDecimalNumber) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "商品零售价数值有误!"));
                    continue;
                }
            }
            int goodsType = goodsBaseInfo.getGoodsType();
            //商品规格
            String goodsSpec = goodsBaseInfo.getGoodsSpecStr();
            //替换商品规格空格
            goodsSpec = goodsSpec.replaceAll(" ", "");
            goodsBaseInfo.setGoodsSpecStr(goodsSpec);
            if (goodsType != GoodsConstants.TYPE_GOODS_WEIGHT && TextUtils.isEmpty(goodsSpec)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请设置商品规格"));
                continue;
            }
            //初始库存
            String goodsInitStock = goodsBaseInfo.getGoodsInitStock();
            //替换初始库存空格
            goodsInitStock = goodsInitStock.replaceAll(" ", "");
            goodsBaseInfo.setGoodsInitStock(goodsInitStock);
            //进货价格
            String goodsWholesalePrice = goodsBaseInfo.getGoodsInitPrice();
            //替换进货价格空格
            goodsWholesalePrice = goodsWholesalePrice.replaceAll(" ", "");
            goodsBaseInfo.setGoodsInitPrice(goodsWholesalePrice);
            if (!TextUtils.isEmpty(goodsInitStock) && TextUtils.isEmpty(goodsWholesalePrice)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写进货价格"));
                continue;
            }
            if (TextUtils.isEmpty(goodsInitStock) && !TextUtils.isEmpty(goodsWholesalePrice)) {
                errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写初始库存"));
                return;
            }
            if (!TextUtils.isEmpty(goodsInitStock) && !TextUtils.isEmpty(goodsWholesalePrice)) {
                boolean validDecimalNumber1 = GoodsCheckHelper.isValidDecimalNumber(goodsWholesalePrice);
                if (!validDecimalNumber1) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "进货价格数值有误!"));
                    continue;
                }
                boolean validDecimalNumber2 = GoodsCheckHelper.isValidDecimalNumber(goodsInitStock);
                if (!validDecimalNumber2) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "初始库存数值有误!"));
                    continue;
                }
            }
            //生产日期
            String goodsProductDate = goodsBaseInfo.getProductDate();
            //替换生产日期空格
            goodsProductDate = goodsProductDate.replaceAll(" ", "");
            goodsBaseInfo.setProductDate(goodsProductDate);
            //保质期
            String expireDays = goodsBaseInfo.getExpireDays();
            //替换保质期空格
            expireDays = expireDays.replaceAll(" ", "");
            goodsBaseInfo.setExpireDays(expireDays);
            if (!TextUtils.isEmpty(goodsProductDate)) {
                if (TextUtils.isEmpty(goodsInitStock) || TextUtils.isEmpty(goodsWholesalePrice)) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写进货价格和初始库存"));
                    continue;
                }
                if (TextUtils.isEmpty(expireDays)) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写保质期"));
                    continue;
                }
            }
            if (!TextUtils.isEmpty(expireDays)) {
                if (TextUtils.isEmpty(goodsInitStock) || TextUtils.isEmpty(goodsWholesalePrice)) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写进货价格和初始库存"));
                    continue;
                }
                if (TextUtils.isEmpty(goodsProductDate)) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "请填写生产日期"));
                    CommonDialogUtils.showTipsDialog(mActivity, "请填写生产日期");
                    continue;
                }
            }
            if (!TextUtils.isEmpty(expireDays) && !TextUtils.isEmpty(goodsProductDate)) {
                boolean validDecimalNumber = GoodsCheckHelper.isValidDecimalNumber(expireDays);
                if (!validDecimalNumber) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "保质期数值有误!"));
                    continue;
                }
                boolean validDate = GoodsCheckHelper.isValidDate(goodsProductDate);
                if (!validDate) {
                    errorGoodsList.add(new ErrorCheckGoodsBaseInfo(goodsBaseInfo, "生产日期数值有误!"));
                    continue;
                } else {
                    goodsBaseInfo.setProductDate(GoodsCheckHelper.formatValidDate(goodsProductDate));
                }
            }
            successGoodsList.add(goodsBaseInfo);
        }
        hideLoadingDialog();
        if (errorGoodsList.size() > 0) {
            //解析部分数据有误
            StringBuilder builder = new StringBuilder();
            builder.append("以下商品数据有误，请更改后重新导入:\n");
            for (ErrorCheckGoodsBaseInfo errorCheckGoodsBaseInfo : errorGoodsList) {
                GoodsBaseInfo goodsBaseInfo = errorCheckGoodsBaseInfo.getGoodsBaseInfo();
                builder.append(goodsBaseInfo.getGoodsCode()).append(" 原因:").append(errorCheckGoodsBaseInfo.getErrorMsg());
                builder.append("\n----------------------\n");
            }
            CommonDialogUtils.showTipsDialog(mActivity, builder.toString());
        } else {
            if (successGoodsList.isEmpty()) {
                CommonDialogUtils.showTipsDialog(mActivity, "未解析到商品数据,请重试");
            } else {
                //解析结果
                StringBuilder builder = new StringBuilder();
                builder.append("共计解析数据").append(successGoodsList.size()).append("个,点击确认上传:\n");
                for (GoodsBaseInfo goodsBaseInfo : successGoodsList) {
                    builder.append(goodsBaseInfo.getGoodsCode()).append("|").append(goodsBaseInfo.getGoodsName());
                    builder.append("\n----------------------\n");
                }
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt(builder.toString())
                        .setLeftNavTxt("上传")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                uploadGoodsBaseInfo(successGoodsList);
                            }
                        })
                        .setRightNavTxt("取消")
                        .show(mActivity);
            }
        }
    }

    /**
     * 添加商品到后台
     */
    private void uploadGoodsBaseInfo(List<GoodsBaseInfo> goodsBaseInfoList) {
        showLoadingDialog("上传商品数据中");
        GoodsBaseInfo lastGoodsBaseInfo = goodsBaseInfoList.get(goodsBaseInfoList.size() - 1);
        List<AddGoodsResultWrapper> addGoodsResultWrapperList = new ArrayList<>();
        Observable.fromIterable(goodsBaseInfoList)
                .concatMap(new Function<GoodsBaseInfo, ObservableSource<AddGoodsResultWrapper>>() {
                    @Override
                    public ObservableSource<AddGoodsResultWrapper> apply(GoodsBaseInfo goodsBaseInfo) throws Throwable {
                        GoodsService goodsService = RetrofitManager.INSTANCE.getDefaultRetrofit()
                                .create(GoodsService.class);
                        try {
                            int goodsType = goodsBaseInfo.getGoodsType();
                            String goodsCode = goodsBaseInfo.getGoodsCode();
                            showLoadingDialog("开始上传商品:" + goodsCode);
                            //根据条码获取商品信息
                            Response<BaseResponse<String>> requestCodeDetailResponse = goodsService.requestCodeDetailSync(goodsBaseInfo.getGoodsCode()).execute();
                            if (requestCodeDetailResponse.isSuccessful()) {
                                BaseResponse<String> body = requestCodeDetailResponse.body();
                                if (body != null) {
                                    String data = body.getData();
                                    if (data != null) {
                                        GoodsQrCodeDetail goodsQrCodeDetail = JacksonUtils.convertJsonObject(data, GoodsQrCodeDetail.class);
                                        if (goodsQrCodeDetail != null) {
                                            showLoadingDialog("获取条码信息成功:" + goodsCode);
                                            String qrCodeDetailGoodsName = goodsQrCodeDetail.getGoodsName();
                                            if (!TextUtils.isEmpty(qrCodeDetailGoodsName)) {
                                                goodsBaseInfo.setGoodsName(qrCodeDetailGoodsName);
                                            }
                                            String qrCodeDetailImg = goodsQrCodeDetail.getImg();
                                            if (!TextUtils.isEmpty(qrCodeDetailImg)) {
                                                goodsBaseInfo.setGoodsImg(qrCodeDetailImg);
                                            }
                                            String qrCodeDetailSpec = goodsQrCodeDetail.getSpec();
                                            if (!TextUtils.isEmpty(qrCodeDetailSpec)) {
                                                goodsBaseInfo.setGoodsNote(qrCodeDetailSpec);
                                            }
                                        }
                                    }
                                }
                            }
                            //获取现有商品分类和规格(新增商品不存在，则自动添加)
                            Response<BaseResponse<List<GoodsCate>>> goodsCateListSync = RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).getGoodsCateListSync(goodsType).execute();
                            showLoadingDialog("开始添加分类:" + goodsCode);
                            if (goodsCateListSync.isSuccessful()) {
                                BaseResponse<List<GoodsCate>> body = goodsCateListSync.body();
                                if (body != null) {
                                    String currentGoodsCate = goodsBaseInfo.getGoodsCategoryStr();
                                    List<GoodsCate> goodsCateList = body.getData();
                                    GoodsCate matcherGoodsCate = null;
                                    if (goodsCateList != null) {
                                        for (GoodsCate goodsCate : goodsCateList) {
                                            if (TextUtils.equals(goodsCate.getName(), currentGoodsCate)) {
                                                matcherGoodsCate = goodsCate;
                                                break;
                                            }
                                        }
                                    }
                                    if (matcherGoodsCate != null) {
                                        showLoadingDialog("添加分类成功:" + goodsCode);
                                        goodsBaseInfo.setGoodsCategory(matcherGoodsCate.getId());
                                    } else {
                                        Response<BaseResponse<String>> addGoodsCateSync = goodsService.addGoodsCateSync(new AddGoodsCateParams(currentGoodsCate, goodsType)).execute();
                                        if (addGoodsCateSync.isSuccessful()) {
                                            BaseResponse<String> addGoodsCateBody = addGoodsCateSync.body();
                                            if (addGoodsCateBody != null && !TextUtils.isEmpty(addGoodsCateBody.getData())) {
                                                showLoadingDialog("添加分类成功:" + goodsCode);
                                                goodsBaseInfo.setGoodsCategory(addGoodsCateBody.getData());
                                            } else {
                                                if (addGoodsCateBody != null) {
                                                    return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加分类失败: " + addGoodsCateBody.getMsg()));
                                                } else {
                                                    return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加分类失败: body is null"));
                                                }
                                            }
                                        } else {
                                            return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加分类失败: response code " + addGoodsCateSync.code()));
                                        }
                                    }
                                }
                            }
                            //获取商品规格(称重商品默认只有千克)
                            if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
                                showLoadingDialog("添加规格成功:" + goodsCode);
                                goodsBaseInfo.setGoodsSpecStr(GoodsConstants.SPEC_WEIGHT_GOODS);
                            } else {
                                showLoadingDialog("开始添加规格:" + goodsCode);
                                Response<BaseResponse<List<GoodsSpec>>> goodsSpecListSync = RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).getGoodsSpecListSync(goodsType).execute();
                                if (goodsSpecListSync.isSuccessful()) {
                                    BaseResponse<List<GoodsSpec>> body = goodsSpecListSync.body();
                                    if (body != null) {
                                        String currentGoodsSpec = goodsBaseInfo.getGoodsSpecStr();
                                        List<GoodsSpec> goodsSpecList = body.getData();
                                        GoodsSpec matcherGoodsSpec = null;
                                        if (goodsSpecList != null) {
                                            for (GoodsSpec goodsSpec : goodsSpecList) {
                                                if (TextUtils.equals(goodsSpec.getName(), currentGoodsSpec)) {
                                                    matcherGoodsSpec = goodsSpec;
                                                    break;
                                                }
                                            }
                                        }
                                        if (matcherGoodsSpec != null) {
                                            showLoadingDialog("添加规格成功:" + goodsCode);
                                            goodsBaseInfo.setGoodsSpec(matcherGoodsSpec.getId());
                                        } else {
                                            Response<BaseResponse<String>> addGoodsSpecSync = goodsService.addGoodsSpecSync(new AddGoodsSpecParams(currentGoodsSpec, goodsType)).execute();
                                            if (addGoodsSpecSync.isSuccessful()) {
                                                BaseResponse<String> addGoodsSpecBody = addGoodsSpecSync.body();
                                                if (addGoodsSpecBody != null && !TextUtils.isEmpty(addGoodsSpecBody.getData())) {
                                                    showLoadingDialog("添加规格成功:" + goodsCode);
                                                    goodsBaseInfo.setGoodsSpec(addGoodsSpecBody.getData());
                                                } else {
                                                    if (addGoodsSpecBody != null) {
                                                        return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加规格失败: " + addGoodsSpecBody.getMsg()));
                                                    } else {
                                                        return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加规格失败: body is null"));
                                                    }
                                                }
                                            } else {
                                                return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加规格失败:response code " + addGoodsSpecSync.code()));
                                            }
                                        }
                                    }
                                }
                            }
                            //添加商品接口
                            showLoadingDialog("开始调用添加商品接口:" + goodsCode);
                            Response<BaseResponse<String>> addGoodsExecute = goodsService.addGoodsSync(goodsBaseInfo).execute();
                            if (addGoodsExecute.isSuccessful()) {
                                BaseResponse<String> addGoodsBody = addGoodsExecute.body();
                                if (addGoodsBody == null) {
                                    return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加商品接口调用失败:body is null"));
                                }
                                if (!addGoodsBody.isSuccess()) {
                                    return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加商品接口调用失败: " + addGoodsBody.getMsg()));
                                }
                            } else {
                                return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, "添加商品接口调用失败:response code " + addGoodsExecute.code()));
                            }
                            showLoadingDialog("添加商品成功:" + goodsCode);
                            return Observable.just(AddGoodsResultWrapper.newSuccess(goodsBaseInfo));
                        } catch (Throwable e) {
                            e.printStackTrace();
                            return Observable.just(AddGoodsResultWrapper.newError(goodsBaseInfo, e.getMessage()));
                        }
                    }
                })
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<AddGoodsResultWrapper>() {
                    @Override
                    protected void onSuccess(AddGoodsResultWrapper resultWrapper) {
                        addGoodsResultWrapperList.add(resultWrapper);
                        if (resultWrapper.getGoodsBaseInfo() == lastGoodsBaseInfo) {
                            hideLoadingDialog();
                            //上传完毕
                            //解析部分数据有误
                            StringBuilder builder = new StringBuilder();
                            builder.append("以下为添加商品结果:\n");
                            int successCount = 0;
                            int errorCount = 0;
                            int totalCount = addGoodsResultWrapperList.size();
                            for (AddGoodsResultWrapper addGoodsResultWrapper : addGoodsResultWrapperList) {
                                GoodsBaseInfo goodsBaseInfo = addGoodsResultWrapper.getGoodsBaseInfo();
                                if (addGoodsResultWrapper.isAddSuccess()) {
                                    successCount++;
                                    builder.append(goodsBaseInfo.getGoodsCode()).append("添加成功");
                                    builder.append("\n----------------------\n");
                                } else {
                                    errorCount++;
                                    builder.append(goodsBaseInfo.getGoodsCode()).append("添加失败:").append(addGoodsResultWrapper.getMsg());
                                    builder.append("\n----------------------\n");
                                }
                            }
                            builder.insert(0, "共计处理" + totalCount + "个 , 成功" + successCount + "个 , 失败" + errorCount + "个。\n");
                            int finalSuccessCount = successCount;
                            CommonDialogUtils.showTipsDialog(mActivity, builder.toString(), "确定", new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    if (finalSuccessCount > 0) {
                                        //获取全部商品
                                        etGoodsSearch.setText("");
                                        onClickSearch();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private FilePickerHelper getFilePicker() {
        return mActivity.getWeakRefHolder(FilePickerHelper.class);
    }
}
