package com.Modelclasses.LoginServerclasses;

import com.Enums.ServerMessageType;
import com.Modelclasses.ApplicationUser;
import com.Modelclasses.NetworkMessages.LoginMessage;
import com.Modelclasses.NetworkMessages.ServerMessage;

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

    public static void Connect()
    {
        ApplicationUser user = new ApplicationUser("dev@dev.com", "dfdasev");
        boolean keepConnection = false;
        Socket s = null;
        final ObjectInputStream IN;
        final ObjectOutputStream OUT;
        try
        {
            s = new Socket("127.0.0.1", 3000);
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
            while(keepConnection)
            {
                System.out.println("Still connected");
                Thread.sleep(10000);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                s.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
