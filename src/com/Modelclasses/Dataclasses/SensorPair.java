package com.Modelclasses.Dataclasses;


import NetworkMessages.Section;

/**
 * Created by Gustav on 2016-05-09.
 */

public class SensorPair
{
    private int innerSensor;
    private int outerSensor;
    private Section innerSection;
    private Section neighboringSection;

    public SensorPair()
    {
        this.innerSensor = -1;
        this.outerSensor = -1;
    }

    public SensorPair(int i, int j)
    {
        this.innerSensor = i;
        this.outerSensor = j;
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

    @Override
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

    }
}
