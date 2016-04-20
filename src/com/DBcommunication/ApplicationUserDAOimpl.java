package com.DBcommunication;

import com.Enums.LogEvents;
import com.Interfaces.ApplicationUserDAO;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.PasswordSecurity;

import java.sql.*;
import java.util.*;


/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUserDAOimpl implements ApplicationUserDAO {


    public ApplicationUserDAOimpl(){

    }

    @Override
    public List<ApplicationUser> getAllUsers() {
        try {
            PreparedStatement preparedStatement =  DBhandlerSingleton.getInstance().getConnection().prepareStatement("SELECT * FROM Users");
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
        try {
            PreparedStatement preparedStatement =  DBhandlerSingleton.getInstance().getConnection().prepareStatement("SELECT * FROM Users WHERE email = ?");
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
        try {
            PreparedStatement preparedStatement =  DBhandlerSingleton.getInstance().getConnection().prepareStatement("UPDATE Users SET Password = ? WHERE Email = ?;");
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getEmail());
            if (preparedStatement.executeUpdate() == 0){
                System.out.println("Trollololo");
            }
            else {
                DBhandlerSingleton.getInstance().log(LogEvents.Insert, user);
                System.out.println("log");
            }

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
        try {
            PreparedStatement preparedStatement =  DBhandlerSingleton.getInstance().getConnection().prepareStatement("DELETE FROM Users WHERE Email = ?;");
            preparedStatement.setString(1, user.getEmail());
            if (preparedStatement.executeUpdate() == 0){
                System.out.println("Trollololo");
            }
            else {
                DBhandlerSingleton.getInstance().log(LogEvents.Insert, user);
                System.out.println("log");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a user into the database.
     * @param user
     */
    @Override
    public void insertUser(ApplicationUser user) {
        PasswordSecurity.hashPassword(user);
        try {
            PreparedStatement preparedStatement =  DBhandlerSingleton.getInstance().getConnection().prepareStatement("INSERT INTO Users(Email, Password, Salt) VALUES (?, ?, ?);");
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getSalt());
            if (preparedStatement.executeUpdate() == 0){
                System.out.println("Trollololo");
            }
            else {
                DBhandlerSingleton.getInstance().log(LogEvents.Insert, user);
                System.out.println("log");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
