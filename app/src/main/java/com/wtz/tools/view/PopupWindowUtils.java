package com.wtz.tools.view;

import android.view.View;
import android.widget.PopupWindow;

/**
 * 不适合使用 LayoutParams.WRAP_CONTENT 和 LayoutParams.MATCH_PARENT 的 PopupWindow
 * 因为调用时拿不到真实的宽高
 */
public class PopupWindowUtils {

    public static void show(View anchor, PopupWindow window, AnchorGravity gravity, int x_off, int y_off) {
        int[] gravityOffset = getGravityOffset(anchor, window, gravity);
        int x = gravityOffset[0] + x_off;
        int y = gravityOffset[1] + y_off;
        System.out.println("PopupWindowUtils show x=" + x + ",y=" + y);
        window.showAsDropDown(anchor, x, y);
    }

    private static int[] getGravityOffset(View anchor, PopupWindow window, AnchorGravity gravity) {
        int anchWidth = anchor.getWidth();
        int anchHeight = anchor.getHeight();

        int winWidth = window.getWidth();
        int winHeight = window.getHeight();
        View view = window.getContentView();
        if (winWidth <= 0)
            winWidth = view.getWidth();
        if (winHeight <= 0)
            winHeight = view.getHeight();

        System.out.println("PopupWindowUtils anchWidth=" + anchWidth + ",anchHeight=" + anchHeight
                + winWidth + ",winHeight=" + winHeight);

        int xoff = 0;
        int yoff = 0;

        switch (gravity.getHorizontalParam()) {
            case AnchorGravity.ALIGN_LEFT:
                xoff = 0;
                break;
            case AnchorGravity.ALIGN_RIGHT:
                xoff = anchWidth - winWidth;
                break;
            case AnchorGravity.TO_LEFT:
                xoff = -winWidth;
                break;
            case AnchorGravity.TO_RIGHT:
                xoff = anchWidth;
                break;
            case AnchorGravity.HORIZONTAL_CENTER:
                xoff = (anchWidth - winWidth) / 2;
                break;
            default:
                break;
        }
        switch (gravity.getVerticalParam()) {
            case AnchorGravity.ALIGN_TOP:
                yoff = -anchHeight;
                break;
            case AnchorGravity.ALIGN_BOTTOM:
                yoff = -winHeight;
                break;
            case AnchorGravity.TO_TOP:
                yoff = -anchHeight - winHeight;
                break;
            case AnchorGravity.TO_BOTTOM:
                yoff = 0;
                break;
            case AnchorGravity.VERTICAL_CENTER:
                yoff = (-winHeight - anchHeight) / 2;
                break;
            default:
                break;
        }
        return new int[]{xoff, yoff};
    }

    public static class AnchorGravity {

        private int mGravity;

        private static final int HORIZONTAL_MASK = 0x1F;
        private static final int HORIZONTAL_START = 0x1;
        private static final int HORIZONTAL_END = HORIZONTAL_START << 4;
        public static final int ALIGN_LEFT = HORIZONTAL_START;
        public static final int ALIGN_RIGHT = HORIZONTAL_START << 1;
        public static final int TO_LEFT = HORIZONTAL_START << 2;
        public static final int TO_RIGHT = HORIZONTAL_START << 3;
        public static final int HORIZONTAL_CENTER = HORIZONTAL_START << 4;

        private static final int VERTICAL_MASK = 0x3E0;
        private static final int VERTICAL_START = 0x20;
        private static final int VERTICAL_END = VERTICAL_START << 4;
        public static final int ALIGN_TOP = VERTICAL_START;
        public static final int ALIGN_BOTTOM = VERTICAL_START << 1;
        public static final int TO_TOP = VERTICAL_START << 2;
        public static final int TO_BOTTOM = VERTICAL_START << 3;
        public static final int VERTICAL_CENTER = VERTICAL_START << 4;


        public AnchorGravity(int gravity) {
            setGravity(gravity);
        }

        public void setGravity(int gravity) {
            int h_in = gravity & HORIZONTAL_MASK;
            int h_out = ALIGN_LEFT;
            for (int i = HORIZONTAL_START; i <= HORIZONTAL_END; i = i << 1) {
                if ((i & h_in) > 0) {
                    h_out = i;
                    break;
                }
            }

            int v_in = gravity & VERTICAL_MASK;
            int v_out = ALIGN_BOTTOM;
            for (int j = VERTICAL_START; j <= VERTICAL_END; j = j << 1) {
                if ((j & v_in) > 0) {
                    v_out = j;
                    break;
                }
            }

            mGravity = h_out | v_out;
        }

        public int getHorizontalParam() {
            return mGravity & HORIZONTAL_MASK;
        }

        public int getVerticalParam() {
            return mGravity & VERTICAL_MASK;
        }

    }

}
