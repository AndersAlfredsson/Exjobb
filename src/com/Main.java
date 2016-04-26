package com;

import NetworkMessages.GPSCoordMessage;
import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.LoginServerclasses.LoginServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        //ApplicationUser user = new ApplicationUser("dev@dev.com", "dev");
        //DBhandlerSingleton.getInstance().insertUser(user);
        //ApplicationUser DbUser = new ApplicationUser(2, "Logtestaren@a.com", user.getPassword(), user.getSalt());
        //user.setPassword("TAYTO");
//        try {
//            UserHandler userHandler = new UserHandler(new Socket());
//            userHandler.loginAttempt(user);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //testdao.insertUser(user);
        //testdao.deleteUser(new ApplicationUser(2,"a@a.com", "TAYTO", ""));
        //user.setPassword("nyttpassword");
        //testdao.updateUser(user);


        try
        {
            (new Thread(new LoginServer(12, 9058))).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
