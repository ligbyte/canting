package com.stkj.cashier.consumer.ui.presentation;

import android.app.Activity;
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

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.widget.FacePassCameraLayout;
import com.stkj.cashier.consumer.callback.ConsumerController;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.consumer.callback.OnInputNumberListener;
import com.stkj.cashier.consumer.ui.weight.SimpleInputNumber;
import com.stkj.cashier.home.ui.widget.HomeTitleLayout;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.CircleProgressBar;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

/**
 * 食堂收银客户展示页
 */
public class ConsumerPresentation extends Presentation implements ConsumerController {

    //    private LinearLayout llOrderList;
//    private RecyclerView rvGoodsList;
//    private LinearLayout llFastPayPresentation;
//    private TextView tvGoodsCount;
//    private TextView tvGoodsPrice;
//    private ShapeFrameLayout sflOrderList;
//    private CommonRecyclerAdapter mOrderAdapter;
    private ShapeFrameLayout sflConsumerContent;
    private FacePassCameraLayout fpcFace;
    private HomeTitleLayout htlConsumer;

    private ConsumerListener consumerListener;
    private OnConsumerConfirmListener facePassConfirmListener;
    private boolean isSetPayOrderInfo;
    private LinearLayout llFaceConfirm;
    private ShapeTextView stvFaceLeftBt;
    private ShapeTextView stvFaceRightBt;
    private ShapeTextView stvPayPrice;
    private LinearLayout llTakeMealWay;
    private ShapeTextView stvTakeMealByCode;
    private ShapeTextView stvTakeMealByPhone;
    private SimpleInputNumber sinNumber;
    private ShapeFrameLayout sflInputNumber;
    private CircleProgressBar pbConsumer;
    private ShapeTextView stvCancelPay;

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
            setContentView(com.stkj.cashier.R.layout.presentation_consumer_s1);
        } else if (consumeLayRes == 2) {
            setContentView(R.layout.presentation_consumer_s2);
        } else {
            setContentView(R.layout.presentation_consumer);
        }
        findViews();
        LogHelper.print("-Consumer--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }

    private void findViews() {
        stvCancelPay = (ShapeTextView) findViewById(R.id.stv_cancel_pay);
        pbConsumer = (CircleProgressBar) findViewById(R.id.pb_consumer);
        sflInputNumber = (ShapeFrameLayout) findViewById(R.id.sfl_input_number);
        sinNumber = (SimpleInputNumber) findViewById(R.id.sin_number);
        stvPayPrice = (ShapeTextView) findViewById(R.id.stv_pay_price);
        htlConsumer = (HomeTitleLayout) findViewById(R.id.htl_consumer);
        sflConsumerContent = (ShapeFrameLayout) findViewById(R.id.sfl_consumer_content);
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
//        sflOrderList = (ShapeFrameLayout) findViewById(R.id.sfl_order_list);
//        rvGoodsList = (RecyclerView) findViewById(R.id.rv_goods_list);
//        llFastPayPresentation = (LinearLayout) findViewById(R.id.ll_fast_pay_presentation);
//        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
//        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
//        mOrderAdapter = new CommonRecyclerAdapter(false);
//        rvGoodsList.setAdapter(mOrderAdapter);
        llTakeMealWay = (LinearLayout) findViewById(R.id.ll_take_meal_way);
        stvTakeMealByCode = (ShapeTextView) findViewById(R.id.stv_take_meal_by_code);
        stvTakeMealByCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(true);
                }
                sflInputNumber.setVisibility(View.VISIBLE);
                sinNumber.setInputNumberCount(4);
            }
        });
        stvTakeMealByPhone = (ShapeTextView) findViewById(R.id.stv_take_meal_by_phone);
        stvTakeMealByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(true);
                }
                sflInputNumber.setVisibility(View.VISIBLE);
                sinNumber.setInputNumberCount(11);
            }
        });
        sinNumber.setInputNumberListener(new OnInputNumberListener() {
            @Override
            public void onConfirmNumber(String number) {
                sflInputNumber.setVisibility(View.GONE);
                if (facePassConfirmListener != null) {
                    int inputNumberCount = sinNumber.getInputNumberCount();
                    if (inputNumberCount == 4) {
                        facePassConfirmListener.onConfirmTakeMealCode(number);
                    } else if (inputNumberCount == 11) {
                        facePassConfirmListener.onConfirmPhone(number);
                    }
                }
            }

            @Override
            public void onClickBack() {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(false);
                }
                sflInputNumber.setVisibility(View.GONE);
            }

            @Override
            public void onConfirmError(boolean hasInputNumber) {
                int inputNumberCount = sinNumber.getInputNumberCount();
                if (inputNumberCount == 4) {
                    speakTTSVoice(hasInputNumber ? "请输入完整的取餐码" : "请输入取餐码");
                } else if (inputNumberCount == 11) {
                    speakTTSVoice(hasInputNumber ? "请输入完整的手机号" : "请输入手机号");
                }
            }
        });
    }

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
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

    private boolean isConsumerAuthTips;

    @Override
    public void setConsumerAuthTips(String tips) {
        if (fpcFace != null) {
            isConsumerAuthTips = true;
            fpcFace.setFaceCameraTips(tips);
        }
    }

    @Override
    public boolean isConsumerAuthTips() {
        return isConsumerAuthTips;
    }

    @Override
    public void setConsumerTips(String tips) {
        setConsumerTips(tips, 0);
    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {
        if (fpcFace != null) {
            isConsumerAuthTips = false;
            fpcFace.setFaceCameraTips(tips);
            if (consumerPro > 0) {
                pbConsumer.setVisibility(View.VISIBLE);
                pbConsumer.setProgress(consumerPro);
            } else {
                pbConsumer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setConsumerConfirmFaceInfo(FacePassPeopleInfo facePassPeopleInfo, boolean needConfirm, int consumerType) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
        fpcFace.setFaceImage(facePassPeopleInfo.getImgData());
        if (needConfirm) {
            fpcFace.setFaceCameraTips("识别成功,请确认?");
            llFaceConfirm.setVisibility(View.VISIBLE);
            stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        if (consumerType == PayConstants.PAY_TYPE_IC_CARD) {
                            facePassConfirmListener.onConfirmCardNumber(facePassPeopleInfo.getCard_Number());
                        } else {
                            facePassConfirmListener.onConfirmFacePass(facePassPeopleInfo);
                        }
                    }
                }
            });
            stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        if (consumerType == PayConstants.PAY_TYPE_IC_CARD) {
                            facePassConfirmListener.onCancelCardNumber(facePassPeopleInfo.getCard_Number());
                        } else {
                            facePassConfirmListener.onCancelFacePass(facePassPeopleInfo);
                        }
                    }
                }
            });
        } else {
            if (facePassConfirmListener != null) {
                if (consumerType == PayConstants.PAY_TYPE_IC_CARD) {
                    facePassConfirmListener.onConfirmCardNumber(facePassPeopleInfo.getCard_Number());
                } else {
                    facePassConfirmListener.onConfirmFacePass(facePassPeopleInfo);
                }
            }
            llFaceConfirm.setVisibility(View.GONE);
        }
    }

    @Override
    public void setConsumerConfirmCardInfo(String cardNumber, boolean needConfirm) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
        fpcFace.setFaceImage("");
        if (needConfirm) {
            fpcFace.setFaceCameraTips("读卡成功,请确认?");
            llFaceConfirm.setVisibility(View.VISIBLE);
            stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onConfirmCardNumber(cardNumber);
                    }
                }
            });
            stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onCancelCardNumber(cardNumber);
                    }
                }
            });
        } else {
            if (facePassConfirmListener != null) {
                facePassConfirmListener.onConfirmCardNumber(cardNumber);
            }
        }
    }

    @Override
    public void setConsumerConfirmScanInfo(String scanData, boolean needConfirm) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
        fpcFace.setFaceImage("");
        if (needConfirm) {
            fpcFace.setFaceCameraTips("扫码成功,请确认?");
            llFaceConfirm.setVisibility(View.VISIBLE);
            stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onConfirmScanData(scanData);
                    }

                }
            });
            stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llFaceConfirm.setVisibility(View.GONE);
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onCancelScanData(scanData);
                    }
                }
            });
        } else {
            if (facePassConfirmListener != null) {
                facePassConfirmListener.onConfirmScanData(scanData);
            }
        }
    }

    @Override
    public void setConsumerTakeMealWay() {
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNormalConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        sflInputNumber.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(View.GONE);
    }

    @Override
    public void setPayConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        sflInputNumber.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(isSetPayOrderInfo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPayPrice(String payPrice, boolean showCancelPay) {
        setCanCancelPay(showCancelPay);
        pbConsumer.setVisibility(View.GONE);
        stvPayPrice.setVisibility(View.VISIBLE);
        stvPayPrice.setText("¥ " + payPrice);
    }

    @Override
    public void setCanCancelPay(boolean showCancelPay) {
        if (showCancelPay) {
            stvCancelPay.setVisibility(View.VISIBLE);
            stvCancelPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onConsumerCancelPay();
                    }
                }
            });
        } else {
            stvCancelPay.setVisibility(View.GONE);
        }
    }

    @Override
    public void resetFaceConsumerLayout() {
        if (fpcFace != null) {
            stvCancelPay.setVisibility(View.GONE);
            pbConsumer.setVisibility(View.GONE);
            sflInputNumber.setVisibility(View.GONE);
            llTakeMealWay.setVisibility(View.GONE);
            llFaceConfirm.setVisibility(View.GONE);
            stvPayPrice.setText("¥ 0.00");
            stvPayPrice.setVisibility(View.GONE);
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

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words) {
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mainActivity;
            baseActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words);
        }
    }
}
