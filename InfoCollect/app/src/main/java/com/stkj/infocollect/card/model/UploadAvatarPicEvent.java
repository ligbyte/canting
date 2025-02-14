package com.stkj.infocollect.card.model;

/**
 * 上传人脸照片
 */
public class UploadAvatarPicEvent {

    private String picUrl;

    public UploadAvatarPicEvent() {
    }

    public UploadAvatarPicEvent(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
