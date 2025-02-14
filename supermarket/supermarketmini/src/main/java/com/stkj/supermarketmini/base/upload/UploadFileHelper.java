package com.stkj.supermarketmini.base.upload;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.service.AppService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadFileHelper extends ActivityWeakRefHolder {

    private UploadFileListener uploadFileListener;

    public UploadFileHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setUploadFileListener(UploadFileListener uploadFileListener) {
        this.uploadFileListener = uploadFileListener;
    }

    public void uploadFile(File file) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            if (uploadFileListener != null) {
                uploadFileListener.onError("界面异常");
            }
            return;
        }
        if (uploadFileListener != null) {
            uploadFileListener.onStart();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part formData = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RetrofitManager.INSTANCE
                .getDefaultRetrofit()
                .create(AppService.class)
                .uploadFile(formData)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        if (response.isSuccess()) {
                            if (uploadFileListener != null) {
                                uploadFileListener.onSuccess(response.getData());
                            }
                        } else {
                            if (uploadFileListener != null) {
                                uploadFileListener.onError(response.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (uploadFileListener != null) {
                            uploadFileListener.onError(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onClear() {

    }

    public interface UploadFileListener {
        void onStart();

        void onSuccess(String fileUrl);

        void onError(String msg);
    }
}
