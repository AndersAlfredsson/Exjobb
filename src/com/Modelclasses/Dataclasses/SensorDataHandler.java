package com.Modelclasses.Dataclasses;

import NetworkMessages.Section;
import com.Enums.XmlParseType;
import com.Modelclasses.Sensorclasses.XmlFileReader;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gustav on 2016-05-02.
 */
public class SensorDataHandler
{
    private ArrayList<SensorPair> sensorPairs;
    private HashMap<Integer, Section> sensorSections;
    private ArrayList<ExpectedValue> expectedValues;

    public SensorDataHandler()
    {
        XmlFileReader reader = new XmlFileReader();
        this.sensorPairs = new ArrayList<>();
        this.sensorSections = new HashMap<>();
        this.expectedValues = new ArrayList<>();
        try
        {
            this.sensorPairs = reader.readFile("src/com/Modelclasses/Sensorclasses/SensorPairs.xml", XmlParseType.SensorPair);
            convertToPairs();
            ArrayList<Section> sectionData = reader.readFile("src/com/Modelclasses/Sensorclasses/SectionGpsCoordinates.xml", XmlParseType.SectionGPS);
            AddSectionsToMap(sectionData);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void newValue(int id)
    {
        SensorPair pair = getPair(id);
        int expected = pair.getOtherSensor(id);
        if(isInExpectedList(expected))
        {
            //System.out.println("Found expected value!");
            pair.sensorPairTriggered(expected);
        }
        else
        {
            //System.out.println("Added new Expected value!");
            this.expectedValues.add(new ExpectedValue(id));
        }
        //printPairs();
    }

    public void printPairs()
    {
        for(SensorPair p : this.sensorPairs)
        {
            System.out.println(p.toString());
        }
    }

    /**
     * Gets a pair with matching id
     * @param id
     * @return
     */
    private SensorPair getPair(int id)
    {
        for(SensorPair pair : this.sensorPairs)
        {
            if(pair.getInnerSensor() == id || pair.getOuterSensor() == id)
            {
                return pair;
            }
        }
        return null;
    }

    /**
     * Checks if a value is in the list of expected values
     * @param expectedId
     * @return
     */
    private boolean isInExpectedList(int expectedId)
    {
        long timeNow = System.currentTimeMillis();
        for(ExpectedValue value : this.expectedValues)
        {
            if((timeNow-value.getTimeAdded()) > 5000)
            {
                value.setRemove(true);
            }
            if(expectedId == value.getSensorValue() && !value.isRemove())
            {
                value.setRemove(true);
                return true;
            }

        }
        return false;
    }

    /**
     * Gets the size of the list with expected values
     * @return
     */
    public int getExpectedSize()
    {
        return this.expectedValues.size();
    }

    /**
     * Reads rows of strings from a file that defines pairs
     * @param PATH
     * @return
     */
    private ArrayList<String> readDataFromFile(final String PATH)
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
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * Converts a list of strings to pairs and sections
     */
    private synchronized void convertToPairs()
    {
        for(SensorPair pair : this.sensorPairs)
        {
            Section section;
            int innerSectionID = pair.getInnerSectionID();
            if(!this.sensorSections.containsKey(innerSectionID) && innerSectionID != -1)
            {
                section = new Section(innerSectionID);
                this.sensorSections.put(section.getId(), section);
            }
            else
            {
                if(innerSectionID != -1)
                {
                    section = this.sensorSections.get(innerSectionID);
                }
                else
                {
                    section = null;
                }
            }
            pair.setInnerSection(section);


            int outerSectionId = pair.getOuterSectionID();
            if(!this.sensorSections.containsKey(outerSectionId) && outerSectionId != -1)
            {
                section = new Section(outerSectionId);
                this.sensorSections.put(section.getId(), section);
            }
            else
            {
                if(outerSectionId != -1)
                {
                    section = this.sensorSections.get(outerSectionId);
                }
                else
                {
                    section = null;
                }
            }
            pair.setNeighboringSection(section);
        }
    }

    /**
     * Converts read data to coordinates
     * @param sectionDataList
     */
    private void AddSectionsToMap(ArrayList<Section> sectionDataList)
    {
        for(Section s : sectionDataList)
        {
            this.sensorSections.put(s.getId(), s);
        }
    }

    public synchronized HashMap<Integer, Section> getSensorSections()
    {
        return this.sensorSections;
    }

    /**
     * The method that runs on Janitor to remove old data
     */
    public synchronized void cleanup()
    {
        long currentTime = System.currentTimeMillis();
        int i= 0;
        ArrayList<ExpectedValue> temp = new ArrayList<>(this.expectedValues);
        for(ExpectedValue value : temp)
        {
            if((currentTime-value.getTimeAdded()) >= 5000 || value.isRemove())
            {
                this.expectedValues.remove(value);
                i++;
            }
        }
        System.out.println("Janitor cleaned sensor data with " + i + " removed");
    }

    /**
     * Inner class for container
     */
    public class ExpectedValue
    {
        private long timeAdded;
        private int sensorValue;
        private boolean remove;
        public ExpectedValue(int id)
        {
            this.sensorValue = id;
            this.timeAdded = System.currentTimeMillis();
            this.remove = false;
        }

        public synchronized boolean isRemove() {
            return remove;
        }

        public synchronized void setRemove(boolean remove) {
            this.remove = remove;
        }

        public long getTimeAdded() {
            return timeAdded;
        }

        public void setTimeAdded(long timeAdded) {
            this.timeAdded = timeAdded;
        }

        public int getSensorValue() {
            return sensorValue;
        }

        public void setSensorValue(int sensorValue) {
            this.sensorValue = sensorValue;
        }
    }
}
