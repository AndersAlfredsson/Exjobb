package com.company.Modelclasses;

/**
 * Created by Anders on 2016-04-14.
 */
public class ApplicationUser {
    private int ID;
    private String email;
    private String password;

    public ApplicationUser(String email, String password){
        this.email = email;
        this.password = password;
    }
}
