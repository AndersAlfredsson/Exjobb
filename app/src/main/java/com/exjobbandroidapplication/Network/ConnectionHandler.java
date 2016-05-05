package com.exjobbandroidapplication.Network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Enums.ServerMessageType;
import NetworkMessages.DisconnectMessage;
import NetworkMessages.Message;
import NetworkMessages.ServerMessage;

/**
 * Created by Anders on 2016-04-26.
 */
public class ConnectionHandler {
    private static ConnectionHandler ourInstance = null;
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private boolean connected = false;
    private final String IPADRESS = "78.68.96.226";
    private final int PORTNR = 9058;
    private String eMail;
    private ServerMessage receivedMessage = null;


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

    private void disconnect() {
        ServerMessage serverMessage = sendMessage(new DisconnectMessage(eMail));
        if (serverMessage.getMessageType() == ServerMessageType.Disconnect) {
            closeStreams();
        }
        //TODO : lägg till en timeout för guds skull!!!!!
    }

    /**
     * Connects to the server using the specified emailaddress and password.
     */
    public boolean connectToServer(){
        boolean successful = false;
        try {
            socket = new Socket(IPADRESS, PORTNR);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            successful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("connectToserver return:", successful + "");
        return successful;
    }


    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String geteMail() {
        return eMail;
    }

    /**
     * Sends a message to the server.
     * @param message Message to be sent to server.
     * @return whether sending the message is successful or not.
     */
    public ServerMessage sendMessage(Message message) {
        try {
            out.writeObject(message);
            try {
                ServerMessage serverMessage = (ServerMessage) in.readObject();
                if (serverMessage != null) {
                    return serverMessage;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
