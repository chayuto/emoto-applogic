package me.chayut.eMotoApp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class manageAds extends ActionBarActivity {

    eMotoCell myMotoCell = new eMotoCell();
    eMotoAdsCollection myAdsCollection = new eMotoAdsCollection();
    private ArrayList<String> myStringArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ads);

        Intent intent = getIntent();
        String token = intent.getExtras().getString("token");

        myMotoCell.deviceID = "00000000";
        myMotoCell.deviceLatitude = "-33.7238297";
        myMotoCell.deviceLongitude = "151.1220244";

        myAdsCollection.myEMotoCell = myMotoCell;
        myAdsCollection.token = token;

        new getAdsCollectionTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_ads, menu);
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

    private class getAdsCollectionTask extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(Object... prams) {
            try {
                myAdsCollection.getAdsCollection();
                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            //completion handler

           for(Map.Entry<String, eMotoAds> entry: myAdsCollection.map.entrySet())  {
               myStringArray.add(entry.getValue().AdsId());
               Log.d("Application*", entry.getKey());
            }

            fillListView();
        }
    }

    private void fillListView(){
        ListView listview = (ListView) findViewById(R.id.adsListView);

        ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, myStringArray);

        listview.setAdapter(codeLearnArrayAdapter);
    }

}
