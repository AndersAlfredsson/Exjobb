package com.Modelclasses.Sensorclasses;

import NetworkMessages.GpsCoordinates;
import NetworkMessages.Section;
import com.Enums.XmlParseType;
import com.Modelclasses.Dataclasses.SensorPair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;

/**
 * Created by Gustav on 2016-05-16.
 * Class that reads XML from file and converts it to an object
 */
public class XmlFileReader
{
    public XmlFileReader()
    {

    }

    /**
     * Reads the XML file from PATH
     * @param PATH
     * @param type
     * @return
     * @throws JAXBException
     */
    public ArrayList readFile(final String PATH, XmlParseType type) throws  JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlWrapper.class, SensorPair.class, Section.class, GpsCoordinates.class, IpContainer.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if(type == XmlParseType.SensorPair)
        {
            ArrayList<SensorPair> sensorPairData = unmarshal(unmarshaller,PATH);
            return sensorPairData;
        }
        else if(type == XmlParseType.SectionGPS)
        {
            ArrayList<Section> sensorPairData = unmarshal(unmarshaller, PATH);
            return sensorPairData;
        }
        else if(type == XmlParseType.SensorIP)
        {
            ArrayList<IpContainer> containers = unmarshal(unmarshaller, PATH);
            return containers;
        }
        return null;
    }

    /**
     * Reads and converts the XML-file
     * @param unmarshaller
     * @param xmlLocation
     * @param <T>
     * @return
     */
    private <T> ArrayList<T> unmarshal(Unmarshaller unmarshaller, String xmlLocation)
    {
        StreamSource xml = new StreamSource(xmlLocation);
        try
        {
            XmlWrapper wrapper = unmarshaller.unmarshal(xml, XmlWrapper.class).getValue();
            return wrapper.getItems();
        }
        catch(JAXBException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
