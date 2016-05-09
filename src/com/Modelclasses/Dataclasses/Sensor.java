package com.Modelclasses.Dataclasses;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Gustav on 2016-05-09.
 */
public class Sensor implements Runnable
{
    private Socket s;
    private PrintWriter OUT;
    private int ID;

    public Sensor(Socket s, int ID)
    {
        System.out.println("Sensor was connected " + ID);
        this.ID = ID;
        this.s = s;
        try
        {
            this.OUT = new PrintWriter(s.getOutputStream(), true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getID() {
        return ID;
    }

    public void sendMessage(int id)
    {
        this.OUT.write(id);
    }

    @Override
    public void run()
    {

    }
}
