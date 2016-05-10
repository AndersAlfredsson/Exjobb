package com.exjobbandroidapplication.Activities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
    private ArrayList<Polygon> campusSections = new ArrayList<>();
    private MapView map;
    private long lastMessageTime = 0;
    private long currentTime;
    private int secondsBetweenMessages = 2;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private MyLocationNewOverlay myLocationNewOverlay;
    private int iconOverlayIndex;
    private final GeoPoint SECTION_ONE_COORDINATES  = new GeoPoint(59.254132, 15.246469);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the titlebar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_osmtest);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupMapView();
        myLocationNewOverlay = new MyLocationNewOverlay(map);
        gpsMyLocationProvider = new GpsMyLocationProvider(this);
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
                    }
                    else{
                        Log.d("GPS", "Not enough time has passed since last update");
                    }
                }
            }
        };

        gpsMyLocationProvider.startLocationProvider(iMyLocationConsumer);
    }

    private void linkClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.openstreetmap.org/copyright")));
    }

    /**
     * Setup the MapView to center on the campus area and lock the zoom level to 18.
     */
    private void setupMapView() {
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        final IMapController mapController = map.getController();
        final GeoPoint centerGeoPoint = new GeoPoint(59.2542299, 15.2476963);
        mapController.setCenter(centerGeoPoint);
        map.setMinZoomLevel(17);
        map.setMaxZoomLevel(18);
        mapController.setZoom(18);

        //Disable scrolling on the map.
        map.setOnTouchListener(new View.OnTouchListener() {
       @Override
       public boolean onTouch(View v, MotionEvent event) {
           if(event.getAction() ==  MotionEvent.ACTION_UP) {
               mapController.setCenter(centerGeoPoint);
               return true;
           }
           if(event.getPointerCount() > 1) {
               mapController.setCenter(centerGeoPoint);
               return false;
           }
           else {
               return true;
           }
       }
   });
    }

    /**
     *
     * @return
     */
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
     * Displays a marker for every set of coordinates received from the server.
     * Also displays the number of estimated people in a section.
     * @param list
     */
    private void displayGPSCoordinates(ArrayList<GpsCoordinates> list, Object peopleInSection){
        ArrayList<OverlayItem> markerList = new ArrayList<>();
        if (list == null  || list.size() < 1) {
            for (GpsCoordinates gpsCoordinates : list) {
                OverlayItem currentLocMarker = new OverlayItem(Double.toString(gpsCoordinates.getLatitude()), Double.toString(gpsCoordinates.getLongitude()), new GeoPoint(gpsCoordinates.getLatitude(), gpsCoordinates.getLongitude()));
                Drawable marker = getResources().getDrawable(R.drawable.person);
                currentLocMarker.setMarker(marker);
                markerList.add(currentLocMarker);
            }
        }

        //TODO : hantera data för antal personer i en sektion.
        //Display the number of people in section.
        //OverlayItem textOverlayItem = new OverlayItem("123123" , "33", SECTION_ONE_COORDINATES);
        //Drawable textMarker = writeOnDrawable(R.mipmap.text_marker, Integer.toString(peopleInSection));
        //textOverlayItem.setMarker(textMarker);
        //markerList.add(textOverlayItem);

        ItemizedIconOverlay<OverlayItem> itemItemizedIconOverlay = new ItemizedIconOverlay<>(markerList, null, activity);
        if (map.getOverlays().size() > iconOverlayIndex) {
            map.getOverlays().remove(iconOverlayIndex);
        }
        map.getOverlays().add(itemItemizedIconOverlay);
        map.invalidate();
    }

    /**
     * Method takes a marker and returns the marker with the specified text written on it.
     * @param drawableId
     * @param text
     * @return
     */
    private BitmapDrawable writeOnDrawable(int drawableId, String text){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(140);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight()/2, paint);
        return new BitmapDrawable(bm);
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

    /**
     * Asynchronous task that sends a message to the server and handles the
     */
    class SendGPSTask extends AsyncTask<Void, Void, Boolean> {
        Location location;

        public SendGPSTask(Location loc) {
            location = loc;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            GPSCoordMessage message = new GPSCoordMessage(ConnectionHandler.getInstance().geteMail(), location.getLatitude(),location.getLongitude());
            ServerMessage serverMessage = ConnectionHandler.getInstance().sendMessage(new RequestMessage(message));

            //TODO : om man ska inte ska få någon data från servern skall null, null skickas. Detta måste hanteras och kanske skrivas ut i appen.
            if (serverMessage == null) {
                Log.d("serverMessage", "Received message is null");
                return false;
            }

            if (serverMessage != null && serverMessage.getMessageType() == ServerMessageType.SensorData) {
                if (serverMessage.getMessage() == null && serverMessage.getSensorData() == null) {
                    Log.d("Received message", "information is null, user outside of bounding box or GPS turned of");
                }
                final ArrayList<GpsCoordinates> list = (ArrayList) serverMessage.getMessage();
                //final int peopleInSection = serverMessage.getSensorData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayGPSCoordinates(list, null);
                    }
                });
                Log.d("GPS", "Received sensor data, running displayGPSCoordinates()");
            }
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