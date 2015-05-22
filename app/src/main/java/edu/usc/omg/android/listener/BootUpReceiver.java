package edu.usc.omg.android.listener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/*
Created by Vidhi on 05/16/2015.
This class listens to the Android Boot Completed action. It invokes the AlarmReceiver after every 10 seconds.
*
*/
public class BootUpReceiver extends BroadcastReceiver {

    private static String TAG = "OMG:BootUpReceiver";
    private long timeInterval = 60*1000;
    public BootUpReceiver() {
    }

    // This method is called when the BroadcastReceiver is receiving
    // an Intent broadcast.
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            logi("Boot Completed");
            Intent alarmAction = new Intent("usc.edu.omg.action.alarm_receiver");
            PendingIntent pendingAlarm = PendingIntent.getBroadcast(context, 0, alarmAction, 0);
            long firstTime = SystemClock.elapsedRealtime();

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, timeInterval, pendingAlarm);

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
