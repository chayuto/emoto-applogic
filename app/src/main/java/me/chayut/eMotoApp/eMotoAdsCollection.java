package me.chayut.eMotoApp;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by chayut on 6/01/15.
 */
public class eMotoAdsCollection {

    public eMotoCell myEMotoCell;
    public String token;
    public Map<String,eMotoAds> map =  new HashMap<String,eMotoAds>();

    public void getAdsCollection () {

        BufferedReader rd  = null;

        try {

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

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


            URL u = new URL(String.format("https://emotovate.com/api/ads/all/%s?deviceId=%s&lat=%s&lng=%s",token,myEMotoCell.deviceID,myEMotoCell.deviceLatitude,myEMotoCell.deviceLongitude));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

            c.setRequestMethod("GET");

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
                    JSONArray jArray  = new JSONArray(json);
                    for(int n = 0; n < jArray.length(); n++) {
                        eMotoAds myAds = new eMotoAds(jArray.getJSONObject(n));
                        map.put(myAds.AdsId(),myAds);
                    }

                    for(Map.Entry<String, eMotoAds> entry: map.entrySet())  {

                        Log.d("Application", entry.getValue().AdsId());
                    }

                    break;
                case 401:
                    Log.d("Application:","Server unauthorized");
                    break;
                default:


            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (NoSuchAlgorithmException ex ){
            ex.printStackTrace();
        }
        catch (KeyManagementException ex){
            ex.printStackTrace();
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }

    }


}
