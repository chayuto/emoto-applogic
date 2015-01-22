package me.chayut.eMotoLogic;

import android.content.Context;
import android.location.LocationManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chayut on 22/01/15.
 */
public class eMotoLogic {

    private ScheduledThreadPoolExecutor stpe;
    private LocationManager locationManager;
    private Context mContext;

    public eMotoLogic(Context mContext){
        this.mContext = mContext;
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
        // Get the location manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }


}
