package com.exjobbandroidapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroupOverlay;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import com.exjobbandroidapplication.Network.ConnectionHandler;
import com.exjobbandroidapplication.R;

import java.util.ArrayList;

import NetworkMessages.GPSCoordMessage;
import NetworkMessages.Message;
import NetworkMessages.RequestMessage;

public class osmtest extends AppCompatActivity {

    osmtest activity = this;
    GeoPoint myLocation = new GeoPoint(59.2542299, 15.2476963);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmtest);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        final MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = map.getController();
        GeoPoint geoPoint = new GeoPoint(59.2542299, 15.2476963);
        mapController.setCenter(geoPoint);
        mapController.setZoom(18);

        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);
        OverlayItem currentLocMarker = new OverlayItem("PO", "TATO", myLocation);
        Drawable marker = getResources().getDrawable(R.drawable.direction_arrow);
        currentLocMarker.setMarker(marker);

        final ArrayList<OverlayItem> itemList = new ArrayList<>();
        itemList.add(currentLocMarker);

        ItemizedIconOverlay<OverlayItem> itemItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(itemList, null, activity);

        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(map);


        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(itemItemizedIconOverlay);
        map.getOverlays().add(myLocationNewOverlay);


        IMyLocationConsumer iMyLocationConsumer = new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                if (location == null){
                    Log.d("det", "gick inte");
                }
                else{
                    SendGPSTask sendGPSTask = new SendGPSTask(location);
                    sendGPSTask.execute();
                    myLocation = new GeoPoint(location);
                    Log.d("Det", "gick");
                }
            }
        };

        gpsMyLocationProvider.startLocationProvider(iMyLocationConsumer);


        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Storage Permission", "Granted");

                } else {
                    Log.d("Storage Permission", "Denied");
                }
                return;
            }
        }
    }

    class SendGPSTask extends AsyncTask<Void, Void, Boolean> {
        Location location;

        public SendGPSTask(Location loc) {
            location = loc;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            GPSCoordMessage message = new GPSCoordMessage("asunfasf", location.getLongitude(), location.getLatitude());
            ConnectionHandler.getInstance().sendMessage(new RequestMessage(message));
            //TODO : ska göras räättt..
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
