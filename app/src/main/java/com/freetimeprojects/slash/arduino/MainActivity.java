package com.freetimeprojects.slash.arduino;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Pubnub mPubnub;

    private static String PUBLISH_KEY = "pub-c-2ba9bda1-fac8-42ab-b3e8-35523e97ee01";
    private static String SUBSCRIBE_KEY = "sub-c-e8532d58-f803-11e6-ac91-02ee2ddab7fe";
    private static String TAG = "";

    private GoogleMap mGoogleMap;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        try {
            mPubnub.subscribe("Demo Maps Tracking", subscribeCallback);
        } catch (PubnubException e) {
            Log.e(TAG, e.toString());
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        initializeMap();
    }

    private PolylineOptions mPolylineOptions;

    private void initializeMap() {
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.BLUE).width(10);
    }


    Callback subscribeCallback = new Callback() {

        @Override
        public void successCallback(String channel, Object message) {
            JSONObject jsonMessage = (JSONObject) message;
            try {
                double mLat = jsonMessage.getDouble("lat");
                double mLng = jsonMessage.getDouble("lng");
                mLatLng = new LatLng(mLat, mLng);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updatePolyline();
                    updateCamera();
                    updateMarker();
                }
            });
        }
    };

    private void updatePolyline() {
        mGoogleMap.clear();
        mGoogleMap.addPolyline(mPolylineOptions.add(mLatLng));
    }
    private void updateMarker() {
        mGoogleMap.addMarker(new MarkerOptions().position(mLatLng));
    }
    private void updateCamera() {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

}
