package com.stkj.supermarket.pay.ui.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.model.CBGFacePassRecognizeResult;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultDisposeObserver;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.UnderlineTextView;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.common.utils.TimeUtils;
import com.stkj.deviceinterface.callback.OnPrintListener;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.deviceinterface.model.PrinterData;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.callback.OnFacePassConfirmListener;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.tts.TTSVoiceHelper;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.JacksonUtils;
import com.stkj.supermarket.base.utils.PriceUtils;
import com.stkj.supermarket.consumer.ConsumerManager;
import com.stkj.supermarket.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarket.goods.data.RefreshSearchGoodsListEvent;
import com.stkj.supermarket.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.ui.widget.GoodsAutoSearchLayout;
import com.stkj.supermarket.home.helper.CBGCameraHelper;
import com.stkj.supermarket.home.model.StoreInfo;
import com.stkj.supermarket.home.ui.activity.MainActivity;
import com.stkj.supermarket.login.callback.LoginCallback;
import com.stkj.supermarket.login.helper.LoginHelper;
import com.stkj.supermarket.pay.callback.OnCalculateListener;
import com.stkj.supermarket.pay.callback.OnPayListener;
import com.stkj.supermarket.pay.data.PayConstants;
import com.stkj.supermarket.pay.helper.OrderHistoryDBHelper;
import com.stkj.supermarket.pay.helper.PayHelper;
import com.stkj.supermarket.pay.model.AddOrderGoodsDetail;
import com.stkj.supermarket.pay.model.AddOrderRequest;
import com.stkj.supermarket.pay.model.AddOrderResult;
import com.stkj.supermarket.pay.model.ConsumeOrderRequest;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.pay.model.OrderHistoryListRequestPageParams;
import com.stkj.supermarket.pay.model.OrderHistoryListResponse;
import com.stkj.supermarket.pay.model.OrderHistoryResponse;
import com.stkj.supermarket.pay.model.PayHistoryOrderInfo;
import com.stkj.supermarket.pay.model.WaitHistoryOrderInfo;
import com.stkj.supermarket.pay.service.PayService;
import com.stkj.supermarket.pay.ui.adapter.GoodsOrderListInfoViewHolder;
import com.stkj.supermarket.pay.ui.adapter.GoodsWaitHistoryOrderInfoViewHolder;
import com.stkj.supermarket.pay.ui.widget.SimpleCalculator;
import com.stkj.supermarket.setting.data.PaymentSettingMMKV;
import com.stkj.supermarket.setting.helper.FacePassHelper;
import com.stkj.supermarket.setting.helper.StoreInfoHelper;
import com.stkj.supermarket.setting.model.FacePassPeopleInfo;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

/**
 * 收银页面
 */
public class TabPayFragment extends BaseRecyclerFragment implements OnPayListener, OrderHistoryDBHelper.OnOrderHistoryListener, OrderHistoryDetailAlertFragment.OnConfirmOrderListener, OnFacePassConfirmListener, WeightGoodsPayFragment.OnWeightGoodsPayListener {

