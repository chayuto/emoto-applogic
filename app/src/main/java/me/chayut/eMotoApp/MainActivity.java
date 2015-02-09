package me.chayut.eMotoApp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;

import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;


import org.json.JSONArray;

import me.chayut.eMotoLogic.eMotoAds;
import me.chayut.eMotoLogic.eMotoCell;
import me.chayut.eMotoLogic.eMotoLoginResponse;
import me.chayut.eMotoLogic.eMotoService;
import me.chayut.eMotoLogic.eMotoUtility;


public class MainActivity extends ActionBarActivity {

    //Service
    eMotoService mService;
    boolean mBound = false;

    eMotoLoginResponse mLoginResponse;
    String value = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Application:","onCreate");

        // Bind to LocalService
        //this.start();
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

    public void mainBtnPressed(View view) {
       // Intent intent = new Intent(this, DisplayMessageActivity.class);
        Log.d("Application:","btnPressed");

        //perform login with Asynctask
        new LoginTask ().execute();
    }


    private class LoginTask extends AsyncTask<Object, Void, String> {

        String username,password;

        @Override
        protected void onPreExecute()
        {
            EditText userEdit = (EditText)findViewById(R.id.editTextUsername);
            EditText passEdit = (EditText)findViewById(R.id.editTextPassword);

            username = userEdit.getText().toString();
            password = passEdit.getText().toString();
        }
        @Override
        protected String doInBackground(Object... prams) {
            try {


                mLoginResponse = eMotoUtility.performLogin(username, password);

                JSONArray deviceArray = eMotoUtility.getDeviceListFromServer(mLoginResponse.token);
                for(int n = 0; n < deviceArray.length(); n++) {
                    eMotoCell myCell = new eMotoCell(deviceArray.getJSONObject(n));
                   Log.d("Debug",String.format("DevID:%s DevName:%s",myCell.deviceID,myCell.deviceName));
                }

                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //completion handler

            if(mLoginResponse.success){
                Log.d("Application:",String.format("Token:%s",mLoginResponse.token));
                Log.d("Application:",String.format("Idle:%s",mLoginResponse.idle));
                value = mLoginResponse.token;
                loginSuccessful();
            }
            else
            {
                loginUnauthorizedAlert();
            }


            Log.d("AyncThread", "onPostExecute");
        }



    }

    private void loginSuccessful (){

        // use this to start and trigger a service
        Intent i= new Intent(getApplicationContext(), eMotoService.class);
        // potentially add data to the intent
        i.putExtra("ServiceCMD", eMotoService.CMD_STARTAUTOREAUTHENTICATE);
        i.putExtra("eMotoLoginResponse",mLoginResponse);

        getApplicationContext().startService(i);

        //Intent myIntent = new Intent(getBaseContext(), AdsManagement.class);
       Intent myIntent = new Intent(getBaseContext(), manageAds.class);
        myIntent.putExtra("eMotoLoginResponse", mLoginResponse); //pass login perimeter to the new activity

        MainActivity.this.startActivity(myIntent);


    }



    private void loginUnauthorizedAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Invalid Username/Password");
        builder1.setCancelable(true);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d("MainActivity", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MainActivity", "onServiceDisconnected");
        }
    };

    public void start() {

        Intent i= new Intent(MainActivity.this, eMotoService.class);
        // mContext is defined upper in code, I think it is not necessary to explain what is it
        MainActivity.this.bindService(i, mServerConn, Context.BIND_AUTO_CREATE);


    }

    public void stop() {

        //this.unbindService(mServerConn);
    }

}

