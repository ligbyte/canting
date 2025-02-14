package com.stkj.supermarket.consumer.callback;

import android.view.SurfaceView;

import androidx.camera.view.PreviewView;

import com.stkj.supermarket.home.ui.widget.HomeTitleLayout;

public interface ConsumerListener {
    default void onCreateFaceXPreviewView(PreviewView previewView) {

    }

    default void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreviewView) {

    }

    default void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {

    }

    default void onConsumerDismiss() {

    }

    default void onConsumerChanged() {

    }
}
