package com.exjobbandroidapplication.Activities;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Iterator;

import Enums.ServerMessageType;
import NetworkMessages.GPSCoordMessage;
import NetworkMessages.GpsCoordinates;
import NetworkMessages.RequestMessage;
import NetworkMessages.Section;
import NetworkMessages.SensorDataMessage;
import NetworkMessages.ServerMessage;

public class osmtest extends AppCompatActivity {
    private osmtest activity = this;
    private MapView map;
    private long lastMessageTime = 0;
    private long currentTime;
    private int secondsBetweenMessages = 2;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private MyLocationNewOverlay myLocationNewOverlay;
    private Display display;
    private int screenHeight;
    private int screenWidth;
    private int textSize;

    /**
     * Is run when the activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calculateTextSize();

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

        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);

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

    /**
     * Calculate the size of the text displaying number of people in a section. Calculation uses the resolution of the device that the
     * application is run on.
     */
    private void calculateTextSize() {
        display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenHeight = point.y;
        screenWidth = point.x;

        textSize = screenWidth / 12;
    }

    /**
     * Method called when copyrightlink is clicked. Opens up the openstreetmap website.
     */
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
        if (screenWidth < 1650) {
            mapController.setZoom(17);
        }
        else {
            mapController.setZoom(18);
        }

        //Disable scrolling on the map.
        map.setOnTouchListener(new View.OnTouchListener() {
       @Override
       public boolean onTouch(View v, MotionEvent event) {
           return true;
       }
   });
    }

    /**
     * Checks if its time to send a message with gpscoordinates to the server.
     * @return returns if gpscoordinates should be sent to the server.
     */
    private synchronized boolean isTimeToSendMessage() {
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
     * @param
     */
    private void displayGPSCoordinates(SensorDataMessage sensorDataMessage){
        ArrayList<GpsCoordinates> gpsCoordinates = sensorDataMessage.getGpsCoordinatesList();
        HashMap<Integer, Section> hashMap = sensorDataMessage.getSectionMap();


        ArrayList<OverlayItem> markerList = new ArrayList<>();
        ArrayList<GeoPoint> list = new ArrayList<>();
        ArrayList<Polygon> campusSections = new ArrayList<>();

        //Display sections and the number og people in them on the map.
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry sec = (HashMap.Entry) iterator.next();
            Section section = (Section) sec.getValue();
            list.clear();
            for (GpsCoordinates coordinate : section.getSectionGpsCoordinates()) {
                GeoPoint geoPoint = new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
                list.add(geoPoint);
            }
            Polygon polygon = new Polygon(this);
            polygon.setPoints(list);
            polygon.setStrokeColor(Color.argb(50, 0 , 120 , 0));
            polygon.setFillColor(Color.argb(50, 0 , 120 , 0));
            polygon.setVisible(true);
            campusSections.add(polygon);

            GeoPoint markerLocation = new GeoPoint(list.get(2).getLatitude(),list.get(3).getLongitude() + ((list.get(2).getLongitude() - list.get(3).getLongitude()) / 2));

            //Display the number of people in section.
            OverlayItem textOverlayItem = new OverlayItem("" , "", markerLocation);
            Drawable textMarker = writeOnDrawable(R.mipmap.text_marker, Integer.toString(section.getAmount()));
            //Log.d("Amount : ", section.getAmount() + "");
            textOverlayItem.setMarker(textMarker);
            markerList.add(textOverlayItem);
        }

        //Display other logged in people on the map.
        if (gpsCoordinates != null  || gpsCoordinates.size() > 1) {
            for (GpsCoordinates gpsCoords : gpsCoordinates) {
                OverlayItem currentLocMarker = new OverlayItem(Double.toString(gpsCoords.getLatitude()), Double.toString(gpsCoords.getLongitude()), new GeoPoint(gpsCoords.getLatitude(), gpsCoords.getLongitude()));
                Drawable marker = getResources().getDrawable(R.drawable.person);
                currentLocMarker.setMarker(marker);
                markerList.add(currentLocMarker);
            }
        }

        ItemizedIconOverlay<OverlayItem> itemItemizedIconOverlay = new ItemizedIconOverlay<>(markerList, null, activity);
        while (map.getOverlays().size() > 1) {
            map.getOverlays().remove(1);
        }

        map.getOverlays().addAll(campusSections);
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
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, canvas.getWidth() / 2 ,(float)(canvas.getHeight() * 0.9), paint);
        return new BitmapDrawable(bm);
    }

    /**
     * Asynchronous task that sends a message to the server and handles the message received from the server.
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

            if (serverMessage == null) {
                Log.d("serverMessage", "Received message is null");
                Toast.makeText(activity, "Error receiving message from server or outside bounding box" , Toast.LENGTH_LONG);
                return false;
            }

            if (serverMessage != null && serverMessage.getMessageType() == ServerMessageType.SensorData) {
                if (serverMessage.getMessage() == null) {
                    Log.d("Received message", "information is null, user outside of bounding box or GPS turned of");
                }

                final SensorDataMessage sensorDataMessage = (SensorDataMessage) serverMessage.getMessage();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayGPSCoordinates(sensorDataMessage);
                    }
                });
                Log.d("GPS", "Received sensor data, running displayGPSCoordinates()");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Log.d("Receiving GPS", "Successfully received GPS coordinates from server");
            }
            else {
                Log.d("Receiving GPS", "Failed receiving GPS coordinates from server");
            }
        }
    }
}