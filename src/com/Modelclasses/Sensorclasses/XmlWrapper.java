package com.Modelclasses.Sensorclasses;

import javax.xml.bind.annotation.XmlAnyElement;
import java.util.ArrayList;

/**
 * Created by Gustav on 2016-05-16.
 * Wrapper class, used in XmlFileReader
 */
public class XmlWrapper<T>
{
    private ArrayList<T> items;
    public XmlWrapper()
    {
        this.items = new ArrayList<>();
    }
    public XmlWrapper(ArrayList<T> items)
    {
        this.items = items;
    }
    @XmlAnyElement(lax=true)
    public ArrayList<T> getItems()
    {
        return this.items;
    }

}
