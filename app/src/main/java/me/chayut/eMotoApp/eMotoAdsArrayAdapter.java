package me.chayut.eMotoApp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chayut on 8/01/15.
 */


public class eMotoAdsArrayAdapter extends ArrayAdapter<eMotoAds> {


    // declaring our ArrayList of items
    private ArrayList<eMotoAds> objects;
    private LayoutInflater inflater;



    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public eMotoAdsArrayAdapter(Context context, int textViewResourceId, ArrayList<eMotoAds> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

        inflater = ((Activity)context).getLayoutInflater();
    }


    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        String adsImageURL;
        Bitmap bitmap;
    }


    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){


        ViewHolder viewholder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adsview_item_row,null);
            viewholder = new ViewHolder();
            viewholder.imageView = (ImageView) convertView.findViewById(R.id.adsThumbnail);
            viewholder.textView = (TextView) convertView.findViewById(R.id.adsTitle);


            convertView.setTag(viewholder);
        }

        viewholder = (ViewHolder)convertView.getTag();


        /*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        eMotoAds i = objects.get(position);

        viewholder.imageView.setImageResource(R.drawable.em_logo_120);
        viewholder.textView.setText(i.description());
        viewholder.adsImageURL = i.getAdsThumbnailURL();
        new ImageThumbnailDownloadTask().execute(viewholder);

        // the view must be returned to our activity
        return convertView;

    }

    private class ImageThumbnailDownloadTask extends AsyncTask<ViewHolder,Void,ViewHolder>
    {
        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {

            // TODO Auto-generated method stub

            //load image directly

            ViewHolder viewHolder = params[0];
            try {

                URL imageURL = new URL(viewHolder.adsImageURL);

                viewHolder.bitmap = BitmapFactory.decodeStream(imageURL.openStream());

            } catch (IOException e) {

                // TODO: handle exception

                Log.e("error", "Downloading Image Failed");

                viewHolder.bitmap = null;
            }
            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            // TODO Auto-generated method stub
            if (result.bitmap == null) {
                result.imageView.setImageResource(R.drawable.em_logo_120);
            } else {
                result.imageView.setImageBitmap(result.bitmap);
            }
        }

    }

}

