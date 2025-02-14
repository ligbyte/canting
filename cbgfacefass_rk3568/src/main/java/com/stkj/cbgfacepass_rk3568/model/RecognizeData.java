package com.stkj.cbgfacepass_rk3568.model;

import mcv.facepass.types.FacePassTrackOptions;

public class RecognizeData {
    public byte[] message;
    public FacePassTrackOptions[] trackOpt;

    public RecognizeData(byte[] message) {
        this.message = message;
        this.trackOpt = null;
    }

    public RecognizeData(byte[] message, FacePassTrackOptions[] opt) {
        this.message = message;
        this.trackOpt = opt;
    }
}