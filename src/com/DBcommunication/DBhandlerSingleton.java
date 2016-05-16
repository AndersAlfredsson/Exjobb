package com.DBcommunication;

import com.Enums.LogEvents;
import com.Logging.Logging;
import com.Modelclasses.ApplicationUser;

import java.sql.*;
import java.util.List;


/**
 * Created by Anders on 2016-04-14.
 */
public class DBhandlerSingleton {
    private static DBhandlerSingleton INSTANCE = null;
    private ApplicationUserDAOimpl applicationUserDAOimpl;
    private Logging logger;
    private Connection connection = null;
    private ResultSet resultSet = null;
    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "JDBC:sqlite:src/com/Database/DB.db";


    //

    /**
     * Private constructor to prevent initialization from outside of the class.
     * Instantiates the objects used for logging and database communication.
     */
    private DBhandlerSingleton(){
        applicationUserDAOimpl = new ApplicationUserDAOimpl();
        logger = new Logging();
        setupDBConnection();
        connectToDB();
    }

    /**
     * Returns the instance of the singleton.
     * Creates new instance of the object if the INSTANCE variable is null.
     * @return
     */
    public static DBhandlerSingleton getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DBhandlerSingleton();
        }
        return INSTANCE;
    }

    /**
     * Selects the driver to use for the database connection.
     */
    private void setupDBConnection(){
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the database.
     */
    private void connectToDB(){
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the connection to the database is valid.
     * If the connection is not valid an atempt to reconnect to the database will be made.
     * The method returns if the database is valid or not after one reconnection attempt.
     * @return
     */
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

    /**
     * Calls the insertUser in the ApplicationUser class if the connection is valid.
     * @param user
     */
    public synchronized void insertUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.insertUser(user);
        }
    }

    /**
     * Calls the updateUser in the ApplicationUser class if the connection is valid.
     * @param user
     */
    public synchronized void updateUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.deleteUser(user);
        }
    }

    /**
     * Calls the deleteUser in the ApplicationUser class if the connection is valid.
     * @param user to be deleted.
     */
    public synchronized void deleteUser(ApplicationUser user){
        if (checkConnection()){
            applicationUserDAOimpl.updateUser(user);
        }
    }

    /**
     * Calls the getAllUsers in the ApplicationUser class if the connection is valid.
     * @return returns all users in the database.
     */
    public synchronized List<ApplicationUser> getAllUsers(){
        List<ApplicationUser> userList = null;
        if (checkConnection()){
             userList = applicationUserDAOimpl.getAllUsers();
        }
        return userList;
    }

    /**
     * Calls the getUser in the ApplicationUser class if the connection is valid.
     * @param email
     * @return the user with the matching email.
     */
    public synchronized ApplicationUser getUser(String email){
        ApplicationUser user = null;
        if (checkConnection()){
            user = applicationUserDAOimpl.getUser(email);
            //System.out.println(user.getPassword());
            //System.out.println(user.getPassword());
            //System.out.println(user.getSalt());
        }
        return user;
    }

    /**
     * Logs an event to the database by calling the logEvent method in Logging class.
     * @param event
     * @param user
     */
    public synchronized void log(LogEvents event, ApplicationUser user){
        if (checkConnection()){
            logger.logEvent(event, user);
        }
    }

    public synchronized void logGPS(int anonymousID, double latitude, double longitude){
        if (checkConnection()){
            logger.logGPS(anonymousID, latitude, longitude);
        }
    }

    public synchronized void logSensorEvent(int firstSensor, int secondSensor){
        if (checkConnection()){
            logger.logSensorEvent(firstSensor, secondSensor);
        }
    }

    //Getters and Setters.
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
