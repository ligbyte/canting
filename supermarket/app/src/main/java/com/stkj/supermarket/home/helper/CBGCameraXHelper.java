package com.stkj.supermarket.home.helper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.permissions.request.CameraPermissionRequest;
import com.stkj.supermarket.base.permission.AppPermissionHelper;
import com.stkj.supermarket.base.utils.CBGFacePassUtils;

import java.util.concurrent.Executors;

import mcv.facepass.types.FacePassImage;

/**
 * 旷视人脸检测帮助类
 */
public class CBGCameraXHelper extends ActivityWeakRefHolder {

    private PreviewView previewView;
    private LifecycleCameraController cameraController;
    private CBGFacePassHandlerHelper facePassHandlerHelper;
    private CBGFacePassHandlerHelper.OnDetectFaceListener onDetectFaceListener;

    public CBGCameraXHelper(@NonNull Activity activity) {
        super(activity);
        AppPermissionHelper.with((FragmentActivity) activity)
                .requestPermission(new CameraPermissionRequest(), new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        cameraController = new LifecycleCameraController(activity);
                        cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS);
                        facePassHandlerHelper = ActivityHolderFactory.get(CBGFacePassHandlerHelper.class, activity);
                    }
                });
    }

    public void setOnDetectFaceListener(CBGFacePassHandlerHelper.OnDetectFaceListener onDetectFaceListener) {
        this.onDetectFaceListener = onDetectFaceListener;
    }

    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
    }

    /**
     * 开始人脸检测
     */
    public void startFacePassDetect() {
        if (previewView == null) {
            return;
        }
        if (cameraController == null) {
            return;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        cameraController.setImageAnalysisAnalyzer(Executors.newFixedThreadPool(3), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                FacePassImage facePassImage = CBGFacePassUtils.convertFacePassImage(image);
                runUIThreadWithCheck(new Runnable() {
                    @Override
                    public void run() {
                        facePassHandlerHelper.addFeedFrame(facePassImage);
                        facePassHandlerHelper.startFeedFrameDetectTask();
                        facePassHandlerHelper.startRecognizeFrameTask();
                    }
                });
            }
        });
        cameraController.bindToLifecycle((LifecycleOwner) activityWithCheck);
        previewView.setController(cameraController);
        facePassHandlerHelper.setOnDetectFaceListener(onDetectFaceListener);
    }

    /**
     * 停止人脸检测
     */
    public void stopFacePassDetect() {
        cameraController.clearImageAnalysisAnalyzer();
    }

    @Override
    public void onClear() {
        cameraController.unbind();
    }
}
