package com.stkj.supermarket.setting.ui.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.deviceinterface.callback.DeviceStatusListener;
import com.stkj.deviceinterface.model.DeviceHardwareInfo;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.setting.data.PaymentSettingMMKV;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银设置
 */
public class TabPaymentSettingFragment extends BaseRecyclerFragment {

    private FrameLayout flDevice1;
    private ShapeTextView stvDevice1;
    private ImageView ivDevice1;
    private FrameLayout flDevice2;
    private ShapeTextView stvDevice2;
    private ImageView ivDevice2;
    private FrameLayout flDevice3;
    private ShapeTextView stvDevice3;
    private ImageView ivDevice3;
    private ImageView ivSwitchCashierPay;
//    private LinearLayout llSwitchMoJiao;
//    private ImageView ivSwitchMoJiao;
//    private LinearLayout llSwitchMoFen;
//    private ImageView ivSwitchMoFen;
//    private LinearLayout llSwitchChangeOrderPrice;
//    private ImageView ivSwitchChangeOrderPrice;
    private ImageView ivSwitchTongLianPay;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_payment_setting;
    }

    @Override
    protected void initViews(View rootView) {
        flDevice1 = (FrameLayout) findViewById(R.id.fl_device1);
        stvDevice1 = (ShapeTextView) findViewById(R.id.stv_device1);
        ivDevice1 = (ImageView) findViewById(R.id.iv_device1);
        flDevice2 = (FrameLayout) findViewById(R.id.fl_device2);
        stvDevice2 = (ShapeTextView) findViewById(R.id.stv_device2);
        ivDevice2 = (ImageView) findViewById(R.id.iv_device2);
        flDevice3 = (FrameLayout) findViewById(R.id.fl_device3);
        stvDevice3 = (ShapeTextView) findViewById(R.id.stv_device3);
        ivDevice3 = (ImageView) findViewById(R.id.iv_device3);
        //是否允许现金支付
        ivSwitchCashierPay = (ImageView) findViewById(R.id.iv_switch_cashier_pay);
        ivSwitchCashierPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean openCashier = !ivSwitchCashierPay.isSelected();
                ivSwitchCashierPay.setSelected(openCashier);
                PaymentSettingMMKV.putCashierPay(openCashier);
            }
        });
        boolean openCashierPay = PaymentSettingMMKV.isOpenCashierPay();
        ivSwitchCashierPay.setSelected(openCashierPay);
