package com.Modelclasses.Serverclasses;

import Enums.ServerMessageType;
import NetworkMessages.*;
import com.DBcommunication.DBhandlerSingleton;
import com.Enums.LogEvents;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.Dataclasses.GpsDataHandler;
import com.Modelclasses.Dataclasses.SensorDataHandler;
import com.Modelclasses.PasswordSecurity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private final GpsDataHandler handler;
    private final SensorDataHandler sensorDataHandler;
    private static ArrayList<Integer> usedIDs = new ArrayList<>();
    private int anonymousID;
    private final int logMinimumDelay = 8000;
    private long currentTime;
    private long previousTime;

    /**
     * Constructor that sets up the connection and the I/O-ObjectStreams
     * @param socket
     * @param handler
     * @throws IOException
     */
    public UserHandler(Socket socket, GpsDataHandler handler, SensorDataHandler sensorDataHandler) throws IOException
    {
        this.SOCKET = socket;
        this.IN = new ObjectInputStream(socket.getInputStream());
        this.OUT = new ObjectOutputStream(socket.getOutputStream());
        this.connected = false;
        this.authenticated = false;
        this.handler = handler;
        this.sensorDataHandler = sensorDataHandler;
        anonymousID = -1;
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
     * it reads an object and then calls the handleMessage-function.
     */
    @Override
    public void run()
    {
        this.connected = true;
        //System.out.println("Client connected...");
        while(this.connected)
        {
            try
            {
                Message m = (Message) this.IN.readObject();
                if(!Server.isIsServerShutdownInitiated())
                {
                    handleMessage(m);
                }
                else
                {
                    disconnect("Server is shutting down");
                }
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                this.connected = false;
                try
                {
                    System.err.println("Client closed connection without disconnect...");

                    this.SOCKET.close();
                }
                catch (IOException e1)
                {

                    e1.printStackTrace();
                }
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        if (anonymousID != -1) {
            usedIDs.remove(anonymousID);
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
            //System.out.println("LoginMessage");
            user = new ApplicationUser(message.getUsername(), ((LoginMessage) message).getPassword());

            this.authenticated = this.loginAttempt(user);

            if(!this.authenticated)
            {
                disconnect("Wrong username or password");
            }
            else
            {
                sendMessage(new ServerMessage(ServerMessageType.Authenticated, "Login Successful"));
            }
        }
        else if(message instanceof RegisterMessage)
        {
            //System.out.println("RegisterMessage");
            user = new ApplicationUser(message.getUsername(), ((RegisterMessage) message).getPassword());

            this.authenticated = registerNewUser(user);
            if(!this.authenticated)
            {
                disconnect("Email already in use");
            }
            else
            {
                sendMessage(new ServerMessage(ServerMessageType.Authenticated, "Register & Login Successful"));
                giveAnonymousID();
            }
        }
        else if(message instanceof DisconnectMessage)
        {
            //System.out.println("DisconnectMessage");
            DBhandlerSingleton.getInstance().log(LogEvents.Disconnect, new ApplicationUser(message.getUsername(), null));
            disconnect("Disconnect request");
        }
        else if(message instanceof RequestMessage)
        {
            GPSCoordMessage gpsCoords = ((RequestMessage) message).getGpsCoords();

            //Log if 8 seconds has passed since last log.
            logGPS(gpsCoords);
            handler.putData(gpsCoords);
            handler.printMap();

            if(handler.getOUTER_BOX().isInsideBox(gpsCoords))
            {
                ArrayList<GpsCoordinates> data = handler.getGpsData(((RequestMessage) message).getGpsCoords().getUsername());
                HashMap<Integer, Section> sectionData = sensorDataHandler.getSensorSections();
                SensorDataMessage sdm = new SensorDataMessage(data, sectionData);
                System.out.println(sdm.getSectionMap().get(0).getAmount());
                System.out.println(sdm.getSectionMap().get(1).getAmount());
                sendMessage(new ServerMessage(ServerMessageType.SensorData, sdm));
            }
            else
            {
                sendMessage(new ServerMessage(ServerMessageType.SensorData, null));
            }
        }
    }


    private void logGPS(GPSCoordMessage message) {
        currentTime = System.currentTimeMillis();
        if (currentTime >= (previousTime + logMinimumDelay)) {
            DBhandlerSingleton.getInstance().logGPS(anonymousID, message.getLatitude(),message.getLongitude());
            previousTime = currentTime;
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
                this.OUT.reset();
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
            System.err.println("Disconnect unsuccessful");
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
        if(DBhandlerSingleton.getInstance().getUser(user.getEmail()) == null)
        {
            PasswordSecurity.hashPassword(user);
            DBhandlerSingleton.getInstance().insertUser(user);
            //System.out.println("Register Complete");
            return true;
        }
        //System.out.println("Register not possible");
        return false;
    }

    /**
     * Does a loginattempt by first checking if the user exists, if id does not, username is wrong
     * otherwise it tries to hash password and salt the same way as the saved password, if they are not the same
     * the password is wrong, otherwise it is a successful login and the client can continue
     * @param user
     */
    private boolean loginAttempt(ApplicationUser user)
    {
        ApplicationUser dbUser = DBhandlerSingleton.getInstance().getUser(user.getEmail());
        if(dbUser != null)
        {
            if(PasswordSecurity.authenticate(user, dbUser))
            {
                //System.out.println("Login Successful");
                DBhandlerSingleton.getInstance().log(LogEvents.SuccessfulLoginAttempt, user);
                giveAnonymousID();
                return true;
            }
            else
            {
                //System.out.println("Login unsuccessful");
                DBhandlerSingleton.getInstance().log(LogEvents.UnsuccessfulLoginAttempt, user);
                return false;
            }
        }
        else
        {
            //System.out.println("Login unsuccessful");
            DBhandlerSingleton.getInstance().log(LogEvents.UnsuccessfulLoginAttempt, user);
            return false;
        }

    }

    /**
     * Give a user an anonymous id to separate logged in users in the gpslog in the database.
     */
    private void giveAnonymousID() {
        int id = 0;
        while(usedIDs.contains(id))
        {
            id++;
        }
        anonymousID = id;
    }
}
