package com.stkj.supermarket.consumer.callback;

import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.setting.model.FacePassPeopleInfo;

import java.util.List;

public interface ConsumerController {

    void setFacePreview(boolean preview);

    void setFaceConsumerTips(String tips);

    void setFaceConsumerInfo(FacePassPeopleInfo facePassPeopleInfo, int consumerType);

    void setFaceConsumerInfo(String cardNumber);

    void setFaceConsumerInfo();

    void resetFaceConsumerLayout();

    /**
     * 设置默认状态
     */
    void setNormalConsumeStatus();

    /**
     * 设置支付状态
     */
    void setPayConsumeStatus();

    void setPayOrderInfo(boolean isFastPay, List<GoodsOrderListInfo> orderListInfoList, int totalCount, String totalPrice);

    void clearPayOrderInfo();

    boolean hasSetPayOrderInfo();
}
