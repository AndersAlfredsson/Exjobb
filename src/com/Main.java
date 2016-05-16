package com;

import com.Modelclasses.Dataclasses.SensorSimulator;
import com.Modelclasses.Serverclasses.Server;

public class Main {

    public static void main(String[] args)
    {
        SensorSimulator s = new SensorSimulator();
        Server server = new Server();
        server.startServer();
    }
}
