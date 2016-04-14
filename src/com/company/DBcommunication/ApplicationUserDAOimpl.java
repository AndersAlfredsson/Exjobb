package com.company.DBcommunication;

import com.company.Interfaces.ApplicationUserDAO;
import com.company.Modelclasses.ApplicationUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUserDAOimpl implements ApplicationUserDAO {
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;



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

    }
}
