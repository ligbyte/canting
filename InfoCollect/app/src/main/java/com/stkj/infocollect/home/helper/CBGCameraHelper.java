package com.stkj.infocollect.home.helper;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.stkj.infocollect.base.permission.AppPermissionHelper;
import com.stkj.infocollect.base.utils.EventBusUtils;
import com.stkj.infocollect.setting.model.PauseFacePassDetect;
import com.stkj.infocollect.setting.model.ResumeFacePassDetect;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.common.camera.CameraHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.log.LogHelper;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.permissions.request.CameraPermissionRequest;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;

/**
 * 旷视人脸检测帮助类
 */
public class CBGCameraHelper extends ActivityWeakRefHolder {

    private SurfaceView irPreview;
    private SurfaceView preview;
    private CameraHelper cameraHelper;
    private CameraHelper irCameraHelper;
    private CBGFacePassHandlerHelper facePassHandlerHelper;
    private CBGFacePassHandlerHelper.OnDetectFaceListener onDetectFaceListener;
    private boolean isFaceDualCamera;

    public CBGCameraHelper(@NonNull Activity activity) {
        super(activity);
        EventBusUtils.registerEventBus(this);
    }

    public void setOnDetectFaceListener(CBGFacePassHandlerHelper.OnDetectFaceListener onDetectFaceListener) {
        this.onDetectFaceListener = onDetectFaceListener;
    }

    public void setPreviewView(SurfaceView surfaceView, SurfaceView irPreview, boolean isFaceDualCamera) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        this.preview = surfaceView;
        this.irPreview = irPreview;
        this.isFaceDualCamera = isFaceDualCamera;
        AppPermissionHelper.with((FragmentActivity) activityWithCheck)
                .requestPermission(new CameraPermissionRequest(), new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        facePassHandlerHelper = ActivityHolderFactory.get(CBGFacePassHandlerHelper.class, activityWithCheck);
                    }
                });
    }

    /**
     * 开始人脸检测
     */
    public void prepareFacePassDetect() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (this.preview == null) {
            return;
        }
        if (cameraHelper == null) {
            cameraHelper = new CameraHelper(activityWithCheck);
            cameraHelper.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        if (cameraHelper != null) {
            if (cameraHelper.hasPreviewView()) {
//            cameraHelper.startPreview();
            } else {
                cameraHelper.setNeedPreviewCallBack(true);
                cameraHelper.setCameraHelperCallback(new CameraHelper.OnCameraHelperCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera, int displayOrientation, int previewOrientation) {
                        try {
                            if (!facePassHandlerHelper.isStartFrameDetectTask()) {
                                return;
                            }
                            Camera.Parameters parameters = camera.getParameters();
                            int width = parameters.getPreviewSize().width;
                            int height = parameters.getPreviewSize().height;
                            FacePassImage facePassImage = new FacePassImage(data, width, height, displayOrientation, FacePassImageType.NV21);
                            runUIThreadWithCheck(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFaceDualCamera) {
                                        facePassHandlerHelper.addRgbFrame(facePassImage);
                                    } else {
                                        facePassHandlerHelper.addFeedFrame(facePassImage);
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
                cameraHelper.prepare(preview);
            }
        }
        //人脸识别回调
        if (facePassHandlerHelper != null) {
            facePassHandlerHelper.setOnDetectFaceListener(onDetectFaceListener);
        }
    }

    private boolean needResumeFacePassDetect;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPauseFacePassDetect(PauseFacePassDetect eventBus) {
        if (facePassHandlerHelper != null && facePassHandlerHelper.isStartFrameDetectTask()) {
            needResumeFacePassDetect = true;
            stopFacePassDetect();
            AppToast.toastMsg("人脸检测功能已停止");
        } else {
            needResumeFacePassDetect = false;
        }
        LogHelper.print("--EventBusUtils-onPauseFacePassDetect");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResumeFacePassDetect(ResumeFacePassDetect eventBus) {
        if (needResumeFacePassDetect) {
            startFacePassDetect();
            AppToast.toastMsg("人脸检测功能已恢复");
        }
        needResumeFacePassDetect = false;
        LogHelper.print("--EventBusUtils-onResumeFacePassDetect");
    }

    /**
     * 停止人脸检测
     */
    public void startFacePassDetect() {
        if (facePassHandlerHelper != null) {
            facePassHandlerHelper.startFeedFrameDetectTask();
            facePassHandlerHelper.startRecognizeFrameTask();
        }
    }

    /**
     * 停止人脸检测
     */
    public void stopFacePassDetect() {
        if (facePassHandlerHelper != null) {
            facePassHandlerHelper.stopFeedFrameDetectTask();
            facePassHandlerHelper.stopRecognizeFrameTask();
            facePassHandlerHelper.resetHandler();
        }
    }

    @Override
    public void onClear() {
        EventBusUtils.unRegisterEventBus(this);
        stopFacePassDetect();
    }

    /**
     * 释放相机
     */
    public void releaseCameraHelper() {
        stopFacePassDetect();
        //人脸识别回调
        if (facePassHandlerHelper != null) {
            facePassHandlerHelper.setOnDetectFaceListener(null);
        }
        if (cameraHelper != null) {
            cameraHelper.onClear();
            cameraHelper = null;
        }
        if (irCameraHelper != null) {
            irCameraHelper.onClear();
            irCameraHelper = null;
        }
    }
}
