package me.chayut.eMotoLogic;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by chayut on 7/02/15.
 */
public class eMotoServiceBroadcaster {

    private LocalBroadcastManager mBroadcaster;

    /**
     * Creates a BroadcastNotifier containing an instance of LocalBroadcastManager.
     * LocalBroadcastManager is more efficient than BroadcastManager; because it only
     * broadcasts to components within the app, it doesn't have to do parceling and so forth.
     *
     * @param context a Context from which to get the LocalBroadcastManager
     */
    public eMotoServiceBroadcaster(Context context) {

        // Gets an instance of the support library local broadcastmanager
        mBroadcaster = LocalBroadcastManager.getInstance(context);

    }

    public void broadcastIntentWithState(String status) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoService.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoService.BROADCAST_STATUS, status);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);
    }


    public void broadcastNewToken(String token) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoService.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoService.BROADCAST_STATUS, eMotoService.RES_TOKEN_UPDATE);
        localIntent.putExtra(eMotoService.RES_TOKEN_UPDATE,token);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);
    }
}
