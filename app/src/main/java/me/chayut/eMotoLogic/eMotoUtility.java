package me.chayut.eMotoLogic;


import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by chayut on 6/01/15.
 */
public class eMotoUtility
{
    private static final String TAG = "eMotoUtility";

    public static eMotoLoginResponse performLogin ( String username, String password){

        eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();
        mLoginResponse.idle = null;
        mLoginResponse.success = false;
        mLoginResponse.token= null;

        try {
            String text = String.format("%s:%s", username, password);

            byte[] data = text.getBytes("UTF-8");

            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            mLoginResponse.credential = base64;

            performLoginWithLoginResponse(mLoginResponse);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mLoginResponse;
    }

    public static void performLoginWithLoginResponse(eMotoLoginResponse mLoginResponse)
    {

        BufferedReader rd  = null;

        mLoginResponse.idle = null;
        mLoginResponse.success = false;
        mLoginResponse.token= null;

        try {
            bypassSSLAllCertificate();

            URL u = new URL(String.format("https://emotovate.com/api/security/authenticate/%s",mLoginResponse.credential));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("POST");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));

                    String json = rd.readLine();
                    JSONObject jObj = new JSONObject(json);
                    mLoginResponse.token  = jObj.getString("token");
                    mLoginResponse.idle = jObj.getString("idle");
                    mLoginResponse.success = true;

                    break;
                case 401:
                    Log.d(TAG,"login unauthorized");
                    break;
                default:


            }
        }
        catch (MalformedURLException ex) {
        ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }

    }

    public static void bypassSSLAllCertificate(){
        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }

                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }
        catch (NoSuchAlgorithmException ex ){
            ex.printStackTrace();
        }
        catch (KeyManagementException ex){
            ex.printStackTrace();
        }
    }

    public static JSONArray getCountryDataFromServer () {

        BufferedReader rd  = null;
        JSONArray jArray = null;
        //TODO: remove SSL bypass
        eMotoUtility.bypassSSLAllCertificate();

        try {
            URL u = new URL(String.format("https://emotovate.com/api/location/countries"));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String json = rd.readLine();
                    jArray  = new JSONArray(json);
                    Log.d(TAG,String.format("JSON Length %d",jArray.length()));

                    for(int n = 0; n < jArray.length(); n++) {

                        //Log.d("Logic",jArray.getJSONObject(n).getString("FullName"));
                    }



                    break;
                case 401:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));

                    Log.d(TAG,"Server unauthorized: " +rd.readLine());
                    break;
                default:


            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jArray;
    }
    public static JSONArray getCityDataFromServer (String countryIDorShortName) {

        BufferedReader rd  = null;
        JSONArray jArray = null;
        //TODO: remove SSL bypass
        eMotoUtility.bypassSSLAllCertificate();

        try {
            URL u = new URL(String.format("https://emotovate.com/api/location/cities/%s",countryIDorShortName));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String json = rd.readLine();
                    jArray  = new JSONArray(json);
                    Log.d(TAG,String.format("JSON Length %d",jArray.length()));

                    for(int n = 0; n < jArray.length(); n++) {

                        //Log.d("Logic",jArray.getJSONObject(n).getString("FullName"));
                    }



                    break;
                case 401:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));

                    Log.d(TAG,"Server unauthorized: " +rd.readLine());
                    break;
                default:


            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jArray;
    }
    public static JSONArray getZoneDataFromServer (String cityId) {

        BufferedReader rd  = null;
        JSONArray jArray = null;
        //TODO: remove SSL bypass
        eMotoUtility.bypassSSLAllCertificate();

        try {
            URL u = new URL(String.format("https://emotovate.com/api/location/cityzones/%s",cityId));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String json = rd.readLine();
                    jArray  = new JSONArray(json);
                    Log.d(TAG,String.format("JSON Length %d",jArray.length()));

                    for(int n = 0; n < jArray.length(); n++) {

                        //Log.d("Logic",jArray.getJSONObject(n).getString("FullName"));
                    }



                    break;
                case 401:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));

                    Log.d(TAG,"Server unauthorized: " +rd.readLine());
                    break;
                default:


            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jArray;
    }


    public static JSONArray getDeviceListFromServer (String token) {

        BufferedReader rd  = null;
        JSONArray jArray = null;
        //TODO: remove SSL bypass
        eMotoUtility.bypassSSLAllCertificate();

        try {
            URL u = new URL(String.format("https://emotovate.com/api/device/all/%s",token));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String json = rd.readLine();
                    jArray  = new JSONArray(json);
                    Log.d(TAG,String.format("eMotoCells: JSON Length %d",jArray.length()));

                    /*
                    for(int n = 0; n < jArray.length(); n++) {

                        Log.d("Logic",jArray.getJSONObject(n).getString("FullName"));
                    }
                    */
                    break;
                case 401:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));

                    Log.d(TAG,"Server unauthorized: " +rd.readLine());
                    break;
                default:


            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jArray;
    }


}
