package com.stkj.cashier.stat.ui.weight;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.common.log.LogHelper;

/**
 * 统计页PieChartRenderer
 */
public class StatPieChartRenderer extends PieChartRenderer {
    public StatPieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawEntryLabel(Canvas c, String label, float x, float y) {
        Paint paintEntryLabels = getPaintEntryLabels();
        int consumeLayRes = DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes();
        if (consumeLayRes == 1) {
            paintEntryLabels.setTextSize(Utils.convertDpToPixel(8f));
        }
        if (paintEntryLabels != null) {
            String[] lines = label.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    paintEntryLabels.setColor(Color.parseColor("#000000"));
                } else {
                    paintEntryLabels.setColor(Color.parseColor("#666666"));
                }
                c.drawText(lines[i], x, y, paintEntryLabels);
                y += paintEntryLabels.descent() - paintEntryLabels.ascent();
            }
        }
    }
}
