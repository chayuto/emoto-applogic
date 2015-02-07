package me.chayut.eMotoApp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import me.chayut.eMotoLogic.eMotoAds;
import me.chayut.eMotoLogic.eMotoAdsArrayAdapter;
import me.chayut.eMotoLogic.eMotoAdsCollection;
import me.chayut.eMotoLogic.eMotoCell;
import me.chayut.eMotoLogic.eMotoLoginResponse;
import me.chayut.eMotoLogic.eMotoService;


public class manageAds extends ActionBarActivity {

    eMotoCell myMotoCell = new eMotoCell();
    eMotoAdsCollection myAdsCollection = new eMotoAdsCollection();
    eMotoLoginResponse mLoginResponse;

    ServiceResponseReceiver mServiceResponseReceiver;

    //ads array for ListView
    private ArrayList<eMotoAds> adsArray = new ArrayList<eMotoAds>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ads);

        Intent intent = getIntent();
        mLoginResponse = intent.getExtras().getParcelable("eMotoLoginResponse");

         /*
         * Creates an intent filter for DownloadStateReceiver that intercepts broadcast Intents
         */

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                eMotoService.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mServiceResponseReceiver = new ServiceResponseReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mServiceResponseReceiver,
                statusIntentFilter);



        myMotoCell.deviceID = "00000000";
        myMotoCell.deviceLatitude = "-33.7238297";
        myMotoCell.deviceLongitude = "151.1220244";

        myAdsCollection.eMotoCell = myMotoCell;
        new getAdsCollectionTask().execute();
    }

    /*
    * This callback is invoked when the system is about to destroy the Activity.
    */
    @Override
    public void onDestroy() {

        // If the DownloadStateReceiver still exists, unregister it and set it to null
        if (mServiceResponseReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceResponseReceiver);
            mServiceResponseReceiver = null;
        }

        // Unregisters the FragmentDisplayer instance
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mServiceResponseReceiver);

        // Must always call the super method at the end.
        super.onDestroy();
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



    private void fillListView(){
        ListView listview = (ListView) findViewById(R.id.adsListView);
        eMotoAdsArrayAdapter myAdapter = new eMotoAdsArrayAdapter(this,R.layout.adsview_item_row,adsArray);
        listview.setAdapter(myAdapter);
        listview .setOnItemClickListener(mOnClickListener);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) { }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);

            Toast.makeText(getApplicationContext(),String.format("Item Clicked %s",adsArray.get(position).description()),
                    Toast.LENGTH_SHORT).show();

            if(adsArray.get(position).isApproved()) {
                new UnapproveTask().execute(adsArray.get(position).id(),mLoginResponse.token);
            }
            else
            {
                new ApproveTask().execute(adsArray.get(position).id(),mLoginResponse.token);
            }

        }
    };

    //region Asynctasks

    private class getAdsCollectionTask extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(Object... prams) {

                  myMotoCell.putDeviceOnServer(mLoginResponse.token);
                    //Log.d("debug",mLoginResponse.token);
                  myAdsCollection.getAdsCollection(mLoginResponse.token);

            return "test";

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            //completion handler
            adsArray.clear();
            for(Map.Entry<String, eMotoAds> entry: myAdsCollection.adsHashMap.entrySet())  {
                adsArray.add(entry.getValue());
                //Log.d("Application*", entry.getValue().getAdsThumbnailURL());
            }

            fillListView();
        }
    }

    private class ApproveTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... prams) {
            try {
                myAdsCollection.approveAdsWithID(prams[0],prams[1]);
                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            new getAdsCollectionTask().execute();
            //completion handler
        }
    }

    private class UnapproveTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... prams) {
            try {
                myAdsCollection.unapproveAdsWithID(prams[0],prams[1]);
                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            new getAdsCollectionTask().execute();
            //completion handler
        }
    }

    //endregion

    public void btnPressed (View view){

        Log.d("Activity","Btn Pressed");

        //mLogic.startLocationService();

        // use this to start and trigger a service
        Intent i= new Intent(this, eMotoService.class);
        // potentially add data to the intent
        i.putExtra("ServiceCMD", eMotoService.CMD_GETTOKEN);
        this.startService(i);
    }

    //Call back functions
    public void logicCallback (){
        Log.d("Activity","Call Back Triggered");

    }
    public void toastOnUI (String toastMessage){
        Toast.makeText(getApplicationContext(),toastMessage,
                Toast.LENGTH_SHORT).show();
    }


    private class ServiceResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ServiceResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Activity","BroadCastReceived: " +intent.getStringExtra(eMotoService.BROADCAST_STATUS));

            switch(intent.getStringExtra(eMotoService.BROADCAST_STATUS)){
                case eMotoService.RES_TOKEN_UPDATE:
                    mLoginResponse.token = intent.getStringExtra(eMotoService.RES_TOKEN_UPDATE);
                    break;
                case eMotoService.RES_LOCATION_ERROR:
                case eMotoService.RES_TOKEN_UNAUTHORIZED:
                case eMotoService.RES_EXCEPTION_ENCOUNTERED:
                    break;
                default:
                    break;
            }
        }
    }



}
