package com.Modelclasses.Dataclasses;

import java.util.ArrayList;

/**
 * Created by Gustav on 2016-05-02.
 * Class for containing and storing data about a section, also what sensors belong to that section
 */
public class Section
{
    private String name;
    private int numPeople;
    private ArrayList<Integer> sensorList;

    /**
     * Default constructor
     */
    public Section()
    {
        this.numPeople = 0;
        this.sensorList = new ArrayList<>();
        name = "";
    }

    /**
     * Adds a sensor to the list, checks if it is already added first
     * @param id
     */
    public void addSensor(int id)
    {
        if(!isInList(id))
        {
            sensorList.add(id);
            name += id;
        }
    }

    /**
     * Overridden toString function
     * @return
     */
    @Override
    public String toString()
    {
        return "name: " + name + ", amount: " + this.numPeople;
    }

    /**
     * Rerurns the list with all the sensors
     * @return
     */
    public ArrayList<Integer> getSensorList()
    {
        return this.sensorList;
    }

    /**
     * Adds a person to the section
     */
    public void addPeople()
    {
        this.numPeople++;
    }

    /**
     * Removes a person from the section
     */
    public boolean removePeople()
    {
        if(numPeople > 0)
        {
            this.numPeople--;
            return true;
        }
        return false;
    }

    /**
     * Checks if the sensor has already been added to the list
     * @param id
     * @return
     */
    private boolean isInList(int id)
    {
        for(int i : this.sensorList)
        {
            if(i == id)
            {
                return true;
            }
        }
        return false;
    }
}
