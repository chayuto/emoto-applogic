package me.chayut.eMotoApp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;

import me.chayut.eMotoLogic.eMotoLogic;
import me.chayut.eMotoLogic.eMotoLoginResponse;
import me.chayut.eMotoLogic.eMotoUtility;


public class MainActivity extends ActionBarActivity {


    eMotoLoginResponse mLoginResponse;
    String value = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Application:","onCreate");

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
                eMotoUtility.getCountryDataFromServer();
                mLoginResponse = eMotoUtility.performLogin(username, password);

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

        Intent myIntent = new Intent(MainActivity.this, manageAds.class);
        myIntent.putExtra("eMotoLoginResponse", mLoginResponse); //Optional parameters

        MainActivity.this.startActivity(myIntent);

    }


    private void loginUnauthorizedAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Invalid Username/Password");
        builder1.setCancelable(true);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}

