package com.Modelclasses.LoginServerclasses;

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
       // this.IN = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
       // this.OUT = new PrintWriter(SOCKET.getOutputStream(), true);
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
            /*try
            {
                //TestingClient.TestObject t = (TestingClient.TestObject) this.IN.readObject();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }*/
        }
    }
}
