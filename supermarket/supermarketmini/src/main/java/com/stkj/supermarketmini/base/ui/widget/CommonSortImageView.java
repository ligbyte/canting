package com.stkj.supermarketmini.base.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.stkj.supermarketmini.R;

public class CommonSortImageView extends AppCompatImageView {
    public static final int TYPE_SORT_DEF = 0;
    public static final int TYPE_SORT_UP = 1;
    public static final int TYPE_SORT_DOWN = 2;
    private int sortType = TYPE_SORT_DEF;
    private OnSortSelectListener onSortSelectListener;

    public CommonSortImageView(Context context) {
        super(context);
        init();
    }

    public CommonSortImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommonSortImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private OnClickListener onSortClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sortType == TYPE_SORT_DEF) {
                setUpSort();
            } else if (sortType == TYPE_SORT_UP) {
                setDownSort();
            } else if (sortType == TYPE_SORT_DOWN) {
                setDefaultSort();
            }
            if (onSortSelectListener != null) {
                onSortSelectListener.onSortSelect(sortType);
            }
        }
    };

    public OnClickListener getSortClickListener() {
        return onSortClickListener;
    }

    private void init() {
        setOnClickListener(onSortClickListener);
        setDefaultSort();
    }

    public void setDefaultSort() {
        setImageResource(R.mipmap.icon_sort_default);
        sortType = TYPE_SORT_DEF;
    }

    public void setUpSort() {
        setImageResource(R.mipmap.icon_sort_up);
        sortType = TYPE_SORT_UP;
    }

    public void setDownSort() {
        setImageResource(R.mipmap.icon_sort_down);
        sortType = TYPE_SORT_DOWN;
    }

    public void setOnSortSelectListener(OnSortSelectListener onSortSelectListener) {
        this.onSortSelectListener = onSortSelectListener;
    }

    public interface OnSortSelectListener {
        void onSortSelect(int sortType);
    }

}
