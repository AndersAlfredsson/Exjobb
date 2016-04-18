package com;

import com.DBcommunication.ApplicationUserDAOimpl;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.PasswordSecurity;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ApplicationUserDAOimpl testdao = new ApplicationUserDAOimpl();
        ApplicationUser user = new ApplicationUser("b@n.com", "TATO");
        //PasswordSecurity ps = new PasswordSecurity(user);
        //testdao.insertUser(user);
        //testdao.deleteUser(new ApplicationUser(2,"a@a.com", "TAYTO", ""));
        //user.setPassword("nyttpassword");
        //testdao.updateUser(user);

        testdao.getUser("a@a.com").print();

        //List<ApplicationUser> list = testdao.getAllUsers();
        //for (int i = 0; i < list.size(); i++) {
        //  list.get(i).print();
        //}
    }
}
