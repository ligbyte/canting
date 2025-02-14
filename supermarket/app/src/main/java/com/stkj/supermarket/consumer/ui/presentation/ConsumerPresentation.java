package com.stkj.supermarket.consumer.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.callback.OnFacePassConfirmListener;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.ui.widget.FacePassCameraLayout;
import com.stkj.supermarket.consumer.callback.ConsumerController;
import com.stkj.supermarket.consumer.callback.ConsumerListener;
import com.stkj.supermarket.consumer.ui.adapter.GoodsConsumerViewHolder;
import com.stkj.supermarket.home.ui.widget.HomeTitleLayout;
import com.stkj.supermarket.pay.data.PayConstants;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.setting.model.FacePassPeopleInfo;

import java.util.List;

/**
 * 快速收银展示页
 */
public class ConsumerPresentation extends Presentation implements ConsumerController {

    private LinearLayout llOrderList;
    private RecyclerView rvGoodsList;
    private LinearLayout llFastPayPresentation;
    private TextView tvGoodsCount;
    private TextView tvGoodsPrice;
    private ShapeFrameLayout sflOrderList;
    private ShapeLinearLayout sllConsumerContent;
    private TextView tvBottomTips;
    //    private FacePassCameraXLayout fpcFaceX;
    private FacePassCameraLayout fpcFace;
    private HomeTitleLayout htlConsumer;

    private ConsumerListener consumerListener;
    private OnFacePassConfirmListener facePassConfirmListener;
    private CommonRecyclerAdapter mOrderAdapter;
    private boolean isSetPayOrderInfo;
    private LinearLayout llFaceConfirm;
    private ShapeTextView stvFaceLeftBt;
    private ShapeTextView stvFaceRightBt;

