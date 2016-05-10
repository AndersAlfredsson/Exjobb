package com.Modelclasses.Sensorclasses;

import com.Modelclasses.Dataclasses.SensorDataHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Gustav on 2016-05-09.
 */
public class ClientSensors
{
    private ArrayList<String> IpList;
    private SensorDataHandler handler;
    private final ExecutorService pool;

    public ClientSensors(SensorDataHandler handler)
    {
        this.IpList = new ArrayList<>();
        this.handler = handler;
        this.pool = Executors.newCachedThreadPool();
        if(readIpsFromFile("src/com/Modelclasses/Sensorclasses/SensorIps.txt"))
        {
            System.out.println("Read clients");
            createClients();
        }
        else
        {
            System.err.println("Not able to read the Ip Addresses");
        }
    }
    private void createClients()
    {
        for(String ip : IpList)
        {
            pool.execute(new SensorClient(this.handler, ip));
        }
    }
    private boolean readIpsFromFile(String PATH)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(PATH));
            String line = br.readLine();
            while(line != null)
            {
                IpList.add(line);
                line = br.readLine();
            }
            br.close();
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
