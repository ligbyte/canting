package com.stkj.infocollect.card.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.infocollect.R;

/**
 * 裁剪遮挡层
 */
public class CropOverLayerView extends View {

    private int mCropWidth = 0;
    private int mCropHeight = 0;
    private Paint layerPaint;
    private Paint framePaint;
    private RectF mTempRect = new RectF();

    public CropOverLayerView(Context context) {
        super(context);
        init(context, null);
    }

    public CropOverLayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CropOverLayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, com.stkj.infocollect.R.styleable.CropOverLayerView);
            mCropWidth = array.getDimensionPixelSize(R.styleable.CropOverLayerView_colv_crop_width, 0);
            mCropHeight = array.getDimensionPixelSize(R.styleable.CropOverLayerView_colv_crop_height, 0);
            array.recycle();
        }
        layerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framePaint.setColor(Color.parseColor("#53ABFC"));
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(5);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#70000000"));
        if (mCropWidth > 0 && mCropHeight > 0 && getWidth() > mCropWidth && getHeight() > mCropHeight) {
            canvas.save();
            int halfCropWidth = (getWidth() - mCropWidth) / 2;
            int halfCropHeight = (getHeight() - mCropHeight) / 2;
            mTempRect.set(halfCropWidth, halfCropHeight, halfCropWidth + mCropWidth, halfCropHeight + mCropHeight);
            canvas.drawRect(mTempRect, layerPaint);
            canvas.drawRect(mTempRect, framePaint);
            canvas.restore();
        }
    }

    public RectF getCropRect() {
        return mTempRect;
    }
}
