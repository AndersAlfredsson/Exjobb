package com.exjobbandroidapplication.Network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import Enums.ServerMessageType;
import NetworkMessages.DisconnectMessage;
import NetworkMessages.Message;
import NetworkMessages.SensorDataMessage;
import NetworkMessages.ServerMessage;

/**
 * Created by Anders on 2016-04-26.
 */
public class ConnectionHandler {
    private static ConnectionHandler ourInstance = null;
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private final String IPADRESS = "192.168.1.242";
    private final int PORTNR = 8181;
    private String eMail;

    public static ConnectionHandler getInstance() {
        if (ourInstance == null){
            ourInstance = new ConnectionHandler();
        }
        return ourInstance;
    }

    private ConnectionHandler() {

    }

    /**
     * Closes all streams.
     */
    private void closeStreams() {
        if (out != null){
            try {
                out.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean disconnect() {
        ServerMessage serverMessage = sendMessage(new DisconnectMessage(eMail));
        if (serverMessage != null && serverMessage.getMessageType() == ServerMessageType.Disconnect) {
            closeStreams();
        }
        return true;
    }

    /**
     * Connects to the server using the specified emailaddress and password.
     */
    public boolean connectToServer(){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(IPADRESS, PORTNR), 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("connectToserver return:", socket.isConnected() + "");
        return socket.isConnected();
    }




    /**
     * Sends a message to the server.
     * @param message Message to be sent to server.
     * @return whether sending the message is successful or not.
     */
    public synchronized ServerMessage sendMessage(Message message) {
        try {
            out.writeObject(message);
            try {
                ServerMessage serverMessage = (ServerMessage) in.readObject();

                if (serverMessage == null || serverMessage.getMessage() == null) {
                    return null;
                }

                return serverMessage;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //region Getters & Setters
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String geteMail() {
        return eMail;
    }
    //endregion
}
