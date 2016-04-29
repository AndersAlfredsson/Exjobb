package com;

import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.Serverclasses.LoginServer;
import com.Modelclasses.Serverclasses.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();

    }
}
