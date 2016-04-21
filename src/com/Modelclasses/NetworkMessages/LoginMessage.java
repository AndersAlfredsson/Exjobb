package com.Modelclasses.NetworkMessages;

/**
 * Created by Goustmachine on 2016-04-21.
 */
public class LoginMessage extends Message
{
    private String password;

    public LoginMessage()
    {
        super();
        this.password = null;
    }

    public LoginMessage(String username, String password)
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
