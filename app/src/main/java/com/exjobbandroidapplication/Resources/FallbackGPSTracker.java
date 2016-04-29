package com.exjobbandroidapplication.Resources;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.exjobbandroidapplication.Interfaces.LocationTracker;

/**
 * Created by Anders on 2016-04-28.
 */
public class FallbackGPSTracker implements LocationTracker, LocationTracker.LocationUpdateListener {

    private boolean isRunning;

    private GPSTracker gps;
    private GPSTracker net;

    private LocationUpdateListener locationUpdateListener;

    Location lastLoc;

    long lastTime;

    public FallbackGPSTracker(Context context) {
        gps = new GPSTracker(context, GPSTracker.ProviderType.GPS);
        net = new GPSTracker(context, GPSTracker.ProviderType.NETWORK);
    }

    @Override
    public void start() {
        if (isRunning){
            return;
        }

        gps.start(this);
        net.start(this);
        isRunning = true;
    }

    @Override
    public void start(LocationUpdateListener updateListener) {
        start();
        this.locationUpdateListener = updateListener;
    }

    @Override
    public void stop() {
        if (isRunning) {
            gps.stop();
            net.stop();
            isRunning = false;
            locationUpdateListener = null;
        }
    }

    @Override
    public boolean hasLocation() {
        return gps.hasLocation() || net.hasLocation();
    }

    @Override
    public boolean hasPosiblyStaleLocation() {
        return gps.hasPosiblyStaleLocation() || net.hasPosiblyStaleLocation();
    }

    @Override
    public Location getLocation() {
        Location ret = gps.getLocation();
        if (ret == null) {
            ret = net.getLocation();
        }
        return ret;
    }

    @Override
    public Location getPosiblyStaleLocation() {
        Location ret = gps.getPosiblyStaleLocation();
        if (ret == null) {
            ret = net.getPosiblyStaleLocation();
        }
        return ret;
    }

    @Override
    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        boolean update = false;

        if (lastLoc == null) {
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())) {
            update = true;
        }
        else if (newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if (newTime - lastTime > 5 * 60 * 1000) {
            update = true;
        }

        if (update) {
            if (locationUpdateListener != null) {
                locationUpdateListener.onUpdate(lastLoc,lastTime,newLoc,newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }
    }
}
