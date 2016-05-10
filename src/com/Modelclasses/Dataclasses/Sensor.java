package com.Modelclasses.Dataclasses;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Gustav on 2016-05-09.
 */
public class Sensor implements Runnable
{
    private Socket s;
    private boolean isConnected;
    private DataOutputStream OUT;
    private final int ID1;
    private final int ID2;

    public Sensor(Socket s, int ID1, int ID2)
    {
        System.out.println("Sensor was connected " + ID1 + "&" + ID2);
        this.ID1 = ID1;
        this.ID2 = ID2;
        this.s = s;
        this.isConnected = false;
        try
        {
            this.OUT = new DataOutputStream(s.getOutputStream());
            this.isConnected = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getID1() {
        return ID1;
    }

    public int getID2() {
        return ID2;
    }

    public void sendMessage(int id)
    {
        try
        {
            this.OUT.writeInt(id);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return this.ID1 + " - " + this.ID2;
    }

    @Override
    public void run()
    {
        while(this.isConnected)
        {
            int sleepTime = (ThreadLocalRandom.current().nextInt(2, 20+1))*1000;
            try
            {
                Thread.sleep(sleepTime);
                if(ThreadLocalRandom.current().nextInt(0, 100) < 50) {
                    //System.out.println("Sleeping for " + sleepTime + " ms");

                    int whoSend = ThreadLocalRandom.current().nextInt(this.ID1, this.ID2 + 1);
                    int other;
                    if (whoSend == this.ID1) {
                        other = this.ID2;
                    } else {
                        other = this.ID1;
                    }
                    System.out.println("sending: " + whoSend + ", from " + this.toString());
                    sendMessage(whoSend);
                    sleepTime = (ThreadLocalRandom.current().nextInt(1, 5 + 1)) * 1000;
                    Thread.sleep(sleepTime);
                    if (ThreadLocalRandom.current().nextInt(0, 100) < 95) {
                        System.out.println("sending: " + other + ", from " + this.toString());
                        sendMessage(other);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
