package com.Modelclasses.Dataclasses;

import NetworkMessages.GPSCoordMessage;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Gustav on 2016-04-26.
 * The class that handles GPS-data from clients
 */
public class GpsDataHandler
{
    private HashMap<String, GpsDataContainer> dataMap;

    /**
     * Default constructor
     */
    public GpsDataHandler()
    {
        this.dataMap = new HashMap<>();
    }

    /**
     * Puts a new object in the map, but checks first if there is already one from the same user,
     * if it is, it removes the object first and then adds the new one.
     * @param message
     */
    public synchronized void putData(GPSCoordMessage message)
    {
        GpsDataContainer container = new GpsDataContainer(message, ZonedDateTime.now());
        if(dataMap.containsKey(message.getUsername()))
        {
            System.out.println("Replaced data in map");
            dataMap.remove(message.getUsername());
            dataMap.put(message.getUsername(), container);
        }
        else
        {
            System.out.println("Put data in map");
            dataMap.put(message.getUsername(), container);
        }
    }

    /**
     * Print the hashmap, used for testing purposes
     */
    public void printMap()
    {
        Iterator it = this.dataMap.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            GpsDataContainer m = (GpsDataContainer) pair.getValue();
            System.out.println(pair.getKey() + ", long: " + m.getMessage().getLongitude() + ", lat: " + m.getMessage().getLatitude());
            System.out.println("Time: " + m.getLastUpdatedHour()+":"+m.getLastUpdatedMinute());

        }
    }

    /**
     * Function that cleans up the hashmap of old values,
     * if timedifference >= TIMEDIFFERENCE which is thought to be 5-10 mins
     * but can change, it gets removed
     */
    public synchronized void cleanup()
    {
        System.out.println("Cleanup started...");
        HashMap<String, GpsDataContainer> copy = new HashMap<>(this.dataMap);
        final int TIMEDIFFERENCE = 5; //The max amount of timedifference before it gets removed

        Iterator it = copy.entrySet().iterator();
        int hour = ZonedDateTime.now().getHour();
        int minute = ZonedDateTime.now().getMinute();
        int amountRemoved = 0;
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            GpsDataContainer m = (GpsDataContainer) pair.getValue();
            if(hour > m.getLastUpdatedHour())
            {
                int i = m.getLastUpdatedMinute() -(60 + minute);
                if(i <= -TIMEDIFFERENCE)
                {
                    //System.out.println("Old Data found with different hours");
                    this.dataMap.remove(m.getMessage().getUsername());
                    amountRemoved++;
                }
            }
            else if(hour == m.getLastUpdatedHour())
            {
                int j = m.getLastUpdatedMinute() - minute;
                if(j < -TIMEDIFFERENCE)
                {
                    //System.out.println("Old data found with same hour");
                    this.dataMap.remove(m.getMessage().getUsername());
                    amountRemoved++;
                }
            }
        }
        System.out.println("Cleanup done with " + amountRemoved + " removed");
    }

    /**
     * Threaded cleanup-function that calls the cleanup if the hashmap is not empty.
     * @param interval
     */
    public void startCleanupThread(int interval)
    {
        long timeInterval = 1000 * 60 * interval; //minutes -> milliseconds

        Thread cleanUpThread = new Thread(() -> {
            while(true)
            {
                try
                {
                    if(this.dataMap.size() > 0)
                    {
                        cleanup();
                    }
                    else
                    {
                        System.out.println("Cleanup Skipped, map is empty");
                    }
                    Thread.sleep(timeInterval);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        cleanUpThread.setDaemon(true);
        cleanUpThread.start();
    }

    /**'
     * Inner class for containing some extra data that is not used anywhere else
     */
    private class GpsDataContainer
    {
        private GPSCoordMessage message;
        private int lastUpdatedHour;
        private int lastUpdatedMinute;

        /**
         * Constructor that takes a message that was sent over socket with some
         * longitude and latitude values and a email, also uses the time from the computer
         * to save for later use.
         * @param message
         * @param lastUpdated
         */
        public GpsDataContainer(GPSCoordMessage message, ZonedDateTime lastUpdated)
        {
            this.message = message;
            this.lastUpdatedHour = lastUpdated.getHour();
            this.lastUpdatedMinute = lastUpdated.getMinute();
        }

        //region Getters and Setters
        public GPSCoordMessage getMessage() {
            return message;
        }

        public void setMessage(GPSCoordMessage message) {
            this.message = message;
        }

        public int getLastUpdatedMinute() {
            return lastUpdatedMinute;
        }

        public void setLastUpdatedMinute(int lastUpdatedMinute) {
            this.lastUpdatedMinute = lastUpdatedMinute;
        }

        public int getLastUpdatedHour() {
            return lastUpdatedHour;
        }

        public void setLastUpdatedHour(int lastUpdatedHour) {
            this.lastUpdatedHour = lastUpdatedHour;
        }
        //endregion
    }
}
