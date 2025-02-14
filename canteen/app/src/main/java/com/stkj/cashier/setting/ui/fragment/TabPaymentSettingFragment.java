package com.stkj.cashier.setting.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.stkj.cashier.R;
import com.stkj.cashier.base.model.CommonExpandItem;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银设置
 */
public class TabPaymentSettingFragment extends BaseRecyclerFragment {

    private FrameLayout flConsumerMode;
    private ShapeTextView stvConsumerMode;
    private ImageView ivConsumerMode;
    private FrameLayout flPaySuccessDelay;
    private ShapeTextView stvPaySuccessDelay;
    private ImageView ivPaySuccessDelay;
    private ShapeTextView stvAutoWeightCount;
    private ShapeTextView stvChangeAutoWeightCount;
    private ShapeTextView stvTakeMealQueueLimit;
    private ShapeTextView stvChangeTakeMealQueueLimit;
    private ShapeTextView stvWeightAttachAmount;
    private ShapeTextView stvChangeWeightAttachAmount;
    private ImageView ivSwitchConsumerConfirm;
    private ImageView ivSwitchPayTypeCard;
    private ImageView ivSwitchPayTypeFace;
    private ImageView ivSwitchPayTypeScan;
    private ImageView ivSwitchWeightAutoCancelPay;
    private ImageView ivSwitchTongLianPay;

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_tab_payment_setting;
    }

    @Override
    protected void initViews(View rootView) {
        stvTakeMealQueueLimit = (ShapeTextView) findViewById(R.id.stv_take_meal_queue_limit);
        stvChangeTakeMealQueueLimit = (ShapeTextView) findViewById(R.id.stv_change_take_meal_queue_limit);
        int takeMealQueueLimit = PaymentSettingMMKV.getTakeMealQueueLimit();
        stvTakeMealQueueLimit.setText(String.valueOf(takeMealQueueLimit));
        View.OnClickListener takeMealQueueLimitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改取餐排队人数")
                        .setInputContent(stvTakeMealQueueLimit.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                try {
                                    if (TextUtils.isEmpty(input)) {
                                        input = "0";
                                    }
                                    PaymentSettingMMKV.putTakeMealQueueLimit(Integer.parseInt(input));
                                    stvTakeMealQueueLimit.setText(input);
                                    AppToast.toastMsg("修改成功");
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show(mActivity);
            }
        };
        stvTakeMealQueueLimit.setOnClickListener(takeMealQueueLimitClickListener);
        stvChangeTakeMealQueueLimit.setOnClickListener(takeMealQueueLimitClickListener);
        stvWeightAttachAmount = (ShapeTextView) findViewById(R.id.stv_weight_attach_amount);
        stvChangeWeightAttachAmount = (ShapeTextView) findViewById(R.id.stv_change_weight_attach_amount);
        String weightAttachAmount = PaymentSettingMMKV.getWeightAttachAmount();
        stvWeightAttachAmount.setText(weightAttachAmount);
        View.OnClickListener weightAttachAmountClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改称重附加金额")
                        .setInputContent(stvWeightAttachAmount.getText().toString())
                        .setNeedLimitNumber(true)
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                try {
                                    if (TextUtils.isEmpty(input)) {
                                        input = "0";
                                    }
                                    PaymentSettingMMKV.putWeightAttachAmount(input);
                                    stvWeightAttachAmount.setText(input);
                                    AppToast.toastMsg("修改成功");
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show(mActivity);
            }
        };
        stvWeightAttachAmount.setOnClickListener(weightAttachAmountClickListener);
        stvChangeWeightAttachAmount.setOnClickListener(weightAttachAmountClickListener);
        flConsumerMode = (FrameLayout) findViewById(R.id.fl_consumer_mode);
        stvConsumerMode = (ShapeTextView) findViewById(R.id.stv_consumer_mode);
        ivConsumerMode = (ImageView) findViewById(R.id.iv_consumer_mode);
        flPaySuccessDelay = (FrameLayout) findViewById(R.id.fl_pay_success_delay);
        stvPaySuccessDelay = (ShapeTextView) findViewById(R.id.stv_pay_success_delay);
        ivPaySuccessDelay = (ImageView) findViewById(R.id.iv_pay_success_delay);
        stvAutoWeightCount = (ShapeTextView) findViewById(R.id.stv_auto_weight_count);
        stvChangeAutoWeightCount = (ShapeTextView) findViewById(R.id.stv_change_auto_weight_count);
        int autoWeightCount = PaymentSettingMMKV.getAutoWeightCount();
        stvAutoWeightCount.setText(String.valueOf(autoWeightCount));
        View.OnClickListener changeAutoWeightCountClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改自动称重稳定次数")
                        .setInputContent(stvAutoWeightCount.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                try {
                                    if (TextUtils.isEmpty(input)) {
                                        input = "10";
                                    }
                                    PaymentSettingMMKV.putAutoWeightCount(Integer.parseInt(input));
                                    stvAutoWeightCount.setText(input);
                                    AppToast.toastMsg("修改成功");
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show(mActivity);
            }
        };
        stvAutoWeightCount.setOnClickListener(changeAutoWeightCountClickListener);
        stvChangeAutoWeightCount.setOnClickListener(changeAutoWeightCountClickListener);
        List<CommonExpandItem> consumerModeExpandList = new ArrayList<>();
        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_AMOUNT_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_AMOUNT_MODE)));
        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_NUMBER_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_NUMBER_MODE)));
        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_TAKE_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_TAKE_MODE)));
