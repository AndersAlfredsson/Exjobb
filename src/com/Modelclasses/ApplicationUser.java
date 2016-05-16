package com.Modelclasses;

import java.io.Serializable;

/**
 * Created by Anders on 2016-04-14.
 * Class representing an android application user.
 */
public class ApplicationUser implements Serializable
{
    private int ID;
    private int anonymousID;
    private String email;
    private String password;
    private String salt;

    public ApplicationUser(int ID, String email, String password, String salt){
        this.ID = ID;
        this.salt = salt;
        this.email = email;
        this.password = password;
    }

    public ApplicationUser(String email, String password){
        this.email = email;
        this.password = password;
    }

    /**
     * For printing purposes.
     * @return
     */
    @Override
    public String toString() {
        return ID + email + password + salt;
    }

    public void print(){
        System.out.println(this.toString());
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getAnonymousID() {
        return anonymousID;
    }

    public void setAnonymousID(int anonymousID) {
        this.anonymousID = anonymousID;
    }
}
