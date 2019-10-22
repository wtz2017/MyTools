package com.wtz.tools.view.custom_view_study;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * https://github.com/GcsSloop/ViewSupport/blob/master/Library/src/main/java/com/gcssloop/view/utils/CanvasUtils.java
 */
public class CanvasUtils {


    private CanvasUtils() {
    }

    /**
     * Draw a straight line through the points.
     */
    public static void drawLine(Point p1, Point p2, Canvas canvas, Paint paint) {
        canvas.save();
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        canvas.restore();
    }

    /**
     * Draw a straight line through the points
     */
    public static void drawLine(PointF p1, PointF p2, Canvas canvas, Paint paint) {
        canvas.save();
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        canvas.restore();
    }

}

