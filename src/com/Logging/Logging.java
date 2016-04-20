package com.Logging;

import com.DBcommunication.DatabaseConnection;
import com.Enums.LogEvents;
import com.Modelclasses.ApplicationUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anders on 2016-04-19.
 */
public class Logging {
    DatabaseConnection dbc;

    public Logging() {
        this.dbc = new DatabaseConnection();
    }

    private void connectToDB(){
        dbc.setupDBConnection();
        dbc.connectToDB();
    }

    public void logEvent(LogEvents event, ApplicationUser user){
        connectToDB();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try {
            PreparedStatement preparedStatement =  dbc.getConnection().prepareStatement(
                    "INSERT INTO Logs (Eventtype, Datetime, Useremail) VALUES (?,?,?)");
            preparedStatement.setString(1, event.toString());
            preparedStatement.setString(2, dateFormat.format(date));
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
