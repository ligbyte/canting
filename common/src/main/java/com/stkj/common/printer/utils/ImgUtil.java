package com.stkj.common.printer.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import com.stkj.common.printer.bean.Dish;
import java.util.ArrayList;

public class ImgUtil {

    private static final String TAG = "ImgUtil";

    /**
     * 获取文本宽度
     * @param paint Paint
     * @param text String
     * @return Int
     */
    public static int getFontWidth(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    /**
     * 判断菜品需要显示几行
     *
     * @param paint Paint
     * @param text String
     * @return Int
     */
    public static int getFontHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, 1, rect);
        return rect.height();
    }

    /**
     * 获取打印图片的菜品需要打印的高度
     *
     * @param list 菜品列表
     * @param normalFontHeight 正常字体高度
     * @param perLineDistance 行与行之间的距离
     * @param toKitchen 是否发送到厨房
     * @return 菜品需要打印的高度
     */
    public static int getProBmpHeight(ArrayList<Dish> list, int normalFontHeight, int perLineDistance, boolean toKitchen) {
        int h = 0; // 总高度
        for (int i = 0; i < list.size(); i++) {
            // 根据菜品文本长度显示多行
            int lines = getShowLine(list.get(i).getName());
            for (int j = 0; j < lines; j++) {
                h += normalFontHeight + perLineDistance;
            }

            // 如果是发送到厨房的需要计算备注的高度
            if (toKitchen) {
                // 显示备注
                lines = getShowLine(list.get(i).getNote());
                for (int j = 0; j < lines; j++) {
                    h += normalFontHeight + perLineDistance;
                }
            }
            h += perLineDistance; // 菜品之间距离大点，方便区分
        }
        return h;
    }

    /**
     * 计算菜品需要显示的行数
     *
     * @param dish 菜品名称
     * @return 菜品需要显示的行数
     */
    public static int getShowLine(String dish) {
        int line = 1; // 默认显示一行

        // 如果菜品名称长度超过12个字符，需要计算显示的行数
        if (dish.length() > 12) {
            line = dish.length() % 12 == 0 ? dish.length() / 12 : dish.length() / 12 + 1;
        }

        return line;
    }

    /**
     * 截取每行需要显示的菜品文本
     *
     * @param dishName 菜品名称
     * @param lineIndex 行索引
     * @param lines 总行数
     * @return 每行需要显示的菜品文本
     */
    public static String getDisplayTextForLine(String dishName, int lineIndex, int lines) {
        String subStr;
        if (lines == 1) {
            subStr = dishName;
        } else if (lineIndex != lines - 1 || dishName.length() % 12 == 0) {
            subStr = dishName.substring(lineIndex * 12, (lineIndex + 1) * 12);
        } else {
            subStr = dishName.substring(lineIndex * 12);
        }
        return subStr;
    }

    /**
     * 把pos端小票信息转成图片
     *
     * @return
     */
    public static Bitmap generatePosReceiptImage(ArrayList<Dish> pros) {
        Paint paint = new Paint();
        paint.setTextSize(48f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setHinting(Paint.ANTI_ALIAS_FLAG);

        String[] restaurantInfo = new String[]{
            "盛图食堂",
            "电话：16639439562",
            "日期: 2025/02/11   08:50",
            "区域: 大厅",
            "桌位: A04",
            "上座人数: 5人",
            "服务员: 李军",
            "订单号: 4884977449494949494"
        };

        int restaurantNameHeight = getFontHeight(paint, "盛图食堂");
        paint.setTextSize(32f);
        int blackFontHeight = getFontHeight(paint, "盛图食堂");
        paint.setTypeface(Typeface.DEFAULT);
        int normalFontHeight = getFontHeight(paint, "盛图食堂");

        Log.d(TAG, "blackFontHeight:" + blackFontHeight + " ,normalFontHeight:" + normalFontHeight);

        int perLineDistance = 28;
        int perFontDistance = 28;
        // 小票宽度
        int receiptCanvasWidth = 432;
        int topBlank = 64;
        // 订单信息与店铺名称之间的间距
        int restaurantOrderSpacing = 100;
        // 店铺名高度+标准字号高度+行间距
        int receiptCanvasHeadHeight = topBlank + restaurantNameHeight + normalFontHeight + perLineDistance + restaurantOrderSpacing + (restaurantInfo.length - 3) * (normalFontHeight + perLineDistance);

        // 底部留白（可能要撕纸，需要留白）
        int bottomBlank = 200;
        int welcomeMsgDistance = 100;
        // 小票菜单标题栏高度+总计金额
        int receiptCanvasBottomHeight = perFontDistance * 2 + welcomeMsgDistance + normalFontHeight * 1 + bottomBlank;
        // 小票高度
        int receiptCanvasHeight = receiptCanvasHeadHeight + receiptCanvasBottomHeight + getProBmpHeight(pros, normalFontHeight, perLineDistance, false);

        // 创建小票图片
        Bitmap bmp = Bitmap.createBitmap(receiptCanvasWidth, receiptCanvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.WHITE);

        int textWidth;
        int y = restaurantNameHeight + topBlank;
        float x;
        for (int index = 0; index < restaurantInfo.length; index++) {
            String str = restaurantInfo[index];
            switch (index) {
                case 0:
                    paint.setTextSize(48f);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                case 1:
                    paint.setTextSize(28f);
                    paint.setTypeface(Typeface.DEFAULT);
                    y += normalFontHeight + perLineDistance;
                    break;
                case 2:
                    paint.setTextSize(28f);
                    y += restaurantOrderSpacing;
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    y += blackFontHeight + perLineDistance;
                    break;
                default:
                    paint.setTypeface(Typeface.DEFAULT);
                    break;
            }

            textWidth = getFontWidth(paint, str);
           switch (index) {
                case 0 :
               case  1 :
                    x = (receiptCanvasWidth / 2 - textWidth / 2);

                case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
                    x =  16f;

                default :  x = receiptCanvasWidth - textWidth - 50f - 16f;
            };
            canvas.drawText(str, x, y, paint);
        }

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setStrokeWidth(4f);
        // 画线
        y += perLineDistance;
        canvas.drawLine(0f, y, receiptCanvasWidth, y, paint);

        // title(菜品、单价、数量、小计)
        y += perLineDistance * 2;
        canvas.drawText("菜品", 16f, y, paint);
        canvas.drawText("单价", receiptCanvasWidth - getFontWidth(paint, "小计") - 50 - 100 - 100, y, paint);
        canvas.drawText("数量", receiptCanvasWidth - getFontWidth(paint, "小计") - 50 - 100, y, paint);
        canvas.drawText("小计", receiptCanvasWidth - getFontWidth(paint, "小计") - 50, y, paint);
        // 画线
        y += blackFontHeight;
        canvas.drawLine(0f, y, receiptCanvasWidth, y, paint);
        paint.setTypeface(Typeface.DEFAULT);
        y += perLineDistance * 2;
        // 下面是画菜品文本
        for (int i = 0; i < pros.size(); i++) {
            // 先画后面的值，菜品名称后面画，因为菜品名称可能多行显示，以菜品显示高度为准；
            canvas.drawText("￥" + pros.get(i).getPrice(), receiptCanvasWidth - getFontWidth(paint, "小计") - 50 - 100 - 100, y, paint);
            canvas.drawText(String.valueOf(pros.get(i).getCount()), receiptCanvasWidth - getFontWidth(paint, "小计") - 50 - 100, y, paint);
            canvas.drawText("￥ " + pros.get(i).getPrice() * pros.get(i).getCount(), receiptCanvasWidth - getFontWidth(paint, "小计") - 50, y, paint);
            String dish;
            // 根据菜品文本长度显示多行
            int lines = getShowLine(pros.get(i).getName());
            for (int j = 0; j < lines; j++) {
                dish = getDisplayTextForLine(pros.get(i).getName(), j, lines);
                canvas.drawText(dish, 16f, y, paint);
                y += normalFontHeight + perLineDistance;
            }
            y += perLineDistance;
        }
        // 画线
        canvas.drawLine(0f, y, receiptCanvasWidth, y, paint);

        paint.setTextSize(25f);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        y += blackFontHeight + perFontDistance;
        canvas.drawText("最终实收", 16f, y, paint);
        canvas.drawText("￥78", receiptCanvasWidth - getFontWidth(paint, "￥20000") - 50, y, paint);

        paint.setTextSize(25f);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        y += welcomeMsgDistance;
        String welcomeMsg = "谢谢光临,欢迎下次再来";
        textWidth = getFontWidth(paint, welcomeMsg);
        canvas.drawText(welcomeMsg, receiptCanvasWidth / 2 - textWidth / 2, y, paint);

        y += normalFontHeight;
        welcomeMsg = "@洛阳盛图科技有限公司提供技术支持";
        textWidth = getFontWidth(paint, welcomeMsg);
        canvas.drawText(welcomeMsg, receiptCanvasWidth / 2 - textWidth / 2, y, paint);

        y += bottomBlank;

        return bmp;
    }
}
