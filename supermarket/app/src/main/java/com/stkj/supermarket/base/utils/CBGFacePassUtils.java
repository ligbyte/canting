package com.stkj.supermarket.base.utils;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

import mcv.facepass.types.FacePassImage;

public class CBGFacePassUtils {
    public static FacePassImage convertFacePassImage(ImageProxy imageProxy) {
        try {
            return new FacePassImage(yuv420ToNv21(imageProxy), imageProxy.getWidth(), imageProxy.getHeight(), imageProxy.getImageInfo().getRotationDegrees(), 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * YUV_420_888转NV21
     *
     * @param image CameraX ImageProxy
     * @return byte array
     */
    public static byte[] yuv420ToNv21(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        int size = image.getWidth() * image.getHeight();
        byte[] nv21 = new byte[size * 3 / 2];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);

        byte[] u = new byte[uSize];
        uBuffer.get(u);

        //每隔开一位替换V，达到VU交替
        int pos = ySize + 1;
        for (int i = 0; i < uSize; i++) {
            if (i % 2 == 0) {
                nv21[pos] = u[i];
                pos += 2;
            }
        }
        return nv21;
    }
}
