package com.Modelclasses.Sensorclasses;

import com.Enums.XmlParseType;
import com.Modelclasses.Dataclasses.SensorDataHandler;

import javax.xml.bind.JAXBException;
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
    private ArrayList<IpContainer> IpList;
    private SensorDataHandler handler;
    private final ExecutorService pool;

    public ClientSensors(SensorDataHandler handler)
    {
        this.IpList = new ArrayList<>();
        this.handler = handler;
        this.pool = Executors.newCachedThreadPool();
        if(readIpsFromFile("src/com/Modelclasses/Sensorclasses/SensorIps.xml"))
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
        for(IpContainer ip : IpList)
        {
            pool.execute(new SensorClient(this.handler, ip));
        }
    }

    /**
     * Reads the IP-addresses from a xml-file
     * @param PATH
     * @return
     */
    private boolean readIpsFromFile(String PATH)
    {
        XmlFileReader fr = new XmlFileReader();
        try
        {
            this.IpList = fr.readFile(PATH, XmlParseType.SensorIP);
            return true;
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
