package com.Modelclasses.Sensorclasses;

import java.net.Socket;

/**
 * Created by Gustav on 2016-05-06.
 */
public class SensorClient implements Runnable
{
    // COMPUTER MUST BE ON GUESTNET!
    private Socket socket;
    private final String IP_ADDRESS = "127.0.0.1";

    public SensorClient()
    {

    }

    @Override
    public void run() {

    }
}
