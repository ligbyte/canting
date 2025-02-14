package com.stkj.infocollect.card.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.base.utils.PriceUtils;
import com.stkj.infocollect.card.model.CardSetInfo;
import com.stkj.infocollect.card.model.UserCardInfo;
import com.stkj.infocollect.card.service.CardService;
import com.stkj.infocollect.card.ui.widget.CardAccountInfoLayout;

/**
 * 挂失补卡
 */
public class LossReplaceCardFragment extends BaseRecyclerFragment {

    private CardAccountInfoLayout accountInfoLay;
    private ShapeTextView stvCancel;
    private ShapeSelectTextView stvLossCard;
    private ShapeSelectTextView stvLossReplaceCard;
    private UserCardInfo userCardInfo;
    private String customerId;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_loss_replace_card;
    }

    @Override
    protected void initViews(View rootView) {
        accountInfoLay = (CardAccountInfoLayout) findViewById(R.id.account_info_lay);
        stvCancel = (ShapeTextView) findViewById(R.id.stv_cancel);
        stvLossCard = (ShapeSelectTextView) findViewById(R.id.stv_loss_card);
        stvLossReplaceCard = (ShapeSelectTextView) findViewById(R.id.stv_loss_replace_card);
        stvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), LossReplaceCardFragment.this);
            }
        });
        stvLossCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stvLossCard.isShapeSelect()) {
                    if (lossCardType == 2) {
                        CommonDialogUtils.showTipsDialog(mActivity, "餐卡已提交挂失申请");
                    } else if (lossCardType == 4) {
                        CommonDialogUtils.showTipsDialog(mActivity, "餐卡已提交挂失补卡申请");
                    }
                    return;
                }
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("餐卡挂失")
                        .setAlertContentTxt("提交后，已办理的餐卡即时解绑，且不可用， 确定进行挂失操作？")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                applyLossCard();
                            }
                        })
                        .setRightNavTxt("取消")
                        .show(mActivity);
            }
        });
        stvLossReplaceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stvLossCard.isShapeSelect()) {
                    if (lossCardType == 2) {
                        CommonDialogUtils.showTipsDialog(mActivity, "餐卡已提交挂失申请");
                    } else if (lossCardType == 4) {
                        CommonDialogUtils.showTipsDialog(mActivity, "餐卡已提交挂失补卡申请");
                    }
                    return;
                }
                getCardSetInfo();
            }
        });
    }

    public void setUserCardInfo(String customerId, UserCardInfo userCardInfo) {
        this.customerId = customerId;
        this.userCardInfo = userCardInfo;
    }

    /**
     * 申请挂失
     */
    private void applyLossCard() {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .lossCardApply(customerId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<Object>>() {
                    @Override
                    protected void onSuccess(BaseResponse<Object> baseResponse) {
                        hideLoadingDialog();
                        if (baseResponse.isSuccess()) {
                            SuccessStatusFragment successStatusFragment = new SuccessStatusFragment();
                            successStatusFragment.setTitleTxt("挂失成功");
                            successStatusFragment.setContentTxt("你的餐卡挂失完成，餐卡已不可用。");
                            successStatusFragment.setConfirmTxt("确定");
                            mActivity.addContentPlaceHolderFragment(successStatusFragment);
                            FragmentUtils.safeRemoveFragment(getParentFragmentManager(), LossReplaceCardFragment.this);
                        } else {
                            String errorMsg = baseResponse.getMsg();
                            if (TextUtils.isEmpty(errorMsg)) {
                                CommonDialogUtils.showTipsDialog(mActivity, "挂失申请失败");
                            } else {
                                CommonDialogUtils.showTipsDialog(mActivity, "挂失申请失败:" + errorMsg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "挂失申请失败:" + e.getMessage());
                    }
                });
    }

    /**
     * 获取补卡费
     */
    private void getCardSetInfo() {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .getCardSetInfo()
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<CardSetInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<CardSetInfo> cardSetInfoBaseNetResponse) {
                        hideLoadingDialog();
                        CardSetInfo data = cardSetInfoBaseNetResponse.getData();
                        if (data != null) {
                            showApplyLossReplaceCardDialog(data.getReplaceCardFee());
                        } else {
                            showApplyLossReplaceCardDialog("");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        showApplyLossReplaceCardDialog("");
                    }
                });
    }

    private void showApplyLossReplaceCardDialog(String replaceCardFee) {
        String contentTips;
        if (!TextUtils.isEmpty(replaceCardFee)) {
            contentTips = "提交后，已办理的餐卡即时解绑，且不可用，补卡将产生" + PriceUtils.formatPrice2(replaceCardFee) + "元补卡费。确定进行挂失补卡操作？";
        } else {
            contentTips = "提交后，已办理的餐卡即时解绑，且不可用，确定进行挂失补卡操作？";
        }
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("餐卡挂失补卡")
                .setAlertContentTxt(contentTips)
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        applyLossReplaceCard();
                    }
                })
                .setRightNavTxt("取消")
                .show(mActivity);
    }

    /**
     * 申请挂失并补卡
     */
    private void applyLossReplaceCard() {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .lossReplaceCardApply(customerId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<Object>>() {
                    @Override
                    protected void onSuccess(BaseResponse<Object> baseResponse) {
                        hideLoadingDialog();
                        if (baseResponse.isSuccess()) {
                            SuccessStatusFragment successStatusFragment = new SuccessStatusFragment();
                            successStatusFragment.setTitleTxt("补卡申请已提交");
                            successStatusFragment.setContentTxt("挂失成功， 请到办卡处领取餐卡。");
                            successStatusFragment.setConfirmTxt("确定");
                            mActivity.addContentPlaceHolderFragment(successStatusFragment);
                            FragmentUtils.safeRemoveFragment(getParentFragmentManager(), LossReplaceCardFragment.this);
                        } else {
                            String errorMsg = baseResponse.getMsg();
                            if (TextUtils.isEmpty(errorMsg)) {
                                CommonDialogUtils.showTipsDialog(mActivity, "补卡申请失败");
                            } else {
                                CommonDialogUtils.showTipsDialog(mActivity, "补卡申请失败:" + errorMsg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "补卡申请失败:" + e.getMessage());
                    }
                });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            checkLossCardApplyHistory(customerId);
        }
    }

    private int lossCardType = 0;

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
                        hideLoadingDialog();
                        if (userCardInfo != null) {
                            accountInfoLay.setCardUserInfo(userCardInfo);
                        }
                        String responseData = baseResponse.getData();
                        if (TextUtils.equals(responseData, "2")) {
                            lossCardType = 2;
                            stvLossCard.setShapeSelect(true);
                            stvLossReplaceCard.setShapeSelect(true);
                        } else if (TextUtils.equals(responseData, "4")) {
                            lossCardType = 4;
                            stvLossCard.setShapeSelect(true);
                            stvLossReplaceCard.setShapeSelect(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        if (userCardInfo != null) {
                            accountInfoLay.setCardUserInfo(userCardInfo);
                        }
                    }
                });
    }

}
