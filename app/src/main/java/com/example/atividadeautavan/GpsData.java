package com.example.atividadeautavan;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class GpsData extends Thread implements LocationListener {

    private Marker marker;
    private TextView Latitude;
    private TextView Longitude;
    private GoogleMap googleMap;
    private static final String TAG = "LocationThread";
    private final Context mContext;
    private LocationManager locationManager;
    private Handler handler;

    private Location location;

    public GpsData(Context context, TextView latitude, TextView longitude, Handler handler, GoogleMap googleMap, Marker marker) {
        mContext = context;
        Latitude = latitude;
        Longitude = longitude;
        this.googleMap = googleMap;
        this.handler = handler;
        this.marker = marker;
    }

    @Override
    public void run() {
        requestLocation();
        handler.postDelayed(this, 100);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
        Longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
        handler.post(new Runnable() {
            @Override
            public void run() {
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                marker.setPosition(newLocation);
            }
        });
        locationManager.removeUpdates(this);
    }

    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        }
    }
}
