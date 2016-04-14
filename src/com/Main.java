package com;

import com.DBcommunication.ApplicationUserDAOimpl;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.PasswordSecurity;

public class Main {

    public static void main(String[] args) {
        ApplicationUserDAOimpl testdao = new ApplicationUserDAOimpl();
        ApplicationUser user = new ApplicationUser(2,"a@a.com", "TAYTO", "");
        PasswordSecurity ps = new PasswordSecurity(user);
        testdao.insertUser(user);
    }
}
