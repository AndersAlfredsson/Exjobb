package com.Interfaces;

import com.Modelclasses.ApplicationUser;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by Anders on 2016-04-14.
 * Interface implemented by the class that is interacting with the database.
 */
public interface ApplicationUserDAO {
    List<ApplicationUser> getAllUsers();
    ApplicationUser getUser(String email);
    void updateUser(ApplicationUser user);
    void deleteUser(ApplicationUser user);
    void insertUser(ApplicationUser user);
}
