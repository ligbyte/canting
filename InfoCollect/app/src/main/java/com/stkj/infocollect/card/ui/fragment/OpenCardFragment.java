package com.stkj.infocollect.card.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.model.BaseNetResponse;
import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.base.utils.EventBusUtils;
import com.stkj.infocollect.card.callback.StatusSuccessConfirmListener;
import com.stkj.infocollect.card.model.CardSetInfo;
import com.stkj.infocollect.card.model.UploadAvatarPicEvent;
import com.stkj.infocollect.card.service.CardService;
import com.stkj.infocollect.card.ui.widget.CardAccountInfoLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * 开卡页面
 */
public class OpenCardFragment extends BaseRecyclerFragment {

    private CardAccountInfoLayout accountInfoLay;
    private ShapeTextView stvCancel;
    private ShapeTextView stvUpload;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_open_card;
    }

    @Override
    protected void initViews(View rootView) {
        accountInfoLay = (CardAccountInfoLayout) findViewById(R.id.account_info_lay);
        stvCancel = (ShapeTextView) findViewById(R.id.stv_cancel);
        stvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OpenCardFragment.this);
            }
        });
        stvUpload = (ShapeTextView) findViewById(R.id.stv_upload);
        stvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOpenCardInfo();
            }
        });
        accountInfoLay.setAvatarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountInfoLay.clearEditFocus();
                mActivity.addContentPlaceHolderFragment(new AvatarCameraFragment());
            }
        });
    }

    /**
     * 提交开卡信息
     */
    private void submitOpenCardInfo() {
        //姓名
        String userName = accountInfoLay.getUserName();
        if (TextUtils.isEmpty(userName)) {
            CommonDialogUtils.showTipsDialog(mActivity, "姓名不能为空");
            return;
        }
        //身份证号
        String idCard = accountInfoLay.getIDCard();
        if (TextUtils.isEmpty(idCard)) {
            CommonDialogUtils.showTipsDialog(mActivity, "身份证号不能为空");
            return;
        }
        if (idCard.length() != 18) {
            CommonDialogUtils.showTipsDialog(mActivity, "请输入18位身份账号");
            return;
        }
        //家长姓名
        String parentName = accountInfoLay.getParentName();
        if (TextUtils.isEmpty(parentName)) {
            CommonDialogUtils.showTipsDialog(mActivity, "家长姓名不能为空");
            return;
        }
        //家长手机号
        String parentPhone = accountInfoLay.getParentPhone();
        if (TextUtils.isEmpty(parentPhone)) {
            CommonDialogUtils.showTipsDialog(mActivity, "家长手机号不能为空");
            return;
        }
        if (parentPhone.length() != 11) {
            CommonDialogUtils.showTipsDialog(mActivity, "请输入11位手机号");
            return;
        }
        //人脸头像
//        String avatarUrl = accountInfoLay.getAvatarUrl();
//        if (TextUtils.isEmpty(avatarUrl)) {
//            CommonDialogUtils.showTipsDialog(mActivity, "请点击添加人脸");
//            return;
//        }
        accountInfoLay.clearEditFocus();
        showLoadingDialog();
        HashMap<String, Object> openCardParams = new HashMap<>();
        openCardParams.put("name", userName);
        openCardParams.put("type", 1);
        openCardParams.put("idCardNumber", idCard);
        openCardParams.put("emergencyContact", parentName);
        openCardParams.put("emergencyPhone", parentPhone);
        openCardParams.put("faceImg", accountInfoLay.getAvatarUrl());
        getCardSetInfo(openCardParams);
    }

    private void getCardSetInfo(Map<String, Object> openCardInfoParams) {
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .getCardSetInfo()
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<CardSetInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<CardSetInfo> cardSetInfoBaseNetResponse) {
                        CardSetInfo data = cardSetInfoBaseNetResponse.getData();
                        if (data != null) {
                            openCardInfoParams.put("depositFee", data.getDepositFee());
                            openCardInfoParams.put("opencardFee", data.getOpencardFee());
                        } else {
                            openCardInfoParams.put("depositFee", "0");
                            openCardInfoParams.put("opencardFee", "0");
                        }
                        uploadOpenCardInfo(openCardInfoParams);
                    }

                    @Override
                    public void onError(Throwable e) {
                        openCardInfoParams.put("depositFee", "0");
                        openCardInfoParams.put("opencardFee", "0");
                        uploadOpenCardInfo(openCardInfoParams);
                    }
                });
    }

    private void uploadOpenCardInfo(Map<String, Object> openCardInfoParams) {
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .submitOpenCardInfo(openCardInfoParams)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> objectBaseNetResponse) {
                        hideLoadingDialog();
                        if (objectBaseNetResponse.isSuccess()) {
                            SuccessStatusFragment successStatusFragment = new SuccessStatusFragment();
                            successStatusFragment.setTitleTxt("开卡申请已提交");
                            successStatusFragment.setContentTxt(objectBaseNetResponse.getData());
                            successStatusFragment.setConfirmTxt("返回首页");
                            mActivity.addContentPlaceHolderFragment(successStatusFragment);
                            FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OpenCardFragment.this);
                        } else {
                            String message = objectBaseNetResponse.getMsg();
                            if (TextUtils.isEmpty(message)) {
                                CommonDialogUtils.showTipsDialog(mActivity, "提交失败,请重试");
                            } else {
                                CommonDialogUtils.showTipsDialog(mActivity, "提交失败:" + message);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "提交失败:" + e.getMessage());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadAvatarPic(UploadAvatarPicEvent uploadAvatarPicEvent) {
        String picUrl = uploadAvatarPicEvent.getPicUrl();
        if (!TextUtils.isEmpty(picUrl)) {
            accountInfoLay.setUserAvatar(picUrl);
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);
    }

    @Override
    public void onDestroy() {
        EventBusUtils.unRegisterEventBus(this);
        super.onDestroy();
    }
}
