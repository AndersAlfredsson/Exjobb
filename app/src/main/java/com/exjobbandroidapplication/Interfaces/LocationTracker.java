package com.exjobbandroidapplication.Interfaces;

import android.location.Location;

/**
 * Created by Anders on 2016-04-28.
 */
public interface LocationTracker {
    public interface LocationUpdateListener {
        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }

    public void start();

    public void start(LocationUpdateListener updateListener);

    public void stop();

    public boolean hasLocation();

    public boolean hasPosiblyStaleLocation();

    public Location getLocation();

    public Location getPosiblyStaleLocation();

}