    public ConsumerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public ConsumerPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            //窗口标记属性
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            window.setAttributes(attributes);
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        int consumeLayRes = DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes();
        if (consumeLayRes == 1) {
            setContentView(R.layout.presentation_consumer_s1);
        } else if (consumeLayRes == 2) {
            setContentView(R.layout.presentation_consumer_s2);
        } else {
            setContentView(R.layout.presentation_consumer);
        }
        findViews();
        LogHelper.print("-Consumer--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }

    private void findViews() {
        htlConsumer = (HomeTitleLayout) findViewById(R.id.htl_consumer);
        sflOrderList = (ShapeFrameLayout) findViewById(R.id.sfl_order_list);
        rvGoodsList = (RecyclerView) findViewById(R.id.rv_goods_list);
        llFastPayPresentation = (LinearLayout) findViewById(R.id.ll_fast_pay_presentation);
        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
        sllConsumerContent = (ShapeLinearLayout) findViewById(R.id.sll_consumer_content);
        tvBottomTips = (TextView) findViewById(R.id.tv_bottom_tips);
//        fpcFaceX = (FacePassCameraXLayout) findViewById(R.id.fpc_face_x);
//        if (consumerListener != null) {
//            consumerListener.onCreateFaceXPreviewView(fpcFaceX.getFacePreviewFace());
//        }
        fpcFace = (FacePassCameraLayout) findViewById(R.id.fpc_face);
        llFaceConfirm = (LinearLayout) findViewById(R.id.ll_face_confirm);
        stvFaceLeftBt = (ShapeTextView) findViewById(R.id.stv_face_left_bt);
        stvFaceRightBt = (ShapeTextView) findViewById(R.id.stv_face_right_bt);
        if (consumerListener != null) {
            consumerListener.onCreateFacePreviewView(fpcFace.getFacePreviewFace(), fpcFace.getIrPreviewFace());
        }
        if (consumerListener != null) {
            consumerListener.onCreateTitleLayout(htlConsumer);
        }

        mOrderAdapter = new CommonRecyclerAdapter(false);
        mOrderAdapter.addViewHolderFactory(new GoodsConsumerViewHolder.Factory());
        rvGoodsList.setAdapter(mOrderAdapter);
    }

    public void setFacePassConfirmListener(OnFacePassConfirmListener facePassConfirmListener) {
        this.facePassConfirmListener = facePassConfirmListener;
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    @Override
    public void setFacePreview(boolean preview) {
        if (fpcFace != null) {
            fpcFace.setPreviewFace(preview);
        }
    }

    @Override
    public void setFaceConsumerTips(String tips) {
        if (fpcFace != null) {
            fpcFace.setFaceCameraTips(tips);
        }
    }

    /**
     * 现金支付
     */
    @Override
    public void setFaceConsumerInfo() {
        llFaceConfirm.setVisibility(View.VISIBLE);
        fpcFace.setFaceCameraTips("请确定支付?");
        stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onConfirmFacePass();
                }
                llFaceConfirm.setVisibility(View.GONE);
            }
        });
        stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onCancelFacePass();
                }
                resetFaceConsumerLayout();
            }
        });
    }

    @Override
    public void setFaceConsumerInfo(String cardNumber) {
        llFaceConfirm.setVisibility(View.VISIBLE);
        fpcFace.setFaceCameraTips("读卡成功,请确定支付?");
        stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onConfirmFacePass(cardNumber);
                }
                llFaceConfirm.setVisibility(View.GONE);
            }
        });
        stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onCancelFacePass(cardNumber);
                }
                resetFaceConsumerLayout();
            }
        });
    }

    @Override
    public void setFaceConsumerInfo(FacePassPeopleInfo facePassPeopleInfo, int consumerType) {
        llFaceConfirm.setVisibility(View.VISIBLE);
        fpcFace.setFaceCameraTips("识别成功:" + facePassPeopleInfo.getFull_Name() + " 请确定支付?");
        fpcFace.setFaceImage(facePassPeopleInfo.getImgData());
        stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    if (consumerType == PayConstants.PAY_TYPE_IC_CARD) {
                        facePassConfirmListener.onConfirmFacePass(facePassPeopleInfo.getCard_Number());
                    } else {
                        facePassConfirmListener.onConfirmFacePass(facePassPeopleInfo);
                    }
                }
                llFaceConfirm.setVisibility(View.GONE);
            }
        });
        stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    if (consumerType == PayConstants.PAY_TYPE_IC_CARD) {
                        facePassConfirmListener.onCancelFacePass(facePassPeopleInfo.getCard_Number());
                    } else {
                        facePassConfirmListener.onCancelFacePass(facePassPeopleInfo);
                    }
                }
                resetFaceConsumerLayout();
            }
        });
    }

    @Override
    public void setNormalConsumeStatus() {
        tvBottomTips.setVisibility(View.GONE);
        sflOrderList.setVisibility(View.GONE);
    }

    @Override
    public void setPayConsumeStatus() {
        tvBottomTips.setVisibility(View.VISIBLE);
        sflOrderList.setVisibility(isSetPayOrderInfo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPayOrderInfo(boolean isFastPay, List<GoodsOrderListInfo> orderListInfoList, int totalCount, String totalPrice) {
        isSetPayOrderInfo = true;
        sflOrderList.setVisibility(View.VISIBLE);
        if (isFastPay) {
            llFastPayPresentation.setVisibility(View.VISIBLE);
            rvGoodsList.setVisibility(View.GONE);
        } else {
            llFastPayPresentation.setVisibility(View.GONE);
            rvGoodsList.setVisibility(View.VISIBLE);
            mOrderAdapter.removeAllData();
            mOrderAdapter.addDataList(orderListInfoList);
            rvGoodsList.scrollToPosition(0);
        }
        int countColor = getResources().getColor(com.stkj.common.R.color.color_333333);
        SpanUtils.with(tvGoodsCount)
                .append(String.valueOf(totalCount))
                .setForegroundColor(countColor)
                .append(" 件")
                .create();
        int priceColor = getResources().getColor(R.color.color_FF3C30);
        SpanUtils.with(tvGoodsPrice)
                .append("共计: ")
                .append("¥")
                .setForegroundColor(priceColor)
                .append(totalPrice)
                .setForegroundColor(priceColor)
                .create();
    }

    @Override
    public void clearPayOrderInfo() {
        isSetPayOrderInfo = false;
        mOrderAdapter.removeAllData();
        tvGoodsCount.setText("");
        tvGoodsPrice.setText("");
        sflOrderList.setVisibility(View.GONE);
    }

    @Override
    public boolean hasSetPayOrderInfo() {
        return isSetPayOrderInfo;
    }

    @Override
    public void resetFaceConsumerLayout() {
        if (fpcFace != null) {
            llFaceConfirm.setVisibility(View.GONE);
            fpcFace.resetFaceInfoLayout();
        }
    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
        if (consumerListener != null) {
            consumerListener.onConsumerDismiss();
        }
        LogHelper.print("ConsumerPresentation--onDisplayRemoved");
    }

    @Override
    public void onDisplayChanged() {
        super.onDisplayChanged();
        if (consumerListener != null) {
            consumerListener.onConsumerChanged();
        }
        LogHelper.print("ConsumerPresentation--onDisplayChanged");
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            //非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Throwable e) {
            e.printStackTrace();
            AppToast.toastMsg("副屏初始化失败");
        }
    }
}
