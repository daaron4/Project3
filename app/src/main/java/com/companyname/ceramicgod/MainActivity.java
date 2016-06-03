package com.companyname.ceramicgod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        PhotoCallback, PhotoCallbackSigns{

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildGoogleApiClient();

        //Establishing the Tabs on the tabLayout.
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Nearby"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Add New Review"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

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
    }

    public void handleCallback(String response) {
        Fragment toiletFragment = (Fragment) pagerAdapter.getCurrentFragment();
        if (toiletFragment != null && toiletFragment instanceof ToiletFragment) {
            ((ToiletFragment)toiletFragment).handleCallback(response);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
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
        //Toast.makeText(MainActivity.this, R.string.connection_suspended, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(MainActivity.this, getString(R.string.connection_failed) + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
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

}
