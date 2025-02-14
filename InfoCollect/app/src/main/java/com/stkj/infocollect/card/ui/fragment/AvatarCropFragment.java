package com.stkj.infocollect.card.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.FutureTarget;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.upload.UploadFileHelper;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.card.model.UploadAvatarPicEvent;
import com.stkj.infocollect.card.ui.widget.CropOverLayerView;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.GestureCropImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import mcv.facepass.types.FacePassAddFaceDetectionResult;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 头像裁剪页面
 */
public class AvatarCropFragment extends BaseRecyclerFragment {

    private FrameLayout flCrop;
    private String picPath;
    private GestureCropImageView mCropImageView;
    private CropOverLayerView colvLayer;
    private ShapeTextView stvBack;
    private ShapeTextView stvUpload;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_avatar_crop;
    }

    @Override
    protected void initViews(View rootView) {
        flCrop = (FrameLayout) findViewById(R.id.fl_crop);
        colvLayer = (CropOverLayerView) findViewById(R.id.colv_layer);
        stvBack = (ShapeTextView) findViewById(R.id.stv_back);
        stvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarCropFragment.this);
            }
        });
        stvUpload = (ShapeTextView) findViewById(R.id.stv_upload);
        stvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropFacePic();
            }
        });
    }

    /**
     * 裁剪人脸照片
     */
    private void cropFacePic() {
        if (mCropImageView == null) {
            showCropFacePic("图片初始化失败");
            return;
        }
        showLoadingDialog();
        LogHelper.print("AvatarCropFragment----uploadCameraPic--start");
        mCropImageView.setCropRect(colvLayer.getCropRect());
        mCropImageView.cropAndSaveImage(Bitmap.CompressFormat.JPEG, 100, new BitmapCropCallback() {
            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                compressCropPic(resultUri);
                LogHelper.print("AvatarCropFragment----onBitmapCropped--x:" + offsetX + "|y:" + offsetY + " w:" + imageWidth + "|h:" + imageHeight + "resultUri:" + resultUri);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                hideLoadingDialog();
                showCropFacePic("裁剪照片失败，请重试!");
                LogHelper.print("AvatarCropFragment----onCropFailure--e:" + t.getMessage());
            }
        });
    }

    /**
     * 压缩照片
     */
    private void compressCropPic(Uri resultUri) {
        LogHelper.print("AvatarCropFragment----compressCropPic--start");
        showLoadingDialog();
        Luban.with(mActivity)
                .ignoreBy(250)
                .load(resultUri)
                .setTargetDir(StorageHelper.getExternalShareDirPath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        LogHelper.print("AvatarCropFragment----compressCropPic--success");
                        detectFaceWithPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.print("AvatarCropFragment----compressCropPic--error: " + e.getMessage());
                        hideLoadingDialog();
                        showCropFacePic("图片压缩失败,请重试");
                    }
                })
                .launch();
    }

    /**
     * 检测人脸照片是否可以上传
     */
    private void detectFaceWithPic(File resultFile) {
        showLoadingDialog();
        LogHelper.print("AvatarCropFragment----detectFaceWithPic--start");
        Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                        try {
                            FutureTarget<Bitmap> futureTarget = GlideApp.with(AvatarCropFragment.this)
                                    .asBitmap()
                                    .load(resultFile).submit();
                            Bitmap bitmap = futureTarget.get();
                            if (bitmap == null) {
                                emitter.onNext(199);
                            } else {
                                CBGFacePassHandlerHelper facePassHandlerHelper = mActivity.getWeakRefHolder(CBGFacePassHandlerHelper.class);
//                              //第一次可能失败问题
                                FacePassAddFaceDetectionResult addFaceDetectionResult0 = facePassHandlerHelper.addFaceDetect(bitmap);
//                              //第一次可能失败问题
                                FacePassAddFaceDetectionResult addFaceDetectionResult = facePassHandlerHelper.addFaceDetect(bitmap);
                                if (addFaceDetectionResult != null && addFaceDetectionResult.image != null && addFaceDetectionResult.faceList != null && addFaceDetectionResult.faceList.length > 0) {
                                    emitter.onNext(200);
                                } else {
                                    emitter.onNext(201);
                                }
//                                FacePassDetectFacesResult detectFacesResult = facePassHandlerHelper.detectFace(bitmap);
//                                if (detectFacesResult != null && detectFacesResult.faceList != null && detectFacesResult.faceList.length > 0) {
//                                    emitter.onNext(200);
//                                } else {
//                                    emitter.onNext(201);
//                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                })
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(AvatarCropFragment.this))
                .subscribe(new DefaultObserver<Integer>() {
                    @Override
                    protected void onSuccess(Integer resultCode) {
                        LogHelper.print("AvatarCropFragment----detectFaceWithPic--result =  " + resultCode);
                        if (resultCode == 199) {
                            hideLoadingDialog();
                            showCropFacePic("获取人脸照片失败，请重试!");
                        } else if (resultCode == 200) {
                            uploadFacePic(resultFile);
                        } else {
                            hideLoadingDialog();
                            mActivity.addContentPlaceHolderFragment(new AvatarUploadErrorFragment());
                            FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarCropFragment.this);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.print("AvatarCropFragment----detectFaceWithPic--error =  " + e.getMessage());
                        hideLoadingDialog();
                        showCropFacePic("检测人脸照片失败，请重试!");
                    }
                });
    }

    /**
     * 上传人脸照片
     */
    private void uploadFacePic(File resultFile) {
        showLoadingDialog();
        LogHelper.print("AvatarCropFragment----uploadFacePic--start");
        UploadFileHelper uploadFileHelper = new UploadFileHelper(mActivity);
        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
            @Override
            public void onSuccess(String fileUrl) {
                LogHelper.print("AvatarCropFragment----uploadFacePic--onSuccess fileUrl = " + fileUrl);
                hideLoadingDialog();
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarCropFragment.this);
                EventBus.getDefault().post(new UploadAvatarPicEvent(fileUrl));
            }

            @Override
            public void onError(String msg) {
                LogHelper.print("AvatarCropFragment----uploadFacePic--onError");
                hideLoadingDialog();
                showCropFacePic("人脸照片上传失败:" + msg);
            }
        });
        uploadFileHelper.uploadFile(resultFile);
    }

    /**
     * 统一处理弹窗失败
     */
    private void showCropFacePic(String message) {
        CommonDialogUtils.showTipsDialog(mActivity, message, "确定", new CommonAlertDialogFragment.OnSweetClickListener() {
            @Override
            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                reloadCropImageView();
            }
        });
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            reloadCropImageView();
        }
    }

    private void reloadCropImageView() {
        if (!TextUtils.isEmpty(picPath)) {
            if (mCropImageView != null) {
                flCrop.removeView(mCropImageView);
            }
            mCropImageView = new GestureCropImageView(mActivity);
            mCropImageView.setMaxResultImageSizeX(720);
            mCropImageView.setMaxResultImageSizeY(720);
            try {
                Uri pathUri = Uri.fromFile(new File(picPath));
                mCropImageView.setImageUri(pathUri, pathUri);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            flCrop.addView(mCropImageView, 0);
        }
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
