package com.DBcommunication;

import java.sql.*;

/**
 * Created by Anders on 2016-04-14.
 */
public class DatabaseConnection {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "JDBC:sqlite:src/com/Database/DB.db";

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
}
