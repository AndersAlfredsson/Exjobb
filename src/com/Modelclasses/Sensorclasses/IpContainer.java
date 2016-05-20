package com.Modelclasses.Sensorclasses;

/**
 * Created by Gustav on 2016-05-20.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container class for reading IPs from the XML file
 */
@XmlRootElement(name = "IpContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class IpContainer
{
    @XmlElement(name = "ID")
    private int SENSOR_ID;
    @XmlElement(name = "IP_ADDRESS")
    private String IP_ADDRESS;

    public IpContainer()
    {
        this.SENSOR_ID = -1;
        this.IP_ADDRESS = "";
    }
    public IpContainer(int id, String ip)
    {
        this.SENSOR_ID = id;
        this.IP_ADDRESS = ip;
    }

    public String getIP_ADDRESS() {
        return IP_ADDRESS;
    }

    public int getSENSOR_ID() {
        return SENSOR_ID;
    }

    public void setSENSOR_ID(int SENSOR_ID) {
        this.SENSOR_ID = SENSOR_ID;
    }

    public void setIP_ADDRESS(String IP_ADDRESS) {
        this.IP_ADDRESS = IP_ADDRESS;
    }
}
