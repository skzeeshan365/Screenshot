package com.reiserx.screenshot.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.reiserx.screenshot.Interfaces.FusedLocationResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FusedLocation {
    Context context;
    FusedLocationResult result;
    LocationCallback locationCallback;

    public FusedLocation(Context context, FusedLocationResult result) {
        this.context = context;
        this.result = result;
    }

    void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null)
                        result.onSuccess(location);
                    else
                        result.onFailure(new Exception("Location not available"));
                }
                if (locationCallback != null)
                    fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(100);
        locationRequest.setMaxWaitTime(3000);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        } else {
            result.onFailure(new SecurityException("Permission required"));
        }
    }

    public static String geolocateCoordinates(double latitude, double longitude, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getSubLocality();
                if (locality != null)
                    return locality;
                else if (address.getLocality() != null)
                    return address.getLocality();
                else if (address.getAdminArea() != null)
                    return address.getAdminArea();
                else
                    return address.getCountryName();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
