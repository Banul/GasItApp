
package com.example.user.proba3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by User on 2017-03-27.
 */

public class GPSTracker extends Service implements LocationListener {
    private final Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;

    Location location;
    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.context = context;
    }

    public static boolean ReturnLocation()
    {
        return canGetLocation;
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

                if (location == null) {
                    if (isNetworkEnabled == true) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }

                    }
                }

                if (!isGPSEnabled && !isNetworkEnabled)
                {
                    //wtedy nie da się nawiązać połączenia, zatem canGetLocation = false
                }
                else
                {
                    this.canGetLocation = true;
                }

            }
        }
        catch (Exception e)
        {
        }
        return location;
    }


    @Nullable
    @Override
    public IBinder onBind (Intent intent){
        return null;
    }

    @Override
    public void onLocationChanged (Location location){

    }

    @Override
    public void onStatusChanged (String provider,int status, Bundle extras){

    }

    @Override
    public void onProviderEnabled (String provider){

    }

    @Override
    public void onProviderDisabled (String provider){

    }

    public void showAlertWindow()
    {
        Log.d("tutaj","tutaj1");

        AlertDialog.Builder builder = new AlertDialog.Builder(GPSTracker.this);

        builder.setMessage("Twoj GPS jest wylaczany, czy chcesz go wlaczyc?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog,final int id) {

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
