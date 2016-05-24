package com.Modelclasses.Dataclasses;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Goustmachine on 2016-05-05.
 * Simulation of sensor events
 */
public class SensorSimulator
{
    private HashMap<Integer, Sensor> outMap;
    private final ExecutorService pool;
    public boolean runSimulator;


    /**
     * Listens for connections from server
     */
    public SensorSimulator()
    {
        outMap = new HashMap<>();
        this.pool = Executors.newCachedThreadPool();
        runSimulator = true;
        start();


    }
    private void start()
    {
        Thread test = new Thread (() -> {
            ServerSocket s;
            int i = 0;
            try
            {
                System.out.println("SensorListener started");
                s = new ServerSocket(2390);
                while(runSimulator)
                {
                    Sensor sensor;
                    pool.execute(sensor = new Sensor(s.accept(), i++, i++));
                    outMap.put(sensor.getID1(), sensor);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                shutdownAndAwaitTermination();
            }
        });
        test.setDaemon(true);
        test.start();
    }

    /**
     * Shutdowns
     */
    public void shutdownAndAwaitTermination()
    {
        runSimulator = false;
        try
        {
            if(!pool.awaitTermination(10, TimeUnit.SECONDS))
            {
                pool.shutdownNow();
                if(!pool.awaitTermination(10, TimeUnit.SECONDS))
                {
                    System.err.println("Pool did not terminate correctly");
                }
            }
            System.out.println("SimulatorPool exited...");
        }
        catch(InterruptedException ie)
        {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
