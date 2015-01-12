package me.chayut.eMotoApp;


import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static eMotoLoginResponse performLogin ( String username, String password){


        eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();

        BufferedReader rd  = null;

        mLoginResponse.idle = null;
        mLoginResponse.success = false;
        mLoginResponse.token= null;
        mLoginResponse.username = username;

        String LoginResponse = null;
        try {

            String text = String.format("%s:%s",username,password);
            byte[] data = text.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);

            URL u = new URL(String.format("https://emotovate.com/api/security/authenticate/%s",base64));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("POST");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(3000);
            c.setReadTimeout(3000);
            c.connect();
            int status = c.getResponseCode();

            Log.d("Application:", String.format("http-response:%3d", status));
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
                    Log.d("Application:","login unauthorized");
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
        return mLoginResponse;
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
}
