/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import de.haukesomm.healthdemo.data.Blackbox;
import de.haukesomm.healthdemo.data.SessionDatabase;
import de.haukesomm.healthdemo.data.SessionDescription;
import de.haukesomm.healthdemo.data.SessionDescriptionAdapter;

/**
 * Created on 27.11.17
 * <p>
 * This Fragment provides a list of all {@link de.haukesomm.healthdemo.data.Session} entries in a
 * reverse (newest-to-oldest) order.
 *
 * @author Hauke Sommerfeld
 */
public class TimelineFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        if (getContext() == null) {
            return view;
        }


        ListView recents = view.findViewById(R.id.fragment_timeline_list);

        try (SessionDatabase database = new SessionDatabase(getContext())) {
            List<SessionDescription> descriptions = database.listSessions();
            SessionDescriptionAdapter adapter = new SessionDescriptionAdapter(getContext(), descriptions);
            recents.setAdapter(adapter);
        }


        return view;
    }

}
