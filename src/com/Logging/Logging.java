package com.Logging;

import com.DBcommunication.DBhandlerSingleton;
import com.Enums.LogEvents;
import com.Modelclasses.ApplicationUser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anders on 2016-04-19.
 * Class to handle logging to database.
 */
public class Logging {
    DateFormat dateFormat;
    public Logging() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * Logs an event to the database.
     *
     * @param event
     * @param user
     */
    public void logEvent(LogEvents event, ApplicationUser user) {
        Date date = new Date();
        try {
            PreparedStatement preparedStatement = DBhandlerSingleton.getInstance().getConnection().prepareStatement(
                    "INSERT INTO Logs (Eventtype, Datetime, Useremail) VALUES (?,?,?)");
            preparedStatement.setString(1, event.toString());
            preparedStatement.setString(2, dateFormat.format(date));
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logGPS(int anonymousID, double latitude, double longitude) {
        Date date = new Date();
        try {
            PreparedStatement preparedStatement = DBhandlerSingleton.getInstance().getConnection().prepareStatement(
                    "INSERT INTO GPSLogs (AnonymousID, Datetime, Latitude, Longitude) VALUES (?,?,?,?)");
            preparedStatement.setString(1, Integer.toString(anonymousID));
            preparedStatement.setString(2, dateFormat.format(date));
            preparedStatement.setString(3, Double.toString(latitude));
            preparedStatement.setString(4, Double.toString(longitude));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logSensorEvent(int firstSensor, int secondSensor) {
        Date date = new Date();
        try {
            PreparedStatement preparedStatement = DBhandlerSingleton.getInstance().getConnection().prepareStatement(
                    "INSERT INTO SensorEvents (Datetime, FirstTriggeredSensor, SecondTriggeredSensor) VALUES (?,?,?)");
            preparedStatement.setString(1, dateFormat.format(date));
            preparedStatement.setString(2, Integer.toString(firstSensor));
            preparedStatement.setString(3, Integer.toString(secondSensor));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}