package com.stkj.cashiermini.order.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.cashiermini.R;
import com.stkj.cashiermini.base.model.BaseNetResponse;
import com.stkj.cashiermini.base.net.ParamsUtils;
import com.stkj.cashiermini.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.cashiermini.base.utils.CommonDialogUtils;
import com.stkj.cashiermini.login.helper.LoginHelper;
import com.stkj.cashiermini.order.data.OrderConstants;
import com.stkj.cashiermini.order.model.OrderListInfo;
import com.stkj.cashiermini.order.model.OrderListQueryInfo;
import com.stkj.cashiermini.order.model.OrderListResponse;
import com.stkj.cashiermini.order.service.OrderService;
import com.stkj.cashiermini.order.ui.adapter.OrderListInfoItemViewHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;

import java.util.List;
import java.util.TreeMap;

/**
 * 订单管理页
 */
public class TabOrderFragment extends BaseRecyclerFragment {

    private ImageView ivRefresh;
    private RecyclerView rvOrderList;
    private AppSmartRefreshLayout srlOrderList;
    private CommonRecyclerAdapter orderListAdapter;
    //网络请求查询参数
    private OrderListQueryInfo queryInfo = new OrderListQueryInfo();
    private int mLastRequestPage;

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_order;
    }

    @Override
    protected void initViews(View rootView) {
        srlOrderList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh_list);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRefresh();
                rvOrderList.scrollToPosition(0);
                srlOrderList.autoRefresh();
            }
        });
        rvOrderList = (RecyclerView) findViewById(R.id.rv_content);
        srlOrderList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getOrderList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                resetRequestPage();
                queryInfo.resetDefaultData();
                getOrderList();
            }
        });
        orderListAdapter = new CommonRecyclerAdapter(false);
        orderListAdapter.addViewHolderFactory(new OrderListInfoItemViewHolder.Factory());
        orderListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                OrderListInfo orderListInfo = (OrderListInfo) obj;
                if (eventId == OrderListInfoItemViewHolder.EVENT_CLICK) {
                    CommonDialogUtils.showTipsDialog(mActivity, "订单id:" + orderListInfo.getId() +
                            "\n餐别:" + OrderConstants.getFeeTypeStr(orderListInfo.getFeeType()) +
                            "\n支付方式:" + OrderConstants.getPayTypeStr(orderListInfo.getConsumeMethod()));
                }
            }
        });
        rvOrderList.setAdapter(orderListAdapter);
    }

    private void animateRefresh() {
        ivRefresh.animate()
                .rotationBy(360)
                .setDuration(300)
                .start();
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            srlOrderList.autoRefresh();
        }
    }

    private void getOrderList() {
        int currentPage = mLastRequestPage + 1;
        queryInfo.setPageIndex(currentPage);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("mode", "ConsumeRecordList");
        paramsMap.put("machine_Number", LoginHelper.INSTANCE.getMachineNumber());
        paramsMap.put("pageIndex", String.valueOf(queryInfo.getPageIndex()));
        paramsMap.put("pageSize", String.valueOf(queryInfo.getPageSize()));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(OrderService.class)
                .getOrderList(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<OrderListResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OrderListResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        srlOrderList.finishRefresh();
                        srlOrderList.finishLoadMore();
                        //第一页清空旧数据
                        if (currentPage == 1) {
                            orderListAdapter.removeAllData();
                        }
                        OrderListResponse responseData = responseBaseResponse.getData();
                        if (responseBaseResponse.isSuccess() && responseData != null) {
                            List<OrderListInfo> dataRecords = responseData.getResults();
                            if (dataRecords != null && !dataRecords.isEmpty()) {
                                mLastRequestPage = currentPage;
                                orderListAdapter.addDataList(dataRecords);
                            } else {
                                AppToast.toastMsg("没有更多数据了");
                            }
                        } else {
                            AppToast.toastMsg("没有更多数据了");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlOrderList.finishRefresh();
                        srlOrderList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

}
