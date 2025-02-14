package com.stkj.supermarketmini.goods.ui.weight;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.common.camera.OnSimpleCameraListener;
import com.stkj.common.camera.SimpleCameraFragment;
import com.stkj.common.camera.SimpleCameraXFragment;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.fragment.ImagePreviewFragment;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.goods.callback.OnCapturePicListener;

import java.io.File;

/**
 * 上传图片item
 */
public class UploadPicItemLayout extends FrameLayout {

    private ImageView ivUploadPic;
    private ImageView ivUploadDel;
    private String uploadPicUrl;
    private OnCapturePicListener capturePicListener;
    private boolean isEditMode;

    public UploadPicItemLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public UploadPicItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UploadPicItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_upload_pic_layout, this);
        ivUploadPic = (ImageView) findViewById(R.id.iv_upload_pic);
        ivUploadDel = (ImageView) findViewById(R.id.iv_upload_del);
        ivUploadPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(uploadPicUrl)) {
                    //进入预览弹窗
                    ImagePreviewFragment.build(uploadPicUrl).show(context);
                    return;
                }
                //非编辑不可点击
                if (!isEditMode) {
                    return;
                }
                if (context instanceof BaseActivity) {
                    BaseActivity baseActivity = (BaseActivity) context;
                    SimpleCameraXFragment simpleCameraXFragment = new SimpleCameraXFragment();
//                    simpleCameraXFragment.setDisplayOrientation(DeviceManager.INSTANCE.getDeviceInterface().getCameraDisplayOrientation());
//                    SimpleCameraFragment simpleCameraFragment = new SimpleCameraFragment();
                    simpleCameraXFragment.setSimpleCameraListener(new OnSimpleCameraListener() {
                        @Override
                        public void onTakePicture(String path) {
                            if (capturePicListener != null) {
                                capturePicListener.onCapturePic(Uri.fromFile(new File(path)));
                            }
                        }

                        @Override
                        public void onTakePictureError(String msg) {
                            CommonDialogUtils.showTipsDialog(baseActivity, "拍照失败:" + msg);
                        }
                    });
                    baseActivity.addContentPlaceHolderFragment(simpleCameraXFragment);
                }
            }
        });
//                FilePickerHelper pickerHelper = ActivityHolderFactory.get(FilePickerHelper.class, context);
//                if (pickerHelper != null) {
//                    pickerHelper.setAppPickFileListener(new FilePickerHelper.OnPickFileListener() {
//                        @Override
//                        public void onPickFile(@Nullable Uri[] uri, int pickType) {
//                            pickerHelper.setAppPickFileListener(null);
//                            if (uri != null && uri.length > 0) {
//                                if (capturePicListener != null) {
//                                    capturePicListener.onCapturePic(uri[0]);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onPickCancel(int pickType) {
//                            pickerHelper.setAppPickFileListener(null);
//                        }
//                    });
//                    pickerHelper.startImageCapture();
//                }
//            }
//        });
        ivUploadDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditMode) {
                    return;
                }
                resetUploadPic();
            }
        });
    }

    public void resetUploadPic() {
        uploadPicUrl = null;
        ivUploadPic.setImageResource(0);
        ivUploadPic.setBackgroundResource(R.mipmap.icon_upload_pic_default);
        ivUploadPic.setClickable(true);
        ivUploadDel.setVisibility(GONE);
    }

    public String getUploadPicUrl() {
        return uploadPicUrl;
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (editMode) {
            if (!TextUtils.isEmpty(uploadPicUrl)) {
                ivUploadDel.setVisibility(VISIBLE);
                ivUploadPic.setBackgroundResource(0);
            } else {
                ivUploadPic.setImageResource(0);
                ivUploadPic.setBackgroundResource(R.mipmap.icon_upload_pic_default);
                ivUploadDel.setVisibility(GONE);
            }
        } else {
            ivUploadPic.setImageResource(0);
            ivUploadPic.setBackgroundResource(R.mipmap.icon_upload_pic_default);
            ivUploadDel.setVisibility(GONE);
        }
    }

    public void loadGoodsPic(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        uploadPicUrl = url;
        ivUploadDel.setVisibility(isEditMode ? VISIBLE : GONE);
        ivUploadPic.setBackgroundResource(0);
        GlideApp.with(getContext()).load(url).placeholder(R.mipmap.icon_goods_default).into(ivUploadPic);
    }

    public void setCapturePicListener(OnCapturePicListener capturePicListener) {
        this.capturePicListener = capturePicListener;
    }
}
