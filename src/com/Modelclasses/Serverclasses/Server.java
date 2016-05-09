package com.Modelclasses.Serverclasses;

import com.DBcommunication.DBhandlerSingleton;
import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.Dataclasses.SensorDataHandler;
import com.Modelclasses.Janitor.Janitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 * Created by Gustav on 2016-04-29.
 * The main class for the serverapplication, just a container for all the other server modules
 */
public class Server
{
    private static boolean isServerShutdown = false;
    private GpsDataHandler gpsDataHandler;
    private LoginServer loginServer;
    private SensorDataHandler sensorDataHandler;
    private final Janitor janitor;


    public Server()
    {
        this.gpsDataHandler = new GpsDataHandler();
        this.sensorDataHandler = new SensorDataHandler();
        this.janitor = new Janitor(gpsDataHandler, sensorDataHandler, 3, 2);

    }

    /**
     * Starts a new server and all the components
     */
    public void startServer()
    {
        try
        {
            (new Thread(this.loginServer = new LoginServer(9058, gpsDataHandler))).start();
            janitor.startCleanupThread();
            System.out.println("Type 'quit', 'shutdown' or 'exit' to initiate shutdown");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            String line = "";
            while(!line.equalsIgnoreCase("quit") && !line.equalsIgnoreCase("shutdown") && !line.equalsIgnoreCase("exit")){
                line = in.readLine();
            }
            shutdown();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            shutdown();
        }
    }

    /**
     * Starts the shutdown process, takes several minutes to wait for everything to finish
     */
    public void shutdown()
    {
        isServerShutdown = true;
        System.out.println("Shutdown initiated");
        System.out.println("Please wait, this might take several minutes");
        janitor.setRunCleanup(false);
        this.loginServer.shutdown();
        try
        {
            DBhandlerSingleton.getInstance().getConnection().close();
            this.loginServer.getServerSocket().close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isIsServerShutdownInitiated()
    {
        return isServerShutdown;
    }
}
