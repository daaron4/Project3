package com.companyname.ceramicgod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitudeLabel, mLongitudeLabel, mAddressOutput;
    private TextView mLatitudeText, mLongitudeText, mLocationAddressTextView;
    private boolean mAddressRequested;
    private ProgressBar mProgressBar;
    private Button mFetchAddressButton;
    private static String ADDRESS_REQUESTED_KEY = "address-requested-pending";
    private static String LOCATION_ADDRESS_KEY = "location-address";
    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeText = (TextView) findViewById(R.id.latitude);
        mLongitudeText = (TextView) findViewById(R.id.longitude);
        mLocationAddressTextView = (TextView) findViewById(R.id.address);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFetchAddressButton = (Button) findViewById(R.id.fetch_address);
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);
        updateUIWidgets();
        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Cursor cursor = DatabaseHelper.getInstance(MapsActivity.this).getAllReviews();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COL_LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COL_LONGITUDE));
            LatLng current = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(current));
        }
        LatLng santaMonica = new LatLng(34.02, -118.49);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(santaMonica));
        //Gives the default view of the map to a closer location.
        //Target sets the default location.
        //Zoom of 15 allows user to see by streets. BUT it limits the view. Will come back to.
        //Need to implement onItemClickListener to any parts of the map to allow for additional zooming options.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(santaMonica)
                .zoom(17)
                .tilt(0)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        MapFragment.newInstance(new GoogleMapOptions().camera(cameraPosition));
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
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

    public void fetchAddressButtonHandler(View view) {
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        mAddressRequested = true;
        updateUIWidgets();
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
    public void onConnected(Bundle connectionHint) {
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
                Toast.makeText(MapsActivity.this, R.string.no_geocoder_available, Toast.LENGTH_SHORT).show();
                return;
            }
            if (mAddressRequested) {
                startIntentService();
            }
            //Check to see if the two lines of code below are necessary.
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Toast.makeText(MapsActivity.this, R.string.location_not_detected, Toast.LENGTH_LONG).show();
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity.this, getString(R.string.connection_failed) + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Toast.makeText(MapsActivity.this, R.string.connection_suspended, Toast.LENGTH_SHORT).show();
    }

    private void displayAddressOutput() {
        mLocationAddressTextView.setText(mAddressOutput);
    }

    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    protected void showToast(String text) {
        Toast.makeText(MapsActivity.this, text, Toast.LENGTH_SHORT).show();
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
            displayAddressOutput();
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }
            mAddressRequested = false;
            updateUIWidgets();
        }
    }
}