    private LinearLayout llChangeOrderPrice;
    private ShapeTextView stvResetOrderPrice;
    private ShapeLinearLayout sllGoodsWeight;
    private LinearLayout llFastPayPage;
    private SimpleCalculator calculateFastPay;
    private ShapeFrameLayout sflGoodsListPay;
    private RecyclerView rvGoodsOrderList;
    private LinearLayout llOrderListEmpty;
    private ShapeLinearLayout sllFastPay;
    private GoodsAutoSearchLayout goodsAutoSearch;
    private FrameLayout flGoPay;
    private TextView orderAllPrice;
    private UnderlineTextView tvBackFastPay;
    private ShapeTextView stvChangeOrderPrice;
    private ShapeTextView stvMoJiao;
    private ShapeTextView stvMoFen;
    private TextView payAllPrice;
    private LinearLayout llPrintOrder;
    private ImageView ivPrintOrderCheck;
    private LinearLayout llWeixinPay;
    private ImageView ivWeixinPay;
    private TextView tvWeixinPay;
    private LinearLayout llAlipayPay;
    private LinearLayout llCashPay;
    private LinearLayout llFacePay;
    private LinearLayout llOtherPay;
    private ShapeTextView stvClearOrder;
    private ShapeTextView stvWaitOrder;
    private ShapeTextView stvGoPay;
    private ShapeLinearLayout sflPayStatus;
    private ImageView ivPayStatus;
    private TextView tvPayStatus;
    private ShapeTextView tvCurrentOrder;
    private ShapeTextView tvLastOrder;
    private ShapeTextView tvOrderHistory;
    private RecyclerView rvWaitHistoryOrder;
    //当前订单
    private PayHistoryOrderInfo mCurrentOrderInfo;
    //上一个订单
    private PayHistoryOrderInfo mLastOrderInfo;
    //订单商品列表
    private CommonRecyclerAdapter mGoodsOrderList;
    //历史订单列表
    private CommonRecyclerAdapter mWaitHistoryOrderList;
    //请求参数
    private AddOrderRequest mAddOrderRequest = new AddOrderRequest();
    private PayOrderTipsDialog mPayOrderTipsDialog;
    //称重商品
    private WeightGoodsPayFragment mWeightGoodsFragment;
    //挂单时间格式化
    private SimpleDateFormat waitOrderDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_pay;
    }

    @Override
    protected void initViews(View rootView) {
        llChangeOrderPrice = (LinearLayout) findViewById(R.id.ll_change_order_price);
        if (LoginHelper.INSTANCE.hasPermissionChangePrice()) {
            llChangeOrderPrice.setVisibility(View.VISIBLE);
        } else {
            llChangeOrderPrice.setVisibility(View.GONE);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsAutoSearch.hideSearchGoodsList();
            }
        });
        stvResetOrderPrice = (ShapeTextView) findViewById(R.id.stv_reset_order_price);
        stvResetOrderPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshOrderListAndPrice();
            }
        });
        sflGoodsListPay = (ShapeFrameLayout) findViewById(R.id.sfl_goods_list_pay);
        rvGoodsOrderList = (RecyclerView) findViewById(R.id.rv_goods_order_list);
        mGoodsOrderList = new CommonRecyclerAdapter(false);
        mGoodsOrderList.addViewHolderFactory(new GoodsOrderListInfoViewHolder.Factory());
        mGoodsOrderList.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsOrderListInfoViewHolder.EVENT_CLICK) {

                } else if (eventId == GoodsOrderListInfoViewHolder.EVENT_DELETE) {
                    mGoodsOrderList.removeData(obj);
                    refreshOrderListAndPrice();
                } else if (eventId == GoodsOrderListInfoViewHolder.EVENT_REFRESH_PRICE) {
                    refreshOrderListAndPrice();
                }
            }
        });
        rvGoodsOrderList.setAdapter(mGoodsOrderList);
        llOrderListEmpty = (LinearLayout) findViewById(R.id.ll_order_list_empty);
        sllFastPay = (ShapeLinearLayout) findViewById(R.id.sll_fast_pay);
        goodsAutoSearch = (GoodsAutoSearchLayout) findViewById(R.id.goods_auto_search);
        goodsAutoSearch.setGoodsAutoSearchListener(this, new GoodsAutoSearchListener() {
            @Override
            public void onStartGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo) {
                showLoadingDialog();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(GoodsSaleListInfo saleListInfo) {
                hideLoadingDialog();
                //正在支付中，不添加商品
                PayHelper payHelper = getPayHelper();
                if (payHelper.isPaying()) {
                    return;
                }
                //称重商品跳转到称重页面
                if (saleListInfo.isWeightGoods()) {
                    if (mWeightGoodsFragment == null) {
                        mWeightGoodsFragment = new WeightGoodsPayFragment();
                        mWeightGoodsFragment.setWeightGoodsPayListener(TabPayFragment.this);
                        mWeightGoodsFragment.setNeedSelectedGoods(saleListInfo);
                        FragmentUtils.safeReplaceFragment(getChildFragmentManager(), mWeightGoodsFragment, R.id.fl_pay_second_content);
                    } else {
                        mWeightGoodsFragment.setNeedSelectedGoods(saleListInfo);
                        FragmentUtils.safeAttachFragment(getChildFragmentManager(), mWeightGoodsFragment);
                    }
                    return;
                }
                llOrderListEmpty.setVisibility(View.GONE);
                //判断当前是否包含,包含数量+1(非称重商品)
                List<Object> dataList = mGoodsOrderList.getDataList();
                int size = dataList.size();
                for (int i = 0; i < size; i++) {
                    GoodsOrderListInfo orderListInfo = (GoodsOrderListInfo) dataList.get(i);
                    if (!orderListInfo.isWeightGoods()) {
                        if (TextUtils.equals(saleListInfo.getId(), orderListInfo.getGoodsId())) {
                            //相同商品价格和数量更新
                            int lastGoodsCount = orderListInfo.getInputGoodsCountWithInt();
                            orderListInfo.setInputGoodsCount(String.valueOf(lastGoodsCount + 1));
                            double totalPrice = BigDecimalUtils.mul(orderListInfo.getSalePrice(), (lastGoodsCount + 1));
                            orderListInfo.setInputGoodsTotalPrice(String.valueOf(totalPrice));
                            mGoodsOrderList.notifyItemChanged(i);
                            refreshOrderListAndPrice();
                            return;
                        }
                    }
                }
                //不包含当前商品时，直接添加到订单列表
                mGoodsOrderList.addFirstData(new GoodsOrderListInfo(saleListInfo, "1.0", "1", saleListInfo.getDiscountPrice()));
                //刷新价格
                refreshOrderListAndPrice();
                //滚动列表到头部
                rvGoodsOrderList.scrollToPosition(0);
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
        flGoPay = (FrameLayout) findViewById(R.id.fl_go_pay);
        orderAllPrice = (TextView) findViewById(R.id.order_all_price);
        llFastPayPage = (LinearLayout) findViewById(R.id.ll_fast_pay_page);
        tvBackFastPay = (UnderlineTextView) findViewById(R.id.tv_back_fast_pay);
        calculateFastPay = (SimpleCalculator) findViewById(R.id.calculate_fast_pay);
        stvChangeOrderPrice = (ShapeTextView) findViewById(R.id.stv_change_order_price);
        stvChangeOrderPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //整单改价
                String price = payAllPrice.getText().toString();
                CommonInputDialogFragment.build().setTitle("整单改价").setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL).setInputNumberRange(0, 99999999).setNeedLimitNumber(true).setInputContent(price).setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        String formatPrice = PriceUtils.formatPrice(input);
                        setOrderRealPayPrice(formatPrice);
                    }
                }).show(mActivity);
                mAddOrderRequest.setChangePriceType(String.valueOf(PayConstants.CHAGE_ORDER_PRICE));
                mAddOrderRequest.setChangePriceDesc(PayConstants.getChangePriceTypeDesc(PayConstants.CHAGE_ORDER_PRICE));
            }
        });
        stvMoJiao = (ShapeTextView) findViewById(R.id.stv_mo_jiao);
        stvMoJiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //抹角
                String price = payAllPrice.getText().toString();
                if (!TextUtils.isEmpty(price)) {
                    setOrderRealPayPrice(PriceUtils.moJiaoPrice(price));
                    mAddOrderRequest.setChangePriceType(String.valueOf(PayConstants.CHAGE_MO_JIAO));
                    mAddOrderRequest.setChangePriceDesc(PayConstants.getChangePriceTypeDesc(PayConstants.CHAGE_MO_JIAO));
                }
            }
        });
        stvMoFen = (ShapeTextView) findViewById(R.id.stv_mo_fen);
        stvMoFen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //抹分
                String price = payAllPrice.getText().toString();
                if (!TextUtils.isEmpty(price)) {
                    setOrderRealPayPrice(PriceUtils.moFenPrice(price));
                    mAddOrderRequest.setChangePriceType(String.valueOf(PayConstants.CHAGE_MO_FEN));
                    mAddOrderRequest.setChangePriceDesc(PayConstants.getChangePriceTypeDesc(PayConstants.CHAGE_MO_FEN));
                }
            }
        });
        payAllPrice = (TextView) findViewById(R.id.pay_all_price);
        llPrintOrder = (LinearLayout) findViewById(R.id.ll_print_order);
        ivPrintOrderCheck = (ImageView) findViewById(R.id.iv_print_order_check);
        boolean supportPrint = DeviceManager.INSTANCE.getDeviceInterface().isSupportPrint();
        if (supportPrint) {
            llPrintOrder.setVisibility(View.VISIBLE);
            ivPrintOrderCheck.setSelected(true);
        } else {
            llPrintOrder.setVisibility(View.GONE);
            ivPrintOrderCheck.setSelected(false);
        }
        llPrintOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = ivPrintOrderCheck.isSelected();
                ivPrintOrderCheck.setSelected(!selected);
            }
        });
        llWeixinPay = (LinearLayout) findViewById(R.id.ll_weixin_pay);
        ivWeixinPay = (ImageView) findViewById(R.id.iv_weixin_pay);
        tvWeixinPay = (TextView) findViewById(R.id.tv_weixin_pay);
        llWeixinPay.setOnClickListener(buildSelectPayListener(PayConstants.PAY_TYPE_THIRD));
        llAlipayPay = (LinearLayout) findViewById(R.id.ll_alipay_pay);
        llAlipayPay.setOnClickListener(buildSelectPayListener(PayConstants.PAY_TYPE_IC_CARD));
        llCashPay = (LinearLayout) findViewById(R.id.ll_cash_pay);
        llCashPay.setOnClickListener(buildSelectPayListener(PayConstants.PAY_TYPE_CASH));
        llFacePay = (LinearLayout) findViewById(R.id.ll_face_pay);
        llFacePay.setOnClickListener(buildSelectPayListener(PayConstants.PAY_TYPE_FACE));
        llOtherPay = (LinearLayout) findViewById(R.id.ll_other_pay);
        llOtherPay.setOnClickListener(buildSelectPayListener(PayConstants.PAY_TYPE_QRCODE));
        stvClearOrder = (ShapeTextView) findViewById(R.id.stv_clear_order);
        stvClearOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPayOrderInfo();
            }
        });
        stvWaitOrder = (ShapeTextView) findViewById(R.id.stv_wait_order);
        RxView.clicks(stvWaitOrder).throttleFirst(500, TimeUnit.MILLISECONDS).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Unit>() {
            @Override
            protected void onSuccess(Unit unit) {
                //挂单（快速收银不支持）
                int orderType = mAddOrderRequest.getOrderType();
                if (orderType == PayConstants.ORDER_TYPE_FAST_PAY) {
                    CommonDialogUtils.showTipsDialog(mActivity, "快速收银暂时不支持挂单");
                    return;
                }
                int orderListItemCount = mGoodsOrderList.getItemCount();
                if (orderListItemCount <= 0) {
                    CommonDialogUtils.showTipsDialog(mActivity, "商品支付列表为空");
                    return;
                }
                List<Object> dataList = mGoodsOrderList.getDataList();
                List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
                for (Object obj : dataList) {
                    orderListInfoList.add((GoodsOrderListInfo) obj);
                }
                long orderCreateTime = System.currentTimeMillis();
                String formatCreateTime = waitOrderDateFormat.format(new Date(orderCreateTime));
                WaitHistoryOrderInfo historyOrderInfo = new WaitHistoryOrderInfo(mAddOrderRequest.getRealPayPrice(), String.valueOf(mAddOrderRequest.getGoodsCount()), orderCreateTime, formatCreateTime, orderListInfoList);
                getHistoryOrderHelper().addWaitOrderHistory(historyOrderInfo);
                showLoadingDialog();
            }
        });
        stvGoPay = (ShapeTextView) findViewById(R.id.stv_go_pay);
        sflPayStatus = (ShapeLinearLayout) findViewById(R.id.sfl_pay_status);
        ivPayStatus = (ImageView) findViewById(R.id.iv_pay_status);
        tvPayStatus = (TextView) findViewById(R.id.tv_pay_status);
        tvCurrentOrder = (ShapeTextView) findViewById(R.id.tv_current_order);
        tvLastOrder = (ShapeTextView) findViewById(R.id.tv_last_order);
        View.OnClickListener onHistoryOrderListClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderHistoryListAlertFragment historyListAlertFragment = new OrderHistoryListAlertFragment();
                historyListAlertFragment.setOnConfirmOrderListener(TabPayFragment.this);
                FragmentUtils.safeAddFragment(getChildFragmentManager(), historyListAlertFragment, R.id.fl_pay_second_content);
            }
        };
        tvCurrentOrder.setOnClickListener(onHistoryOrderListClickListener);
        tvLastOrder.setOnClickListener(onHistoryOrderListClickListener);
        tvOrderHistory = (ShapeTextView) findViewById(R.id.tv_order_history);
        rvWaitHistoryOrder = (RecyclerView) findViewById(R.id.rv_history_order);
        mWaitHistoryOrderList = new CommonRecyclerAdapter(false);
        mWaitHistoryOrderList.addViewHolderFactory(new GoodsWaitHistoryOrderInfoViewHolder.Factory());
        mWaitHistoryOrderList.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                WaitHistoryOrderInfo historyOrderInfo = (WaitHistoryOrderInfo) obj;
                if (eventId == GoodsWaitHistoryOrderInfoViewHolder.EVENT_CLICK) {
                    //快速收银不支持挂单操作
                    if (mAddOrderRequest.getOrderType() == PayConstants.ORDER_TYPE_FAST_PAY) {
                        CommonDialogUtils.showTipsDialog(mActivity, "当前快速收银不支持挂单操作");
                        return;
                    }
                    //判断订单列表是否为空，不为空也不可操作挂单
                    if (!mGoodsOrderList.isEmptyData()) {
                        CommonDialogUtils.showTipsDialog(mActivity, "当前存在待支付商品,不支持挂单操作");
                        return;
                    }
                    OrderHistoryDetailAlertFragment historyDetailAlertFragment = new OrderHistoryDetailAlertFragment();
                    historyDetailAlertFragment.setWaitHistoryOrderInfo(historyOrderInfo);
                    historyDetailAlertFragment.setOnConfirmOrderListener(TabPayFragment.this);
                    FragmentUtils.safeAddFragment(getChildFragmentManager(), historyDetailAlertFragment, R.id.fl_pay_second_content);
                } else if (eventId == GoodsWaitHistoryOrderInfoViewHolder.EVENT_LONG_CLICK) {
                    CommonAlertDialogFragment.build().setAlertTitleTxt("提示").setAlertContentTxt("确认删除当前挂单吗").setRightNavTxt("取消").setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            showLoadingDialog();
                            getHistoryOrderHelper().deleteWaitOrderHistory(historyOrderInfo);
                        }
                    }).show(mActivity);
                }
            }
        });
        rvWaitHistoryOrder.setAdapter(mWaitHistoryOrderList);
        sflGoodsListPay = (ShapeFrameLayout) findViewById(R.id.sfl_goods_list_pay);
        findViewById(R.id.sll_fast_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPayOrderInfo();
                showOrHideFastPayLay(true);
                mAddOrderRequest.setOrderType(PayConstants.ORDER_TYPE_FAST_PAY);
            }
        });
        sllGoodsWeight = (ShapeLinearLayout) findViewById(R.id.sll_goods_weight);
        sllGoodsWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeightGoodsFragment == null) {
                    mWeightGoodsFragment = new WeightGoodsPayFragment();
                    mWeightGoodsFragment.setWeightGoodsPayListener(TabPayFragment.this);
                    FragmentUtils.safeReplaceFragment(getChildFragmentManager(), mWeightGoodsFragment, R.id.fl_pay_second_content);
                } else {
                    FragmentUtils.safeAttachFragment(getChildFragmentManager(), mWeightGoodsFragment);
                }
            }
        });
        findViewById(R.id.tv_back_fast_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPayOrderInfo();
                showOrHideFastPayLay(false);
                mAddOrderRequest.setOrderType(PayConstants.ORDER_TYPE_NORMAL_PAY);
            }
        });
        calculateFastPay.setOnCalculateListener(new OnCalculateListener() {
            @Override
            public void onConfirmMoney(String payMoney) {
                String fastPayPrice = PriceUtils.formatPrice(payMoney);
                setOrderRealPayPrice(fastPayPrice);
                mAddOrderRequest.setTotalPrice(fastPayPrice);
            }
        });
        RxView.clicks(stvGoPay).throttleFirst(1000, TimeUnit.MILLISECONDS).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Unit>() {
            @Override
            protected void onSuccess(Unit unit) {
                int orderType = mAddOrderRequest.getOrderType();
                if (orderType == PayConstants.ORDER_TYPE_NORMAL_PAY) {
                    if (mGoodsOrderList.isEmptyData()) {
                        CommonDialogUtils.showTipsDialog(mActivity, "支付列表为空,请选择快速收银");
                        return;
                    }
                }
                int payType = mAddOrderRequest.getPayType();
                if (payType == 0) {
                    CommonDialogUtils.showTipsDialog(mActivity, "请选择一种支付方式");
                    return;
                }
                double parsePrice = PriceUtils.parsePrice(payAllPrice.getText().toString());
                if (parsePrice <= 0) {
                    if (orderType == PayConstants.ORDER_TYPE_FAST_PAY) {
                        CommonDialogUtils.showTipsDialog(mActivity, "支付金额为0,请点击确认金额");
                    } else {
                        CommonDialogUtils.showTipsDialog(mActivity, "支付金额为0");
                    }
                    return;
                }
                //添加商品列表订单信息
                List<Object> dataList = mGoodsOrderList.getDataList();
                List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
                for (Object obj : dataList) {
                    orderListInfoList.add((GoodsOrderListInfo) obj);
                }
                mAddOrderRequest.addOrderGoodsInfoList(orderListInfoList);
                goToPay(payType);
            }
        });
    }

    @Override
    public void onConfirmPayOrder(PayHistoryOrderInfo payHistoryOrderInfo) {
        addConfirmOrderGoodList(payHistoryOrderInfo.getOrderListInfoList());
    }

    @Override
    public void onConfirmWaitOrder(WaitHistoryOrderInfo waitHistoryOrderInfo) {
        //删除本地数据库该订单
        getHistoryOrderHelper().deleteWaitOrderHistory(waitHistoryOrderInfo);
        mWaitHistoryOrderList.removeData(waitHistoryOrderInfo);
        addConfirmOrderGoodList(waitHistoryOrderInfo.getOrderListInfoList());
    }

    /**
     * 重新支付或者下单（历史订单、历史挂单）
     */
    private void addConfirmOrderGoodList(List<GoodsOrderListInfo> historyOrderInfo) {
        clearPayOrderInfo();
        mGoodsOrderList.addDataList(historyOrderInfo);
        rvGoodsOrderList.scrollToPosition(0);
        refreshOrderListAndPrice();
    }

    private void showOrHideFastPayLay(boolean show) {
        if (show) {
            sflGoodsListPay.setVisibility(View.GONE);
            llFastPayPage.setVisibility(View.VISIBLE);
        } else {
            sflGoodsListPay.setVisibility(View.VISIBLE);
            llFastPayPage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddWaitOrderHistory(WaitHistoryOrderInfo orderInfo) {
        hideLoadingDialog();
        clearPayOrderInfo();
        mWaitHistoryOrderList.addFirstData(orderInfo);
        rvWaitHistoryOrder.scrollToPosition(0);
    }

    @Override
    public void onDeleteWaitOrderHistory(WaitHistoryOrderInfo orderInfo) {
        hideLoadingDialog();
        mWaitHistoryOrderList.removeData(orderInfo);
    }

    @Override
    public void onLoadValidWaitHistoryList(List<WaitHistoryOrderInfo> orderInfoList) {
        hideLoadingDialog();
        mWaitHistoryOrderList.addDataList(orderInfoList);
    }

    @Override
    public void onWaitOrderHistoryError(String error) {
        hideLoadingDialog();
    }

    /**
     * 刷新订单列表底部价格
     */
    private void refreshOrderListAndPrice() {
        List<Object> dataList = mGoodsOrderList.getDataList();
        if (dataList.isEmpty()) {
            llOrderListEmpty.setVisibility(View.VISIBLE);
            flGoPay.setVisibility(View.GONE);
            orderAllPrice.setText("");
            setOrderRealPayPrice("0.00");
            return;
        }
        llOrderListEmpty.setVisibility(View.GONE);
        flGoPay.setVisibility(View.VISIBLE);
        double totalOriginPrice = 0;
        double totalDiscountPrice = 0;
        double totalPrice = 0;
        int totalCount = 0;
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            GoodsOrderListInfo goodsOrderListInfo = (GoodsOrderListInfo) obj;
            //最终价格
            double goodsTotalPriceWithDouble = goodsOrderListInfo.getInputGoodsTotalPriceWithDouble();
            totalPrice = BigDecimalUtils.add(totalPrice, goodsTotalPriceWithDouble);
            //原始价格
            totalOriginPrice = BigDecimalUtils.add(totalOriginPrice, goodsOrderListInfo.getGoodsTotalOriginPrice());
            //折扣价格
            totalDiscountPrice = BigDecimalUtils.add(totalDiscountPrice, goodsOrderListInfo.getGoodsTotalDiscountPrice());
            //商品数量
            GoodsSaleListInfo goodsSaleListInfo = goodsOrderListInfo.getGoodsSaleListInfo();
            if (goodsSaleListInfo.isWeightGoods()) {
                //称重商品表示一件商品
                totalCount = totalCount + 1;
            } else {
                totalCount = totalCount + goodsOrderListInfo.getInputGoodsCountWithInt();
            }
        }
        String formatTotalPrice = PriceUtils.formatPrice(String.valueOf(totalPrice));
        String formatDiscountPrice = PriceUtils.formatPrice(String.valueOf(totalDiscountPrice));
        String formatTotalOriginPrice = PriceUtils.formatPrice(String.valueOf(totalOriginPrice));
        String formatTotalDiscountPrice = "0.00";
        if (totalOriginPrice > totalPrice) {
            double tempPrice = BigDecimalUtils.sub(totalOriginPrice, totalPrice);
            formatTotalDiscountPrice = PriceUtils.formatPrice(String.valueOf(tempPrice));
        }
        LogHelper.print("--refreshOrderListAndPrice--tabPay: totalPrice:" + formatTotalPrice + " totalOriginPrice: " + formatTotalOriginPrice + " totalDiscountPrice: " + totalDiscountPrice);
        int redColor = mResources.getColor(R.color.color_FF3C30);
        int blueColor = mResources.getColor(R.color.color_0087FA);
        int grayColor = mResources.getColor(R.color.color_999999);
        int priceFontSize = mResources.getDimensionPixelSize(com.stkj.common.R.dimen.sp_19);
        SpanUtils.with(orderAllPrice).append("¥ ").setForegroundColor(redColor).append(formatTotalPrice).setFontSize(priceFontSize).setForegroundColor(redColor).setBold().append("  共计" + totalCount + "件\n").setForegroundColor(blueColor).append("(总价：¥" + formatTotalOriginPrice + ",  活动优惠：-¥" + formatTotalDiscountPrice + ")").setForegroundColor(grayColor).create();
        //设置原始价格
        mAddOrderRequest.setTotalPrice(formatTotalPrice);
        //设置折扣价格
        mAddOrderRequest.setSettlePrice(formatDiscountPrice);
        //设置活动优惠
        mAddOrderRequest.setDiscountPrice(formatTotalDiscountPrice);
        //设置商品数据
        mAddOrderRequest.setGoodsCount(totalCount);
        //右侧显示的价格
        setOrderRealPayPrice(formatTotalPrice);
    }

    private void setOrderRealPayPrice(String formatTotalPrice) {
        payAllPrice.setText(formatTotalPrice);
        mAddOrderRequest.setRealPayPrice(formatTotalPrice);
        refreshConsumerPayOrderInfo(formatTotalPrice);
    }

    /**
     * 刷新消费者页面订单数据
     */
    private void refreshConsumerPayOrderInfo(String formatTotalPrice) {
        //消费页面数据
        int orderType = mAddOrderRequest.getOrderType();
        if (orderType == PayConstants.ORDER_TYPE_FAST_PAY) {
            ConsumerManager.INSTANCE.setPayOrderInfo(true, null, 1, formatTotalPrice);
        } else {
            List<Object> dataList = mGoodsOrderList.getDataList();
            if (dataList.isEmpty()) {
                ConsumerManager.INSTANCE.clearPayOrderInfo();
            } else {
                List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
                int totalCount = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    Object obj = dataList.get(i);
                    GoodsOrderListInfo goodsOrderListInfo = (GoodsOrderListInfo) obj;
                    //商品数量
                    GoodsSaleListInfo goodsSaleListInfo = goodsOrderListInfo.getGoodsSaleListInfo();
                    if (goodsSaleListInfo.isWeightGoods()) {
                        //称重商品表示一件商品
                        totalCount = totalCount + 1;
                    } else {
                        totalCount = totalCount + goodsOrderListInfo.getInputGoodsCountWithInt();
                    }
                    //添加在最终列表
                    orderListInfoList.add(goodsOrderListInfo);
                }
                ConsumerManager.INSTANCE.setPayOrderInfo(false, orderListInfoList, totalCount, formatTotalPrice);
            }
        }
    }

    private void goToPay(int payType) {
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).clearMainFocus();
        }
        String payOrderTips = "";
        if (payType == PayConstants.PAY_TYPE_THIRD) {
            //聚合支付
            payOrderTips = "请打开付款码";
        } else if (payType == PayConstants.PAY_TYPE_IC_CARD) {
            //刷卡
            payOrderTips = "请刷卡";
        } else if (payType == PayConstants.PAY_TYPE_CASH) {
            //现金
            payOrderTips = "请确认支付";
        } else if (payType == PayConstants.PAY_TYPE_FACE) {
            //人脸
            payOrderTips = "请刷脸支付";
        } else if (payType == PayConstants.PAY_TYPE_QRCODE) {
            //职工码
            payOrderTips = "请打开职工码";
        }
        if (!TextUtils.isEmpty(payOrderTips)) {
            speakTTSVoice(payOrderTips);
            ConsumerManager.INSTANCE.setFaceConsumerTips(payOrderTips);
            mPayOrderTipsDialog = new PayOrderTipsDialog();
            mPayOrderTipsDialog.setAlertTitleTxt("支付提示").setAlertContentTxt(payOrderTips).setLeftNavTxt("取消支付").setNeedHandleDismiss(true).setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                @Override
                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                    cancelGoPay();
                }
            });
            mPayOrderTipsDialog.show(mActivity);
            //去支付
            if (payType == PayConstants.PAY_TYPE_THIRD) {
                //聚合支付
                goThirdPay();
            } else if (payType == PayConstants.PAY_TYPE_IC_CARD) {
                //刷卡
                goICCardPay();
            } else if (payType == PayConstants.PAY_TYPE_CASH) {
                //现金
                goCashPay();
            } else if (payType == PayConstants.PAY_TYPE_FACE) {
                //人脸
                goFacePay();
            } else if (payType == PayConstants.PAY_TYPE_QRCODE) {
                //职工码
                goQRCodePay();
            }
        }
    }

    /**
     * 取消去支付
     */
    private void cancelGoPay() {
        PayHelper payHelper = mActivity.getWeakRefHolder(PayHelper.class);
        if (payHelper.isPaying()) {
            AppToast.toastMsg("正在支付中,不能取消");
            return;
        }
        int payType = mAddOrderRequest.getPayType();
        if (payType == PayConstants.PAY_TYPE_THIRD) {
            //聚合支付
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mThirdScanQRCodeListener);
        } else if (payType == PayConstants.PAY_TYPE_IC_CARD) {
            //餐卡支付
            stopConsumerFacePass();
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterICCardListener(mReadICCardListener);
        } else if (payType == PayConstants.PAY_TYPE_CASH) {
            //现金支付
        } else if (payType == PayConstants.PAY_TYPE_FACE) {
            //人脸识别
            stopConsumerFacePass();
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        } else if (payType == PayConstants.PAY_TYPE_QRCODE) {
            //职工码
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mPeopleScanQRCodeListener);
        }
        resetPayType();
        ConsumerManager.INSTANCE.clearPayOrderInfo();
        hidePayOrderTipsDialog();
        //等待扫码加入商品列表
        waitScanQrCode();
    }

    /**
     * 三方二维码扫描
     */
    private OnScanQRCodeListener mThirdScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            //取消注册
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(this);
            PayHelper payHelper = getPayHelper();
            payHelper.setOnPayListener(TabPayFragment.this);
            payHelper.pay(mAddOrderRequest, data);
        }

        @Override
        public void onScanQRCodeError(String message) {
            //取消注册
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(this);
            showPayOrderTipsDialog(message);
        }
    };

    /**
     * 三方聚合支付
     */
    private void goThirdPay() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mThirdScanQRCodeListener);
    }

    private OnReadICCardListener mReadICCardListener = new OnReadICCardListener() {
        @Override
        public void onReadCardData(String data) {
            if (TextUtils.isEmpty(data)) {
                CommonDialogUtils.showTipsDialog(mActivity, "读卡失败,请重试!");
                return;
            }
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterICCardListener(this);
            //读卡成功去支付
            handleReadCardResult(data);
        }

        @Override
        public void onReadCardError(String message) {
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterICCardListener(this);
            showPayOrderTipsDialog(message);
        }
    };

    /**
     * 餐卡支付
     */
    private void goICCardPay() {
        ConsumerManager.INSTANCE.setFacePassConfirmListener(this);
        DeviceManager.INSTANCE.getDeviceInterface().readICCard(mReadICCardListener);
    }

    /**
     * 处理读卡
     */
    private void handleReadCardResult(String cardNumber) {
        FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
        facePassHelper.setOnHandleCardNumberListener(new FacePassHelper.OnHandleCardNumberListener() {
            @Override
            public void onHandleLocalCardNumber(String cardNumber, FacePassPeopleInfo facePassPeopleInfo) {
                FacePassHelper.OnHandleCardNumberListener.super.onHandleLocalCardNumber(cardNumber, facePassPeopleInfo);
                speakTTSVoice("读卡成功,请确认支付");
                showPayOrderTipsDialog("读卡成功,请确认支付\n姓名: " + facePassPeopleInfo.getFull_Name() + "卡号: " + facePassPeopleInfo.getCard_Number());
                ConsumerManager.INSTANCE.setFaceConsumerInfo(facePassPeopleInfo, PayConstants.PAY_TYPE_IC_CARD);
            }

            @Override
            public void onHandleLocalCardNumberError(String cardNumber) {
                FacePassHelper.OnHandleCardNumberListener.super.onHandleLocalCardNumberError(cardNumber);
                speakTTSVoice("读卡成功,请确认支付");
                showPayOrderTipsDialog("读卡成功,请确认支付\n卡号: " + cardNumber);
                ConsumerManager.INSTANCE.setFaceConsumerInfo(cardNumber);
//                showPayOrderTipsDialog("用户不存在", "确定");
//                speakTTSVoice("用户不存在");
//                ConsumerManager.INSTANCE.setConsumerTips("用户不存在");
            }
        });
        facePassHelper.searchFacePassByCardNumber(cardNumber);
    }

    /**
     * 现金支付
     */
    private void goCashPay() {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(TabPayFragment.this);
        payHelper.pay(mAddOrderRequest, "");
//        ConsumerManager.INSTANCE.setFacePassConfirmListener(this);
////        speakTTSVoice("请确认支付");
//        showPayOrderTipsDialog("请客户确认支付");
//        ConsumerManager.INSTANCE.setFaceConsumerInfo();
//        //打开钱箱
//        DeviceManager.INSTANCE.getDeviceInterface().openMoneyBox(null);
    }

    /**
     * 刷脸支付
     */
    private void goFacePay() {
        canSpeakFacePassFail = true;
        if (canSpeakFacePassFailObserver != null) {
            canSpeakFacePassFailObserver.dispose();
            canSpeakFacePassFailObserver = null;
        }
        ConsumerManager.INSTANCE.setFacePassConfirmListener(this);
        ConsumerManager.INSTANCE.setFacePreview(true);
        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.setOnDetectFaceListener(new CBGFacePassHandlerHelper.OnDetectFaceListener() {
            @Override
            public void onDetectFaceToken(List<CBGFacePassRecognizeResult> faceTokenList) {
                handleFacePassResult(faceTokenList);
            }
        });
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    cbgCameraHelper.prepareFacePassDetect();
                    cbgCameraHelper.startFacePassDetect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 处理识别人脸结果
     */
    private void handleFacePassResult(List<CBGFacePassRecognizeResult> faceTokenList) {
        if (faceTokenList != null && !faceTokenList.isEmpty()) {
            FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
            CBGFacePassRecognizeResult facePassRecognizeResult = faceTokenList.get(0);
            if (facePassRecognizeResult.isFacePassSuccess()) {
                facePassHelper.setOnHandleFaceTokenListener(new FacePassHelper.OnHandleFaceTokenListener() {
                    @Override
                    public void onHandleLocalFace(String faceToken, FacePassPeopleInfo facePassPeopleInfo) {
                        //停止识别
                        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
                        cbgCameraHelper.stopFacePassDetect();
                        ConsumerManager.INSTANCE.setFacePreview(false);
                        speakTTSVoice("识别成功,请确认支付");
                        showPayOrderTipsDialog("识别成功,请确认支付\n姓名: " + facePassPeopleInfo.getFull_Name() + "卡号: " + facePassPeopleInfo.getCard_Number());
                        ConsumerManager.INSTANCE.setFaceConsumerInfo(facePassPeopleInfo, PayConstants.PAY_TYPE_FACE);
                    }

                    @Override
                    public void onHandleLocalFaceError(String faceToken) {
                        facePassFailRetryDelay(-1);
                    }
                });
                facePassHelper.searchFacePassByFaceToken(facePassRecognizeResult.getFaceToken());
            } else {
                facePassFailRetryDelay(facePassRecognizeResult.getRecognitionState());
            }
        }
    }

    private boolean canSpeakFacePassFail;
    private DefaultDisposeObserver<Long> canSpeakFacePassFailObserver;

    /**
     * 识别失败自动重试
     */
    private void facePassFailRetryDelay(int recognizeState) {
        if (canSpeakFacePassFail) {
            canSpeakFacePassFail = false;
            canSpeakFacePassFailObserver = new DefaultDisposeObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    canSpeakFacePassFail = true;
                    canSpeakFacePassFailObserver = null;
                }
            };
            //10秒之后重置识别失败语音提醒
            Observable.timer(10, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(canSpeakFacePassFailObserver);
            speakTTSVoice("识别失败,正在重试");
        }
        showPayOrderTipsDialog("识别失败(" + recognizeState + "),正在重试");
        ConsumerManager.INSTANCE.setFaceConsumerTips("识别失败,正在重试");
    }

    /**
     * 餐卡支付确认
     */
    @Override
    public void onConfirmFacePass(String cardNumber) {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(TabPayFragment.this);
        mAddOrderRequest.setCustomerNo(cardNumber);
        payHelper.pay(mAddOrderRequest, "");
    }

    @Override
    public void onCancelFacePass(String cardNumber) {
        speakTTSVoice("用户取消支付");
        cancelGoPay();
        hidePayOrderTipsDialog();
    }

    /**
     * 刷脸支付确认
     */
    @Override
    public void onConfirmFacePass(FacePassPeopleInfo passPeopleInfo) {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(TabPayFragment.this);
        mAddOrderRequest.setCustomerNo(passPeopleInfo.getCard_Number());
        payHelper.pay(mAddOrderRequest, "");
    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        speakTTSVoice("用户取消支付");
        cancelGoPay();
        hidePayOrderTipsDialog();
    }

    /**
     * 现金支付确认
     */
    @Override
    public void onConfirmFacePass() {
        PayHelper payHelper = getPayHelper();
        payHelper.setOnPayListener(TabPayFragment.this);
        payHelper.pay(mAddOrderRequest, "");
    }

    @Override
    public void onCancelFacePass() {
        speakTTSVoice("用户取消支付");
        cancelGoPay();
        hidePayOrderTipsDialog();
    }

    /**
     * 播放语音
     *
     * @param words
     */
    private void speakTTSVoice(String words) {
        mActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words);
    }

    /**
     * 职工码扫描
     */
    private OnScanQRCodeListener mPeopleScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            //取消注册
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(this);
            PayHelper payHelper = getPayHelper();
            payHelper.setOnPayListener(TabPayFragment.this);
            mAddOrderRequest.setCustomerNo(data);
            payHelper.pay(mAddOrderRequest, "");
        }

        @Override
        public void onScanQRCodeError(String message) {
            //取消注册
            DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(this);
            showPayOrderTipsDialog(message);
        }
    };

    /**
     * 职工码支付
     */
    private void goQRCodePay() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mPeopleScanQRCodeListener);
    }

    /**
     * 清理付款订单信息
     */
    private void clearPayOrderInfo() {
        //关闭支付提示弹窗
        hidePayOrderTipsDialog();
        //重置支付方式
        resetPayType();
        //清空订单商品列表和金额
        mGoodsOrderList.removeAllData();
        refreshOrderListAndPrice();
        //清除快捷支付金额
        calculateFastPay.clearCalcData();
        //清除请求参数
        mAddOrderRequest.resetRequest();
        //欢迎页
        ConsumerManager.INSTANCE.clearPayOrderInfo();
    }

    /**
     * 隐藏订单支付状态
     */
    private void hidePayOrderTipsDialog() {
        if (mPayOrderTipsDialog != null) {
            mPayOrderTipsDialog.dismiss();
            mPayOrderTipsDialog = null;
        }
    }

    private void showPayOrderTipsDialog(String tips) {
        showPayOrderTipsDialog(tips, "");
    }

    /**
     * 设置支付窗口状态提示
     */
    private void showPayOrderTipsDialog(String tips, String leftNavTxt) {
        if (mPayOrderTipsDialog != null) {
            if (!TextUtils.isEmpty(leftNavTxt)) {
                mPayOrderTipsDialog.setLeftNavTxt(leftNavTxt);
            }
            mPayOrderTipsDialog.setAlertContentTxt(tips);
        }
    }

    private DefaultDisposeObserver<Long> payCountDownObserver;
    private ValueAnimator payLoadingAnimator;

    private void showPayLoadingStatus() {
        sflPayStatus.setVisibility(View.VISIBLE);
        ivPayStatus.setImageResource(R.mipmap.icon_pay_loading);
        startPayLoadAnim();
        String statusTips = "支付中,请稍等";
        tvPayStatus.setText(statusTips);
        showPayOrderTipsDialog(statusTips);
        ConsumerManager.INSTANCE.setFaceConsumerTips(statusTips);
    }

    private void stopPayLoadAnim() {
        if (payLoadingAnimator != null) {
            payLoadingAnimator.end();
            payLoadingAnimator = null;
        }
    }

    private void startPayLoadAnim() {
        stopPayLoadAnim();
        payLoadingAnimator = ValueAnimator.ofInt(0, 360);
        payLoadingAnimator.setDuration(1500);
        payLoadingAnimator.setInterpolator(new LinearInterpolator());
        payLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        payLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue instanceof Integer) {
                    ivPayStatus.setRotation((Integer) animatedValue);
                }
            }
        });
        payLoadingAnimator.start();
    }

    private void showPaySuccessStatus() {
        stopPayLoadAnim();
        sflPayStatus.setVisibility(View.VISIBLE);
        sflPayStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePayLoadingStatus();
                //重置头像
                ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            }
        });
        ivPayStatus.setImageResource(R.mipmap.icon_save_storage_success);
        tvPayStatus.setText("已完成支付(3)");
        if (payCountDownObserver != null) {
            payCountDownObserver.dispose();
        }
        payCountDownObserver = new DefaultDisposeObserver<Long>() {
            @Override
            protected void onSuccess(Long integer) {
                LogHelper.print("payCountDownObserver: " + integer);
                int countDown = (int) (3 - integer);
                if (countDown == 0) {
                    hidePayLoadingStatus();
                    //重置头像
                    ConsumerManager.INSTANCE.resetFaceConsumerLayout();
                } else {
                    tvPayStatus.setText("已完成支付(" + countDown + ")");
                }
            }
        };
        Observable.intervalRange(1, 3, 1, 1, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(payCountDownObserver);
        clearPayOrderInfo();
        hidePayOrderTipsDialog();
        ConsumerManager.INSTANCE.setFaceConsumerTips("支付成功，欢迎下次光临!");
    }

    private void hidePayLoadingStatus() {
        stopPayLoadAnim();
        if (payCountDownObserver != null) {
            payCountDownObserver.dispose();
            payCountDownObserver = null;
        }
        sflPayStatus.setVisibility(View.GONE);
        sflPayStatus.setOnClickListener(null);
    }

    private void showPayErrorStatus(String msg) {
        hidePayLoadingStatus();
        showPayOrderTipsDialog("支付失败: " + msg, "确定");
        ConsumerManager.INSTANCE.setFaceConsumerTips("支付失败");
    }

    private PayHelper getPayHelper() {
        return mActivity.getWeakRefHolder(PayHelper.class);
    }

    private OrderHistoryDBHelper getHistoryOrderHelper() {
        return mActivity.getWeakRefHolder(OrderHistoryDBHelper.class);
    }

    private View.OnClickListener buildSelectPayListener(int payType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPayType(payType);
            }
        };
    }

    private void selectPayType(int payType) {
        int mPayType = mAddOrderRequest.getPayType();
        if (mPayType != payType) {
            resetPayType();
            mAddOrderRequest.setPayType(payType);
            if (payType == PayConstants.PAY_TYPE_CASH) {
                llCashPay.setSelected(true);
            } else if (payType == PayConstants.PAY_TYPE_FACE) {
                llFacePay.setSelected(true);
            } else if (payType == PayConstants.PAY_TYPE_QRCODE) {
                llOtherPay.setSelected(true);
            } else if (payType == PayConstants.PAY_TYPE_IC_CARD) {
                llAlipayPay.setSelected(true);
            } else if (payType == PayConstants.PAY_TYPE_THIRD) {
                llWeixinPay.setSelected(true);
            }
            //刷新消费者订单信息
            if (!ConsumerManager.INSTANCE.hasSetPayOrderInfo()) {
                refreshConsumerPayOrderInfo(mAddOrderRequest.getRealPayPrice());
            }
        }
    }

    /**
     * 停止人脸识别
     */
    private void stopConsumerFacePass() {
        ConsumerManager.INSTANCE.setFacePreview(false);
        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.stopFacePassDetect();
    }

    private void resetPayType() {
        mAddOrderRequest.setPayType(0);
        llWeixinPay.setSelected(false);
        llAlipayPay.setSelected(false);
        llCashPay.setSelected(false);
        llFacePay.setSelected(false);
        llOtherPay.setSelected(false);
        ConsumerManager.INSTANCE.setFaceConsumerTips("欢迎光临!");
    }

    @Override
    public void onPause() {
        super.onPause();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterPrintListener(mPrintListener);
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mPeopleScanQRCodeListener);
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mThirdScanQRCodeListener);
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mAddOrderScanQRCodeListener);
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterICCardListener(mReadICCardListener);
        if (mPayOrderTipsDialog != null) {
            mPayOrderTipsDialog.setNeedCancelPay(true);
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            LoginHelper.INSTANCE.addLoginCallback(loginCallback);
            OrderHistoryDBHelper historyOrderHelper = getHistoryOrderHelper();
            historyOrderHelper.setOnOrderHistoryListener(this);
            //加载历史挂单订单
            historyOrderHelper.loadValidWaitHistoryList();
            //加载历史订单
            loadPayHistoryList();
        }
        //获取当前是否打开现金支付
        boolean openCashierPay = PaymentSettingMMKV.isOpenCashierPay();
        llCashPay.setVisibility(openCashierPay ? View.VISIBLE : View.GONE);
        if (!openCashierPay && mAddOrderRequest.getPayType() == PayConstants.PAY_TYPE_CASH) {
            resetPayType();
        }
        //获取当前是否打开通联支付
        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
        if (switchTongLianPay) {
            tvWeixinPay.setText("聚合支付");
            ivWeixinPay.setImageResource(R.mipmap.icon_juhe_pay);
        } else {
            tvWeixinPay.setText("微信支付");
            ivWeixinPay.setImageResource(R.mipmap.icon_weixin_pay);
        }
        waitScanQrCode();
        if (mPayOrderTipsDialog != null && mPayOrderTipsDialog.isNeedCancelPay()) {
            cancelGoPay();
            mPayOrderTipsDialog = null;
        }
    }

    private void loadPayHistoryList() {
//        PayHistoryOrderInfo lastOrderHistory = PayOrderHistoryMMKV.getLastOrderHistory();
//        if (lastOrderHistory != null && !TextUtils.isEmpty(lastOrderHistory.getOrderId())) {
//            setLastOrderHistory(lastOrderHistory);
//        }
        OrderHistoryListRequestPageParams pageParams = new OrderHistoryListRequestPageParams();
        pageParams.setSize(1);
        RetrofitManager.INSTANCE.getDefaultRetrofit().create(PayService.class).getOrderHistoryList(JacksonUtils.convertObjectToMap(pageParams)).map(new Function<BaseResponse<OrderHistoryListResponse>, List<PayHistoryOrderInfo>>() {
            @Override
            public List<PayHistoryOrderInfo> apply(BaseResponse<OrderHistoryListResponse> baseResponse) throws Throwable {
                OrderHistoryListResponse data = baseResponse.getData();
                List<PayHistoryOrderInfo> payHistoryOrderInfoList = new ArrayList<>();
                if (data != null && data.getRecords() != null && !data.getRecords().isEmpty()) {
                    List<OrderHistoryResponse> historyList = data.getRecords();
                    for (OrderHistoryResponse orderHistory : historyList) {
                        payHistoryOrderInfoList.add(orderHistory.convertPayHistoryOrderInfo());
                    }
                }
                return payHistoryOrderInfoList;
            }
        }).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<List<PayHistoryOrderInfo>>() {
            @Override
            protected void onSuccess(List<PayHistoryOrderInfo> payHistoryOrderInfos) {
                if (payHistoryOrderInfos != null && !payHistoryOrderInfos.isEmpty()) {
                    setLastOrderHistory(payHistoryOrderInfos.get(0));
                }
            }
        });
    }

    @Override
    public void onStartPay(AddOrderRequest addOrderRequest) {
        //停止人脸识别检测
        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.stopFacePassDetect();
        showPayLoadingStatus();
    }

    @Override
    public void onPaySuccess(AddOrderRequest addOrderRequest, AddOrderResult addOrderResult, ConsumeOrderRequest consumeOrderRequest) {
        //订单id
        String orderId = "";
        //订单编号
        String orderNumber = "";
        if (addOrderResult != null) {
            orderId = addOrderResult.getId();
            orderNumber = addOrderResult.getOrderNo();
        }
        //刷新tab页商品列表
        EventBus.getDefault().post(new RefreshSearchGoodsListEvent());
        //打印小票
        boolean supportPrint = DeviceManager.INSTANCE.getDeviceInterface().isSupportPrint();
        if (supportPrint && ivPrintOrderCheck.isSelected()) {
            printOrder(addOrderRequest, orderNumber);
        }
        if (!TextUtils.isEmpty(orderNumber)) {
            PayHistoryOrderInfo payHistoryOrderInfo = new PayHistoryOrderInfo();
            payHistoryOrderInfo.setTotalPrice(addOrderRequest.getRealPayPrice());
            payHistoryOrderInfo.setTotalCount(String.valueOf(addOrderRequest.getGoodsCount()));
            payHistoryOrderInfo.setOrderStatus(PayConstants.ORDER_SUCCESS_STATUS);
            payHistoryOrderInfo.setOrderType(addOrderRequest.getOrderType());
            payHistoryOrderInfo.setPayType(addOrderRequest.getPayType());
            payHistoryOrderInfo.setOrderId(orderId);
            payHistoryOrderInfo.setOrderNumber(orderNumber);
//            payHistoryOrderInfo.setOrderListInfoList(getCurrentGoodsOrderListInfo());
            setCurrentOrderHistory(payHistoryOrderInfo);
        }
        String formatPrice2 = PriceUtils.formatPrice2(addOrderRequest.getRealPayPrice());
        speakTTSVoice("支付成功" + formatPrice2 + "元,欢迎下次光临");
        showPaySuccessStatus();
        //等待扫码加入商品列表
        waitScanQrCode();
    }

    private OnPrintListener mPrintListener = new OnPrintListener() {
        @Override
        public void onPrintSuccess() {
            AppToast.toastMsg("小票打印成功");
        }

        @Override
        public void onPrintError(String message) {
            CommonDialogUtils.showTipsDialog(mActivity, "打印失败: " + message);
        }
    };

    /**
     * 打印订单
     */
    private void printOrder(AddOrderRequest addOrderRequest, String orderNumber) {
        List<PrinterData> printerDataList = new ArrayList<>();
        if (!TextUtils.isEmpty(orderNumber)) {
            printerDataList.add(PrinterData.newDivideLinePrintData());
            StoreInfoHelper storeInfoHelper = mActivity.getWeakRefHolder(StoreInfoHelper.class);
            StoreInfo storeInfo = storeInfoHelper.getStoreInfo();
            if (storeInfo != null) {
                printerDataList.add(PrinterData.newTitlePrintData(storeInfo.getDeviceName() + "\n"));
            } else {
                printerDataList.add(PrinterData.newTitlePrintData("智慧超市\n"));
            }
            printerDataList.add(PrinterData.newDivideLinePrintData());
            printerDataList.add(PrinterData.newContentPrintData(
                    "订单编号: " + orderNumber + "\n" +
                            "订单时间: " + TimeUtils.getNowString() + "\n"
            ));
            printerDataList.add(PrinterData.newDivideLinePrintData());
            StringBuilder goodsInfoBuilder = new StringBuilder();
            List<AddOrderGoodsDetail> orderDetailList = addOrderRequest.getOrderDetailList();
            if (orderDetailList != null) {
                int printLineMaxLength = DeviceManager.INSTANCE.getDeviceInterface().getPrintLineMaxLength();
                int spaceLength = printLineMaxLength - 10 * 2;
                int spacePerLength = spaceLength / 6;
                if (spacePerLength < 1) {
                    spacePerLength = 1;
                }
                int goodsNameSpace = spacePerLength * 4 + 8;
                int goodsSinglePriceSpace = spacePerLength * 4 + 4;
                int goodsCountSpace = spacePerLength * 4 + 4;

                StringBuilder spaceInsertBuilder = new StringBuilder();
                for (int i = -1; i < orderDetailList.size(); i++) {
                    spaceInsertBuilder.delete(0, spaceInsertBuilder.length());
                    if (i == -1) {
                        //商品名称
                        spaceInsertBuilder.append("商品名称");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsNameSpace - 8);
                        //单价
                        spaceInsertBuilder.append("单价");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace - 4);
                        //数量
                        spaceInsertBuilder.append("数量");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsCountSpace - 4);
                        //金额
                        spaceInsertBuilder.append("金额\n");
                    } else {
                        AddOrderGoodsDetail addOrderGoodsDetail = orderDetailList.get(i);
                        //商品名称
                        String goodsNameStr = addOrderGoodsDetail.getGoodsName();
                        if (goodsNameStr != null) {
                            LogHelper.print("---printOrder---goodsNameStr:" + goodsNameStr + " nameLength:" + goodsNameStr.length() + " spaceLength:" + goodsSinglePriceSpace);
                            spaceInsertBuilder.append(goodsNameStr).append("\n");
                        }
                        //商品名称占位
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsNameSpace);
                        //单价
                        String goodsSinglePriceStr = addOrderGoodsDetail.getDiscountUnitPrice();
                        if (goodsSinglePriceStr != null) {
                            LogHelper.print("---printOrder---goodsSinglePriceStr:" + goodsSinglePriceStr + " singlePriceLength:" + goodsSinglePriceStr.length() + " spaceLength:" + goodsSinglePriceSpace);
                            if (goodsSinglePriceStr.length() < goodsSinglePriceSpace) {
                                spaceInsertBuilder.append(goodsSinglePriceStr);
                                appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace - goodsSinglePriceStr.length());
                            } else {
                                spaceInsertBuilder.append(goodsSinglePriceStr.substring(0, goodsSinglePriceSpace));
                            }
                        } else {
                            appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace);
                        }
                        //数量
                        String goodsCountStr = addOrderGoodsDetail.getGoodsCount();
