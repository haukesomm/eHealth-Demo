/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.haukesomm.healthdemo.R;

/**
 * Created on 19.11.17
 * <p>
 * This class is a subclass of {@link BaseAdapter} and functions as a List- or Spinner-Adapter for
 * data of the Blackbox's cache-table in form of {@link JSONObject}s.<br>
 * For each cache entry a view representing the start- and destination-locations as well as the
 * average speed will be generated.
 *
 * @author Hauke Sommerfeld
 */
public class BlackboxAdapter extends BaseAdapter {

    /**
     * This constructor takes the app's context and a list of JSONObjects representing the cache-
     * entries. If created with this constructor the adapter can be used as is with no further
     * action needed.
     *
     * @param context   The app's context
     * @param tables    List of tables to display
     */
    public BlackboxAdapter(@NonNull Context context, @NonNull Blackbox blackbox,
                           @NonNull ArrayList<String> tables) {
        mContext = context;

        mBlackbox = blackbox;
        mBlackbox.open();

        mTables = tables;
    }


    @Override
    public void finalize() {
        mBlackbox.close();
    }



    private final Context mContext;



    private final Blackbox mBlackbox;


    private final ArrayList<String> mTables;


    /**
     * This method returns the number of objects the adapter is working with.
     *
     * @return  Number of objects
     */
    @Override
    public int getCount() {
        return mTables.size();
    }


    /**
     * This method returns the corresponding object to a position in the list.
     *
     * @param position  Position of the object which should be returned
     * @return          The corresponding object
     */
    @Override
    public Object getItem(int position) {
        return mBlackbox.getEntireTable(position);
    }


    /**
     * @deprecated
     * This method does nothing and should not be used.
     */
    @Deprecated
    @Override
    public long getItemId(int position) {
        return 0;
    }



    /**
     * This method generates the actual view for each object.
     *
     * @return The actual view
     */
    @SuppressLint({"InflateParams", "SetTextI18n", "SimpleDateFormat"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.blackbox_data_preview, null, false);
            int padding = (int) mContext.getResources().getDimension(R.dimen.margin_default);
            view.setPadding(padding, padding / 2, padding, padding / 2);
        } else {
            return view;
        }



        final String table = mTables.get(position);
        final ArrayList<JSONObject> data = mBlackbox.getEntireTable(table);


        final String date = generateDateFromTable(table);
        TextView dateText = view.findViewById(R.id.blackbox_data_preview_date);
        dateText.setText(date);


        Geocoder geocoder = new Geocoder(mContext);

        final TextView start = view.findViewById(R.id.blackbox_data_preview_start);
        setAddress(geocoder, data.get(0), start);

        final TextView destination = view.findViewById(R.id.blackbox_data_preview_destination);
        setAddress(geocoder, data.get(data.size() - 1), destination);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataActivity = new Intent(mContext, DataActivity.class);
                dataActivity.putExtra(DataActivity.EXTRA_BLACKBOX_TABLE, table);
                dataActivity.putExtra(DataActivity.EXTRA_DATE, date);
                mContext.startActivity(dataActivity);
            }
        });


        return view;
    }



    private String generateDateFromTable(String table) {
        try {
            SimpleDateFormat formatIn = new SimpleDateFormat(Blackbox.DATA_TABLE_DATE_FORMAT);
            Date rawDate = formatIn.parse(table.replace(Blackbox.DATA_TABLE_PREFIX, ""));

            DateFormat formatOut = DateFormat.getDateInstance();
            return formatOut.format(rawDate);
        } catch (ParseException e) {
            Log.w("BlackboxAdapter", "Unable to generate date from table: " + e.getMessage());
            return table;
        }
    }



    private void setAddress(Geocoder geocoder, JSONObject blackboxData, TextView textView) {
        try {
            Address address = geocoder.getFromLocation(
                    Double.parseDouble(blackboxData.getString(Blackbox.DATA_LATITUDE)),
                    Double.parseDouble(blackboxData.getString(Blackbox.DATA_LONGITUDE)), 1).get(0);

            String street = address.getThoroughfare();
            String locality = address.getLocality();
            String formattedAddress = (street != null ? street: "")
                    + (street != null && locality != null ? ", " : "")
                    + (locality != null ? locality : "");

            textView.setText(formattedAddress);
        } catch (IOException | JSONException e) {
            // Do nothing (text set to 'unknown' by default via XML resource)
        }
    }
}
