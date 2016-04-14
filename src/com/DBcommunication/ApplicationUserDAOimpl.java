package com.DBcommunication;

import com.Interfaces.ApplicationUserDAO;
import com.Modelclasses.ApplicationUser;

import java.sql.*;
import java.util.List;


/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUserDAOimpl implements ApplicationUserDAO {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "JDBC:sqlite:src/com/Database/DB.db";

    public ApplicationUserDAOimpl(){
        setupDBConnection();
        connectToDB();
    }

    private void setupDBConnection(){
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void connectToDB(){
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        try {
            statement.execute(
                    "INSERT INTO Users (ID, Email, Password, Salt) " +
                            "VALUES (" + user.getID() + "," + user.getEmail() + "," + user.getPassword() + "," + user.getSalt() + ");"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
