package com.stkj.cashier.stat.ui.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import com.stkj.cashier.stat.model.CanteenSummary;
import com.stkj.cashier.stat.service.StatService;
import com.stkj.cashier.stat.ui.adapter.StatConsumerRecordListItemViewHolder;
import com.stkj.cashier.stat.ui.weight.StatPieChart;
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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 统计页面
 */
public class TabStatFragment extends BaseRecyclerFragment {

    private AppSmartRefreshLayout srlRecordList;
    private RecyclerView rvContent;
    private BarChart chartConsumerNumber;
    private TextView tvConsumerNumber;
    private StatPieChart chartConsumerAmount;
    private TextView tvConsumerAmount;
    private CommonRecyclerAdapter orderListAdapter;
    //网络请求查询参数
    private ConsumerRecordListQueryInfo queryInfo = new ConsumerRecordListQueryInfo();
    private int mLastRequestPage;
    private boolean needRefreshStatData;

    private void resetRequestPage() {
        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_stat;
    }

    @Override
    protected void initViews(View rootView) {
        srlRecordList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        chartConsumerNumber = (BarChart) findViewById(R.id.chart_consumer_number);
        tvConsumerNumber = (TextView) findViewById(R.id.tv_consumer_number);
        chartConsumerAmount = (StatPieChart) findViewById(R.id.chart_consumer_amount);
        tvConsumerAmount = (TextView) findViewById(R.id.tv_consumer_amount);
        srlRecordList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
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
        orderListAdapter.addViewHolderFactory(new StatConsumerRecordListItemViewHolder.Factory());
        orderListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                ConsumerRecordListInfo consumerRecordListInfo = (ConsumerRecordListInfo) obj;
                if (eventId == StatConsumerRecordListItemViewHolder.EVENT_CLICK) {
                    CommonDialogUtils.showTipsDialog(mActivity, "账单编号:" + consumerRecordListInfo.getId() +
                            "\n餐别:" + PayConstants.getFeeTypeStr(consumerRecordListInfo.getFeeType()) +
                            "\n支付方式:" + PayConstants.getPayTypeStr(consumerRecordListInfo.getConsumeMethod()) +
                            "\n金额:" + consumerRecordListInfo.getBizAmount() +
                            "\n支付时间:" + consumerRecordListInfo.getBizDate() +
                            "\n姓名:" + consumerRecordListInfo.getFull_Name() +
                            "\n卡号:" + consumerRecordListInfo.getCard_Number() +
                            "\n手机号:" + consumerRecordListInfo.getUser_Tel());
                }
            }
        });
        rvContent.setAdapter(orderListAdapter);
    }

    private boolean isOnPause;

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        isOnPause = false;
        EventBusUtils.registerEventBus(this);
        if (isFirstOnResume) {
            srlRecordList.autoRefresh();
            getCanteenSummary();
        } else {
            if (needRefreshStatData) {
                needRefreshStatData = false;
                srlRecordList.autoRefresh();
                getCanteenSummary();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerRecordList(ConsumerSuccessEvent eventBus) {
        LogHelper.print("--EventBusUtils-onRefreshConsumerRecordList");
        if (isOnPause) {
            needRefreshStatData = true;
        } else {
            srlRecordList.autoRefresh();
            getCanteenSummary();
        }
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
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
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
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

    /**
     * 获取餐厅统计信息
     */
    private void getCanteenSummary() {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("Canteen_summary");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(StatService.class)
                .getCanteenSummary(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<CanteenSummary>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<CanteenSummary> baseNetResponse) {
                        if (baseNetResponse.isSuccess()) {
                            CanteenSummary data = baseNetResponse.getData();
                            List<CanteenSummary.ConsumeMethodList> consumeMethodList = data.getConsumeMethodList();
                            if (consumeMethodList != null) {
                                refreshChartAmount(consumeMethodList);
                            }
                            List<CanteenSummary.FeeTypeList> feeTypeList = data.getFeeTypeList();
                            if (feeTypeList != null) {
                                refreshChartNumber(feeTypeList);
                            }
                        }
                    }
                });
    }

    /**
     * 刷新消费金额表格
     */
    private void refreshChartAmount(List<CanteenSummary.ConsumeMethodList> consumeMethodList) {
        //总共消费金额
        float totalAmount = 0f;
//        chartConsumerAmount.animateXY(1000,1000);
        //设置绘不绘制中间空白
        chartConsumerAmount.setDrawHoleEnabled(false);
        //设置手动旋转
        chartConsumerAmount.setRotationEnabled(true);
        //设置默认旋转度数
        chartConsumerAmount.setRotationAngle(90f);
        //取消右下角描述
        chartConsumerAmount.getDescription().setEnabled(false);
//        //设置文字描述为白色
//        chartConsumerAmount.setEntryLabelColor(resources.getColor(R.color.text_6));
//        //设置文字描述的大小
//        chartConsumerAmount.setEntryLabelTextSize(15f);
//        //设置文字描述的样式
//        chartConsumerAmount.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
//        //设置四个方向的偏移
//        chartConsumerAmount.setExtraOffsets(5f,5f,5f,5f);
        //设置图标的转动阻力摩擦系数
        chartConsumerAmount.setDragDecelerationFrictionCoef(0.2f);

        chartConsumerAmount.setDrawEntryLabels(true);
        chartConsumerAmount.setEntryLabelColor(mResources.getColor(R.color.color_666666));
        chartConsumerAmount.setEntryLabelTextSize(12f);
//        chartConsumerAmount.setExtraOffsets(15f,15f,15f,15f)

        //得到图例
        Legend legend = chartConsumerAmount.getLegend();
        legend.setEnabled(false);
        //设置图例的样式
        float code = 0f;
        float code1 = 0f;
        float code2 = 0f;
        float code3 = 0f;

        float face = 0f;
        float face1 = 0f;
        float face2 = 0f;
        float face3 = 0f;

        float card = 0f;
        float card1 = 0f;
        float card2 = 0f;
        float card3 = 0f;

        float other = 0f;
        float other1 = 0f;
        float other2 = 0f;
        float other3 = 0f;
        for (CanteenSummary.ConsumeMethodList item : consumeMethodList) {
            //总共消费金额
            totalAmount += item.getValue();
            if ("刷脸".equals(item.getKey())) {
                face += item.getValue();
                if ("早餐".equals(item.getKey1())) {
                    face1 += item.getValue();
                } else if ("午餐".equals(item.getKey1())) {
                    face2 += item.getValue();
                } else if ("晚餐".equals(item.getKey1())) {
                    face3 += item.getValue();
                }
            } else if ("扫码".equals(item.getKey())) {
                code += item.getValue();
                if ("早餐".equals(item.getKey1())) {
                    code1 += item.getValue();
                } else if ("午餐".equals(item.getKey1())) {
                    code2 += item.getValue();
                } else if ("晚餐".equals(item.getKey1())) {
                    code3 += item.getValue();
                }
            } else if ("刷卡".equals(item.getKey())) {
                card += item.getValue();
                if ("早餐".equals(item.getKey1())) {
                    card1 += item.getValue();
                } else if ("午餐".equals(item.getKey1())) {
                    card2 += item.getValue();
                } else if ("晚餐".equals(item.getKey1())) {
                    card3 += item.getValue();
                }
            } else if ("其他".equals(item.getKey())) {
                other += item.getValue();
                if ("早餐".equals(item.getKey1())) {
                    other1 += item.getValue();
                } else if ("午餐".equals(item.getKey1())) {
                    other2 += item.getValue();
                } else if ("晚餐".equals(item.getKey1())) {
                    other3 += item.getValue();
                }
            }
        }
        //设置数据集合
        List<PieEntry> entries = new ArrayList<>();
        String strCode = String.format("%.2f", code);
        String strFace = String.format("%.2f", face);
        String strCard = String.format("%.2f", card);
        String strOther = String.format("%.2f", other);
        if (code != 0f) {
            entries.add(new PieEntry(code, "扫码： " + strCode + "\n早餐：" + code1 + "\n午餐：" + code2 + "\n晚餐：" + code3));
        }
        if (face != 0f) {
            entries.add(new PieEntry(face, "刷脸：" + strFace + "\n早餐：" + face1 + "\n午餐：" + face2 + "\n晚餐：" + face3));
        }
        if (card != 0f) {
            entries.add(new PieEntry(card, "刷卡：" + strCard + "\n早餐：" + card1 + "\n午餐：" + card2 + "\n晚餐：" + card3));
        }
        if (other != 0f) {
            entries.add(new PieEntry(other, "其他：" + strOther + "\n早餐：" + other1 + "\n午餐：" + other2 + "\n晚餐：" + other3));
        }
        //设置显示的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#14C9C8"));
        colors.add(Color.parseColor("#3489F5"));
        colors.add(Color.parseColor("#99CEFF"));
        //添加数据集合
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(mResources.getColor(R.color.color_666666));
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataSet.setDrawValues(false);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        chartConsumerAmount.setData(pieData);
        chartConsumerAmount.setNoDataText("");
        chartConsumerAmount.notifyDataSetChanged();
        chartConsumerAmount.invalidate();

        //总共消费金额
        String str = String.format("%.2f", totalAmount);
        tvConsumerAmount.setText("¥ " + str);
    }

    private static final String[] feeTypeConstStr = new String[]{"早餐", "午餐", "晚餐"};

    /**
     * 刷新消费人员表格
     */
    private void refreshChartNumber(List<CanteenSummary.FeeTypeList> feeTypeList) {
        //总共消费人数
        int totalConsumerNumber = 0;
        //设置图例的样式
        //UI
        // 不显示图例
        chartConsumerNumber.getLegend().setEnabled(false);
        // 不显示描述
        chartConsumerNumber.getDescription().setEnabled(false);
        // 左右空出barWidth/2，更美观
        chartConsumerNumber.setFitBars(true);
        // 不绘制网格
        chartConsumerNumber.setDrawGridBackground(false);
        XAxis xAxis = chartConsumerNumber.getXAxis();
        // 设置x轴显示在下方
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置x轴不画线
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(3);
        // 设置自定义的ValueFormatter
        ValueFormatter axisValueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return feeTypeConstStr[(int) value];
            }
        };
        xAxis.setValueFormatter(axisValueFormatter);

        // 设置左y轴
        YAxis yAxis = chartConsumerNumber.getAxisLeft();
        // 设置y-label显示在图表外
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // Y轴从0开始，不然会上移一点
        yAxis.setAxisMinimum(0f);
        // 设置y轴不画线
        yAxis.setDrawGridLines(true);
        yAxis.setLabelCount(6, false);
        yAxis.enableGridDashedLine(4f, 2f, 2f);
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        // 不显示右y轴
        YAxis rightAxis = chartConsumerNumber.getAxisRight();
        rightAxis.setEnabled(false);

        float meal1 = 0;
        float meal2 = 0;
        float meal3 = 0;
        for (CanteenSummary.FeeTypeList item : feeTypeList) {
            //总共消费人数
            totalConsumerNumber += item.getValue();
            if ("早餐".equals(item.getKey())) {
                meal1 += item.getValue();
            } else if ("午餐".equals(item.getKey())) {
                meal2 += item.getValue();
            } else if ("晚餐".equals(item.getKey())) {
                meal3 += item.getValue();
            }

        }
        //data
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, meal1));
        barEntries.add(new BarEntry(1f, meal2));
        barEntries.add(new BarEntry(2f, meal3));
//        yAxis.valueFormatter = IAxisValueFormatter { value, axis ->
//            //value的值是从0开始的
//            barEntries[value.toInt()].y.toInt().toString()
//        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        // 设置颜色
        List<Integer> colors = new ArrayList<>();
//        colors.add(R.color.color_58afff)
//        colors.add(R.color.color_3489f5)
//        colors.add(R.color.color_99ceff)
        colors.add(Color.parseColor("#58afff"));
        colors.add(Color.parseColor("#3489f5"));
        colors.add(Color.parseColor("#99ceff"));
        barDataSet.setColors(colors);
        BarData ba = new BarData(barDataSet);

        ba.setBarWidth(0.5f);
        ba.setValueTextColor(mResources.getColor(R.color.color_3489F5));
        chartConsumerNumber.setData(ba);
        chartConsumerNumber.setNoDataText("");
        chartConsumerNumber.notifyDataSetChanged();
        chartConsumerNumber.setTouchEnabled(false);
        chartConsumerNumber.invalidate();

        //总消费人数
        tvConsumerNumber.setText(totalConsumerNumber + "位");
    }

}
