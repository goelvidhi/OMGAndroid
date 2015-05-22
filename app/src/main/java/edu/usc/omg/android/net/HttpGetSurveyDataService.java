package edu.usc.omg.android.net;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.usc.omg.android.model.BuildNotificationService;

/**
 * Created by Vidhi on 05/16/2015
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class HttpGetSurveyDataService extends IntentService {
    private static String TAG = "OMG:HttpGetSurveyDataService";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private String surveyData;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_SURVEY = "edu.usc.omg.android.net.action.get_survey";
    private static final String ACTION_BAZ = "edu.usc.omg.android.net.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "edu.usc.omg.android.net.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "edu.usc.omg.android.net.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetSurvey(Context context, String param1, String param2) {
        Intent intent = new Intent(context, HttpGetSurveyDataService.class);
       //  intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, HttpGetSurveyDataService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public HttpGetSurveyDataService() {
        super("HttpGetSurveyDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

                final String requestId = intent.getStringExtra(BuildNotificationService.PARAM_REQUEST_ID);
                final String username = intent.getStringExtra(BuildNotificationService.PARAM_USERNAME);
                final String url = intent.getStringExtra(BuildNotificationService.PARAM_URL);
                final ResultReceiver receiver = intent.getParcelableExtra(BuildNotificationService.PARAM_RECEIVER);

                if("101".equals(requestId)) {
                    handleActionGetSurvey(username, url, receiver);
                }

        }
    }

    /**
     * Handle action Get Survey in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetSurvey(String username, String url, ResultReceiver receiver) {
        logi("::handleActionGetSurvey");
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        HttpURLConnection httpURLConnection = null;
        Bundle bundle = new Bundle();
        try
        {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            surveyData = readInputStream(in);
            logi("Received data from the server " + surveyData);
            bundle.putString(BuildNotificationService.PARAM_SURVEY_DATA, surveyData);
            receiver.send(STATUS_FINISHED, bundle);

        }
        catch (MalformedURLException e)
        {
            loge(e.getMessage().toString());
        }
        catch (IOException e)
        {
            loge(e.getMessage().toString());
        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    This method will read the data from the input stream of http connection
     */
    private String readInputStream(InputStream in)
    {

        BufferedReader bufferedReader = null;
        StringBuffer sb = new StringBuffer();
        String line = "";
        try
        {
        bufferedReader = new BufferedReader(new InputStreamReader(in));

            while((line = bufferedReader.readLine()) != null)
                sb.append(line);
        }
        catch(IOException e)
        {
        loge(e.getMessage().toString());
        }
        finally {
            if(bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    loge(e.getMessage().toString());
                }
              }
            }

        return sb.toString();
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
