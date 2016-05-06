package com.Modelclasses.Sensorclasses;

import com.Modelclasses.Dataclasses.SensorDataHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Gustav on 2016-05-06.
 */
public class SensorClient
{
    // COMPUTER MUST BE ON GUESTNET!
    private Socket socket;
    private final String IP_ADDRESS = "127.0.0.1";
    private final int PORT = 2390;
    private InputStreamReader IN;
    private boolean isConnected;
    private SensorDataHandler dataHandler;

    //TODO containerclass för klienttrådar
    //TODO sensor simulator
    //TODO sensorlogik
    //TODO spara lista med statiska IP-adresser till fil/hårdkodad klass
    //TODO
    //TODO
    //TODO

    public SensorClient(SensorDataHandler dataHandler)
    {
        try
        {
            this.isConnected = false;
            InetAddress address = InetAddress.getByName(IP_ADDRESS);
            socket = new Socket(address, PORT);
            IN = new InputStreamReader(socket.getInputStream());
            this.dataHandler = dataHandler;
            runClient();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void runClient()
    {
        isConnected = true;
        Thread thread = new Thread(() -> {
            while(isConnected)
            {
                try
                {
                    int message = IN.read();
                    if(message == 0)
                    {
                        disconnect();
                    }
                    else
                    {
                        dataHandler.addPersonToSection(message);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
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
