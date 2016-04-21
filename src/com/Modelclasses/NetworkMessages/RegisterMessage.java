package com.Modelclasses.NetworkMessages;

/**
 * Created by Goustmachine on 2016-04-21.
 */
public class RegisterMessage extends Message
{
    private String password;

    public RegisterMessage(String username, String password)
    {
        super(username);
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
