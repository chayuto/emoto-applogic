package me.chayut.eMotoLogic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 7/01/15.
 */
public class eMotoAds {
    private String AdsId;
    private String AdsDescription;
    private String AdsScheduleAssetId;
    private String AdsApprove;
    private String AdsUrl;

    public eMotoAds(JSONObject ads)
    {
        setAdsProperties(ads);
    }

    public String description() {
       return AdsDescription;
    }

    private void setAdsProperties(JSONObject ads){
        try {
            AdsId = ads.getString("Id");
            AdsDescription= ads.getString("Description");
            AdsApprove = ads.getString("Approved");
            AdsScheduleAssetId =ads.getString("ScheduleAssetId");
            AdsUrl =ads.getString("Url");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    public String id() { return AdsId;}

    public String scheduleAssetId() {

       return AdsScheduleAssetId;
    }

    public String isApprovedStr() {

        return AdsApprove;
    }

    public Boolean isApproved(){
        return Boolean.parseBoolean(isApprovedStr());
    }

    public String getAdsImageURL(){
        return AdsUrl;
    }

    public String getAdsThumbnailURL(){
        String s = getAdsImageURL();
        String ThumbnailURL = s.substring(0,s.length()-4) + "_t.jpg";

        return ThumbnailURL;
    }
}
