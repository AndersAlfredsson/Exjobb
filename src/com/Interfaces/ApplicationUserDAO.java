package com.Interfaces;

import com.Modelclasses.ApplicationUser;

import java.util.List;

/**
 * Created by Anders on 2016-04-14.
 * Interface implemented by the class that is interacting with the database.
 */
public interface ApplicationUserDAO {
    public List<ApplicationUser> getAllUsers();
    public ApplicationUser getUser(String email);
    public void updateUser(ApplicationUser user);
    public void deleteUser(ApplicationUser user);
    public void insertUser(ApplicationUser user);
}
