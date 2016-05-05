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
    private final int GPS_CLEANUP_INTERVAL;
    private final int SENSOR_CLEANUP_INTERVAL;
    private boolean runCleanup;


    public Janitor(GpsDataHandler gpsDataHandler, SensorDataHandler sensorDataHandler, int interval, int sensorInterval)
    {
        this.GPS_CLEANUP_INTERVAL = interval;
        this.SENSOR_CLEANUP_INTERVAL = sensorInterval;
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
        long gpsTimeInterval = 1000 * 60 * this.GPS_CLEANUP_INTERVAL; //minutes -> milliseconds
        long sensorTimeInterval = 1000 * 60 * this.SENSOR_CLEANUP_INTERVAL; // minutes -> milliseconds
        System.out.println("Janitor started...");

        Thread gpsCleanUpThread = new Thread(() -> {
            System.out.println("GpsCleanupThreadStarted...");
            while(this.runCleanup)
            {
                try
                {
                    Thread.sleep(gpsTimeInterval);
                    if(this.gpsDataHandler.getMapSize() > 0)
                    {
                        gpsDataHandler.cleanup();
                    }
                    else
                    {
                        System.out.println("GpsMap Cleanup Skipped, map is empty");
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("GpsJanitor exited...");
        });

        Thread sensorCleanUpThread = new Thread(() ->
        {
            System.out.println("SensorCleanupThread started...");
            while(this.runCleanup)
            {
                try
                {
                    Thread.sleep(sensorTimeInterval);
                    System.out.println("Sensor data cleanup started...");
                    sensorDataHandler.cleanup();
                    System.out.println("Sensor data cleanup finished...");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("ServerJanitor exited...");
        });

        gpsCleanUpThread.setDaemon(true);
        gpsCleanUpThread.start();
        sensorCleanUpThread.setDaemon(true);
        sensorCleanUpThread.start();
    }
}
