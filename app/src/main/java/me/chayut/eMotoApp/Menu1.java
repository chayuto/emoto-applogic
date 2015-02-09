package me.chayut.eMotoApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import me.chayut.eMotoLogic.eMotoAdsCollection;
import me.chayut.eMotoLogic.eMotoCell;
import me.chayut.eMotoLogic.eMotoLoginResponse;
import me.chayut.eMotoLogic.eMotoService;


public class Menu1 extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //eMoto
    eMotoCell myMotoCell = new eMotoCell();
    eMotoAdsCollection myAdsCollection = new eMotoAdsCollection();
    eMotoLoginResponse mLoginResponse;
    ServiceResponseReceiver mServiceResponseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Intent intent = getIntent();
        mLoginResponse = intent.getExtras().getParcelable("eMotoLoginResponse");

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                eMotoService.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mServiceResponseReceiver = new ServiceResponseReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mServiceResponseReceiver,
                statusIntentFilter);



        myMotoCell.deviceID = "00000000";
        myMotoCell.deviceLatitude = "-33.7238297";
        myMotoCell.deviceLongitude = "151.1220244";

        myAdsCollection.eMotoCell = myMotoCell;

        //Start Service

        // use this to start and trigger a service
        Intent i= new Intent(this, eMotoService.class);
        // potentially add data to the intent
        i.putExtra("ServiceCMD", eMotoService.CMD_GETTOKEN);
        this.startService(i);

        i  = new Intent(this, eMotoService.class);
        i.putExtra("ServiceCMD", eMotoService.CMD_STARTLOCATIONSERVICE);
        this.startService(i);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu1, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu1, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Menu1) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class ServiceResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ServiceResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Activity", "BroadCastReceived: " + intent.getStringExtra(eMotoService.BROADCAST_STATUS));

            switch(intent.getStringExtra(eMotoService.BROADCAST_STATUS)){
                case eMotoService.RES_TOKEN_UPDATE:
                    mLoginResponse.token = intent.getStringExtra(eMotoService.RES_TOKEN_UPDATE);
                    Log.d("Activity","New Token: " + mLoginResponse.token);
                    break;
                case eMotoService.RES_LOCATION_UPDATE:
                    Location location = intent.getParcelableExtra(eMotoService.RES_LOCATION_UPDATE);
                    Toast.makeText(getApplicationContext(), String.format("Location %f %f %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()),
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_LOCATION_ERROR:
                case eMotoService.RES_TOKEN_UNAUTHORIZED:
                case eMotoService.RES_EXCEPTION_ENCOUNTERED:
                    break;
                default:
                    break;
            }
        }
    }

}
