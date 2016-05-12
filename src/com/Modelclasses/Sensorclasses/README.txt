-----------------------------------------------------------
Documentation for creating and managing sensors and section
-----------------------------------------------------------

*- SensorIps.txt -*
Just add one Ip-Address to each row for each sensor,

NOTE!
Make sure not to add an empty row at the end of the file.


*- SectionGpsCoordinates.txt -*
The main file for defining a new section on the Campus
The formatting is:
SectionID,Latitude,Longitude

SectionID - Defines which section the coordinate is placed in.
Latitude - Defines the latitude coordinate for the point in the section
Longitude - Defines the longitude coordinate for the point in the section

NOTE!
These coordinates must be placed in a specific order!
Which is:

NorthEast
NorthWest
SouthWest
SouthEast

To add new sections, just define the corners of the section in the order
there can be more than 4 corners in a section. The first row of the section
must be the NorthEast corner, and then defined in a counter-clockwise direction.


*- SensorPairs.txt -*
The file defines the pairs of sensors placed on Campus,

The formatting is:
InnerSensorID,OuterSensorID,InnerSectionID,NeighboringSectionID

InnerSensorID - The ID of the sensor that is the closest one to the center of the section
OuterSensorID - The ID of the sensor that is the furthest from the center of the section
InnerSectionID - The ID of the section that is closest to the InnerSensorID
NeighboringSectionID - The ID of the section that is closest to the OuterSensorID

NOTE!
If there is no NeighboringSection set NeighboringSectionID to -1.