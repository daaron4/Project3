package com.companyname.ceramicgod;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // ToDo: put marker here:

        LatLng userLocation = new LatLng(LocationData.latitude, LocationData.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        //Gives the default view of the map to a closer location.
        //Target sets the default location.
        //Zoom of 15 allows user to see by streets. BUT it limits the view. Will come back to.
        //Need to implement onItemClickListener to any parts of the map to allow for additional zooming options.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(17)
                .tilt(0)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        MapFragment.newInstance(new GoogleMapOptions().camera(cameraPosition));
    }
}
