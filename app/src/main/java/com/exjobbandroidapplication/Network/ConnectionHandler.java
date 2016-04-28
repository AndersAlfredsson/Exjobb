package com.exjobbandroidapplication.Network;

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
    private static ConnectionHandler ourInstance = new ConnectionHandler();
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private boolean connected = false;
    private final String IPADRESS = "10.22.19.48";
    private final int PORTNR = 9058;
    private Runnable receiverThread;
    private String eMail;


    public static ConnectionHandler getInstance() {
        return ourInstance;
    }

    private ConnectionHandler() {
        connectToServer();
        setupReceiverThread();
    }

    /**
     * Setup the receiver thread to wait for messages coming from the server.
     */
    private void setupReceiverThread() {
        receiverThread = new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        ServerMessage serverMessage = (ServerMessage) in.readObject();
                        if (serverMessage.getMessageType() == ServerMessageType.Authenticated){
                            connected = true;
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    while (connected) {
                        try {
                            ServerMessage serverMessage = (ServerMessage) in.readObject();
                            if (serverMessage.getMessageType() == ServerMessageType.Disconnect){
                                connected = false;
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Sends a message to the server.
     * @param message Message to be sent to server.
     * @return whether sending the message is successful or not.
     */
    public boolean sendMessage(Message message) {
        boolean isSuccessful = true;
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            isSuccessful = false;
        }
        return isSuccessful;
    }

    /**
     * Closes all streams.
     */
    private void closeStreams() {
        if (out != null){
            try {
                out.writeObject(new DisconnectMessage(eMail));
                out.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connects to the server using the specified emailaddress and password.
     */
    private void connectToServer(){
        try {
            socket = new Socket(IPADRESS, PORTNR);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
