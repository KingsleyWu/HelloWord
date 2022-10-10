package com.kingsley.helloword.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

/**
 * 给每个文字添加方框
 */

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private final int mRadius;
    private final int bgColor;
    private final int textColor;
    private int mSize;

    public RoundBackgroundColorSpan(int bgColor,
                                    int textColor,
                                    int radius) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.mRadius = radius;
    }

    @Override
    public int getSize(@NonNull Paint paint,
                       CharSequence text,
                       int start,
                       int end,
                       Paint.FontMetricsInt fm) {
        mSize = (int) (paint.measureText(text, start, end) + 2 * mRadius);
        return 66;//文字之间的间距
    }

    @Override
    public void draw(@NonNull Canvas canvas,
                     CharSequence text,
                     int start,
                     int end,
                     float x,
                     int top,
                     int y, int bottom,
                     @NonNull Paint paint) {
        int defaultColor = paint.getColor();//保存文字颜色
        float defaultStrokeWidth = paint.getStrokeWidth();

        //绘制圆角矩形
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);

        float h = paint.descent() - paint.ascent();//文字高度
        float size = (mSize - h) / 2;//框的位置

        RectF rectF = new RectF(x + size, y + paint.ascent(), x + mSize - size, y + paint.descent());
        //设置文字背景矩形，x为span起始左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
        // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(rectF, mRadius, mRadius, paint);

        //绘制文字
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(defaultStrokeWidth);
        canvas.drawText(text, start, end, x + mRadius, y, paint);//此处mRadius为文字右移距离

        paint.setColor(defaultColor);//恢复画笔的文字颜色
    }
}
