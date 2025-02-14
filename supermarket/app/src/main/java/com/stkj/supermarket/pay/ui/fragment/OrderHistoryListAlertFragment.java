package com.stkj.supermarket.pay.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.JacksonUtils;
import com.stkj.supermarket.pay.data.PayConstants;
import com.stkj.supermarket.pay.model.OrderHistoryListRequestPageParams;
import com.stkj.supermarket.pay.model.OrderHistoryListResponse;
import com.stkj.supermarket.pay.model.OrderHistoryResponse;
import com.stkj.supermarket.pay.model.PayHistoryOrderInfo;
import com.stkj.supermarket.pay.model.WaitHistoryOrderInfo;
import com.stkj.supermarket.pay.service.PayService;
import com.stkj.supermarket.pay.ui.adapter.GoodsPayHistoryOrderInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Function;

/**
 * 订单历史列表
 */
public class OrderHistoryListAlertFragment extends BaseRecyclerFragment implements OrderHistoryDetailAlertFragment.OnConfirmOrderListener {

    private OrderHistoryDetailAlertFragment.OnConfirmOrderListener onConfirmOrderListener;
    private AppSmartRefreshLayout srlOrderHistoryList;
    private RecyclerView rvOrderHistoryList;
    private CommonRecyclerAdapter orderListAdapter;
    private int mLastRequestPage;
    private OrderHistoryListRequestPageParams requestPageParams = new OrderHistoryListRequestPageParams();

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected void initViews(View rootView) {
        srlOrderHistoryList = (AppSmartRefreshLayout) findViewById(R.id.srl_order_history_list);
        rvOrderHistoryList = (RecyclerView) findViewById(R.id.rv_order_history_list);
        orderListAdapter = new CommonRecyclerAdapter(false);
        orderListAdapter.addViewHolderFactory(new GoodsPayHistoryOrderInfoViewHolder.Factory());
        orderListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                PayHistoryOrderInfo payHistoryOrderInfo = (PayHistoryOrderInfo) obj;
                if (payHistoryOrderInfo.getOrderType() == PayConstants.ORDER_TYPE_FAST_PAY) {
                    AppToast.toastMsg("快速收银没有商品信息");
                    return;
                }
                OrderHistoryDetailAlertFragment historyDetailAlertFragment = new OrderHistoryDetailAlertFragment();
                historyDetailAlertFragment.setPayHistoryOrderInfo(payHistoryOrderInfo);
                historyDetailAlertFragment.setOnConfirmOrderListener(OrderHistoryListAlertFragment.this);
                historyDetailAlertFragment.setHasOrderPay(TextUtils.equals(PayConstants.ORDER_SUCCESS_STATUS, payHistoryOrderInfo.getOrderStatus()));
                FragmentUtils.safeAddFragment(getParentFragmentManager(), historyDetailAlertFragment, R.id.fl_pay_second_content);
            }
        });
        rvOrderHistoryList.setAdapter(orderListAdapter);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryListAlertFragment.this);
            }
        });
        srlOrderHistoryList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getOrderHistoryList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                resetRequestPage();
                requestPageParams.resetPage();
                getOrderHistoryList();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            srlOrderHistoryList.autoRefresh();
        }
    }

    private void getOrderHistoryList() {
        int currentPage = mLastRequestPage + 1;
        requestPageParams.setCurrent(currentPage);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .getOrderHistoryList(JacksonUtils.convertObjectToMap(requestPageParams))
                .map(new Function<BaseResponse<OrderHistoryListResponse>, List<PayHistoryOrderInfo>>() {
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
                })
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<List<PayHistoryOrderInfo>>() {
                    @Override
                    protected void onSuccess(List<PayHistoryOrderInfo> payHistoryOrderInfos) {
                        srlOrderHistoryList.finishRefresh();
                        srlOrderHistoryList.finishLoadMore();
                        //第一页清空旧数据
                        if (currentPage == 1) {
                            orderListAdapter.removeAllData();
                        }
                        if (payHistoryOrderInfos != null && !payHistoryOrderInfos.isEmpty()) {
                            mLastRequestPage = currentPage;
                            orderListAdapter.addDataList(payHistoryOrderInfos);
                        } else {
                            AppToast.toastMsg("没有更多数据了");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        srlOrderHistoryList.finishRefresh();
                        srlOrderHistoryList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

    public void setOnConfirmOrderListener(OrderHistoryDetailAlertFragment.OnConfirmOrderListener onConfirmOrderListener) {
        this.onConfirmOrderListener = onConfirmOrderListener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_alert_order_goods_history_list;
    }

    @Override
    public void onConfirmPayOrder(PayHistoryOrderInfo payHistoryOrderInfo) {
        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryListAlertFragment.this);
        if (onConfirmOrderListener != null) {
            onConfirmOrderListener.onConfirmPayOrder(payHistoryOrderInfo);
        }
    }

    @Override
    public void onConfirmWaitOrder(WaitHistoryOrderInfo waitHistoryOrderInfo) {
        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryListAlertFragment.this);
        if (onConfirmOrderListener != null) {
            onConfirmOrderListener.onConfirmWaitOrder(waitHistoryOrderInfo);
        }
    }

}
