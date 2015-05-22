package edu.usc.omg.android.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import edu.usc.omg.android.model.BuildNotificationService;

/* Created by Vidhi on 05/16/2015.
This class listens to a scheduled alarm of 10 seconds. It calls Notification module for subjective data immediately.
* It invokes Sensor module for contextual data in every 10th iteration
* */
public class AlarmReceiver extends BroadcastReceiver {

    private static String TAG = "OMG:AlarmReceiver";
    long previous = 0;
    private Context mContext;

    public AlarmReceiver() {
    }
/*This method is called when the BroadcastReceiver is receiving
an Intent broadcast.*/
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        if("usc.edu.omg.action.alarm_receiver".equals(intent.getAction()))
        {
            long now = SystemClock.currentThreadTimeMillis();
            logi("now is" + now);
            logi("previous is" + previous);
            logi("Alarm received after " + (now - previous));
            invokeNotificationModule();
            //invokeSensorModuleDelayed();
            previous = now;
        }

    }

/*
This method invokes the Pull Notification module immediately to pull the subjective survey from the server and display the same on UI.
* */
    private void invokeNotificationModule()
    {

    Intent notificationAction = new Intent();
    notificationAction.setClass(mContext, BuildNotificationService.class);
    // TODO: check if service is not already started
    mContext.startService(notificationAction);

    }

    private void invokeSensorModuleDelayed(long time)
    {

    }

    /*
    Method for printing the log messages
     */
    private void logi(String msg)
    {
        Log.i(TAG, msg);
    }
}
