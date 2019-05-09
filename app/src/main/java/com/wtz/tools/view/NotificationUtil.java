package com.wtz.tools.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.wtz.tools.R;
import com.wtz.tools.test.MainActivity;

/**
 * https://blog.csdn.net/dsc114/article/details/51721472
 */
public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;

    public NotificationUtil(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 普通的Notification
     */
    public void postStandardNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("new message")// 第一次提示消失的时候显示在通知栏上的
                .setSmallIcon(R.mipmap.ic_launcher)// 设置图标
                .setContentTitle("ContentTitle")// 设置通知的标题
                .setContentText("ContentText")// 设置通知的内容
                .setNumber(20)
                .setWhen(System.currentTimeMillis())// 设置显示通知的时间，不设置默认获取系统时间
                .setAutoCancel(true)// 设置为true，点击该条通知会自动删除，false时只能通过滑动来删除
                .setOngoing(true);// 设置是否为一个正在进行中的通知，这一类型的通知将无法删除


        setDefaultPendingIntent(builder);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;  //只有全部清除时，Notification才会清除
        notificationManager.notify(0, notification);
    }

    /**
     * 使用下载的Notification,在4.0以后才能使用<p></p>
     * Notification.Builder类中提供一个setProgress(int max,int progress,boolean indeterminate)方法用于设置进度条，
     * max用于设定进度的最大数，progress用于设定当前的进度，indeterminate用于设定是否是一个确定进度的进度条。
     * 通过indeterminate的设置，可以实现两种不同样式的进度条，一种是有进度刻度的（true）,一种是循环流动的（false）。
     */
    public void postDownloadNotification() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("showProgressNotification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ContentTitle")
                .setContentText("ContentText")
                .setContentInfo("contentInfo")
                .setOngoing(true);

        setDefaultPendingIntent(builder);

        // 模拟下载过程
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress;
                for (progress = 0; progress < 100; progress += 5) {
                    // 将setProgress的第三个参数设为true即可显示为无明确进度的进度条样式
                    builder.setProgress(100, progress, false);
                    notificationManager.notify(0, builder.build());
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("sleep failure");
                    }
                }
                builder.setContentTitle("Download complete")
                        .setProgress(0, 0, false)
                        .setOngoing(false);
                notificationManager.notify(0, builder.build());
            }
        }).start();
    }

    /**
     * 大视图通知在4.1以后(api>=16)才能使用，BigTextStyle
     */
    public void postBigTextNotification() {
        NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
        textStyle.setBigContentTitle("BigContentTitle")
                .setSummaryText("SummaryText")
                .bigText("This is bigText!!!This is bigText!!!This is bigText!!!This is bigText!!!This is bigText!!!This is bigText!!!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("BigTextStyle")
                .setSmallIcon(R.mipmap.ic_launcher)// 小图标，5.0之后 大小图标会叠加在一起
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))// 大图标
                .setContentInfo("contentInfo")
                .setStyle(textStyle)
                .setAutoCancel(true);

        setDefaultPendingIntent(builder);

        notificationManager.notify(0, builder.build());
    }

    /**
     * 大布局通知在4.1以后(api>=16)才能使用，BigPictureStyle
     */
    public void postBigPictureNotification() {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSummaryText("SummaryText");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("BigPictureStyle")
                .setSmallIcon(R.mipmap.ic_launcher)// 小图标，5.0之后 大小图标会叠加在一起
                .setContentInfo("contentInfo")
                .setStyle(bigPictureStyle)
                .setAutoCancel(true);

        setDefaultPendingIntent(builder);

        notificationManager.notify(0, builder.build());
    }

    /**
     * 大布局通知在4.1以后(api>=16)才能使用，InboxStyle
     */
    public void postInboxNotification() {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("BigContentTitle")
                .setSummaryText("SummaryText");
        for (int i = 0; i < 10; i++) {
            inboxStyle.addLine("Line-" + i);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("InboxStyle")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentInfo("contentInfo")
                .setStyle(inboxStyle)
                .setAutoCancel(true);

        setDefaultPendingIntent(builder);

        notificationManager.notify(0, builder.build());
    }

    /**
     * 自定义通知
     */
    public void postCustomNotification() {
        RemoteViews contentViews = new RemoteViews(context.getPackageName(),
                R.layout.notice_remote_view);
        contentViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);
        contentViews.setTextViewText(R.id.tv_title, "自定义通知标题");
        contentViews.setTextViewText(R.id.tv_content, "自定义通知内容");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentViews.setOnClickPendingIntent(R.id.tv_title, pendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("custom ticker")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(contentViews)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    private void setDefaultPendingIntent(NotificationCompat.Builder builder) {
        Intent intent = new Intent(context, MainActivity.class);  //需要跳转指定的页面
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
    }

    public void cancelById() {
        // id对应NotificationManager.notify(id,notification)第一个参数
        notificationManager.cancel(0);
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}