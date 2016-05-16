package com.Modelclasses.Dataclasses;

import NetworkMessages.GPSCoordMessage;
import NetworkMessages.GpsCoordinates;
import com.DBcommunication.DBhandlerSingleton;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Gustav on 2016-04-26.
 * The class that handles GPS-data from clients
 */
public class GpsDataHandler
{
    private HashMap<String, GpsDataContainer> dataMap;
    private int messagesReceived;
    private final int logMinimumDelay = 8000;
    private long currentTime;
    private long previousTime;
    private final BoundingBox INNER_BOX = new BoundingBox(59.25545, 15.243498, 59.253333, 15.252124);
    private final BoundingBox OUTER_BOX = new BoundingBox(59.256789, 15.240086, 59.251622, 15.256308);

    /**
     * Default constructor
     */
    public GpsDataHandler()
    {
        this.dataMap = new HashMap<>();
        this.messagesReceived = 0;
    }

    /**
     * Puts a new object in the map, but checks first if there is already one from the same user,
     * if it is, it removes the object first and then adds the new one.
     * @param message Information about gpsCoordinates
     */
    public synchronized void putData(GPSCoordMessage message, int anonymousID)
    {
        GpsDataContainer container = new GpsDataContainer(message, ZonedDateTime.now());
        if(this.INNER_BOX.isInsideBox(message))
        {
            logGPS(message, anonymousID);
            if(dataMap.containsKey(message.getUsername()))
            {
                //System.out.println("Replaced data in map");
                dataMap.remove(message.getUsername());
                dataMap.put(message.getUsername(), container);
                this.messagesReceived++;
            }
            else
            {
                //System.out.println("Put data in map");
                dataMap.put(message.getUsername(), container);
                this.messagesReceived++;
            }
        }
//        else
//        {
//            System.out.println("Coordinate outside bounding box, save skipped");
//        }

    }

    /**
     * Print the hashmap, used for testing purposes
     */
    public void printMap()
    {
        for (Object o : this.dataMap.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            GpsDataContainer m = (GpsDataContainer) pair.getValue();
            System.out.println(pair.getKey()  + ", lat: " + m.getMessage().getLatitude() + ", long: " + m.getMessage().getLongitude());
        }
        System.out.println("Messages total: " + this.messagesReceived);
    }

    /**
     * Function that cleans up the hashmap of old values,
     * if timedifference >= TIMEDIFFERENCE which is thought to be 5-10 mins
     * but can change, it gets removed
     */
    public synchronized void cleanup()
    {
        System.out.println("GpsData Cleanup started...");
        HashMap<String, GpsDataContainer> copy = new HashMap<>(this.dataMap);
        final int TIMEDIFFERENCE = 2; //The max amount of time difference in minutes before it gets removed

        Iterator it = copy.entrySet().iterator();
        int hour = ZonedDateTime.now().getHour();
        int minute = ZonedDateTime.now().getMinute();
        int amountRemoved = 0;
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            GpsDataContainer m = (GpsDataContainer) pair.getValue();
            if(hour > m.getLastUpdatedHour())//Checks if hour is larger than the saved hour
            {
                int i = m.getLastUpdatedMinute() -(60 + minute);//Gives different algorithms
                if(i <= -TIMEDIFFERENCE)//Checks if difference is bigger than the set range
                {
                    this.dataMap.remove(m.getMessage().getUsername());
                    amountRemoved++;
                }
            }
            else if(hour == m.getLastUpdatedHour())//Checks if hour is the same hour as saved
            {
                int j = m.getLastUpdatedMinute() - minute;//Gives different algorithms
                if(j < -TIMEDIFFERENCE)//Checks if difference is bigger than the set range
                {
                    this.dataMap.remove(m.getMessage().getUsername());
                    amountRemoved++;
                }
            }
        }
        System.out.println("GpsData Cleanup done with " + amountRemoved + " removed");
    }

    private void logGPS(GPSCoordMessage message, int anonymousID) {
        currentTime = System.currentTimeMillis();
        if (currentTime >= (previousTime + logMinimumDelay)) {
            DBhandlerSingleton.getInstance().logGPS(anonymousID, message.getLatitude(),message.getLongitude());
            previousTime = currentTime;
        }

    }

    //region Getters & Setters
    /**
     * Gets the size of the datamap
     * @return
     */
    public int getMapSize()
    {
        return this.dataMap.size();
    }

    /**
     * Gets a list of GpsCoordinates that is just a long and lat value stored in a oontainer
     * @param email
     * @return Returns a list of all coordinates
     */
    public ArrayList<GpsCoordinates> getGpsData(String email)
    {
        ArrayList<GpsCoordinates> data = new ArrayList<>();
        Iterator it = dataMap.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            GpsDataContainer container = (GpsDataContainer) pair.getValue();
            if(!container.getMessage().getUsername().equals(email))
            {
                GpsCoordinates gpsData = new GpsCoordinates(container.getMessage().getLatitude(), container.getMessage().getLongitude());
                data.add(gpsData);
            }
        }
        return data;
    }

    /**
     * Gets the inner bounding box
     * @return
     */
    public BoundingBox getINNER_BOX() {
        return INNER_BOX;
    }

    /**
     * Gets the outer bounding boc
     * @return
     */
    public BoundingBox getOUTER_BOX() {
        return OUTER_BOX;
    }

    //endregion

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
         * @param message Information about gpsCoordinates
         * @param lastUpdated A time when server received message
         */
        public GpsDataContainer(GPSCoordMessage message, ZonedDateTime lastUpdated)
        {
            this.message = message;
            this.lastUpdatedHour = lastUpdated.getHour();
            this.lastUpdatedMinute = lastUpdated.getMinute();
            System.out.println("time: " + lastUpdatedHour + ":" + lastUpdatedMinute + ":"+lastUpdated.getSecond());
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
