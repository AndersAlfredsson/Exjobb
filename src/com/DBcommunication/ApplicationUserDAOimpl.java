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
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement("SELECT * FROM Users");
            ResultSet result = preparedStatement.executeQuery();
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

    private ApplicationUser generateUserFromResultSet(ResultSet resultSet){
        try {
            if (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String email = resultSet.getString("Email");
                String password = resultSet.getString("Password");
                String salt = resultSet.getString("Salt");
                ApplicationUser user = new ApplicationUser(ID, email, password, salt);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ApplicationUser getUser(String email) {
        connectToDB();
        try {
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement("SELECT * FROM Users WHERE email = ?");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            return generateUserFromResultSet(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement("UPDATE Users SET Password = ? WHERE Email = ?;");
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.executeUpdate();
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
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement("DELETE FROM Users WHERE Email = ?;");
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.executeUpdate();
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
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement("INSERT INTO Users(Email, Password, Salt) VALUES (?, ?, ?);");
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());

            //Fusksalt tillsvidare.
            preparedStatement.setString(3, "igigiohuiuiugi");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
