/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.data;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.helper.ViewHelper;

/**
 * Created on 09.12.17
 * <p>
 * This Activity displays detailed information about a {@link Blackbox} table in form of various
 * diagrams for speed, height, etc. as well as a map.
 *
 * @author Hauke Sommerfeld
 */
public class DataActivity extends AppCompatActivity {

    /**
     * Use this String in the launch {@link android.content.Intent} to specify the table to display.
     * This is mandatory for the Activity to work. Missing data will result in the Activity
     * finishing.
     */
    public static final String EXTRA_BLACKBOX_TABLE = "blackbox_table";


    /**
     * Use this String in the launch {@link android.content.Intent} to specify a date/title to display.
     */
    public static final String EXTRA_DATE = "title";



    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_data);
        bindActivity();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {

            String date = getIntent().getStringExtra(EXTRA_DATE);
            getSupportActionBar().setTitle(date != null ? date : getString(R.string.unknown));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        initData();
        initMap();
        initRoute();
        initGraphs();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_data, menu);
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            // TODO Add delete option
            /*case R.id.activity_data_menuAction_delete:
                return true;*/

            case R.id.activity_data_menuAction_report:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setIcon(R.drawable.ic_report);
                dialog.setTitle(R.string.data_report_title);
                dialog.setMessage(R.string.data_report_message);
                // Dummy buttons for demo purposes only
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.setPositiveButton(R.string.data_report_proceed, null);
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private Toolbar mToolbar;



    private TextView mRouteStart;


    private TextView mRouteDestination;


    private TelematicsGraphView mGraphSpeed;


    private void bindActivity() {
        mToolbar = findViewById(R.id.activity_data_toolbar);

        mRouteStart = findViewById(R.id.activity_data_route_start);
        mRouteDestination = findViewById(R.id.activity_data_route_destination);
        mGraphSpeed = findViewById(R.id.activity_data_graph_speed);
    }



    private Blackbox mBlackbox;


    private ArrayList<JSONObject> mData;


    private void initData() {
        String table = getIntent().getStringExtra(EXTRA_BLACKBOX_TABLE);

        if (table == null) {
            Log.e("DataActivity", "No Blackbox table specified.");
            Toast.makeText(this, R.string.data_notAvailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        mBlackbox = new Blackbox(this);
        try {
            mBlackbox.open();
            mData = mBlackbox.getEntireTable(table);
            mBlackbox.close();
        } catch (SQLiteException e) {
            Log.e("DataActivity", "Invalid Blackbox table specified.");
            Toast.makeText(this, R.string.data_notAvailable, Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    private GoogleMap mMap;


    private void initMap() {
        final List<LatLng> positions = new LinkedList<>();

        for (JSONObject data : mData) {
            try {
                LatLng position = new LatLng(
                        data.getDouble(Blackbox.DATA_LATITUDE),
                        data.getDouble(Blackbox.DATA_LONGITUDE)
                );
                positions.add(position);
            } catch (JSONException e) {
                Log.w("DataActivity", "Unable to get LatLng data: " + e.getMessage());
            }
        }


        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng position : positions) {
            builder.include(position);
        }
        final LatLngBounds bounds = builder.build();


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_data_map);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);
                initMapMarkers(bounds, positions);
            }
        });
    }


    private void initMapMarkers(LatLngBounds bounds, List<LatLng> positions) {
        PolylineOptions path = new PolylineOptions()
                .color(getColor(R.color.colorAccent))
                .width(20f);

        // Starting position
        mMap.addMarker(
                new MarkerOptions().position(positions.get(0)));
        // Destination marker
        mMap.addMarker(
                new MarkerOptions().position(positions.get(positions.size() - 1)));

        for (LatLng position : positions) {
            path.add(position);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1000, 1000, 200));
        mMap.addPolyline(path);
    }



    private Geocoder mGeocoder;


    private void initRoute() {
        mGeocoder = new Geocoder(this);

        double startLat, startLng;
        double destLat, destLng;
        try {
            startLat = mData.get(0).getDouble(Blackbox.DATA_LATITUDE);
            startLng = mData.get(0).getDouble(Blackbox.DATA_LONGITUDE);

            destLat = mData.get(mData.size() - 1).getDouble(Blackbox.DATA_LATITUDE);
            destLng = mData.get(mData.size() - 1).getDouble(Blackbox.DATA_LONGITUDE);
        } catch (JSONException e) {
            Log.w("DataActivity", "Unable to init route: " + e.getMessage());
            return;
        }

        mRouteStart.setText(getAddressFromLatLng(startLat, startLng));
        mRouteDestination.setText(getAddressFromLatLng(destLat, destLng));
    }


    private String getAddressFromLatLng(double lat, double lng) {
        try {
            Address address = mGeocoder.getFromLocation(lat, lng, 1).get(0);
            return address.getAddressLine(0);
        } catch (IOException e) {
            return getString(R.string.unknown);
        }
    }



    private static final int GRAPH_DEFAULT_THICKNESS = 7;


    private void initGraphs() {
        LineGraphSeries<DataPoint> speedValues = new LineGraphSeries<>();

        Paint color = new Paint();
        color.setColor(getColor(R.color.colorAccent));
        color.setStrokeWidth((float) GRAPH_DEFAULT_THICKNESS);
        speedValues.setCustomPaint(color);

        for (int i = 0; i < mData.size(); i++) {
            JSONObject data = mData.get(i);

            try {
                double speed = data.getDouble(Blackbox.DATA_SPEED);
                speedValues.appendData(new DataPoint(i, speed), true, mData.size(), true);
            } catch (JSONException e) {
                Log.w("DataActivity", "Unable to add value to graph: " + e.getMessage());
            }
        }

        mGraphSpeed.setData(speedValues);
    }
}
