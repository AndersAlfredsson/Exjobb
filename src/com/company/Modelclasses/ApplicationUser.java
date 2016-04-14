package com.company.Modelclasses;

/**
 * Created by Anders on 2016-04-14.
 * Class representing an android application user.
 */
public class ApplicationUser {
    private int ID;
    private String email;
    private String password;
    //http://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm

    public ApplicationUser(String email, String password){
        this.email = email;
        this.password = password;
    }

    //Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
