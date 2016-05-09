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
    private ArrayList<SensorClient> clients;
    private ArrayList<String> IpList;
    private SensorDataHandler handler;
    private final ExecutorService pool;

    public ClientSensors(SensorDataHandler handler)
    {
        this.clients = new ArrayList<>();
        this.IpList = new ArrayList<>();
        this.handler = handler;
        this.pool = Executors.newCachedThreadPool();
        if(readIpsFromFile("src/com/Modelclasses/Sensorclasses/SensorIps.txt"))
        {

        }
        else
        {
            System.err.println("Not able to read the Ip Addresses");
        }
    }
    private void createClients()
    {
        for(String ip: IpList)
        {
            System.out.println(IpList.size());
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            SensorClient client;
            pool.execute(client = new SensorClient(this.handler, ip));
        }
    }
    private boolean readIpsFromFile(String PATH)
    {
        ArrayList<String> dataList = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(PATH));
            String line = br.readLine();
            while(line != null)
            {
                dataList.add(line);
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
