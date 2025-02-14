package com.stkj.infocollect.card.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.model.CBGFacePassRecognizeResult;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultDisposeObserver;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.ui.widget.FacePassCameraLayout;
import com.stkj.infocollect.card.callback.FacePassVerifyListener;
import com.stkj.infocollect.card.model.UserCardInfo;
import com.stkj.infocollect.card.service.CardService;
import com.stkj.infocollect.home.helper.CBGCameraHelper;
import com.stkj.infocollect.setting.helper.FacePassHelper;
import com.stkj.infocollect.setting.model.FacePassPeopleInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 人脸校验
 */
public class FacePassVerificationFragment extends BaseRecyclerFragment {

    /**
     * 申请挂失补卡
     */
    public static final int APPLY_LOSS_REPLACE_CARD = 1;
    /**
     * 查询挂失补卡
     */
    public static final int SEARCH_LOSS_REPLACE_CARD = 2;

    private FacePassCameraLayout fpclAvatar;
    private ShapeTextView stvCancel;
    private FacePassVerifyListener facePassVerifyListener;
    private CBGCameraHelper cbgCameraHelper;
    private int operateType;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_facepass_verificaiton;
    }

    @Override
    protected void initViews(View rootView) {
        fpclAvatar = (FacePassCameraLayout) findViewById(R.id.fpcl_avatar);
        stvCancel = (ShapeTextView) findViewById(R.id.stv_cancel);
        stvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassVerificationFragment.this);
            }
        });
        cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.setPreviewView(fpclAvatar.getFacePreviewFace(), null, false);
        cbgCameraHelper.setOnDetectFaceListener(new CBGFacePassHandlerHelper.OnDetectFaceListener() {
            @Override
            public void onDetectFaceToken(List<CBGFacePassRecognizeResult> faceTokenList) {
                processFacePassResult(faceTokenList);
            }

            @Override
            public void onNoDetectFaceToken() {
                processFacePassFailRetryDelay(-1);
                LogHelper.print("--FacePassVerificationFragment--handleFacePassResult  onNoDetectFaceToken");
            }
        });
    }

    //人脸识别失败语音提示
    private boolean canSpeakFacePassFail = true;
    //人脸识别失败语音提示延迟
    private DefaultDisposeObserver<Long> canSpeakFacePassFailObserver;

    /**
     * 处理识别人脸结果
     */
    protected void processFacePassResult(List<CBGFacePassRecognizeResult> faceTokenList) {
        if (faceTokenList != null && !faceTokenList.isEmpty()) {
            LogHelper.print("--FacePassVerificationFragment--handleFacePassResult  faceTokenList = " + faceTokenList.size());
            FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
            CBGFacePassRecognizeResult facePassRecognizeResult = faceTokenList.get(0);
            if (facePassRecognizeResult.isFacePassSuccess()) {
                facePassHelper.searchFacePassByFaceToken(facePassRecognizeResult.getFaceToken(), new FacePassHelper.OnHandleFaceTokenListener() {
                    @Override
                    public void onHandleLocalFace(String faceToken, FacePassPeopleInfo facePassPeopleInfo) {
                        //停止所有的识别检测
                        cbgCameraHelper.stopFacePassDetect();
                        fpclAvatar.setFaceCameraTips("识别成功");
                        fpclAvatar.setFaceImage(facePassPeopleInfo.getImgData());
                        handleFacePassOperate(facePassPeopleInfo);
                        LogHelper.print("--FacePassVerificationFragment--handleFacePassResult  confirmFaceInfo");
                    }

                    @Override
                    public void onHandleLocalFaceError(String faceToken) {
                        processFacePassFailRetryDelay(-1);
                        LogHelper.print("--FacePassVerificationFragment--handleFacePassResult  onHandleLocalFaceError");
                    }
                });
            } else {
                processFacePassFailRetryDelay(facePassRecognizeResult.getRecognitionState());
            }
        }
    }

    /**
     * 处理人脸识别失败自动重试
     */
    protected void processFacePassFailRetryDelay(int recognizeState) {
        if (canSpeakFacePassFail) {
            fpclAvatar.setFaceCameraTips("人脸信息不存在，正在重试");
            canSpeakFacePassFail = false;
            canSpeakFacePassFailObserver = new DefaultDisposeObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    fpclAvatar.setFaceCameraTips("人脸信息识别中...");
                    canSpeakFacePassFail = true;
                    canSpeakFacePassFailObserver = null;
                }
            };
            //3秒之后重置识别失败语音提醒
            Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(canSpeakFacePassFailObserver);
        }
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            startFacePassVerify();
        }
    }

    private void startFacePassVerify() {
        fpclAvatar.setPreviewFace(true);
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

    public void setFacePassVerifyListener(FacePassVerifyListener facePassVerifyListener) {
        this.facePassVerifyListener = facePassVerifyListener;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    private void getUserCardInfoById(String customerId, int lossCardType) {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .getCustomerById(customerId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<UserCardInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<UserCardInfo> userCardInfoBaseResponse) {
                        hideLoadingDialog();
                        UserCardInfo data = userCardInfoBaseResponse.getData();
                        if (data != null && !TextUtils.isEmpty(data.getCustomerNo())) {
                            if (operateType == APPLY_LOSS_REPLACE_CARD) {
                                if (data.getType() == 1) {
                                    if (data.getCustomerNo().length() == 10) {
                                        LossReplaceCardFragment lossReplaceCardFragment = new LossReplaceCardFragment();
                                        lossReplaceCardFragment.setUserCardInfo(customerId, data);
                                        mActivity.addContentPlaceHolderFragment(lossReplaceCardFragment);
                                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassVerificationFragment.this);
                                    } else {
                                        showFacePassVerifyErrorDialog("暂无餐卡", "系统检测您暂无餐卡，不能进行挂失补卡操作。");
                                    }
                                } else {
                                    showFacePassVerifyErrorDialog("提示", "仅支持学生挂失补卡");
                                }
                            } else if (operateType == SEARCH_LOSS_REPLACE_CARD) {
                                if (data.getType() == 1) {
                                    CancelLossCardFragment cancelLossCardFragment = new CancelLossCardFragment();
                                    cancelLossCardFragment.setUserCardInfo(customerId, data, lossCardType);
                                    mActivity.addContentPlaceHolderFragment(cancelLossCardFragment);
                                    FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassVerificationFragment.this);
                                } else {
                                    showFacePassVerifyErrorDialog("提示", "仅支持学生取消挂失补卡申请");
                                }
                            }
                        } else {
                            String errorMsg = userCardInfoBaseResponse.getMsg();
                            if (TextUtils.isEmpty(errorMsg)) {
                                showFacePassVerifyErrorDialog("提示", "获取用户信息失败:信息为空");
                            } else {
                                if (operateType == APPLY_LOSS_REPLACE_CARD) {
                                    showFacePassVerifyErrorDialog("暂无餐卡", errorMsg);
                                } else if (operateType == SEARCH_LOSS_REPLACE_CARD) {
                                    showFacePassVerifyErrorDialog("暂无挂失补卡记录", errorMsg);
                                } else {
                                    showFacePassVerifyErrorDialog("提示", "获取用户信息失败:" + errorMsg);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        showFacePassVerifyErrorDialog("提示", "获取用户信息失败:" + e.getMessage());
                    }
                });

    }

    private void showFacePassVerifyErrorDialog(String title, String content) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt(title)
                .setAlertContentTxt(content)
                .setLeftNavTxt("回到首页")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassVerificationFragment.this);
                    }
                }).show(mActivity);
    }

    private void handleFacePassOperate(FacePassPeopleInfo passPeopleInfo) {
        if (operateType == APPLY_LOSS_REPLACE_CARD) {
            getUserCardInfoById(passPeopleInfo.getUnique_number(), 0);
        } else if (operateType == SEARCH_LOSS_REPLACE_CARD) {
            checkLossCardApplyHistory(passPeopleInfo.getUnique_number());
        }
    }

    /**
     * 检查挂失补卡记录
     */
    private void checkLossCardApplyHistory(String customerId) {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .checkCancelLossCardApply(customerId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> baseResponse) {
                        String responseData = baseResponse.getData();
                        if (TextUtils.equals(responseData, "2")) {
                            getUserCardInfoById(customerId, 2);
                        } else if (TextUtils.equals(responseData, "4")) {
                            getUserCardInfoById(customerId, 4);
                        } else {
                            hideLoadingDialog();
                            String errorMsg = baseResponse.getMsg();
                            if (TextUtils.isEmpty(errorMsg)) {
                                showFacePassVerifyErrorDialog("提示", "查询补卡记录失败:信息为空");
                            } else {
                                showFacePassVerifyErrorDialog("暂无挂失补卡记录", errorMsg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        showFacePassVerifyErrorDialog("提示", "查询补卡记录失败:" + e.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cbgCameraHelper.releaseCameraHelper();
    }
}
