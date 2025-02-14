package com.stkj.cashiermini.base.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.stkj.cashiermini.R;
import com.google.zxing.Result;
import com.king.camera.scan.AnalyzeResult;
import com.king.camera.scan.CameraScan;
import com.king.camera.scan.analyze.Analyzer;
import com.king.zxing.BarcodeCameraScanFragment;
import com.king.zxing.DecodeConfig;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.stkj.cashiermini.base.callback.QrCodeListener;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.utils.FragmentUtils;

/**
 * 条码扫码界面
 */
public class QrCodeAlertFragment extends BarcodeCameraScanFragment {

    private ImageView ivClose;
    private QrCodeListener qrCodeListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qrcode_alert;
    }

    @Override
    public void initUI() {
        super.initUI();
        ivClose = (ImageView) getRootView().findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Nullable
    @Override
    public Analyzer<Result> createAnalyzer() {
        // 初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setFullAreaScan(true);//设置是否全区域识别，默认false
        return new MultiFormatAnalyzer(decodeConfig);
    }

    public void setQrCodeListener(QrCodeListener qrCodeListener) {
        this.qrCodeListener = qrCodeListener;
    }

    @Override
    public void onScanResultCallback(@NonNull AnalyzeResult<Result> result) {
        try {
            if (qrCodeListener != null) {
                qrCodeListener.onScanResult(result.getResult().toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScanResultFailure() {
        try {
            if (qrCodeListener != null) {
                qrCodeListener.onScanError();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        FragmentManager parentFragmentManager = null;
        try {
            parentFragmentManager = getParentFragmentManager();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (parentFragmentManager == null) {
            LogHelper.print("QrCodeAlertFragment--dismiss--getParentFragmentManager()" + this.getClass().getName());
            return;
        }
        LogHelper.print("QrCodeAlertFragment--dismiss--" + this.getClass().getName());
        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), this);
    }

    public void show(Context context) {
        if (context instanceof BaseActivity) {
            BaseActivity commonActivity = (BaseActivity) context;
            commonActivity.addContentPlaceHolderFragment(this);
            LogHelper.print("QrCodeAlertFragment--show--addContentPlaceHolderFragment()" + this.getClass().getName());
        }
    }

    public void continueScanQrcode() {
        CameraScan<Result> cameraScan = getCameraScan();
        if (cameraScan != null) {
            cameraScan.setAnalyzeImage(true);
        }
    }
}
