package com.stkj.cashier.pay.ui.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.cashier.R;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.ConsumerRecordListInfo;
import com.stkj.cashier.pay.model.ConsumerRecordListQueryInfo;
import com.stkj.cashier.pay.model.ConsumerRecordListResponse;
import com.stkj.cashier.pay.model.ConsumerSuccessEvent;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.pay.ui.adapter.ConsumerRecordListItemViewHolder;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.TreeMap;

/**
 * 消费订单列表页面
 */
public class ConsumerRecordListFragment extends BaseRecyclerFragment {

    private RecyclerView rvOrderList;
    private AppSmartRefreshLayout srlOrderList;
    private CommonRecyclerAdapter orderListAdapter;
    //网络请求查询参数
    private ConsumerRecordListQueryInfo queryInfo = new ConsumerRecordListQueryInfo();
    private int mLastRequestPage;

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_consumer_record_list;
    }

    @Override
    protected void initViews(View rootView) {
        srlOrderList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
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
        orderListAdapter.addViewHolderFactory(new ConsumerRecordListItemViewHolder.Factory());
        orderListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                ConsumerRecordListInfo consumerRecordListInfo = (ConsumerRecordListInfo) obj;
                if (eventId == ConsumerRecordListItemViewHolder.EVENT_CLICK) {
                    CommonDialogUtils.showTipsDialog(mActivity, "账单编号:" + consumerRecordListInfo.getId() +
                            "\n餐别:" + PayConstants.getFeeTypeStr(consumerRecordListInfo.getFeeType()) +
                            "\n支付方式:" + PayConstants.getPayTypeStr(consumerRecordListInfo.getConsumeMethod()) +
                            "\n金额:" + consumerRecordListInfo.getBizAmount() +
                            "\n支付时间:" + consumerRecordListInfo.getBizDate() +
                            "\n姓名:" + consumerRecordListInfo.getFull_Name() +
                            "\n卡号:" + consumerRecordListInfo.getCard_Number() +
                            "\n账号:" + consumerRecordListInfo.getUser_Tel());
                }
            }
        });
        rvOrderList.setAdapter(orderListAdapter);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        isOnPause = false;
        EventBusUtils.registerEventBus(this);
        if (isFirstOnResume) {
            srlOrderList.autoRefresh();
        } else {
            if (needRefreshRecordList) {
                needRefreshRecordList = false;
                srlOrderList.autoRefresh();
            }
        }
    }

    private boolean isOnPause;
    private boolean needRefreshRecordList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerRecordList(ConsumerSuccessEvent eventBus) {
        LogHelper.print("--EventBusUtils-onRefreshConsumerRecordList");
        if (isOnPause) {
            needRefreshRecordList = true;
        } else {
            srlOrderList.autoRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

    private void getOrderList() {
        int currentPage = mLastRequestPage + 1;
        queryInfo.setPageIndex(currentPage);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("ConsumeRecordList");
        paramsMap.put("pageIndex", String.valueOf(queryInfo.getPageIndex()));
        paramsMap.put("pageSize", String.valueOf(queryInfo.getPageSize()));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .getConsumerRecordList(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<ConsumerRecordListResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<ConsumerRecordListResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        srlOrderList.finishRefresh();
                        srlOrderList.finishLoadMore();
                        //第一页清空旧数据
                        if (currentPage == 1) {
                            orderListAdapter.removeAllData();
                        }
                        ConsumerRecordListResponse responseData = responseBaseResponse.getData();
                        if (responseBaseResponse.isSuccess() && responseData != null) {
                            List<ConsumerRecordListInfo> dataRecords = responseData.getResults();
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
