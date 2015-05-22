package edu.usc.omg.android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import edu.usc.omg.android.R;
import edu.usc.omg.android.listener.NotificationReceiver;
import edu.usc.omg.android.model.BuildNotificationService;

/**
 * Created by Vidhi on 5/20/2015.
 */
public class NotificationBuilderUtil {

    private static Context mContext;
    private static String TAG = "NotificationBuilderUtil";

    public static void showNotification(Context context, Bundle bundle)
    {
        mContext = context;

        int surveyId = bundle.getInt(BuildNotificationService.EXTRA_SURVEY_ID);
        int pushType = bundle.getInt(BuildNotificationService.EXTRA_PUSH_TYPE);
        int pushDuration = bundle.getInt(BuildNotificationService.EXTRA_PUSH_DURATION);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;

        Notification mNotification = null;
        if(pushType == 1) {
            mNotification = buildRingerNotification(bundle);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        }
        else if(pushType == 2)
        {
            mNotification = buildVibrateNotification(bundle);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        }
        else if(pushType == 3)
        {

            mNotification = buildScreenOnNotification(bundle);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        }
        if(wl != null)
            wl.acquire(3000);

        if(mNotification != null)
            notificationManager.notify(BuildNotificationService.NOTIFICATION_ID, mNotification);
        if(wl != null)
        {
            wl.release();
            wl = null;
        }


    }


    private static PendingIntent getPendingAction(Context context, String action, Bundle bundle) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, NotificationReceiver.class);

        intent.putExtra(BuildNotificationService.EXTRA_SURVEY_DATA, bundle);
        intent.setAction(action);

        logi("set action : " + action);

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    private static Notification buildRingerNotification(Bundle bundle) {


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(mContext.getResources().getString(R.string.notification_title))
                .setContentText(mContext.getResources().getString(R.string.notification_text))
                .setContentIntent(getPendingAction(mContext, BuildNotificationService.ACTION_TAKE_SURVEY, bundle))
                .setSmallIcon(R.drawable.ic_notif)
                .setSound(soundUri)
                .addAction(R.drawable.ic_notif, "Snooze", getPendingAction(mContext, BuildNotificationService.ACTION_SNOOZE_SURVEY_AFTER_5, bundle))
                .addAction(R.drawable.ic_notif, "Cancel", getPendingAction(mContext, BuildNotificationService.ACTION_CANCEL_SURVEY, bundle))
                .build();

        return notification;
    }

    private static Notification buildVibrateNotification(Bundle bundle) {

        long[] pattern = {0, 1000, 500};
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(mContext.getResources().getString(R.string.notification_title))
                .setContentText(mContext.getResources().getString(R.string.notification_text))
                .setContentIntent(getPendingAction(mContext, BuildNotificationService.ACTION_TAKE_SURVEY, bundle))
                .setSmallIcon(R.drawable.ic_notif)
                .setVibrate(pattern)
                .addAction(R.drawable.ic_notif, "Snooze", getPendingAction(mContext, BuildNotificationService.ACTION_SNOOZE_SURVEY_AFTER_5, bundle))
                .addAction(R.drawable.ic_notif, "Cancel", getPendingAction(mContext, BuildNotificationService.ACTION_CANCEL_SURVEY, bundle))
                .build();

        return notification;
    }

    private static Notification buildScreenOnNotification(Bundle bundle) {
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(mContext.getResources().getString(R.string.notification_title))
                .setContentText(mContext.getResources().getString(R.string.notification_text))
                .setContentIntent(getPendingAction(mContext, BuildNotificationService.ACTION_TAKE_SURVEY, bundle))
                .setSmallIcon(R.drawable.ic_notif)
                .addAction(R.drawable.ic_notif, "Snooze", getPendingAction(mContext, BuildNotificationService.ACTION_SNOOZE_SURVEY_AFTER_5, bundle))
                .addAction(R.drawable.ic_notif, "Cancel", getPendingAction(mContext, BuildNotificationService.ACTION_CANCEL_SURVEY, bundle))
                .build();


        return notification;
    }

    /*
    Method for printing the log messages
     */
    private static void logi(String msg)
    {
        Log.i(TAG, msg);
    }

    private static void loge(String msg)
    {
        Log.e(TAG, msg);
    }

}
