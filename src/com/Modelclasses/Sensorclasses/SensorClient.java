package com.Modelclasses.Sensorclasses;

import com.Modelclasses.Dataclasses.SensorDataHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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

    //TODO containerclass för klienttrådar
    //TODO sensor simulator

    public SensorClient(SensorDataHandler dataHandler, String IP)
    {
        this.dataHandler = dataHandler;
        try
        {
            this.isConnected = false;
            socket = new Socket(IP, PORT);
            IN = new DataInputStream(socket.getInputStream());


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        isConnected = true;
        while(isConnected)
        {
            try {
                int message = IN.readInt();
                if (message == -1)
                {
                    disconnect();
                }
                else
                {
                    //System.out.println("recieved message: " + message);
                    this.dataHandler.newValue(message);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
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
}
