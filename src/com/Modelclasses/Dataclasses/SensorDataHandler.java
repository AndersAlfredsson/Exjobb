package com.Modelclasses.Dataclasses;

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
        this.sensorPairs = new ArrayList<>();
        this.expectedValues = new ArrayList<>();
        ArrayList<String> dataList = readPairsFromFile("com/Modelclasses/Sensorclasses/SensorPairs.txt");
        convertToPairs(dataList);
    }


    public synchronized void newValue(int id)
    {
        SensorPair pair = getPair(id);

        int expected = pair.getOtherSensor(id);
        if(isInExpectedList(expected))
        {
            pair.sensorPairTriggered(expected);
        }
        this.expectedValues.add(new ExpectedValue(id));
    }

    /**
     * gets a pair
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
     * checks if a value is in the list of expected values
     * @param expectedId
     * @return
     */
    private boolean isInExpectedList(int expectedId)
    {
        for(ExpectedValue value : this.expectedValues)
        {
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
    private ArrayList<String> readPairsFromFile(final String PATH)
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
            if(!this.sensorSections.containsKey(sectionId))
            {
                section = new Section(sectionId);
                this.sensorSections.put(section.getId(), section);
            }
            else
            {
                section = this.sensorSections.get(sectionId);
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
            System.out.println((currentTime-value.getTimeAdded()));
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

        public boolean isRemove() {
            return remove;
        }

        public void setRemove(boolean remove) {
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
