package com.Modelclasses.Dataclasses;

import NetworkMessages.GpsCoordinates;

/**
 * Created by Gustav on 2016-05-03.
 * Class saving the gps coodinates for a box when checking if someone is inside an area
 */
public class BoundingBox
{
    private GpsCoordinates northWest;
    private GpsCoordinates southEast;

    /**
     * Constructor defining the box
     * @param nwLat latitude of the northwest point
     * @param nwLong longitude of the northwest point
     * @param seLat latitude of the southeast point
     * @param seLong longitude of the southeast point
     */
    public BoundingBox(double nwLat, double nwLong, double seLat, double seLong)
    {
        northWest = new GpsCoordinates(nwLat, nwLong);
        southEast = new GpsCoordinates(seLat, seLong);
    }

    /**
     * Checks if some gps coordinates is inside the boundingBox
     * @param coords
     * @return
     */
    public boolean isInsideBox(GpsCoordinates coords)
    {
        if(coords.getLongitude() > northWest.getLongitude() &&
                coords.getLatitude() < northWest.getLatitude() &&
                coords.getLongitude() < southEast.getLongitude()&&
                coords.getLatitude() > southEast.getLatitude())
        {
            return true;
        }
        return false;
    }
}
