package com.wtz.tools.Span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class WrapTextBgColorSpan extends ReplacementSpan {
    private int mWidth;
    private int mBackgroundColor;

    public WrapTextBgColorSpan(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        int textColor = paint.getColor();
        if (mBackgroundColor != 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mBackgroundColor);
            bottom = (int) (y + paint.getFontMetrics().bottom);
            canvas.drawRect(x, top, x + mWidth, bottom, paint);
        }
        paint.setColor(textColor);
        canvas.drawText(text, start,end, x, y, paint);
    }
}
