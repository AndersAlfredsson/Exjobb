package com.Modelclasses.Serverclasses;

import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.Dataclasses.SensorDataHandler;
import com.Modelclasses.Janitor.Janitor;

import java.io.IOException;

/**
 * Created by Gustav on 2016-04-29.
 * The main class for the serverapplication, just a container for all the other server modules
 */
public class Server {
    private GpsDataHandler gpsDataHandler;
    private SensorDataHandler sensorDataHandler;
    private final Janitor janitor;
    private final int CLEANUP_INTERVAL;

    public Server()
    {
        this.gpsDataHandler = new GpsDataHandler();
        this.sensorDataHandler = new SensorDataHandler();
        this.janitor = new Janitor(gpsDataHandler, sensorDataHandler, 2);
        this.CLEANUP_INTERVAL = 5;
    }

    public void startServer()
    {
        try
        {
            (new Thread(new LoginServer(5, 9058, gpsDataHandler))).start();
            janitor.startCleanupThread();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
