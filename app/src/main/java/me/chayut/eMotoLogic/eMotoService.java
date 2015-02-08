package me.chayut.eMotoLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chayut on 6/02/15.
 */
public class eMotoService extends Service {

    //Debug
    private final static String TAG = "eMotoService";


    // Defines Intent action
    public static final String BROADCAST_ACTION = "com.emotovate.android.eMotoApp.BROADCAST";
    public static final String BROADCAST_STATUS = "com.emotovate.android.eMotoApp.STATUS";

    //Public RESPONSE
    public static final String RES_LOCATION_UPDATE = "RES_LOCATION_UPDATE";
    public static final String RES_LOCATION_ERROR = "RES_LOCATION_ERROR";
    public static final String RES_TOKEN_UPDATE = "RES_TOKEN_UPDATE";
    public static final String RES_TOKEN_UNAUTHORIZED = "RES_TOKEN_UNAUTHORIZED";
    public static final String RES_EXCEPTION_ENCOUNTERED = "RES_EXCEPTION_ENCOUNTERED";

    //Public CMD
    public final static String CMD_STARTAUTOREAUTHENTICATE = "CMD_STARTAUTOREAUTHENTICATE";
    public final static String CMD_GETTOKEN = "CMD_GETTOKEN";
    public final static String CMD_STARTLOCATIONSERVICE = "CMD_STARTLOCATIONSERVICE";
    public final static String CMD_STOPLOCATIONSERVICE = "CMD_STOPLOCATIONSERVICE";

    //LocalVarible
    eMotoLoginResponse mLoginResponse ;


    //service Broadcaster
   // eMotoServiceBroadcaster mServiveBroadcaster = new eMotoServiceBroadcaster(this);


    /** interface for clients that bind */
    private final IBinder mBinder = new LocalBinder();
    /** indicates whether onRebind should be used */
    boolean mAllowRebind = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
        mLoginResponse = new eMotoLoginResponse();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand() id: " + startId + ": " + intent);

        String ServiceCMD = intent.getStringExtra("ServiceCMD");
        Log.d(TAG, ServiceCMD);

        switch (ServiceCMD){
            case CMD_STARTAUTOREAUTHENTICATE:
                mLoginResponse = intent.getExtras().getParcelable("eMotoLoginResponse");
                if(mLoginResponse != null) {
                    Log.d(TAG, "Login Credential: " + mLoginResponse.token);
                    this.startAutoReauthenticate(mLoginResponse);
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_TOKEN_UPDATE, this);
                }
                else
                {
                    Log.d(TAG, "Null Object Reference!");
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_EXCEPTION_ENCOUNTERED, this);
                }
                break;
            case CMD_GETTOKEN:
                if(mLoginResponse != null) {
                    Log.d(TAG, "Login Credential: " + mLoginResponse.token);
                    this.startAutoReauthenticate(mLoginResponse);
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_TOKEN_UPDATE, this);
                }
                else
                {
                    Log.d(TAG, "Null Object Reference!");
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_EXCEPTION_ENCOUNTERED, this);
                }
                break;
            case CMD_STARTLOCATIONSERVICE:
                startLocationService();
                break;
            case CMD_STOPLOCATIONSERVICE:
                stopLocationService();
                break;
            default:
                Log.d(TAG, "Service Command Unrecognized");
                break;
        }

        return Service.START_NOT_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService()*/
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed*/
    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        eMotoService getService() {
            return eMotoService.this;
        }
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
            eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.token, eMotoService.this);
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
    private boolean locationServiceIsRunning = false;

    private void startLocationService()
    {
        Log.d(TAG, "startLocationService()");
        //if(!locationServiceIsRunning) {
            // Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    Log.d(TAG, location.toString());
                    eMotoServiceBroadcaster.broadcastNewLocation(location, eMotoService.this);
                    //Toast.toastOnUI(String.format("Location: %f, %f : %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("Location", "onStatusChanged : " + provider);
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            // Get the location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            locationProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(locationProvider);

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);//.NETWORK_PROVIDER,.GPS_PROVIDER
            locationServiceIsRunning = true;
        //}
    }

    private void stopLocationService(){
        locationManager.removeUpdates(locationListener);
        locationServiceIsRunning =false;
    }



    //endregion

}
