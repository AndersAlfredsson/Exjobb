package com.DBcommunication;

import com.Enums.LogEvents;
import com.Logging.Logging;
import com.Modelclasses.ApplicationUser;

import java.sql.*;
import java.util.List;


/**
 * Created by Anders on 2016-04-14.
 */
//Singeltonera mera.
public class DBhandlerSingleton {
    private static DBhandlerSingleton INSTANCE = null;
    private ApplicationUserDAOimpl applicationUserDAOimpl;
    private Logging logger;
    private Connection connection = null;
    private ResultSet resultSet = null;
    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "JDBC:sqlite:src/com/Database/DB.db";


    private DBhandlerSingleton(){
        applicationUserDAOimpl = new ApplicationUserDAOimpl();
        logger = new Logging();
        setupDBConnection();
        connectToDB();
    }

    public static DBhandlerSingleton getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DBhandlerSingleton();
        }
        return INSTANCE;
    }

    public void setupDBConnection(){
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void connectToDB(){
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkConnection(){
        try {
            if (!connection.isValid(10)){
                connectToDB();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            return connection.isValid(10);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.insertUser(user);
        }
    }

    public void updateUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.deleteUser(user);
        }
    }

    public void deleteUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.updateUser(user);
        }
    }

    public List<ApplicationUser> getAllUsers(){
        List<ApplicationUser> userList = null;
        if (checkConnection()){
             userList = applicationUserDAOimpl.getAllUsers();
        }
        return userList;
    }

    public ApplicationUser getUser(String email){
        ApplicationUser user = null;
        if (checkConnection()){
            user = applicationUserDAOimpl.getUser(email);
        }
        return user;
    }

    public void log(LogEvents event, ApplicationUser user){
        if (checkConnection()){
            logger.logEvent(event, user);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
}
