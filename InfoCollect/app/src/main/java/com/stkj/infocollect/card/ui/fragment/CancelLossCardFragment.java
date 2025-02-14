package com.stkj.infocollect.card.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.card.model.UserCardInfo;
import com.stkj.infocollect.card.service.CardService;
import com.stkj.infocollect.card.ui.widget.CardAccountInfoLayout;

/**
 * 取消挂失补卡申请
 */
public class CancelLossCardFragment extends BaseRecyclerFragment {

    private CardAccountInfoLayout accountInfoLay;
    private ShapeTextView stvCancel;
    private ShapeTextView stvCancelLossCard;
    private UserCardInfo userCardInfo;
    private String customerId;
    private int lossCardType;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cancel_loss_card;
    }

    @Override
    protected void initViews(View rootView) {
        accountInfoLay = (CardAccountInfoLayout) findViewById(R.id.account_info_lay);
        stvCancel = (ShapeTextView) findViewById(R.id.stv_cancel);
        stvCancelLossCard = (ShapeTextView) findViewById(R.id.stv_cancel_loss_card);
        stvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), CancelLossCardFragment.this);
            }
        });
        stvCancelLossCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "";
                String content = "";
                if (lossCardType == 2) {
                    title = "取消挂失";
                    content = "确定取消挂失操作？";
                } else if (lossCardType == 4) {
                    title = "取消挂失及补卡";
                    content = "确定取消挂失及补卡操作？";
                }
                if (!TextUtils.isEmpty(title)) {
                    CommonAlertDialogFragment.build()
                            .setAlertTitleTxt(title)
                            .setAlertContentTxt(content)
                            .setLeftNavTxt("确定")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    cancelLossCardApply();
                                }
                            })
                            .setRightNavTxt("取消")
                            .show(mActivity);
                }
            }
        });
    }

    public void setUserCardInfo(String customerId, UserCardInfo userCardInfo, int lossCardType) {
        this.customerId = customerId;
        this.userCardInfo = userCardInfo;
        this.lossCardType = lossCardType;
    }

    /**
     * 取消申请挂失
     */
    private void cancelLossCardApply() {
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(CardService.class)
                .cancelLossCardApply(customerId)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<Object>>() {
                    @Override
                    protected void onSuccess(BaseResponse<Object> baseResponse) {
                        hideLoadingDialog();
                        if (baseResponse.isSuccess()) {
                            SuccessStatusFragment successStatusFragment = new SuccessStatusFragment();
                            if (lossCardType == 2) {
                                successStatusFragment.setTitleTxt("挂失已取消");
                                successStatusFragment.setContentTxt("挂失取消，餐卡已可以正常使用。");
                            } else if (lossCardType == 4) {
                                successStatusFragment.setTitleTxt("挂失并补卡已取消");
                                successStatusFragment.setContentTxt("挂失并补卡取消，餐卡已可以正常使用。");
                            }
                            successStatusFragment.setConfirmTxt("返回首页");
                            mActivity.addContentPlaceHolderFragment(successStatusFragment);
                            FragmentUtils.safeRemoveFragment(getParentFragmentManager(), CancelLossCardFragment.this);
                        } else {
                            String errorMsg = baseResponse.getMsg();
                            if (TextUtils.isEmpty(errorMsg)) {
                                CommonDialogUtils.showTipsDialog(mActivity, "取消挂失补卡申请失败");
                            } else {
                                CommonDialogUtils.showTipsDialog(mActivity, "取消挂失补卡申请失败:" + errorMsg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "取消挂失补卡申请失败:" + e.getMessage());
                    }
                });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            if (userCardInfo != null) {
                accountInfoLay.setCardUserInfo(userCardInfo);
            }
            if (lossCardType == 2) {
                accountInfoLay.setCardState("已挂失");
                stvCancelLossCard.setText("取消挂失");
            } else if (lossCardType == 4) {
                accountInfoLay.setCardState("已挂失补卡");
                stvCancelLossCard.setText("取消挂失补卡");
            }
        }
    }
}
