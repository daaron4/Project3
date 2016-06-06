package com.companyname.ceramicgod;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        PhotoCallback, PhotoCallbackSigns{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int NOTIFICATION = 1;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private PagerAdapter pagerAdapter;

    private static String ADDRESS_REQUESTED_KEY = "address-requested-pending";
    private static String LOCATION_ADDRESS_KEY = "location-address";
    private AddressResultReceiver mResultReceiver;
    private String mLatitudeLabel, mLongitudeLabel, mAddressOutput;
    private boolean mAddressRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildGoogleApiClient();
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mAddressOutput = "";
        mAddressRequested = true;
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Nearby"));
        tabLayout.addTab(tabLayout.newTab().setText("Add New Review"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);


        mResultReceiver = new AddressResultReceiver(new Handler());


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.NOTIFICATION);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
        } else {
            showNetworkNotAvailableNotification();
        }
    }

    public void handleCallback(String response) {
        Fragment currentFragment = pagerAdapter.getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof ToiletFragment) {
            ((ToiletFragment)currentFragment).handleCallback(response);
            //toiletFragment.placeImageToilet(response);
        }
    }

    public void handleCallbackSigns(String response) {
        Fragment toiletFragment = (Fragment) pagerAdapter.getCurrentFragment();
        if (toiletFragment != null && toiletFragment instanceof ToiletFragment){
            ((ToiletFragment)toiletFragment).handleCallbackSigns(response);
            //toiletFragment.placeImageSign(response);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(MainActivity.this, R.string.no_geocoder_available, Toast.LENGTH_SHORT).show();
                return;
            }
            if (mAddressRequested) {
                startIntentService();
            }
            double latDouble = mLastLocation.getLatitude();
            double longDouble = mLastLocation.getLongitude();
            float latitude = (float)latDouble;
            float longitude = (float)longDouble;
            LocationData.latitude = latitude;
            LocationData.longitude = longitude;
        } else {
            Toast.makeText(MainActivity.this, R.string.location_not_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Toast.makeText(MainActivity.this, R.string.connection_suspended, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, getString(R.string.connection_failed) + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get Fragment:
            NewReviewFragment newReviewFragment = (NewReviewFragment) pagerAdapter.getCurrentFragment();
            if (newReviewFragment != null) {
                newReviewFragment.putPicOnView(PictureUtility.getPic());
            }
        }
    }

    public void fetchAddressButtonHandler() {
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        mAddressRequested = true;
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private void showNetworkNotAvailableNotification() {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.network_available_no)).build();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.network_icon);
        mBuilder.setContentTitle("Network Alert!");
        mBuilder.setContentText("Connection Unavailable");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigPictureStyle);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);
        outState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(outState);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Toast.makeText(MainActivity.this, "It worked! " + mAddressOutput, Toast.LENGTH_LONG).show();
            LocationData.address = mAddressOutput;
            if (resultCode == Constants.SUCCESS_RESULT) {

            }
            mAddressRequested = false;
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
            }
        }
    }

}
