package com.Modelclasses.LoginServerclasses;

import com.Modelclasses.ApplicationUser;
import com.Modelclasses.PasswordSecurity;

import java.io.*;
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
        while(this.connected)
        {
            try
            {
                ApplicationUser user = (ApplicationUser) this.IN.readObject();
                //registerNewUser(user);
                boolean result = loginAttempt(user);

                if(result)
                {
                    System.out.println("Login Successful, have fun!");
                    this.connected = false;
                    this.SOCKET.close();
                }
                else
                {
                    this.connected = false;
                    this.SOCKET.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Testing function for "registering an account" over the socket, will change soon
     * @param user
     */
    private void registerNewUser(ApplicationUser user)
    {
        PasswordSecurity.hashPassword(user);
        System.out.println(user.getPassword());
        System.out.println("Register Complete");
    }

    /**
     * Testing function for "a login attempt" over the socket, will change soon
     * @param user
     */
    private boolean loginAttempt(ApplicationUser user)
    {
        //should try to get password, email and salt from the DB and put in a object for comparison
        String pw = user.getPassword();
        PasswordSecurity.hashPassword(user);
        ApplicationUser test = new ApplicationUser(1, user.getEmail(), user.getPassword(), user.getSalt());
        user.setPassword(pw);
        //End temporary code for testing

        boolean result = PasswordSecurity.authenticate(user, test);
        if(result)
        {
            System.out.println("Login Successful");
            return true;
        }
        else
        {
            System.out.println("Login unsuccessful");
            return false;
        }
    }
}
