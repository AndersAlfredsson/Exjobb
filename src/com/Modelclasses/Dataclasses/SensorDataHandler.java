package com.Modelclasses.Dataclasses;

import java.util.ArrayList;

/**
 * Created by Gustav on 2016-05-02.
 */
public class SensorDataHandler
{
    private ArrayList<Section> sectionList;

    public SensorDataHandler()
    {
        this.sectionList = new ArrayList<>();
    }

    /**
     * Creates a new section and adds it to the list of sections
     * @param sensors
     * @return
     */
    public boolean addSection(int... sensors)
    {
        Section section = new Section();
        for(int i: sensors)
        {
            section.addSensor(i);
        }
        this.sectionList.add(section);
        return true;
    }

    /**
     * Finds the section that the sensor is part of, and adds a person to that section
     * @param sensorId
     */
    public void addPersonToSection(int sensorId)
    {
        for(Section s: this.sectionList)
        {
            s.getSensorList().stream().filter(i -> i == sensorId).forEach(i -> s.addPeople());
        }
    }

    /**
     * Printing function, used for testing purposes
     */
    public void printSections()
    {
        for(Section section:this.sectionList)
        {
            System.out.println(section.toString());
        }
    }

    public synchronized void cleanup()
    {
        int numSections = 0;
        int numPeopleRemoved = 0;
        for(Section s : this.sectionList)
        {
            numSections++;
            if(s.removePeople())
            {
                numPeopleRemoved++;
            }
        }
        System.out.println("Sections: " + numSections + " cleaned");
        System.out.println("With: " + numPeopleRemoved + " people removed");

    }
}
