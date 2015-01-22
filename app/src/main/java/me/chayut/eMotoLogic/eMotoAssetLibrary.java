package me.chayut.eMotoLogic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chayut on 19/01/15.
 */
public class eMotoAssetLibrary {

    private Map<String,Bitmap> adsHashMap =  new HashMap<String,Bitmap>();


    public Bitmap getThumbnail (eMotoAds ads){

        Bitmap adsThumbnail = adsHashMap.get(ads.id());

        if (adsThumbnail==null)
        {
            try {
                URL imageURL = new URL(ads.getAdsThumbnailURL());
                adsThumbnail = BitmapFactory.decodeStream(imageURL.openStream());
                adsHashMap.put(ads.id(),adsThumbnail);
            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return adsThumbnail;
    }

    public void clearAssetLibrary(){
        adsHashMap.clear();
    }



}
