package com.Modelclasses.Serverclasses;

import com.Modelclasses.Dataclasses.GpsDataHandler;

import java.io.IOException;

/**
 * Created by Gustav on 2016-04-29.
 * The main class for the serverapplication, just a container for all the other server modules
 */
public class Server {
    private GpsDataHandler gpsDataHandler;
    private final int CLEANUP_INTERVAL;

    public Server()
    {
        this.gpsDataHandler = new GpsDataHandler();
        this.CLEANUP_INTERVAL = 2;
    }

    public void startServer()
    {
        try
        {
            (new Thread(new LoginServer(5, 9058, gpsDataHandler))).start();
            gpsDataHandler.startCleanupThread(CLEANUP_INTERVAL);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
