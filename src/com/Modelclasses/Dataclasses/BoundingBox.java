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

    public BoundingBox(double nwLat, double nwLong, double seLat, double seLong)
    {
        northWest = new GpsCoordinates(nwLat, nwLong);
        southEast = new GpsCoordinates(seLat, seLong);
    }

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
