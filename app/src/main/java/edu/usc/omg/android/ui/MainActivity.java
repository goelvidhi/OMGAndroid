package edu.usc.omg.android.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import edu.usc.omg.android.R;
import edu.usc.omg.android.model.BuildNotificationService;


public class MainActivity extends ActionBarActivity {

    private String TAG = "OMG:MainActivity";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        logi(":: onCreate");
        int surveyId = bundle.getInt(BuildNotificationService.EXTRA_SURVEY_ID, -1);

        logi("surveyID = " + surveyId);
        /*if(surveyId != -1)
        {
            if(surveyId == 74)
            setContentView(R.layout.view_survey_button);
        }
        else
        {*/
            setContentView(R.layout.activity_main);
      //  }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
