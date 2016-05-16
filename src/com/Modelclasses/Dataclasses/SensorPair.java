package com.Modelclasses.Dataclasses;


import NetworkMessages.Section;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Gustav on 2016-05-09.
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

    public synchronized void sensorPairTriggered(int expected)
    {
        if(expected == innerSensor)
        {
            innerSection.increment();
            if(neighboringSection != null && neighboringSection.getAmount() > 0)
            {
                neighboringSection.decrement();
            }
        }
        else
        {
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
