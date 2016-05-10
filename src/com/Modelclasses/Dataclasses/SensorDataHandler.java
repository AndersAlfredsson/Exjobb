package com.Modelclasses.Dataclasses;

import NetworkMessages.GpsCoordinates;
import NetworkMessages.Section;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.DoubleAccumulator;

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
        this.sensorPairs = new ArrayList<>();
        this.sensorSections = new HashMap<>();
        this.expectedValues = new ArrayList<>();
        ArrayList<String> dataList = readDataFromFile("src/com/Modelclasses/Sensorclasses/SensorPairs.txt");
        ArrayList<String> sectionDataList = readDataFromFile("src/com/Modelclasses/Sensorclasses/SectionGpsCoordinates.txt");
        convertToPairs(dataList);
        convertSectionToCoordinates(sectionDataList);
    }

    private void convertSectionToCoordinates(ArrayList<String> sectionDataList)
    {
        for(String coord : sectionDataList)
        {
            String[] temp = coord.split(",");
            int id = Integer.parseInt(temp[0]);
            double latitude = Double.parseDouble(temp[1]);
            double longitude = Double.parseDouble(temp[2]);
            GpsCoordinates coordinate = new GpsCoordinates(latitude, longitude);
            Section s = sensorSections.get(id);
            s.addCoorinates(coordinate);
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
        /*for(SensorPair p : this.sensorPairs)
        {
            System.out.println(p.toString());
        }*/
        System.out.println(getPair(6).toString());
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
     * @param dataList
     */
    private void convertToPairs(ArrayList<String> dataList)
    {
        for(String s:dataList)
        {
            SensorPair pair = new SensorPair();
            String[] temp = s.split(",");
            Section section;

            pair.setInnerSensor(Integer.parseInt(temp[0]));
            pair.setOuterSensor(Integer.parseInt(temp[1]));
            int sectionId = Integer.parseInt(temp[2]);
            if(!this.sensorSections.containsKey(sectionId) && sectionId != -1)
            {
                section = new Section(sectionId);
                this.sensorSections.put(section.getId(), section);
            }
            else
            {
                if(sectionId != -1)
                {
                    section = this.sensorSections.get(sectionId);
                }
                else
                {
                    section = null;
                }
            }
            pair.setInnerSection(section);

            sectionId = Integer.parseInt(temp[3]);
            if(!this.sensorSections.containsKey(sectionId) && sectionId != -1)
            {
                section = new Section(sectionId);
                this.sensorSections.put(section.getId(), section);
            }
            else
            {
                if(sectionId != -1)
                {
                    section = this.sensorSections.get(sectionId);
                }
                else
                {
                    section = null;
                }
            }
            pair.setNeighboringSection(section);
            this.sensorPairs.add(pair);
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