//        llSwitchMoJiao = (LinearLayout) findViewById(R.id.ll_switch_mo_jiao);
//        ivSwitchMoJiao = (ImageView) findViewById(R.id.iv_switch_mo_jiao);
//        //是否打开了抹角
//        boolean moneyMoJiao = PaymentSettingMMKV.isMoneyMoJiao();
//        ivSwitchMoJiao.setSelected(moneyMoJiao);
//        ivSwitchMoJiao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean moJiao = !ivSwitchMoJiao.isSelected();
//                ivSwitchMoJiao.setSelected(moJiao);
//                PaymentSettingMMKV.putMoneyMoJiao(moJiao);
//            }
//        });
//        llSwitchMoFen = (LinearLayout) findViewById(R.id.ll_switch_mo_fen);
//        ivSwitchMoFen = (ImageView) findViewById(R.id.iv_switch_mo_fen);
//        //是否打开了抹分
//        boolean moneyMoFen = PaymentSettingMMKV.isMoneyMoFen();
//        ivSwitchMoFen.setSelected(moneyMoFen);
//        ivSwitchMoFen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean moFen = !ivSwitchMoFen.isSelected();
//                ivSwitchMoFen.setSelected(moFen);
//                PaymentSettingMMKV.putMoneyMoFen(moFen);
//            }
//        });
//        //是否允许整单改价
//        llSwitchChangeOrderPrice = (LinearLayout) findViewById(R.id.ll_switch_change_order_price);
//        ivSwitchChangeOrderPrice = (ImageView) findViewById(R.id.iv_switch_change_order_price);
//        boolean changeOrderPrice = PaymentSettingMMKV.isChangeOrderPrice();
//        ivSwitchChangeOrderPrice.setSelected(changeOrderPrice);
//        ivSwitchChangeOrderPrice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean changeOrderPrice = !ivSwitchChangeOrderPrice.isSelected();
//                ivSwitchChangeOrderPrice.setSelected(changeOrderPrice);
//                PaymentSettingMMKV.putChangeOrderPrice(changeOrderPrice);
//            }
//        });
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

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        //获取设备状态
        boolean supportUSBDevice = DeviceManager.INSTANCE.getDeviceInterface().isSupportUSBDevice();
        if (supportUSBDevice) {
            DeviceManager.INSTANCE.getDeviceInterface().registerDeviceStatusListener(mDeviceStatusListener);
        }
        refreshDeviceStatus();
    }

    private DeviceStatusListener mDeviceStatusListener = new DeviceStatusListener() {
        @Override
        public void onAttachDevice(DeviceHardwareInfo deviceHardwareInfo) {
            refreshDeviceStatus();
        }

        @Override
        public void onDetachDevice(DeviceHardwareInfo deviceHardwareInfo) {
            refreshDeviceStatus();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterDeviceStatusListener(mDeviceStatusListener);
    }

    /**
     * 刷新设备状态
     */
    private void refreshDeviceStatus() {
        List<DeviceHardwareInfo> deviceHardwareInfoList = DeviceManager.INSTANCE.getDeviceInterface().getUSBDeviceHardwareInfoList();
        if (deviceHardwareInfoList == null) {
            return;
        }
        List<CommonExpandItem> commonExpandItemList = new ArrayList<>();
        for (DeviceHardwareInfo hardwareInfo : deviceHardwareInfoList) {
            int type = hardwareInfo.getType();
            String deviceName = hardwareInfo.getDeviceName();
            if (hardwareInfo.isConnected()) {
                deviceName += "(已连接)";
            } else {
                deviceName += "(已断开)";
            }
            commonExpandItemList.add(new CommonExpandItem(type, deviceName));
            int device1Type = PaymentSettingMMKV.getDevice1Type();
            if (device1Type != 0) {
                if (hardwareInfo.getType() == device1Type) {
                    stvDevice1.setText(deviceName);
                }
            }
            int device2Type = PaymentSettingMMKV.getDevice2Type();
            if (device2Type != 0) {
                if (hardwareInfo.getType() == device2Type) {
                    stvDevice2.setText(deviceName);
                }
            }
            int device3Type = PaymentSettingMMKV.getDevice3Type();
            if (device3Type != 0) {
                if (hardwareInfo.getType() == device3Type) {
                    stvDevice3.setText(deviceName);
                }
            }
        }
        View.OnClickListener onDevice1ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDevice1.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flDevice1.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivDevice1.setSelected(false);
                        stvDevice1.setText(commonExpandItem.getName());
                        PaymentSettingMMKV.putDevice1Type(commonExpandItem.getTypeInt());
                    }
                });
                commonExpandListPopWindow.setExpandItemList(commonExpandItemList);
                commonExpandListPopWindow.showAsDropDown(flDevice1);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivDevice1.setSelected(false);
                    }
                });
            }
        };
        flDevice1.setOnClickListener(onDevice1ClickListener);
        View.OnClickListener onDevice2ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDevice2.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flDevice2.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivDevice2.setSelected(false);
                        stvDevice2.setText(commonExpandItem.getName());
                        PaymentSettingMMKV.putDevice2Type(commonExpandItem.getTypeInt());
                    }
                });
                commonExpandListPopWindow.setExpandItemList(commonExpandItemList);
                commonExpandListPopWindow.showAsDropDown(flDevice2);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivDevice2.setSelected(false);
                    }
                });
            }
        };
        flDevice2.setOnClickListener(onDevice2ClickListener);
        View.OnClickListener onDevice3ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDevice3.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flDevice3.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivDevice3.setSelected(false);
                        stvDevice3.setText(commonExpandItem.getName());
                        PaymentSettingMMKV.putDevice3Type(commonExpandItem.getTypeInt());
                    }
                });
                commonExpandListPopWindow.setExpandItemList(commonExpandItemList);
                commonExpandListPopWindow.showAsDropDown(flDevice3);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivDevice3.setSelected(false);
                    }
                });
            }
        };
        flDevice3.setOnClickListener(onDevice3ClickListener);
    }

}
