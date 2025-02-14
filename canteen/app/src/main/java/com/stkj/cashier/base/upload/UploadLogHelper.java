package com.stkj.cashier.base.upload;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.stkj.cashier.base.callback.OnUploadLogListener;
import com.stkj.cashier.setting.data.DeviceSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.log.LogHelper;

import java.io.File;

/**
 * 上传log文件
 */
public class UploadLogHelper extends ActivityWeakRefHolder {

    private boolean isUploadingLog;
    private OnUploadLogListener uploadLogListener;
    private UploadFileHelper uploadFileHelper;

    public UploadLogHelper(@NonNull Activity activity) {
        super(activity);
        uploadFileHelper = new UploadFileHelper(activity);
    }

    public void setUploadLogListener(OnUploadLogListener uploadLogListener) {
        this.uploadLogListener = uploadLogListener;
    }

    public void uploadXLogFile() {
        if (isUploadingLog) {
            return;
        }
        //获取当前的日志
        File currentLogFile = LogHelper.getCurrentLogFile();
        if (currentLogFile == null || !currentLogFile.exists() || currentLogFile.length() <= 0) {
            if (uploadLogListener != null) {
                uploadLogListener.onUploadLogError("日志文件不存在");
            }
            return;
        }
        boolean openSysLog = DeviceSettingMMKV.isOpenSysLog();
        LogHelper.setLogEnable(false);
        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
            @Override
            public void onStart() {
                isUploadingLog = true;
                if (uploadLogListener != null) {
                    uploadLogListener.onUploadStart();
                }
            }

            @Override
            public void onSuccess(String fileUrl) {
                LogHelper.setLogEnable(openSysLog);
                isUploadingLog = false;
                if (uploadLogListener != null) {
                    uploadLogListener.onUploadLogSuccess();
                }
            }

            @Override
            public void onError(String msg) {
                LogHelper.setLogEnable(openSysLog);
                isUploadingLog = false;
                if (uploadLogListener != null) {
                    uploadLogListener.onUploadLogError(msg);
                }
            }
        });
        uploadFileHelper.uploadFile(currentLogFile);
    }

    public boolean isUploadingLog() {
        return isUploadingLog;
    }

    @Override
    public void onClear() {
        uploadLogListener = null;
    }
}
