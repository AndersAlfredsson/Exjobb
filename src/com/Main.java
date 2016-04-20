package com;

import com.DBcommunication.ApplicationUserDAOimpl;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.LoginServerclasses.LoginServer;
import com.Modelclasses.PasswordSecurity;

import java.io.IOException;
import java.util.concurrent.Executor;

public class Main {

    public static void main(String[] args) {
        ApplicationUserDAOimpl testdao = new ApplicationUserDAOimpl();
        ApplicationUser user = new ApplicationUser(2,"Logtestaren@a.com", "TAYTO", "");
//        PasswordSecurity ps = new PasswordSecurity(user);

        //ApplicationUser DbUser = new ApplicationUser(2, "Logtestaren@a.com", user.getPassword(), user.getSalt());
        //user.setPassword("TAYTO");
//        ps.authenticate(user, DbUser);

        testdao.insertUser(user);
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
