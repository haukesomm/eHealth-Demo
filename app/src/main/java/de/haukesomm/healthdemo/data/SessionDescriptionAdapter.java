/*
 * This file is part of the "eHealth-Demo" project, formerly known as
 * "Telematics App Mockup".
 * Copyright 2017-2018, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *
 * Licensed under the MIT license.
 *
 * For more information and/or a copy of the license visit the following
 * GitHub repository: https://github.com/haukesomm/eHealth-Demo
 */

package de.haukesomm.healthdemo.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.ui.DataActivity;

/**
 * Created on 29.08.18
 * <p>
 * This class is a subclass of {@link BaseAdapter} and functions as a List- or Spinner-Adapter for
 * session data in form of {@link SessionDescription}s.<br>
 * For each cache entry a view representing the basic session info will be generated.
 *
 * @author Hauke Sommerfeld
 */
public class SessionDescriptionAdapter extends BaseAdapter {

    /**
     * This constructor takes the app's context and a list of JSONObjects representing the cache-
     * entries. If created with this constructor the adapter can be used as is with no further
     * action needed.
     *
     * @param context   The app's context
     * @param sessions    List of tables to display
     */
    public SessionDescriptionAdapter(@NonNull Context context, @NonNull List<SessionDescription> sessions) {
        mContext = context;
        mSessionDescriptions = sessions;
    }



    private final Context mContext;



    private final List<SessionDescription> mSessionDescriptions;


    /**
     * This method returns the number of objects the adapter is working with.
     *
     * @return  Number of objects
     */
    @Override
    public int getCount() {
        return mSessionDescriptions.size();
    }


    /**
     * This method returns the corresponding object to a position in the list.
     *
     * @param position  Position of the object which should be returned
     * @return          The corresponding object
     */
    @Override
    public Object getItem(int position) {
        return mSessionDescriptions.get(position);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.view_adapter_sessioninfo, null, false);
        } else {
            return view;
        }


        SessionDescription description = mSessionDescriptions.get(position);

        TextView title = view.findViewById(R.id.view_adapter_sessioninfo_title);
        title.setText(description.description);

        TextView summary = view.findViewById(R.id.view_adapter_sessioninfo_summary);
        summary.setText(description.type.descriptionRes);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DataActivity.class);
                intent.putExtra(DataActivity.EXTRA_SESSION_ID, mSessionDescriptions.get(position).id);
                mContext.startActivity(intent);
            }
        });


        return view;
    }
}
