package com.stkj.supermarket.base.callback;

import com.stkj.supermarket.setting.model.FacePassPeopleInfo;

public interface OnFacePassConfirmListener {

    void onConfirmFacePass(FacePassPeopleInfo passPeopleInfo);

    void onCancelFacePass(FacePassPeopleInfo passPeopleInfo);

    void onConfirmFacePass(String cardNumber);

    void onCancelFacePass(String cardNumber);

    void onConfirmFacePass();

    void onCancelFacePass();
}
