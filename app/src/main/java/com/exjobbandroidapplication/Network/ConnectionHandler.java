package com.exjobbandroidapplication.Network;

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
    private final String IPADRESS = "10.22.6.79";
    private final int PORTNR = 9058;
    private Runnable receiverThread;
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
     * Setup the receiver thread to wait for messages coming from the server.
     */
//    private void setupReceiverThread() {
//        receiverThread = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    try {
//                        ServerMessage serverMessage = (ServerMessage) in.readObject();
//                        if (serverMessage.getMessageType() == ServerMessageType.Authenticated){
//                            connected = true;
//                        }
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    while (connected) {
//                        try {
//                            ServerMessage serverMessage = (ServerMessage) in.readObject();
//                            if (serverMessage.getMessageType() == ServerMessageType.Disconnect){
//                                connected = false;
//                            }
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }

    /**
     * Sends a message to the server.
     * @param message Message to be sent to server.
     * @return whether sending the message is successful or not.
     */
    public ServerMessage sendMessage(Message message) {
        boolean isSuccessful = true;
        try {
            out.writeObject(message);
            try {
                ServerMessage serverMessage = (ServerMessage) in.readObject();
                return serverMessage;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            isSuccessful = false;
        }
        return null;
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
}
