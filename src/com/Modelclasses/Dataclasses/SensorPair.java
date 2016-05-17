package com.Modelclasses.Dataclasses;


import NetworkMessages.Section;
import com.DBcommunication.DBhandlerSingleton;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Gustav on 2016-05-09.
 * Class that handles two sensors to create a pair
 */
@XmlRootElement(name = "SensorPair")
@XmlAccessorType(XmlAccessType.FIELD)
public class SensorPair
{
    @XmlElement(name = "innerSensor")
    private int innerSensor;
    @XmlElement(name = "outerSensor")
    private int outerSensor;
    @XmlElement(name = "innerSectionID")
    private int innerSectionID;
    @XmlElement(name = "outerSectionID")
    private int outerSectionID;
    private Section innerSection;
    private Section neighboringSection;

    public SensorPair()
    {
        this.innerSensor = -1;
        this.outerSensor = -1;
        this.innerSectionID = -1;
        this.outerSectionID = -1;
    }

    public SensorPair(int innerSensor, int outerSensor, int innerSectionID, int outerSectionID)
    {
        this.innerSensor = innerSensor;
        this.outerSensor = outerSensor;
        this.innerSectionID = innerSectionID;
        this.outerSectionID = outerSectionID;
    }

    //region Setters & Getters
    public int getInnerSensor() {
        return innerSensor;
    }

    public void setInnerSensor(int innerSensor) {
        this.innerSensor = innerSensor;
    }

    public int getOuterSensor() {
        return outerSensor;
    }

    public void setOuterSensor(int outerSensor) {
        this.outerSensor = outerSensor;
    }

    public void setInnerSection(Section innerSection) {
        this.innerSection = innerSection;
    }

    public void setNeighboringSection(Section neighboringSection) {
        this.neighboringSection = neighboringSection;
    }

    public int getOuterSectionID() {
        return outerSectionID;
    }

    public void setOuterSectionID(int outerSectionID) {
        this.outerSectionID = outerSectionID;
    }

    public int getInnerSectionID() {
        return innerSectionID;
    }

    public void setInnerSectionID(int innerSectionID) {
        this.innerSectionID = innerSectionID;
    }

    /**
     * Gets the other sensor from the one sent in
     * @param id
     * @return
     */
    public int getOtherSensor(int id)
    {
        if(id == innerSensor)
        {
            return outerSensor;
        }
        else
        {
            return innerSensor;
        }
    }
    //endregion

    /**
     * If called, the function will handle increment/decrement of the appropriate sections for the event
     * @param expected
     */
    public synchronized void sensorPairTriggered(int expected)
    {
        if(expected == innerSensor) {
            innerSection.increment();
            //Log event to database.
            DBhandlerSingleton.getInstance().logSensorEvent(outerSensor, innerSensor);
            if(neighboringSection != null && neighboringSection.getAmount() > 0)
            {
                neighboringSection.decrement();
            }
        }
        else
        {
            //Log event to database.
            DBhandlerSingleton.getInstance().logSensorEvent(innerSensor, outerSensor);
            if(innerSection.getAmount() > 0)
            {
                innerSection.decrement();
                if(neighboringSection != null)
                {
                    neighboringSection.increment();
                }
            }
        }
    }

    /*@Override
    public String toString()
    {
        if(this.neighboringSection == null)
        {
            return this.innerSensor + " - " + this.outerSensor + ", " + this.innerSection.toString() + ", null";
        }
        else
        {
            return this.innerSensor + " - " + this.outerSensor + ", " + this.innerSection.toString() + ", " + this.neighboringSection.toString();
        }

    }*/
}
