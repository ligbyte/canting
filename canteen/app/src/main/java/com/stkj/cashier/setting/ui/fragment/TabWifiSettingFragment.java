package com.stkj.cashier.setting.ui.fragment;

import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.permission.AppPermissionHelper;
import com.stkj.cashier.home.helper.SystemEventWatcherHelper;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.permissions.request.LocationPermissionRequest;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.IntentUtils;
import com.stkj.common.utils.NetworkUtils;

/**
 * wifi设置
 */
public class TabWifiSettingFragment extends BaseRecyclerFragment implements SystemEventWatcherHelper.OnSystemEventListener {

    private LinearLayout llHasWifi;
    private TextView tvWifiName;
    private ShapeTextView stvGoWifiSetting1;
    private LinearLayout llNoWifi;
    private ShapeTextView stvGoWifiSetting2;

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_tab_wifi_setting;
    }

    @Override
    protected void initViews(View rootView) {
        View.OnClickListener wifiSettingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentUtils.getToWifiSetting());
            }
        };
        llHasWifi = (LinearLayout) findViewById(R.id.ll_has_wifi);
        tvWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        stvGoWifiSetting1 = (ShapeTextView) findViewById(R.id.stv_go_wifi_setting1);
        stvGoWifiSetting1.setOnClickListener(wifiSettingClickListener);
        llNoWifi = (LinearLayout) findViewById(R.id.ll_no_wifi);
        stvGoWifiSetting2 = (ShapeTextView) findViewById(R.id.stv_go_wifi_setting2);
        stvGoWifiSetting2.setOnClickListener(wifiSettingClickListener);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        refreshWifiLay();
        SystemEventWatcherHelper systemEventWatcherHelper = mActivity.getWeakRefHolder(SystemEventWatcherHelper.class);
        systemEventWatcherHelper.addSystemEventListener(this);
    }

    @Override
    public void onDetach() {
        SystemEventWatcherHelper systemEventWatcherHelper = mActivity.getWeakRefHolder(SystemEventWatcherHelper.class);
        systemEventWatcherHelper.removeSystemEventListener(this);
        super.onDetach();
    }

    @Override
    public void onNetworkAvailable() {
        refreshWifiLay();
    }

    @Override
    public void onNetworkLost() {
        refreshWifiLay();
    }

    @Override
    public void onNetworkUnavailable() {
        refreshWifiLay();
    }

    private void refreshWifiLay() {
        if (NetworkUtils.isConnected()) {
            //有网络
            llNoWifi.setVisibility(View.GONE);
            llHasWifi.setVisibility(View.VISIBLE);
            boolean wifiConnected = NetworkUtils.isWifiConnected();
            if (wifiConnected) {
                //获取wifi名称 android 10以上需要定位权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppPermissionHelper.with(mActivity)
                            .requestPermission(new LocationPermissionRequest(), new PermissionCallback() {
                                @Override
                                public void onGranted() {
                                    String connectWifiSsid = NetworkUtils.getConnectWifiSsid();
                                    tvWifiName.setText(connectWifiSsid);
                                }
                            });
                } else {
                    String connectWifiSsid = NetworkUtils.getConnectWifiSsid();
                    tvWifiName.setText(connectWifiSsid);
                }
            } else {
                tvWifiName.setText("移动网络");
            }
        } else {
            //无网络
            llNoWifi.setVisibility(View.VISIBLE);
            llHasWifi.setVisibility(View.GONE);
        }
    }
}
