package com.DBcommunication;

import java.sql.*;

/**
 * Created by Anders on 2016-04-14.
 */
public class DatabaseConnection {
    private Connection connection = null;
    private ResultSet resultSet = null;
    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "JDBC:sqlite:src/com/Database/DB.db";

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
