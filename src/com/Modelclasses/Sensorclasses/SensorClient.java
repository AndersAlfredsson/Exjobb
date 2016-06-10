package com.Modelclasses.Sensorclasses;

import com.Modelclasses.Dataclasses.SensorDataHandler;
import com.Modelclasses.Serverclasses.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Gustav on 2016-05-06.
 *
 */
public class SensorClient implements Runnable
{
    // COMPUTER MUST BE ON GUESTNET!
    private Socket socket;
    private final int PORT = 2390;
    private DataInputStream IN;
    private boolean isConnected;
    private final SensorDataHandler dataHandler;
    private final IpContainer CONTAINER;


    public SensorClient(SensorDataHandler dataHandler, IpContainer ipContainer)
    {
        this.dataHandler = dataHandler;
        this.CONTAINER = ipContainer;
        try
        {
            this.isConnected = false;
            socket = new Socket(this.CONTAINER.getIP_ADDRESS(), PORT);
            IN = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        //System.out.println("Started sensorconnection");
        isConnected = true;
        while(isConnected)
        {
            try
            {
                if(!Server.isIsServerShutdownInitiated())
                {
                    int message = IN.readInt();
                    if (message == 0)
                    {
                        disconnect();
                    }
                    else
                    {
                        //System.out.println("recieved message: " + message);
                        this.dataHandler.newValue(message);
                    }
                }
                else
                {
                    disconnect();
                }

            }
            catch (IOException e)
            {
                reconnect();
                //e.printStackTrace();
                System.err.println("Sensor disconnected...");
            }
        }
    }

    private void disconnect()
    {
        try
        {
            isConnected = false;
            this.socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void reconnect()
    {
        this.isConnected = false;
        int i = 0;
        while(!this.isConnected || i < 5)
        {
            try
            {
                socket = new Socket(this.CONTAINER.getIP_ADDRESS(), PORT);
                IN = new DataInputStream(socket.getInputStream());
                Thread.sleep(1000);
                i++;
                this.isConnected = true;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                this.isConnected = false;
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                this.isConnected = false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                this.isConnected = false;
            }
        }
    }
}
