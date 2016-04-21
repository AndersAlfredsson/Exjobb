package com.Modelclasses.NetworkMessages;

import com.Enums.ServerMessageType;

import java.io.Serializable;

/**
 * Created by Goustmachine on 2016-04-21.
 */
public class ServerMessage implements Serializable
{
    private ServerMessageType messageType;
    private String message;

    public ServerMessage(ServerMessageType type, String message)
    {
        this.messageType = type;
        this.message = message;
    }

    //region Getters & Setters
    public ServerMessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(ServerMessageType messageType)
    {
        this.messageType = messageType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    //endregion
}
