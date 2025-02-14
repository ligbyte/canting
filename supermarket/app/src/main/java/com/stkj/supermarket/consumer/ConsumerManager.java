package com.stkj.supermarket.consumer;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.stkj.common.core.MainThreadHolder;
import com.stkj.common.utils.DisplayUtils;
import com.stkj.supermarket.base.callback.OnFacePassConfirmListener;
import com.stkj.supermarket.consumer.callback.ConsumerController;
import com.stkj.supermarket.consumer.callback.ConsumerListener;
import com.stkj.supermarket.consumer.ui.presentation.ConsumerPresentation;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.setting.model.FacePassPeopleInfo;

import java.util.List;

/**
 * 面向消费者屏幕
 */
public enum ConsumerManager implements ConsumerController {
    INSTANCE;
    private ConsumerPresentation consumerPresentation;

    /**
     * 显示消费者页面
     */
    public void showConsumer(Context context, ConsumerListener consumerListener) {
//        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
//        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
//        if (route != null) {
//            Display presentationDisplay = route.getPresentationDisplay();
//            if (presentationDisplay != null) {
//            }
//        }
        Display display = DisplayUtils.getIndexDisplay(1);
        if (display != null) {
            consumerPresentation = new ConsumerPresentation(context, display);
            consumerPresentation.setConsumerListener(consumerListener);
            consumerPresentation.show();
        } else {
            DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            mDisplayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int displayId) {
                    if (displayId != Display.DEFAULT_DISPLAY) {
                        Display managerDisplay = DisplayUtils.getIndexDisplay(1);
                        //add Presentation display
                        if (managerDisplay != null && consumerPresentation == null) {
                            consumerPresentation = new ConsumerPresentation(context, managerDisplay);
                            consumerPresentation.setConsumerListener(consumerListener);
                            consumerPresentation.show();
                        }
                    }
                }

                @Override
                public void onDisplayRemoved(int displayId) {

                }

                @Override
                public void onDisplayChanged(int displayId) {

                }
            }, MainThreadHolder.getMainHandler());
        }
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerListener(consumerListener);
        }
    }

    public void setFacePassConfirmListener(OnFacePassConfirmListener facePassConfirmListener) {
        if (consumerPresentation != null) {
            consumerPresentation.setFacePassConfirmListener(facePassConfirmListener);
        }
    }

    @Override
    public void setFaceConsumerTips(String tips) {
        if (consumerPresentation != null) {
            consumerPresentation.setFaceConsumerTips(tips);
        }
    }

    @Override
    public void setFaceConsumerInfo() {
        if (consumerPresentation != null) {
            consumerPresentation.setFaceConsumerInfo();
        }
    }

    @Override
    public void resetFaceConsumerLayout() {
        if (consumerPresentation != null) {
            consumerPresentation.resetFaceConsumerLayout();
        }
    }

    @Override
    public void setFacePreview(boolean preview) {
        if (consumerPresentation != null) {
            consumerPresentation.setFacePreview(preview);
        }
    }

    @Override
    public void setFaceConsumerInfo(FacePassPeopleInfo facePassPeopleInfo, int consumerType) {
        if (consumerPresentation != null) {
            consumerPresentation.setFaceConsumerInfo(facePassPeopleInfo, consumerType);
        }
    }

    @Override
    public void setFaceConsumerInfo(String cardNumber) {
        if (consumerPresentation != null) {
            consumerPresentation.setFaceConsumerInfo(cardNumber);
        }
    }

    @Override
    public void setNormalConsumeStatus() {
        if (consumerPresentation != null) {
            consumerPresentation.setNormalConsumeStatus();
        }
    }

    @Override
    public void setPayConsumeStatus() {
        if (consumerPresentation != null) {
            consumerPresentation.setPayConsumeStatus();
        }
    }

    @Override
    public void setPayOrderInfo(boolean isFastPay, List<GoodsOrderListInfo> orderListInfoList, int totalCount, String totalPrice) {
        if (consumerPresentation != null) {
            consumerPresentation.setPayOrderInfo(isFastPay, orderListInfoList, totalCount, totalPrice);
        }
    }

    @Override
    public void clearPayOrderInfo() {
        if (consumerPresentation != null) {
            consumerPresentation.clearPayOrderInfo();
        }
    }

    @Override
    public boolean hasSetPayOrderInfo() {
        if (consumerPresentation != null) {
            return consumerPresentation.hasSetPayOrderInfo();
        }
        return true;
    }

    public void clearConsumerPresentation() {
        consumerPresentation = null;
    }
}