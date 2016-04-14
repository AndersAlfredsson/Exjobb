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
    DatabaseConnection dbc = new DatabaseConnection();

    public ApplicationUserDAOimpl(){
    }

    private void connectToDB(){
        dbc.setupDBConnection();
        dbc.connectToDB();
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

    //Skydd mot sqlinjection med Preparet Statement.
    @Override
    public void insertUser(ApplicationUser user) {
        connectToDB();
        try {
            PreparedStatement pstatement =  dbc.getConnection().prepareStatement("INSERT INTO Users(ID, Email, Password, Salt) VALUES (?, ?, ?, ?);");
            pstatement.setInt(1, user.getID());
            pstatement.setString(2,user.getEmail());
            pstatement.setString(3, user.getPassword());
            pstatement.setString(4, user.getSalt());
            pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
