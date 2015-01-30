package me.chayut.eMotoLogic;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chayut on 22/01/15.
 */
public class eMotoLogic {


    private ScheduledThreadPoolExecutor stpe;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;
    private LogicCallBack logicCB;
    private Context mContext;


    public eMotoLogic(Context mContext,LogicCallBack callback){
        this.mContext = mContext;
        this.logicCB = callback;
    }

    public void startAutoReauthenticate (eMotoLoginResponse mLoginResponse) {

        try {
            int corePoolSize = 2;
            //creates ScheduledThreadPoolExecutor object with number of thread 2
            stpe = new ScheduledThreadPoolExecutor(corePoolSize);
            //starts runnable thread
            RunnableThread runThread = new RunnableThread();
            runThread.mLoginResponse = mLoginResponse;
            int delay = Integer.parseInt(mLoginResponse.idle);
            stpe.execute(runThread);

            //starts callable thread that will start after delay minutes
            ScheduledFuture sf = stpe.scheduleAtFixedRate(runThread,delay,delay,
                    TimeUnit.MINUTES);


            int activeCnt = stpe.getActiveCount();
            System.out.println("activeCnt:" + activeCnt);
            //stops all the threads in ScheduledThreadPoolExecutor
            //
            //System.out.println(stpe.isShutdown());
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void stopAutoReauthenticate ()
    {
        stpe.shutdownNow();
    }
    //runnable thread
    class RunnableThread implements Runnable {

        public eMotoLoginResponse mLoginResponse;
        @Override
        public void run() {
            eMotoUtility.performLoginWithLoginResponse(mLoginResponse);
            System.out.println("run:" + mLoginResponse.token);
        }
    }

    public void startLocationService()
    {
        logicCB.toastOnUI("Location Service Started");
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("Location",location.toString());
                logicCB.toastOnUI(String.format("Location: %f, %f : %f", location.getLatitude(),location.getLongitude(),location.getAccuracy()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Location","onStatusChanged : " + provider);
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Get the location manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);//.NETWORK_PROVIDER,

    }

    public void stopLocationService(){
        locationManager.removeUpdates(locationListener);

    }

}