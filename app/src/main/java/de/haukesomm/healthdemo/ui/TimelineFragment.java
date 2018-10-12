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

package de.haukesomm.healthdemo.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import de.haukesomm.healthdemo.R;
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
