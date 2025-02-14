package com.stkj.infocollect.base.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.infocollect.R;
import com.stkj.infocollect.base.callback.SimpleSeekBarChangeListener;

/**
 * 通用的滑动进度条
 */
public class CommonSeekProgressBar extends FrameLayout {

    private SeekBar seekbarPro;
    private TextView tvPro;
    private OnCommonSeekProgressListener seekProgressListener;

    public CommonSeekProgressBar(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CommonSeekProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CommonSeekProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        int progress = 0;
        int maxProgress = 100;
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, com.stkj.infocollect.R.styleable.CommonSeekProgressBar);
            progress = array.getInt(R.styleable.CommonSeekProgressBar_cspb_progress, 0);
            maxProgress = array.getInt(R.styleable.CommonSeekProgressBar_cspb_max_progress, 100);
            array.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.include_common_seek_bar, this);
        tvPro = (TextView) findViewById(R.id.tv_pro);
        seekbarPro = (SeekBar) findViewById(R.id.seekbar_pro);
        seekbarPro.setMax(maxProgress);
        seekbarPro.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekProgressListener != null) {
                    seekProgressListener.onProgressFinished(seekBar.getProgress());
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekProgressListener != null) {
                    seekProgressListener.onProgressChange(progress);
                }
                tvPro.setText(String.valueOf(progress));
            }
        });
        setSeekProgress(progress);
    }

    public void setSeekProgressListener(OnCommonSeekProgressListener seekProgressListener) {
        this.seekProgressListener = seekProgressListener;
    }

    public void setSeekProgress(int progress) {
        if (tvPro == null) {
            return;
        }
        if (progress <= 0) {
            progress = 0;
        }
        int maxProgress = seekbarPro.getMax();
        if (progress >= maxProgress) {
            progress = maxProgress;
        }
        tvPro.setText(String.valueOf(progress));
        seekbarPro.setProgress(progress);
    }

    public int getSeekProgress() {
        if (seekbarPro != null) {
            return seekbarPro.getProgress();
        }
        return 0;
    }

    public int getMaxProgress() {
        if (seekbarPro != null) {
            return seekbarPro.getMax();
        }
        return 100;
    }

    public interface OnCommonSeekProgressListener {
        void onProgressFinished(int progress);

        default void onProgressChange(int progress) {
        }
    }

}
