package com.Modelclasses.Sensorclasses;

import com.Modelclasses.Dataclasses.SensorDataHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Gustav on 2016-05-06.
 *
 */
public class SensorClient implements Runnable
{
    // COMPUTER MUST BE ON GUESTNET!
    private Socket socket;
    private String IP_ADDRESS = "127.0.0.1";
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

    public SensorClient(SensorDataHandler dataHandler, String IP)
    {
        try
        {
            this.isConnected = false;
            InetAddress address = InetAddress.getByName(IP_ADDRESS);
            socket = new Socket(address, PORT);
            IN = new InputStreamReader(socket.getInputStream());
            this.dataHandler = dataHandler;
            this.IP_ADDRESS = IP;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        isConnected = true;
        while(isConnected) {
            try {
                int message = IN.read();
                if (message == 0) {
                    disconnect();
                } else {
                    //dataHandler.addPersonToSection(message);
                }
            } catch (IOException e) {
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