//                        if (addOrderGoodsDetail.isWeightGoods()) {
//                            goodsCountStr = goodsCountStr + "kg";
//                        }
                        if (goodsCountStr != null) {
                            LogHelper.print("---printOrder---goodsCountStr:" + goodsCountStr + " countLength:" + goodsCountStr.length() + " spaceLength:" + goodsCountSpace);
                            if (goodsCountStr.length() < goodsCountSpace) {
                                spaceInsertBuilder.append(goodsCountStr);
                                //判断金额位数 尽量展示全
                                String payPrice = addOrderGoodsDetail.getPayPrice();
                                int countPlaceHolder = goodsCountSpace - goodsCountStr.length();
                                if (payPrice.length() > 4) {
                                    countPlaceHolder = countPlaceHolder - (payPrice.length() - 4);
                                    if (countPlaceHolder < 0) {
                                        countPlaceHolder = 0;
                                    }
                                }
                                appendSpacePlaceHolder(spaceInsertBuilder, countPlaceHolder);
                            } else {
                                spaceInsertBuilder.append(goodsCountStr.substring(0, goodsCountSpace));
                            }
                        } else {
                            appendSpacePlaceHolder(spaceInsertBuilder, goodsCountSpace);
                        }
                        //金额
                        spaceInsertBuilder.append(addOrderGoodsDetail.getPayPrice()).append("\n");
                    }
                    //添加到商品信息builder
                    goodsInfoBuilder.append(spaceInsertBuilder);
                }
                //商品信息
                PrinterData goodsInfoData = PrinterData.newContentPrintData(goodsInfoBuilder.toString());
                printerDataList.add(goodsInfoData);
            }
            printerDataList.add(PrinterData.newDivideLinePrintData());
            printerDataList.add(PrinterData.newContentPrintData(
                    "支付方式: " + PayConstants.getPayTypeStr(addOrderRequest.getPayType()) + "\n" +
                            "总价: " + PriceUtils.formatPrice(addOrderRequest.getTotalPrice()) + "\n" +
                            "实收: " + addOrderRequest.getRealPayPrice() + "\n"
            ));
            printerDataList.add(PrinterData.newCenterContentPrintData("\n\n谢谢惠顾，欢迎下次光临~\n\n\n\n"));
        }
        DeviceManager.INSTANCE.getDeviceInterface().print(printerDataList, mPrintListener);
    }

    private void appendSpacePlaceHolder(StringBuilder builder, int spaceLength) {
        for (int i = 0; i < spaceLength; i++) {
            builder.append(" ");
        }
    }

    @Override
    public void onPayError(String responseCode, AddOrderRequest addOrderRequest, AddOrderResult addOrderResult, String msg) {
        //订单id
        String orderId = "";
        //订单编号
        String orderNumber = "";
        if (addOrderResult != null) {
            orderId = addOrderResult.getId();
            orderNumber = addOrderResult.getOrderNo();
        }
        if (!TextUtils.isEmpty(orderId)) {
            PayHistoryOrderInfo payHistoryOrderInfo = new PayHistoryOrderInfo();
            payHistoryOrderInfo.setTotalPrice(addOrderRequest.getRealPayPrice());
            payHistoryOrderInfo.setTotalCount(String.valueOf(addOrderRequest.getGoodsCount()));
            payHistoryOrderInfo.setOrderStatus(PayConstants.ORDER_FAIL_STATUS);
            payHistoryOrderInfo.setOrderType(addOrderRequest.getOrderType());
            payHistoryOrderInfo.setPayType(addOrderRequest.getPayType());
            payHistoryOrderInfo.setOrderId(orderId);
//            payHistoryOrderInfo.setOrderListInfoList(getCurrentGoodsOrderListInfo());
            setCurrentOrderHistory(payHistoryOrderInfo);
        }
        if (TextUtils.equals("401", responseCode)) {
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            hidePayLoadingStatus();
            cancelGoPay();
        } else if (TextUtils.equals("-1", responseCode)) {
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            showPayErrorStatus(msg);
        } else {
            speakTTSVoice(msg);
            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
            showPayErrorStatus(msg);
        }
    }

    private List<GoodsOrderListInfo> getCurrentGoodsOrderListInfo() {
        List<Object> dataList = mGoodsOrderList.getDataList();
        List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
        for (Object obj : dataList) {
            orderListInfoList.add((GoodsOrderListInfo) obj);
        }
        return orderListInfoList;
    }

    private void setCurrentOrderHistory(PayHistoryOrderInfo historyOrderInfo) {
        if (historyOrderInfo == null) {
            return;
        }
        setLastOrderHistory(mCurrentOrderInfo);
        mCurrentOrderInfo = historyOrderInfo;
        refreshOrderHistoryLay(tvCurrentOrder, "当前订单: ", historyOrderInfo);
    }

    private void setLastOrderHistory(PayHistoryOrderInfo lastOrderHistory) {
        if (lastOrderHistory == null) {
            return;
        }
        mLastOrderInfo = lastOrderHistory;
        refreshOrderHistoryLay(tvLastOrder, "上笔订单: ", lastOrderHistory);
    }

    private void refreshOrderHistoryLay(TextView textView, String startTag, PayHistoryOrderInfo orderInfo) {
        int grayColor = mResources.getColor(R.color.color_999999);
        int redColor = mResources.getColor(R.color.color_FF3C30);
        String payStatus;
        if (orderInfo.isPaySuccessOrder()) {
            payStatus = "已" + PayConstants.getPayTypeStr(orderInfo.getPayType());
        } else {
            payStatus = "未支付";
        }
        SpanUtils.with(textView).append(startTag).append(orderInfo.getTotalCount() + "件, ").setForegroundColor(grayColor).append("¥" + orderInfo.getTotalPrice()).setForegroundColor(redColor).append(", " + payStatus).setForegroundColor(grayColor).create();
    }

    @Override
    public void onAddWeightGoodsOrderInfo(GoodsOrderListInfo orderListInfo) {
        llOrderListEmpty.setVisibility(View.GONE);
        //不包含当前商品时，直接添加到订单列表
        mGoodsOrderList.addFirstData(orderListInfo);
        //刷新价格
        refreshOrderListAndPrice();
    }

    private OnScanQRCodeListener mAddOrderScanQRCodeListener = new OnScanQRCodeListener() {
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

    /**
     * 等待扫码新增商品
     */
    private void waitScanQrCode() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mAddOrderScanQRCodeListener);
    }

    /**
     * 登录成功回调
     */
    private LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess() {
            if (llChangeOrderPrice != null) {
                if (LoginHelper.INSTANCE.hasPermissionChangePrice()) {
                    llChangeOrderPrice.setVisibility(View.VISIBLE);
                } else {
                    llChangeOrderPrice.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginHelper.INSTANCE.removeLoginCallback(loginCallback);
    }
}
