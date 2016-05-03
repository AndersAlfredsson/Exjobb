package com.Modelclasses.Janitor;

import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.Dataclasses.SensorDataHandler;

/**
 * Created by Gustav on 2016-05-02.
 */
public class Janitor
{
    private final SensorDataHandler sensorDataHandler;
    private final GpsDataHandler gpsDataHandler;
    private final int CLEANUP_INTERVAL;
    private boolean runCleanup;


    public Janitor(GpsDataHandler gpsDataHandler, SensorDataHandler sensorDataHandler, int interval)
    {
        this.CLEANUP_INTERVAL = interval;
        this.gpsDataHandler = gpsDataHandler;
        this.sensorDataHandler = sensorDataHandler;
    }

    //region Setters & Getters
    /**
     * Sets the value of runCleanup
     * @param state returns a state
     */
    public void setRunCleanup(boolean state)
    {
        this.runCleanup = state;
    }
    //endregion

    /**
     * Threaded cleanup-function that calls the cleanup if the hashmap is not empty
     */
    public void startCleanupThread()
    {
        this.runCleanup = true;
        long timeInterval = 1000 * 60 * this.CLEANUP_INTERVAL; //minutes -> milliseconds
        System.out.println("Janitor started...");
        Thread cleanUpThread = new Thread(() -> {
            while(this.runCleanup)
            {
                try
                {
                    Thread.sleep(timeInterval);
                    if(this.gpsDataHandler.getMapSize() > 0)
                    {
                        gpsDataHandler.cleanup();
                    }
                    else
                    {
                        System.out.println("GpsMap Cleanup Skipped, map is empty");
                    }

                    System.out.println("Sensor data cleanup started...");
                    sensorDataHandler.cleanup();
                    System.out.println("Sensor data cleanup finished...");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("Janitor exited...");
        });
        cleanUpThread.setDaemon(true);
        cleanUpThread.start();
    }
}
