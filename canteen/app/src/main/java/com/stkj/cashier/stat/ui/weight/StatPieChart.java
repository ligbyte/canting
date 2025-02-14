package com.stkj.cashier.stat.ui.weight;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.renderer.PieChartRenderer;

/**
 * 统计页面的自定义图标
 */
public class StatPieChart extends PieChart {
    public StatPieChart(Context context) {
        super(context);
    }

    public StatPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new StatPieChartRenderer(this, mAnimator, mViewPortHandler);
    }
}
