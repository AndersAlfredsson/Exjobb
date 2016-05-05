package com.exjobbandroidapplication.Activities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import com.exjobbandroidapplication.Network.ConnectionHandler;
import com.exjobbandroidapplication.R;
import java.util.ArrayList;
import Enums.ServerMessageType;
import NetworkMessages.GPSCoordMessage;
import NetworkMessages.GpsCoordinates;
import NetworkMessages.RequestMessage;
import NetworkMessages.ServerMessage;

public class osmtest extends AppCompatActivity {
    private osmtest activity = this;
    private GeoPoint myLocation = new GeoPoint(59.2542299, 15.2476963);
    private ArrayList<Polygon> campusSections = new ArrayList<>();
    private MapView map;
    private long lastMessageTime = 0;
    private long currentTime;
    private int secondsBetweenMessages = 2;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private MyLocationNewOverlay myLocationNewOverlay;
    private int iconOverlayIndex;

    private final String tag = "OSMclass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hides the titlebar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }




        setContentView(R.layout.activity_osmtest);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupMapView();
        myLocationNewOverlay = new MyLocationNewOverlay(map);
        gpsMyLocationProvider = new GpsMyLocationProvider(this);

        askForStoragePermission();
        askForGPSPermission();
        setupCampusSections();

        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().addAll(campusSections);

        iconOverlayIndex = map.getOverlays().size();

        TextView linkText = (TextView) findViewById(R.id.CopyrightLink);
        linkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkClicked();
            }
        });

        IMyLocationConsumer iMyLocationConsumer = new IMyLocationConsumer() {
            @Override
            public synchronized void onLocationChanged(Location location, IMyLocationProvider source) {
                //TODO : skickar flera meddelanden på "samma" gång till servern.
                Log.d("GPS", "Location has changed");
                if (location == null){
                    Log.d("GPS","Location is null");
                }
                else {
                    if (isTimeToSendMessage()) {
                        Log.d("GPS", "Sending message");
                        SendGPSTask sendGPSTask = new SendGPSTask(location);
                        sendGPSTask.execute();
                        myLocation = new GeoPoint(location);
                    }
                    else{
                        Log.d("GPS", "Not enough time has passed since last update");
                    }
                }
            }
        };

        gpsMyLocationProvider.startLocationProvider(iMyLocationConsumer);

        //Disable scrolling on the map.
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void linkClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.openstreetmap.org/copyright")));
    }

    private void setupMapView() {
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = map.getController();
        GeoPoint centerGeoPoint = new GeoPoint(59.2542299, 15.2476963);
        mapController.setCenter(centerGeoPoint);
        mapController.setZoom(18);
    }

    private boolean isTimeToSendMessage() {
        currentTime = System.currentTimeMillis();
        if (lastMessageTime == 0) {
            lastMessageTime = currentTime;
            return true;
        }
        else {
            if (currentTime >= lastMessageTime + (secondsBetweenMessages * 1000)) {
                lastMessageTime = currentTime;
                return true;
            }
            return false;
        }
    }

    /**
     * Ask user for permission to store data on device and to use the GPS.
     */
    private void askForStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void askForGPSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    /**
     * Displays a marker for every set of coordinates received from the server.
     * @param list
     */
    private void displayGPSCoordinates(ArrayList<GpsCoordinates> list){
        ArrayList<OverlayItem> markerList = new ArrayList<>();
        if (list == null  ||list.size() < 1) {
            return;
        }
        for (GpsCoordinates gpsCoordinates : list) {
            Log.d("GPS", "New object" + gpsCoordinates.toString());
            OverlayItem currentLocMarker = new OverlayItem(Double.toString(gpsCoordinates.getLatitude()) , Double.toString(gpsCoordinates.getLongitude()), new GeoPoint(gpsCoordinates.getLatitude(), gpsCoordinates.getLongitude()));
            Drawable marker = getResources().getDrawable(R.drawable.person);
            currentLocMarker.setMarker(marker);
            markerList.add(currentLocMarker);
            Log.d("GPS", "markerlist size:" + Integer.toString(markerList.size()));
        }
        ItemizedIconOverlay<OverlayItem> itemItemizedIconOverlay = new ItemizedIconOverlay<>(markerList, null, activity);
        if (map.getOverlays().size() > iconOverlayIndex) {
            map.getOverlays().remove(iconOverlayIndex);
        }
        map.getOverlays().add(itemItemizedIconOverlay);
        map.invalidate();
    }

    /**
     * Set up the sections to be displayed and add that section to the list of all sections.
     */
    private void setupCampusSections() {
        GeoPoint topLeft = new GeoPoint(59.254466, 15.245775);
        GeoPoint topRight = new GeoPoint(59.254466, 15.246934);
        GeoPoint bottomRight = new GeoPoint(59.254115, 15.246934);
        GeoPoint bottomLeft = new GeoPoint(59.254115, 15.245775);
        ArrayList<GeoPoint> list = new ArrayList<>();
        list.add(topRight);
        list.add(topLeft);
        list.add(bottomLeft);
        list.add(bottomRight);
        Polygon polygon = new Polygon(this);
        polygon.setPoints(list);
        polygon.setStrokeColor(Color.argb(50, 0 , 120 , 0));
        polygon.setFillColor(Color.argb(50, 0 , 120 , 0));
        Log.d("Sections", "Fillcolor: " + polygon.getOutlinePaint());
        polygon.setVisible(true);
        campusSections.add(polygon);
    }

    class SendGPSTask extends AsyncTask<Void, Void, Boolean> {
        Location location;

        public SendGPSTask(Location loc) {
            location = loc;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            GPSCoordMessage message = new GPSCoordMessage(ConnectionHandler.getInstance().geteMail(), location.getLatitude(),location.getLongitude());
            ServerMessage serverMessage = ConnectionHandler.getInstance().sendMessage(new RequestMessage(message));

            if (serverMessage == null) {
                return false;
            }

            if (serverMessage != null && serverMessage.getMessageType() == ServerMessageType.SensorData) {
                final ArrayList<GpsCoordinates> list = (ArrayList) serverMessage.getMessage();
                final ArrayList<GpsCoordinates> fakelist = new ArrayList<>();
                fakelist.add(new GpsCoordinates(59.254466, 15.245775));
                fakelist.add(new GpsCoordinates(59.254463, 15.246934));
                fakelist.add(new GpsCoordinates(59.254115, 15.246945));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayGPSCoordinates(list);
                    }
                });
                Log.d("GPS", "Received sensor data, running displayGPSCoordinates()");
            }

            //TODO : ska göras räättt..vad händer om vi inte får tillbaka något från servern?
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                Log.d("Receiving GPS", "Successfully received GPS coordinates from server");
            }
            else {
                Log.d("Receiving GPS", "Failed receiving GPS coordinates from server");
            }
        }
    }
}
