package com.exjobbandroidapplication.Resources;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.exjobbandroidapplication.Interfaces.LocationTracker;

public class GPSTracker implements LocationTracker, LocationListener {

    private static final long MIN_UPDATE_DISTANCE = 0;
    private static final long MIN_UPDATE_TIME = 0;//1000 * 60 * 1;
    private LocationManager lm;

    public enum ProviderType {
        NETWORK,
        GPS
    }

    ;

    private String provider;
    private Location lastLocation;
    private long lastTime;
    private boolean isRunning;
    private Context mContext;

    private LocationUpdateListener listener;

    public GPSTracker(Context mContext, ProviderType type) {
        this.mContext = mContext;
        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (type == ProviderType.NETWORK) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            provider = LocationManager.GPS_PROVIDER;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        try{
            lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
            Log.d("Start()",provider.toString());
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }

        lastLocation = null;
        lastTime = 0;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

    }

    @Override
    public void start(LocationUpdateListener updateListener) {
        start();
        listener = updateListener;
    }

    @Override
    public void stop() throws SecurityException {
        if(isRunning){
            lm.removeUpdates(this);
        }
    }

    @Override
    public boolean hasLocation() {
        if(lastLocation == null){
            return false;
        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPosiblyStaleLocation() throws SecurityException {
        if(lastLocation != null){
            return true;
        }
        return lm.getLastKnownLocation(provider) != null;
    }

    @Override
    public void onLocationChanged(Location newLoc){
        long now = System.currentTimeMillis();
        if(listener != null){
            listener.onUpdate(lastLocation, lastTime, newLoc, now);
        }
        lastLocation = newLoc;
        lastTime = now;

    }

    @Override
    public Location getLocation() {
//        if(lastLocation == null){
//            return null;
//        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return null;
        }
        return lastLocation;
    }

    @Override
    public Location getPosiblyStaleLocation() throws SecurityException{
        if(lastLocation != null){
            return lastLocation;
        }
        return lm.getLastKnownLocation(provider);
    }
}