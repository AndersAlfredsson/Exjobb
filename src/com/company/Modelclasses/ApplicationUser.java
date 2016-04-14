package com.company.Modelclasses;

/**
 * Created by Anders on 2016-04-14.
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
}
