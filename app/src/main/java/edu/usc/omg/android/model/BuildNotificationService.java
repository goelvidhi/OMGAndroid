package edu.usc.omg.android.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.usc.omg.android.R;
import edu.usc.omg.android.listener.NotificationReceiver;
import edu.usc.omg.android.net.HttpGetSurveyDataService;
import edu.usc.omg.android.ui.NotificationActivity;
import edu.usc.omg.android.util.NotificationBuilderUtil;

/*
Created by Vidhi on 05/16/2015.
This class is an Android service that is used to create the notification UI. It calls an IntentService that downloads the survey data.
 */

public class BuildNotificationService extends Service {

    private static String TAG = "OMG:BuildNotificationService";
    private String userName;
    private GetSurveyDataReceiver mSurveyDataReceiver;
    private String REQUEST_SURVEY_DATA_ID = "101";

    public static String PARAM_REQUEST_ID = "requestid";
    public static String PARAM_USERNAME = "username";
    public static String PARAM_URL = "url";
    public static String PARAM_RECEIVER = "receiver";
    public static String PARAM_SURVEY_DATA = "surveydata";

    public static int NOTIFICATION_ID = 1001;

    public static String ACTION_TAKE_SURVEY = "usc.edu.omg.action.take_survey";
    public static String ACTION_SNOOZE_SURVEY_AFTER_5 = "usc.edu.omg.action.snooze_survey_after_5";
    //public static String ACTION_SNOOZE_SURVEY_AFTER_15 = "usc.edu.omg.action.snooze_survey_after_15";
    public static String ACTION_CANCEL_SURVEY = "usc.edu.omg.action.cancel_survey";

    public static String EXTRA_SURVEY_ID = "survey_id";
    public static String EXTRA_PUSH_TYPE = "push_type";
    public static String EXTRA_PUSH_DURATION = "push_duration";

    public static String EXTRA_SURVEY_DATA = "push_survey_data";
    public BuildNotificationService() {
        logi(":: BuildNotificationService");
        mSurveyDataReceiver = new GetSurveyDataReceiver(new Handler());
    }

    public ResultReceiver getSurveyDataReceiver()
    {
        return mSurveyDataReceiver;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
            return null;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        logi(":: onCreate");
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     * Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}.
     * Do not call this method directly.
     * This method starts the IntentService to download the survey data
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isUserRegistered = isUserRegistered();
        userName = getUserEmail();
        String URL = "http://building-occupant-gateway.com/EE579Project/api/get_push_notification.php?email="
                + userName;


        Intent downloadAction = new Intent(this, HttpGetSurveyDataService.class);
        downloadAction.putExtra(PARAM_REQUEST_ID, REQUEST_SURVEY_DATA_ID);
        downloadAction.putExtra(PARAM_USERNAME, userName);
        downloadAction.putExtra(PARAM_URL, URL);
        downloadAction.putExtra(PARAM_RECEIVER, mSurveyDataReceiver);

        startService(downloadAction);

        return super.onStartCommand(intent, flags, startId);

    }

    /*
    * This method checks if the user is registered.
    * */
    private boolean isUserRegistered() {

        if(getUserEmail() != null && !"".equals(getUserEmail()))
        return true;

        return  false;
    }

    /*
    * This method reads the shared preferences to retrieve the registered email address for this user
    *
    */
    private String getUserEmail()
    {

        // TODO: Fetch user email dynamically
        return "vidhigoe@usc.edu";
    }



    class GetSurveyDataReceiver extends ResultReceiver {

        private String TAG = "OMG:BuildNotificationService:GetSurveyDataReceiver";
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public GetSurveyDataReceiver(Handler handler) {

            super(handler);
            logi(":: GetSurveyDataReceiver");
        }

        /**
         * Override to receive results delivered to this object.
         *
         * @param resultCode Result code containing the survey data delivered by the sender.         *
         * @param resultData Additional data like URL, Username etc. provided by the sender.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            logi(":: onReceiveResult");

            switch(resultCode)
            {
                case HttpGetSurveyDataService.STATUS_RUNNING:
                    break;
                case HttpGetSurveyDataService.STATUS_FINISHED:
                    buildNotification(resultData);
                    break;
                case HttpGetSurveyDataService.STATUS_ERROR:
                    break;
                default:

            }
        }

        /**
         * Deliver a result to this receiver.  Will call {@link #onReceiveResult},
         * always asynchronously if the receiver has supplied a Handler in which
         * to dispatch the result.
         *
         * @param resultCode Arbitrary result code to deliver, as defined by you.
         * @param resultData Any additional data provided by you.
         */
        @Override
        public void send(int resultCode, Bundle resultData) {
            super.send(resultCode, resultData);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
        }

        /*
        Method for printing the log messages
         */
        private void logi(String msg)
        {
            Log.i(TAG, msg);
        }
    }


    /*
    This method creates the notification based on the result data received from the server (Survey_id and push_type
     */
    private void buildNotification(Bundle resultData)
    {
        String results = resultData.getString(PARAM_SURVEY_DATA);
        logi("received data " + results);
        List<String> surveyData = parseJSONObject(results);

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SURVEY_ID, Integer.parseInt(surveyData.get(0)));
        bundle.putInt(EXTRA_PUSH_TYPE, Integer.parseInt(surveyData.get(1)));
        bundle.putInt(EXTRA_PUSH_DURATION, Integer.parseInt(surveyData.get(2)) );

        NotificationBuilderUtil.showNotification(this, bundle);


        // SurveyID = 0 is default survey (handled directly in MainActivity
       // if(surveyId != 0)
        //{


//        }

   }

    private List parseJSONObject(String results) {

        ArrayList<String> list = new ArrayList<String>();
        try{
            JSONObject jsonObject = new JSONObject(results);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            String survey_id = dataObject.getString("push_survey_id");
            String push_type = dataObject.getString("push_type");
            String push_duration = dataObject.getString("push_duration");

            list.add(survey_id);
            list.add(push_type);
            list.add(push_duration);

        }
        catch(JSONException e) {
            loge(e.getMessage().toString());
        }
        return list;

    }

    /*
        Method for printing the log messages
         */
    private void logi(String msg)
    {
        Log.i(TAG, msg);
    }

    private void loge(String msg)
    {
        Log.e(TAG, msg);
    }

}
