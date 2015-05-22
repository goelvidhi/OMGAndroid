package edu.usc.omg.android.listener;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import edu.usc.omg.android.model.BuildNotificationService;
import edu.usc.omg.android.ui.MainActivity;
import edu.usc.omg.android.util.NotificationBuilderUtil;

/* Created by Vidhi on 05/20/2015.
* This class listens to Notification actions when a new notification is received.
* It listens to take_survey, snooze_survey_after_5 and cancel_survey actions
* */

 public class NotificationReceiver extends BroadcastReceiver {

     private static String TAG = "OMG:NotificationReceiver";
     private long snoozeInterval = 5*60*1000;

    public NotificationReceiver() {
    }

    /*This method is called when the BroadcastReceiver is receiving
     an Intent broadcast.
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
    logi(":: onReceive, action = " + intent.getAction());

     final Bundle surveyDataBundle = intent.getBundleExtra(BuildNotificationService.EXTRA_SURVEY_DATA);
       int surveyId = surveyDataBundle.getInt(BuildNotificationService.EXTRA_SURVEY_ID);

    if(intent.getAction().equals(BuildNotificationService.ACTION_TAKE_SURVEY))
    {
        Intent intentUI = new Intent(context, MainActivity.class);
        intentUI.putExtra(BuildNotificationService.EXTRA_SURVEY_DATA, surveyDataBundle);
        context.startActivity(intentUI);
    }
    else if(intent.getAction().equals(BuildNotificationService.ACTION_SNOOZE_SURVEY_AFTER_5))
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationBuilderUtil.showNotification(context, surveyDataBundle);
            }
        }, snoozeInterval);

    }
    else if(intent.getAction().equals(BuildNotificationService.ACTION_CANCEL_SURVEY))
    {

    }

    }

     /*
Method for printing the log messages
 */
     private void logi(String msg)
     {
         Log.i(TAG, msg);
     }


}
