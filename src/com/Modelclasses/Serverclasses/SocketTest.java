package com.Modelclasses.Serverclasses;

import Enums.ServerMessageType;
import NetworkMessages.*;
import com.Modelclasses.ApplicationUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Goustmachine on 2016-04-21.
 */
public class SocketTest
{
    public static void main(String[] args)
    {
        Connect();
    }

    private static void Connect()
    {
        ApplicationUser user = new ApplicationUser("dev@dev.com", "dev");
        boolean keepConnection = true;
        Socket s = null;
        final ObjectOutputStream OUT;
        final ObjectInputStream IN;

        try
        {
            s = new Socket("localhost", 9058);

            //IMPORTANT ORDER!
            OUT = new ObjectOutputStream(s.getOutputStream());
            IN = new ObjectInputStream(s.getInputStream());

            Thread.sleep(100);

            OUT.writeObject(new LoginMessage(user.getEmail(), user.getPassword()));

            ServerMessage message = (ServerMessage) IN.readObject();

            if(message.getMessageType() == ServerMessageType.Disconnect)
            {
                System.out.println(message.getMessage());
                OUT.flush();
                s.close();
                keepConnection = false;
            }
            else if(message.getMessageType() == ServerMessageType.Authenticated)
            {
                System.out.println(message.getMessage());
                keepConnection = true;
            }
            while(keepConnection)
            {
                System.out.println("Still connected");
                OUT.writeObject(new RequestMessage(new GPSCoordMessage(user.getEmail(), 1.0, 1.0)));
                Thread.sleep(10000);
                System.out.println("Trying to disconnect");
                OUT.writeObject(new DisconnectMessage(user.getEmail()));
                message = (ServerMessage) IN.readObject();
                if(message.getMessageType() == ServerMessageType.Disconnect)
                {
                    System.out.println(message.getMessage());
                    OUT.flush();
                    s.close();
                    keepConnection = false;
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                if (s != null) {
                    s.close();
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        } catch (InterruptedException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
