package com.stkj.supermarket.setting.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.supermarket.R;
import com.stkj.supermarket.BuildConfig;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.net.AppNetManager;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.home.helper.HeartBeatHelper;
import com.stkj.supermarket.setting.data.ServerSettingMMKV;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

/**
 * 服务器地址设置
 */
public class TabServerAddressFragment extends BaseRecyclerFragment {

    private FrameLayout flServerAddress;
    private ShapeEditText setServerAddress;
    private ImageView ivServerAddress;
    private ShapeTextView stvConfirmRestart;
    private FrameLayout flHeartbeatInterval;
    private ShapeTextView stvHeartbeatInterval;
    private ImageView ivHeartbeatInterval;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_server_address;
    }

    @Override
    protected void initViews(View rootView) {
        flServerAddress = (FrameLayout) findViewById(R.id.fl_server_address);
        setServerAddress = (ShapeEditText) findViewById(R.id.set_server_address);
        ivServerAddress = (ImageView) findViewById(R.id.iv_server_address);
        stvConfirmRestart = (ShapeTextView) findViewById(R.id.stv_confirm_restart);
        flHeartbeatInterval = (FrameLayout) findViewById(R.id.fl_heartbeat_interval);
        List<CommonExpandItem> heartBeatExpandList = new ArrayList<>();
        heartBeatExpandList.add(new CommonExpandItem(5, "5秒"));
        heartBeatExpandList.add(new CommonExpandItem(10, "10秒"));
        heartBeatExpandList.add(new CommonExpandItem(30, "30秒"));
        heartBeatExpandList.add(new CommonExpandItem(60, "1分钟"));
        heartBeatExpandList.add(new CommonExpandItem(120, "2分钟"));
        heartBeatExpandList.add(new CommonExpandItem(300, "5分钟"));
        flHeartbeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flHeartbeatInterval.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivHeartbeatInterval.setSelected(false);
                        stvHeartbeatInterval.setText(commonExpandItem.getName());
                        ServerSettingMMKV.putHeartBeatInterval(commonExpandItem.getTypeInt());
                        HeartBeatHelper heartBeatHelper = mActivity.getWeakRefHolder(HeartBeatHelper.class);
                        heartBeatHelper.setServerBeatDelay(commonExpandItem.getTypeInt());
                        AppToast.toastMsg("心跳设置已生效");
                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandList);
                commonExpandListPopWindow.showAsDropDown(flHeartbeatInterval);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });
        stvHeartbeatInterval = (ShapeTextView) findViewById(R.id.stv_heartbeat_interval);
        int heartBeatInterval = ServerSettingMMKV.getHeartBeatInterval();
        String expandName = "30秒";
        for (CommonExpandItem expand : heartBeatExpandList) {
            if (expand.getTypeInt() == heartBeatInterval) {
                expandName = expand.getName();
                break;
            }
        }
        stvHeartbeatInterval.setText(expandName);
        ivHeartbeatInterval = (ImageView) findViewById(R.id.iv_heartbeat_interval);
        RxHelper.clickThrottle(stvConfirmRestart, this, new DefaultObserver<Unit>() {
            @Override
            protected void onSuccess(Unit unit) {
                String address = setServerAddress.getText().toString().trim();
                ServerSettingMMKV.handleChangeServerAddress(mActivity, address);
            }
        });
        List<CommonExpandItem> serverAddressExpandList = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            serverAddressExpandList.add(new CommonExpandItem(0, AppNetManager.API_TEST_URL));
        }
        serverAddressExpandList.add(new CommonExpandItem(1, AppNetManager.API_OFFICIAL_URL));
        //获取本地保存
        String serverAddress = ServerSettingMMKV.getServerAddress();
        if (!TextUtils.isEmpty(serverAddress)) {
            serverAddressExpandList.add(new CommonExpandItem(3, serverAddress));
            setServerAddress.setText(serverAddress);
        } else {
            if (BuildConfig.DEBUG) {
                setServerAddress.setText(AppNetManager.API_TEST_URL);
            } else {
                setServerAddress.setText(AppNetManager.API_OFFICIAL_URL);
            }
        }
        ivServerAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivServerAddress.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flServerAddress.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivServerAddress.setSelected(false);
                        setServerAddress.setText(commonExpandItem.getName());
                        ServerSettingMMKV.putServerAddress(commonExpandItem.getName());
                    }
                });
                commonExpandListPopWindow.setExpandItemList(serverAddressExpandList);
                commonExpandListPopWindow.showAsDropDown(flServerAddress);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivServerAddress.setSelected(false);
                    }
                });
            }
        });
    }
}
