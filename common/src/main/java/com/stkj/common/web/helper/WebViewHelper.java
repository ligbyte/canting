package com.stkj.common.web.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.stkj.common.core.AppManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.utils.BitmapUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import top.zibin.luban.Luban;

public class WebViewHelper {

    public static void getWebViewCacheBitmap(View mTitleView, View mWebView, @Nullable String picFileName, boolean needCompress, BitmapUtils.ViewCacheBitmapCallback bitmapCallback) {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        mTitleView.setDrawingCacheEnabled(true);
                        mTitleView.buildDrawingCache();

                        mWebView.setDrawingCacheEnabled(true);
                        mWebView.buildDrawingCache();

                        Bitmap titleDrawingCache = mTitleView.getDrawingCache();
                        Bitmap webViewDrawingCache = mWebView.getDrawingCache();

                        int titleCacheHeight = titleDrawingCache.getHeight();
                        int webViewCacheHeight = webViewDrawingCache.getHeight();
                        int webViewCacheWidth = webViewDrawingCache.getWidth();

                        Bitmap resultBitmap = Bitmap.createBitmap(webViewCacheWidth, webViewCacheHeight + titleCacheHeight, Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(resultBitmap);
                        //绘制标题
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(0, 0, webViewCacheWidth, titleCacheHeight, paint);
                        canvas.drawBitmap(titleDrawingCache, 0, 0, null);
                        //绘制网页
                        canvas.drawBitmap(webViewDrawingCache, 0, titleCacheHeight, null);

                        mTitleView.setDrawingCacheEnabled(false);
                        mTitleView.destroyDrawingCache();
                        mWebView.setDrawingCacheEnabled(false);
                        mWebView.destroyDrawingCache();

                        String drawCachePath = "";
                        String fileName = picFileName;
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = "view_cache_" + System.currentTimeMillis() + ".jpg";
                        }
                        File shareFile = StorageHelper.createShareFile(fileName);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(shareFile));
                        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                        if (needCompress) {
                            List<File> fileList = Luban.with(AppManager.INSTANCE.getApplication())
                                    .load(shareFile)
                                    .setTargetDir(StorageHelper.getExternalShareDirPath())
                                    .ignoreBy(250)
                                    .get();
                            if (fileList != null && !fileList.isEmpty()) {
                                drawCachePath = fileList.get(0).getAbsolutePath();
                            } else {
                                drawCachePath = shareFile.getAbsolutePath();
                            }
                        } else {
                            drawCachePath = shareFile.getAbsolutePath();
                        }
                        resultBitmap.recycle();
                        emitter.onNext(drawCachePath);
                        emitter.onComplete();
                    }
                }).compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<String>() {
                    @Override
                    protected void onSuccess(@NonNull String s) {
                        if (bitmapCallback != null) {
                            if (!TextUtils.isEmpty(s)) {
                                bitmapCallback.onComplete(s);
                            } else {
                                bitmapCallback.onFail();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (bitmapCallback != null) {
                            bitmapCallback.onFail();
                        }
                    }
                });
    }

    /**
     * 原生WebView长截图
     * android.webkit.WebView.enableSlowWholeDocumentDraw();
     */
    public static Bitmap getWebViewBitmap(android.webkit.WebView webView) {
        Bitmap bitmap = null;
        try {
            float scale = webView.getScale();
            int width = webView.getWidth();
            int height = (int) (webView.getContentHeight() * scale + 0.5);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.d("WebViewHelper", "webView OutOfMemoryError");
        } catch (Exception e) {
            Log.d("WebViewHelper", "webView Exception");
        }
        return bitmap;
    }
}