//        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_SEND_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_SEND_MODE)));
        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_WEIGHT_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_WEIGHT_MODE)));
        consumerModeExpandList.add(new CommonExpandItem(PayConstants.CONSUMER_QUOTA_MODE, PayConstants.getConsumerModeStr(PayConstants.CONSUMER_QUOTA_MODE)));

        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
        stvConsumerMode.setText(PayConstants.getConsumerModeStr(currentConsumerMode));
        flConsumerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConsumerMode.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flConsumerMode.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivConsumerMode.setSelected(false);
                        stvConsumerMode.setText(commonExpandItem.getName());
                        int typeInt = commonExpandItem.getTypeInt();
                        mActivity.getWeakRefHolder(ConsumerModeHelper.class).changeConsumerMode(typeInt);
                    }
                });
                commonExpandListPopWindow.setExpandItemList(consumerModeExpandList);
                commonExpandListPopWindow.showAsDropDown(flConsumerMode);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivConsumerMode.setSelected(false);
                    }
                });
            }
        });
        List<CommonExpandItem> commonDelayTimeExpandList = new ArrayList<>();
        commonDelayTimeExpandList.add(new CommonExpandItem(1000, "1秒"));
        commonDelayTimeExpandList.add(new CommonExpandItem(1500, "1.5秒"));
        commonDelayTimeExpandList.add(new CommonExpandItem(2000, "2秒"));
        commonDelayTimeExpandList.add(new CommonExpandItem(2500, "2.5秒"));
        commonDelayTimeExpandList.add(new CommonExpandItem(3000, "3秒"));
        stvPaySuccessDelay.setText(PaymentSettingMMKV.getPaySuccessDelayString());
        flPaySuccessDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPaySuccessDelay.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flPaySuccessDelay.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivPaySuccessDelay.setSelected(false);
                        stvPaySuccessDelay.setText(commonExpandItem.getName());
                        PaymentSettingMMKV.putPaySuccessDelay(commonExpandItem.getTypeInt());
                        AppToast.toastMsg("修改成功");
                    }
                });
                commonExpandListPopWindow.setExpandItemList(commonDelayTimeExpandList);
                commonExpandListPopWindow.showAsDropDown(flPaySuccessDelay);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivPaySuccessDelay.setSelected(false);
                    }
                });
            }
        });
        ivSwitchWeightAutoCancelPay = (ImageView) findViewById(R.id.iv_switch_weight_auto_cancel_pay);
        boolean switchWeightAutoCancelPay = PaymentSettingMMKV.getSwitchWeightAutoCancelPay();
        ivSwitchWeightAutoCancelPay.setSelected(switchWeightAutoCancelPay);
        ivSwitchWeightAutoCancelPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchAutoCancelPay = !ivSwitchWeightAutoCancelPay.isSelected();
                ivSwitchWeightAutoCancelPay.setSelected(switchAutoCancelPay);
                PaymentSettingMMKV.putSwitchWeightAutoCancelPay(switchAutoCancelPay);
            }
        });
        ivSwitchConsumerConfirm = (ImageView) findViewById(R.id.iv_switch_consumer_confirm);
        boolean switchConsumerConfirm = PaymentSettingMMKV.getSwitchConsumerConfirm();
        ivSwitchConsumerConfirm.setSelected(switchConsumerConfirm);
        ivSwitchConsumerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchConsumerConfirm = !ivSwitchConsumerConfirm.isSelected();
                ivSwitchConsumerConfirm.setSelected(switchConsumerConfirm);
                PaymentSettingMMKV.putSwitchConsumerConfirm(switchConsumerConfirm);
            }
        });
        ivSwitchPayTypeCard = (ImageView) findViewById(R.id.iv_switch_payType_card);
        boolean switchPayTypeCard = PaymentSettingMMKV.getSwitchPayTypeCard();
        ivSwitchPayTypeCard.setSelected(switchPayTypeCard);
        ivSwitchPayTypeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断三种支付方式是否都为选中 至少选中一种
                boolean switchPayTypeCard = !ivSwitchPayTypeCard.isSelected();
                boolean switchPayTypeFace = PaymentSettingMMKV.getSwitchPayTypeFace();
                boolean switchPayTypeScan = PaymentSettingMMKV.getSwitchPayTypeScan();
                if (switchPayTypeCard) {
                    //更改支付方式文案
                    refreshPayTypeVoice(switchPayTypeFace, true, switchPayTypeScan);
                } else {
                    if (!switchPayTypeFace && !switchPayTypeScan) {
                        CommonDialogUtils.showTipsDialog(mActivity, "至少选择一种支付方式");
                        return;
                    }
                    //更改支付方式文案
                    refreshPayTypeVoice(switchPayTypeFace, false, switchPayTypeScan);
                }
                ivSwitchPayTypeCard.setSelected(switchPayTypeCard);
                PaymentSettingMMKV.putSwitchPayTypeCard(switchPayTypeCard);
            }
        });
        ivSwitchPayTypeFace = (ImageView) findViewById(R.id.iv_switch_payType_face);
        boolean switchPayTypeFace = PaymentSettingMMKV.getSwitchPayTypeFace();
        ivSwitchPayTypeFace.setSelected(switchPayTypeFace);
        ivSwitchPayTypeFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断三种支付方式是否都为选中 至少选中一种
                boolean switchPayTypeFace = !ivSwitchPayTypeFace.isSelected();
                boolean switchPayTypeCard = PaymentSettingMMKV.getSwitchPayTypeCard();
                boolean switchPayTypeScan = PaymentSettingMMKV.getSwitchPayTypeScan();
                if (switchPayTypeFace) {
                    //更改支付方式文案
                    refreshPayTypeVoice(true, switchPayTypeCard, switchPayTypeScan);
                } else {
                    if (!switchPayTypeCard && !switchPayTypeScan) {
                        CommonDialogUtils.showTipsDialog(mActivity, "至少选择一种支付方式");
                        return;
                    }
                    //更改支付方式文案
                    refreshPayTypeVoice(false, switchPayTypeCard, switchPayTypeScan);
                }
                ivSwitchPayTypeFace.setSelected(switchPayTypeFace);
                PaymentSettingMMKV.putSwitchPayTypeFace(switchPayTypeFace);
            }
        });
        ivSwitchPayTypeScan = (ImageView) findViewById(R.id.iv_switch_payType_scan);
        boolean switchPayTypeScan = PaymentSettingMMKV.getSwitchPayTypeScan();
        ivSwitchPayTypeScan.setSelected(switchPayTypeScan);
        ivSwitchPayTypeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断三种支付方式是否都为选中 至少选中一种
                boolean switchPayTypeScan = !ivSwitchPayTypeScan.isSelected();
                boolean switchPayTypeFace = PaymentSettingMMKV.getSwitchPayTypeFace();
                boolean switchPayTypeCard = PaymentSettingMMKV.getSwitchPayTypeCard();
                if (switchPayTypeScan) {
                    //更改支付方式文案
                    refreshPayTypeVoice(switchPayTypeFace, switchPayTypeCard, true);
                } else {
                    if (!switchPayTypeFace && !switchPayTypeCard) {
                        CommonDialogUtils.showTipsDialog(mActivity, "至少选择一种支付方式");
                        return;
                    }
                    //更改支付方式文案
                    refreshPayTypeVoice(switchPayTypeFace, switchPayTypeCard, false);
                }
                ivSwitchPayTypeScan.setSelected(switchPayTypeScan);
                PaymentSettingMMKV.putSwitchPayTypeScan(switchPayTypeScan);
            }
        });
        //通联支付开关
        ivSwitchTongLianPay = (ImageView) findViewById(R.id.iv_switch_tonglian_pay);
        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
        ivSwitchTongLianPay.setSelected(switchTongLianPay);
        ivSwitchTongLianPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通联支付
                boolean switchTongLianPay = !ivSwitchTongLianPay.isSelected();
                ivSwitchTongLianPay.setSelected(switchTongLianPay);
                PaymentSettingMMKV.putSwitchTongLianPay(switchTongLianPay);
            }
        });
    }

    private void refreshPayTypeVoice(boolean typeFace, boolean typeCard, boolean typeScan) {
        String payTypeVoice = "请";
        if (typeFace) {
            payTypeVoice += "刷脸";
            if (typeCard) {
                if (typeScan) {
                    payTypeVoice += "、刷卡或扫码";
                } else {
                    payTypeVoice += "或刷卡";
                }
            } else {
                if (typeScan) {
                    payTypeVoice += "或扫码";
                }
            }
        } else {
            if (typeCard) {
                if (typeScan) {
                    payTypeVoice += "刷卡或扫码";
                } else {
                    payTypeVoice += "刷卡";
                }
            } else {
                if (typeScan) {
                    payTypeVoice += "扫码";
                }
            }
        }
        LogHelper.print("--refreshPayTypeVoice--payTypeVoice: " + payTypeVoice);
        TTSSettingMMKV.putPayTypeVoice(payTypeVoice);
        EventBus.getDefault().post(new RefreshPayType(typeFace, typeCard, typeScan));
    }

    private OnConsumerModeListener consumerModeListener = new OnConsumerModeListener() {
        @Override
        public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
            if (stvConsumerMode != null) {
                stvConsumerMode.setText(PayConstants.getConsumerModeStr(consumerMode));
            }
        }
    };

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        mActivity.getWeakRefHolder(ConsumerModeHelper.class).addConsumerModeListener(consumerModeListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.getWeakRefHolder(ConsumerModeHelper.class).removeConsumerModeListener(consumerModeListener);
    }

}
