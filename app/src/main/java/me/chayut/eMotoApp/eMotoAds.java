package me.chayut.eMotoApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 7/01/15.
 */
public class eMotoAds {
    public JSONObject adsJSONObject;

    public eMotoAds(JSONObject ads)
    {
        adsJSONObject = ads;
    }
    public String AdsDescription() {

        String des = null;
        try {
            des = adsJSONObject.getString("Description");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        return des;
    }

    public String AdsId() {

        String Id = null;
        try {
            Id = adsJSONObject.getString("Id");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        return Id;
    }
}
