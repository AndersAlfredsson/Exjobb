package com.DBcommunication;

import com.Interfaces.ApplicationUserDAO;
import com.Modelclasses.ApplicationUser;
import com.sun.glass.ui.EventLoop;

import java.sql.*;
import java.util.*;


/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUserDAOimpl implements ApplicationUserDAO {
    DatabaseConnection dbc = new DatabaseConnection();

    public ApplicationUserDAOimpl(){
    }

    //TODO : ska flyttas till annan klass
    private void connectToDB(){
        dbc.setupDBConnection();
        dbc.connectToDB();
    }

    @Override
    public List<ApplicationUser> getAllUsers() {
        connectToDB();
        try {
            PreparedStatement pstatement =  dbc.getConnection().prepareStatement("SELECT * FROM Users");
            ResultSet result = pstatement.executeQuery();
            return generateListFromResultSet(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ApplicationUser> generateListFromResultSet(ResultSet resultSet){
        List<ApplicationUser> userList = new LinkedList<>();
        try {
            while (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String email = resultSet.getString("Email");
                String password = resultSet.getString("Password");
                String salt = resultSet.getString("Salt");
                ApplicationUser user = new ApplicationUser(ID, email, password, salt);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public ResultSet getUser(String email) {
        return null;
    }

    /**
     * Updates a users password
     * @param user
     */
    @Override
    public void updateUser(ApplicationUser user) {
        connectToDB();
        try {
            PreparedStatement pstatement =  dbc.getConnection().prepareStatement("UPDATE Users SET Password = ? WHERE Email = ?;");
            pstatement.setString(1, user.getPassword());
            pstatement.setString(2, user.getEmail());
            pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the database.
     * @param user
     */
    @Override
    public void deleteUser(ApplicationUser user) {
        connectToDB();
        try {
            PreparedStatement pstatement =  dbc.getConnection().prepareStatement("DELETE FROM Users WHERE Email = ?;");
            pstatement.setString(1, user.getEmail());
            pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Skydd mot sqlinjection med Preparet Statement.

    /**
     * Inserts a user into the database.
     * @param user
     */
    @Override
    public void insertUser(ApplicationUser user) {
        connectToDB();
        try {
            PreparedStatement pstatement =  dbc.getConnection().prepareStatement("INSERT INTO Users(Email, Password, Salt) VALUES (?, ?, ?);");
            pstatement.setString(1, user.getEmail());
            pstatement.setString(2, user.getPassword());

            //Fusksalt tillsvidare.
            pstatement.setString(3, "igigiohuiuiugi");
            pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
