package com;

import com.DBcommunication.ApplicationUserDAOimpl;
import com.DBcommunication.DBhandlerSingleton;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.LoginServerclasses.LoginServer;
import com.Modelclasses.LoginServerclasses.UserHandler;
import com.Modelclasses.PasswordSecurity;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;

public class Main {

    public static void main(String[] args) {
        ApplicationUser user = new ApplicationUser(2,"Logtestarensbrorskompis@a.com", "TAYjnTP", "");
        DBhandlerSingleton.getInstance().insertUser(user);
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


//        try
//        {
//            (new Thread(new LoginServer(12, 3000))).start();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
