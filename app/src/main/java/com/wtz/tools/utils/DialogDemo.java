package com.wtz.tools.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wtz.tools.R;

import java.util.Timer;
import java.util.TimerTask;

public class DialogDemo {

    public static void showCommonDialog(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.alert_circle)
                .setTitle("普通对话框")
                .setMessage("我是对话框的内容")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public static void showListDialog(final Context context) {
        final String items[] = {"我是Item一", "我是Item二", "我是Item三", "我是Item四"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.alert_circle)
                .setTitle("列表对话框")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, items[which], Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public static void showSingleOptionDialog(final Context context) {
        final String items[] = {"我是Item一", "我是Item二", "我是Item三", "我是Item四"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.alert_circle)
                .setTitle("单选列表对话框")
                .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, items[which], Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public static void showMultiOptionDialog(final Context context) {
        final String items[] = {"我是Item一", "我是Item二", "我是Item三", "我是Item四"};
        final boolean checkedItems[] = {true, false, true, false};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.alert_circle)
                .setTitle("多选对话框")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                Toast.makeText(context, "选中了" + i, Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }

                }).create();

        dialog.show();
    }

    public static void showHalfCustomDialog(final Context context) {
        final EditText editText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.alert_circle)
                .setTitle("半自定义对话框")
                .setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = editText.getText().toString();
                        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public static void showCustomDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.NormalDialogStyle);
        View view = View.inflate(context, R.layout.custom_dialog, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        dialog.setContentView(view);

        int[] screenSize = SystemInfoUtils.getScreenPixels(context);
        view.setMinimumHeight((int) (screenSize[1] * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (screenSize[0] * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public static void showCircleProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在加载中");
        dialog.show();
    }

    public static void showHorizontalProgressDialog(Context context) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("正在加载中");
        dialog.setMax(100);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int progress = 0;

            @Override
            public void run() {
                dialog.setProgress(progress += 5);
                if (progress >= 100) {
                    timer.cancel();
                }
            }
        }, 0, 1000);
        dialog.show();
    }

    /**
     * Android Support Library 23.2里的 Design Support Library新加了一个 Bottom Sheets 控件，
     * Bottom Sheets 顾名思义就是底部操作控件，用于在屏幕底部创建一个可滑动关闭的视图，
     * 包含 BottomSheets、BottomSheetDialog和 BottomSheetDialogFragment 三种，
     * 其中应用较多的控件是 BottomSheetDialog，主要运用在界面底部分享列表、评论列表等。
     * <p>
     * 问题：
     * 当我们向下滑动 BottomSheetDialog 隐藏 Dialog 后，无法用 bottomSheetDialog.show() 再次打开
     * <p>
     * 原因：
     * BottomSheetDialog 是基于 BottomSheetBehavior 封装的，当我们滑动隐藏了 View 后，
     * 内部会设置 BottomSheetBehavior 的状态为 STATE_HIDDEN，接着会关闭 Dialog，
     * 所以我们再次调用 show() 的时候 Dialog 没法打开状态为 HIDE 的 Dialog了。
     * <p>
     * 解决：
     * 在 BottomSheetCallback 回调方法中，onSlide 是拖拽的回调，onStateChanged 监听状态的改变:
     * STATE_HIDDEN: 隐藏状态。默认是false，可通过app:behavior_hideable属性设置。
     * STATE_COLLAPSED: 折叠关闭状态。可通过app:behavior_peekHeight来设置显示的高度,peekHeight默认是0。
     * STATE_DRAGGING: 被拖拽状态。
     * STATE_SETTLING: 拖拽松开之后到达终点位置（collapsed or expanded）前的状态。
     * STATE_EXPANDED: 完全展开的状态。
     * 在监听到关闭 BottomSheetDialog 后，把状态设置为 BottomSheetBehavior.STATE_COLLAPSED，就解决了调用 show()方法无法正常打开的问题。
     */
    public static void showBottomSheetDialog(Context context) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog, null);
        dialog.setContentView(view);//设置显示的内容，为了演示方便就直接把主布局设置进去了

        final BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(context.getResources().getDisplayMetrics().heightPixels);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        dialog.show();
    }

}
