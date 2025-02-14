package com.stkj.supermarket.base.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import com.stkj.supermarket.R;

/**
 * 人脸识别摄像头布局（camerax）
 */
public class FacePassCameraXLayout extends FrameLayout {

    private PreviewView previewFace;
    private ImageView ivDefaultFace;
    private TextView tvFaceTips;
    private boolean isPreviewFace;

    public FacePassCameraXLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public FacePassCameraXLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FacePassCameraXLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.FacePassCameraXLayout);
            isPreviewFace = array.getBoolean(R.styleable.FacePassCameraXLayout_faceXPreview, false);
        }
        LayoutInflater.from(context).inflate(R.layout.include_face_pass_camerax, this);
        previewFace = (PreviewView) findViewById(R.id.preview_face);
        ivDefaultFace = (ImageView) findViewById(R.id.iv_default_face);
        tvFaceTips = (TextView) findViewById(R.id.tv_face_tips);
        if (isPreviewFace) {
            setPreviewFace(true);
        }
    }

    public void setPreviewFace(boolean b) {
        isPreviewFace = b;
        if (isPreviewFace) {
            previewFace.setVisibility(VISIBLE);
            ivDefaultFace.setImageResource(R.mipmap.icon_consumer_camera);
        } else {
            previewFace.setVisibility(GONE);
            ivDefaultFace.setImageResource(R.mipmap.icon_welcome_consumer);
        }
    }

    public void setFaceCameraTips(String tips) {
        if (tvFaceTips != null) {
            tvFaceTips.setText(tips);
        }
    }

    public PreviewView getFacePreviewFace() {
        return previewFace;
    }
}
