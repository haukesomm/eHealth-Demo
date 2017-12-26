/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.haukesomm.telematics.R;

/**
 * Created on 19.11.17
 *
 * This class is a subclass of {@link BaseAdapter} and functions as a List- or Spinner-Adapter for
 * data of the Blackbox's cache-table in form of {@link JSONObject}s.
 * For each cache entry a view representing the start- and destination-locations as well as the
 * average speed will be generated.
 *
 * @author Hauke Sommerfeld
 */
public class BlackboxCacheAdapter extends BaseAdapter {

    /**
     * This constructor takes the app's context and a list of JSONObjects representing the cache-
     * entries. If created with this constructor the adapter can be used as is with no further
     * action needed.
     *
     * @param context   The app's context
     * @param data      Cached data from the Blackbox in form of JSONObjects
     */
    public BlackboxCacheAdapter(Context context, List<JSONObject> data) {
        mContext = context;
        mData = data;
    }



    private final Context mContext;



    private final List<JSONObject> mData;


    /**
     * This method returns the number of objects the adapter is working with.
     *
     * @return  Number of objects
     */
    @Override
    public int getCount() {
        return mData.size();
    }


    /**
     * This method returns the corresponding object to a position in the list.
     *
     * @param position  Position of the object which should be returned
     * @return          The corresponding object
     */
    @Override
    public Object getItem(int position) {
        return mData.get(position);
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

        final JSONObject data = mData.get(position);


        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.blackbox_data_preview, null, false);
        }
        int padding = (int) mContext.getResources().getDimension(R.dimen.margin_default);
        view.setPadding(padding, padding / 2, padding, padding / 2);


        String date = null;
        try {
            TextView dateText = view.findViewById(R.id.blackbox_data_preview_date);

            SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMdd");
            Date rawDate = formatIn.parse(data.getString(Blackbox.CACHE_TABLE).replace(Blackbox.MOCKUP_TABLE_PREFIX, ""));

            DateFormat formatOut = DateFormat.getDateInstance();
            date = formatOut.format(rawDate);

            dateText.setText(date);
        } catch (JSONException | ParseException e) {
            Log.w("BlackboxCacheAdapter", "Unable to set date: " + e.getMessage());
        }

        try {
            TextView start = view.findViewById(R.id.blackbox_data_preview_start);
            start.setText(data.getString(Blackbox.CACHE_LOCATION_START));
        } catch (JSONException e) {
            Log.w("BlackboxCacheAdapter", "Unable to set start: " + e.getMessage());
        }

        try {
            TextView destination = view.findViewById(R.id.blackbox_data_preview_destination);
            destination.setText(data.getString(Blackbox.CACHE_LOCATION_DESTINATION));
        } catch (JSONException e) {
            Log.w("BlackboxCacheAdapter", "Unable to set destination: " + e.getMessage());
        }

        try {
            TextView speed = view.findViewById(R.id.blackbox_data_preview_averageSpeed);

            DecimalFormat format = new DecimalFormat("###.##");
            speed.setText(format.format(data.getDouble(Blackbox.CACHE_AVERAGE_SPEED))
                    + " " + mContext.getString(R.string.data_unit_mph));
        } catch (JSONException e) {
            Log.w("BlackboxCacheAdapter", "Unable to set averageSpeed: " + e.getMessage());
        }


        final String _date = date;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent dataActivity = new Intent(mContext, DataActivity.class);
                    dataActivity.putExtra(DataActivity.EXTRA_BLACKBOX_TABLE, data.getString(Blackbox.CACHE_TABLE));
                    dataActivity.putExtra(DataActivity.EXTRA_DATE, _date);
                    mContext.startActivity(dataActivity);
                } catch (JSONException j) {
                    Log.e("BlackboxCacheAdapter", "Error getting table: " + j.getMessage());
                }
            }
        });


        return view;
    }
}
