package com.Modelclasses.LoginServerclasses;

import Enums.ServerMessageType;
import NetworkMessages.*;
import com.DBcommunication.DBhandlerSingleton;
import com.Enums.LogEvents;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.PasswordSecurity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by Gustav on 2016-04-19.
 * The threaded class that the LoginServer-class uses to set ut communication with the clients
 */
public class UserHandler implements Runnable, Serializable
{
    private final Socket SOCKET;
    private final ObjectInputStream IN;
    private final ObjectOutputStream OUT;
    private boolean connected;
    private boolean authenticated;

    /**
     * Constructor that sets up the connection and the I/O-ObjectStreams
     * @param socket
     * @throws IOException
     */
    public UserHandler(Socket socket) throws IOException
    {
        this.SOCKET = socket;
        this.IN = new ObjectInputStream(socket.getInputStream());
        this.OUT = new ObjectOutputStream(socket.getOutputStream());
        this.connected = false;
        this.authenticated = false;
    }

    //region Setters & Getters
    /**
     * Gets the ObjectOutputStream
     * @return
     */
    public ObjectOutputStream getOUT()
    {
        return OUT;
    }

    /**
     * Gets the ObjectInputStream
     * @return
     */
    public ObjectInputStream getIN()
    {
        return IN;
    }

    /**
     * Gets connected
     * @return
     */
    public boolean isConnected()
    {
        return connected;
    }

    /**
     * Sets connected
     * @param connected
     */
    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }
    //endregion


    /**
     * The threaded, overridden run-function that handles the communication with the client
     */
    @Override
    public void run()
    {
        this.connected = true;
        System.out.println("Client connected...");
        while(this.connected)
        {
            try
            {
                Message m = (Message) this.IN.readObject();
                handleMessage(m);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                this.connected = false;
                try {
                    this.SOCKET.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks what instance the message is of and then acts accordingly
     * @param message
     */
    private void handleMessage(Message message)
    {
        ApplicationUser user;
        if(message instanceof LoginMessage)
        {
            System.out.println("LoginMessage");
            user = new ApplicationUser(message.getUsername(), ((LoginMessage) message).getPassword());

            this.authenticated = this.loginAttempt(user);

            if(!this.authenticated)
            {
                String reason = "Wrong username or password";
                disconnect(reason);
            }
            else
            {
                sendMessage(new ServerMessage(ServerMessageType.Authenticated, "Login Successful"));
            }
        }
        else if(message instanceof RegisterMessage)
        {
            System.out.println("RegisterMessage");
            user = new ApplicationUser(message.getUsername(), message.getUsername());

            this.authenticated = registerNewUser(user);
            if(!this.authenticated)
            {
                disconnect("Email already in use");
            }
            else
            {
                sendMessage(new ServerMessage(ServerMessageType.Authenticated, "Register & Login Successful"));
            }
        }
        else if(message instanceof DisconnectMessage)
        {
            System.out.println("Disconnectmessage");
            DBhandlerSingleton.getInstance().log(LogEvents.Disconnect, new ApplicationUser(message.getUsername(), null));
            disconnect("Disconnect request");
        }
    }

    /**
     * Sends a message to the client
     * @param message
     */
    private void sendMessage(ServerMessage message)
    {
        if(this.connected)
        {
            try
            {
                this.OUT.writeObject(message);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disconnects a client from the server by sending a disconnect to the client
     * @return
     */
    private boolean disconnect(String reason)
    {
        try
        {
            sendMessage(new ServerMessage(ServerMessageType.Disconnect, reason));
            this.OUT.flush();
            this.SOCKET.close();
            this.connected = false;
            this.authenticated = false;
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Testing function for "registering an account" over the socket, will change soon
     * @param user
     */
    private boolean registerNewUser(ApplicationUser user)
    {
        if(DBhandlerSingleton.getInstance().getUser(user.getEmail()) == null) //TODO något är fel här
        {
            PasswordSecurity.hashPassword(user);
            DBhandlerSingleton.getInstance().insertUser(user);
            System.out.println("Register Complete");
            return true;
        }
        System.out.println("Register not possible");
        return false;
    }


    /**
     * Testing function for "a login attempt" over the socket, will change soon
     * @param user
     */
    public boolean loginAttempt(ApplicationUser user)
    {
        ApplicationUser dbUser = DBhandlerSingleton.getInstance().getUser(user.getEmail());

        if(PasswordSecurity.authenticate(user, dbUser))
        {
            System.out.println("Login Successful");
            DBhandlerSingleton.getInstance().log(LogEvents.SuccessfulLoginAttempt, user);
            return true;
        }
        else
        {
            System.out.println("Login unsuccessful");
            DBhandlerSingleton.getInstance().log(LogEvents.UnsuccessfulLoginAttempt, user);
            return false;
        }
    }
}
