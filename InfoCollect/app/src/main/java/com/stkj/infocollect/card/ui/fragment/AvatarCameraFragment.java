package com.stkj.infocollect.card.ui.fragment;

import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.stkj.common.camera.CameraHelper;
import com.stkj.common.camera.SimpleCameraPermission;
import com.stkj.common.permissions.PermissionHelper;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 人脸头像相机页面
 */
public class AvatarCameraFragment extends BaseRecyclerFragment {

    private ImageView ivCameraBack;
    private SurfaceView surfaceView;
    private ImageView ivCameraCollect;
    private CameraHelper cameraHelper;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_avatar_camera;
    }

    @Override
    protected void initViews(View rootView) {
        ivCameraBack = (ImageView) findViewById(R.id.iv_camera_back);
        ivCameraBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarCameraFragment.this);
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.pv_camera);
        ivCameraCollect = (ImageView) findViewById(R.id.iv_camera_collect);
        ivCameraCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHelper.takePicture();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        SimpleCameraPermission simpleCameraPermission = new SimpleCameraPermission();
        PermissionHelper.with(mActivity)
                .requestPermission(simpleCameraPermission, new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Schedulers.io().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        AppToast.toastMsg("取消授权，无法使用相机");
                    }
                });
    }

    private void initData() {
        cameraHelper = new CameraHelper(mActivity);
        cameraHelper.setFlipMirrorH(true);
        cameraHelper.prepare(surfaceView, true);
        cameraHelper.setCameraHelperCallback(new CameraHelper.OnCameraHelperCallback() {
            @Override
            public void onTakePictureSuccess(String picPath) {
                AvatarCropFragment avatarCropFragment = new AvatarCropFragment();
                avatarCropFragment.setPicPath(picPath);
                mActivity.addContentPlaceHolderFragment(avatarCropFragment);
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarCameraFragment.this);
            }

            @Override
            public void onTakePictureError(String message) {
                CameraHelper.OnCameraHelperCallback.super.onTakePictureError(message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraHelper != null) {
            cameraHelper.onClear();
        }
    }
}
