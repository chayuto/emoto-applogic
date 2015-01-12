package me.chayut.eMotoApp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;


/**
 * Created by chayut on 6/01/15.
 */
public class eMotoAdsCollection {

    public eMotoCell myEMotoCell;
    public String token;
    public Map<String,eMotoAds> map =  new HashMap<String,eMotoAds>();




    public void getAdsCollection () {

        BufferedReader rd  = null;

        //TODO: remove SSL bypass
        eMotoUtility.bypassSSLAllCertificate();

        try {
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
                        map.put(myAds.id(),myAds);
                    }

                    for(Map.Entry<String, eMotoAds> entry: map.entrySet())  {

                        Log.d("Application", entry.getValue().id());
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

        catch (JSONException ex){
            ex.printStackTrace();
        }

    }

    public boolean approveAdsID(String adsID){

        BufferedReader rd  = null;

        if(map.get(adsID) != null)
        {
            eMotoAds ads = map.get(adsID);
            try {
                URL u = new URL(String.format("https://emotovate.com/api/ads/unapprove/%s?scheduleAssetId=%s&userIP=%s",token,ads.scheduleAssetId(),"192.168.1.1"));
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
                        Log.d("Application:",json);

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
        }
        return false;
    }




}
