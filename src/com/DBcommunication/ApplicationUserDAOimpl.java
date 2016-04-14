package com.DBcommunication;

import com.Interfaces.ApplicationUserDAO;
import com.Modelclasses.ApplicationUser;
import com.sun.glass.ui.EventLoop;

import java.sql.*;
import java.util.List;


/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUserDAOimpl implements ApplicationUserDAO {
    
    public ApplicationUserDAOimpl(){
    }

    @Override
    public List<ApplicationUser> getAllUsers() {
        return null;
    }

    @Override
    public ApplicationUser getUser(String email) {
        return null;
    }

    @Override
    public void updateUser(ApplicationUser user) {

    }

    @Override
    public void deleteUser(ApplicationUser user) {

    }

    @Override
    public void insertUser(ApplicationUser user) {
//        try {
//            statement.execute(
//                    "INSERT INTO Users (ID, Email, Password, Salt) " +
//                            "VALUES (" + user.getID() + "," + user.getEmail() + "," + user.getPassword() + "," + user.getSalt() + ");"
//            );
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
}
