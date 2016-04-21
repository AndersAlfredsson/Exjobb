package com.Modelclasses.NetworkMessages;

import java.io.Serializable;

/**
 * Created by Goustmachine on 2016-04-21.
 */
public class Message implements Serializable
{
    private String username;

    protected  Message()
    {
        this.username = null;
    }

    protected Message(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
