package com.wtz.tools.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.wtz.tools.R;

public class ToastUtils {

    public static void showTopToast(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        int yOffset = context.getResources().getDimensionPixelOffset(R.dimen.dp_30);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yOffset);
        toast.show();
    }

}
