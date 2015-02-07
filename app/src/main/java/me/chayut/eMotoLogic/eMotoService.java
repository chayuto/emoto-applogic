package me.chayut.eMotoLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chayut on 6/02/15.
 */
public class eMotoService extends Service {

    //Debug
    private final static String Tag = "eMotoService";



    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.emotovate.android.eMotoApp.BROADCAST";
    public static final String BROADCAST_STATUS = "com.emotovate.android.eMotoApp.STATUS";

    //Public RESPONSE
    public static final String RES_LOCATION_UPDATE = "RES_LOCATION_UPDATE";
    public static final String RES_LOCATION_ERROR = "RES_LOCATION_ERROR";

    public static final String RES_TOKEN_UPDATE = "RES_TOKEN_UPDATE";
    public static final String RES_TOKEN_UNAUTHORIZED = "RES_TOKEN_UNAUTHORIZED";

    //Public CMD
    public final static String CMD_STARTAUTOREAUTHENTICATE = "CMD_STARTAUTOREAUTHENTICATE";
    public final static String CMD_GETTOKEN = "CMD_GETTOKEN";
    public final static String CMD_STARTLOCATIONSERVICE = "CMD_STARTLOCATIONSERVICE";
    public final static String CMD_STOPLOCATIONSERVICE = "CMD_STOPLOCATIONSERVICE";

    //LocalVarible
    eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();


    //service Broadcaster
    eMotoServiceBroadcaster mServiveBroadcaster = new eMotoServiceBroadcaster(this);


    /** interface for clients that bind */
    IBinder mBinder;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String ServiceCMD = intent.getStringExtra("ServiceCMD");
        Log.d(Tag, ServiceCMD);

        switch (ServiceCMD){
            case CMD_STARTAUTOREAUTHENTICATE:
                mLoginResponse = intent.getExtras().getParcelable("eMotoLoginResponse");
                Log.d(Tag, "Login Credential: " + mLoginResponse.token);
                break;
            case CMD_GETTOKEN:
                Log.d(Tag, "Login Credential: " + mLoginResponse.token);
                mServiveBroadcaster.broadcastIntentWithState(RES_TOKEN_UPDATE);
                break;
            case CMD_STARTLOCATIONSERVICE:
                startLocationService();
                break;
            case CMD_STOPLOCATIONSERVICE:
                stopLocationService();
                break;
            default:
                Log.d(Tag, "Service Command Unrecognized");
                break;
        }

        return Service.START_NOT_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    //region Authentication Service


    private ScheduledThreadPoolExecutor stpe;

    private void startAutoReauthenticate (eMotoLoginResponse mLoginResponse) {

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

    private void stopAutoReauthenticate ()
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

    //endregion


    //region Bluetooth Service

    //endregion

    //region Location Service


    private LocationManager locationManager;
    private LocationListener locationListener;
    private String locationProvider;

    private void startLocationService()
    {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("Location",location.toString());
                //Toast.toastOnUI(String.format("Location: %f, %f : %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Location","onStatusChanged : " + provider);
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Get the location manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(locationProvider);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);//.NETWORK_PROVIDER,.GPS_PROVIDER

    }

    private void stopLocationService(){
        locationManager.removeUpdates(locationListener);

    }



    //endregion

}
