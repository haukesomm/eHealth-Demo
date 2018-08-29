/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.haukesomm.healthdemo.data.Blackbox;
import de.haukesomm.healthdemo.data.BlackboxAdapter;
import de.haukesomm.healthdemo.data.SessionDatabase;
import de.haukesomm.healthdemo.data.SessionDescription;
import de.haukesomm.healthdemo.data.SessionDescriptionAdapter;
import de.haukesomm.healthdemo.privacy.PrivacyMode;
import de.haukesomm.healthdemo.privacy.PrivacyModeView;

/**
 * Created on 27.11.17
 * <p>
 * This Fragment provides an overview over the user's
 * {@link de.haukesomm.healthdemo.privacy.PrivacyMode} settings and his 5 most recent sets of
 * driving data.
 *
 * @author Hauke Sommerfeld
 */
public class OverviewFragment extends Fragment {

    private static final int MAX_PREVIEWS = 5;



    private SharedPreferences mPrefs;



    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());



        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        if (getContext() == null) {
            return view;
        }


        mPrivacyModeView = view.findViewById(R.id.fragment_overview_privacyMode);
        updatePrivacyMode();


        ListView recents = view.findViewById(R.id.fragment_overview_list);

        try (SessionDatabase database = new SessionDatabase(getContext())) {
            List<SessionDescription> descriptions = database.listSessions();

            List<SessionDescription> newest = new ArrayList<>();
            for (int i = 0; i < descriptions.size() && i < 5; i++) {
                newest.add(descriptions.get(i));
            }
            Collections.reverse(newest);

            SessionDescriptionAdapter adapter = new SessionDescriptionAdapter(getContext(), newest);
            recents.setAdapter(adapter);
        }


        return view;
    }



    private PrivacyModeView mPrivacyModeView;


    public void updatePrivacyMode() {
        int id = mPrefs
                .getInt(getString(R.string.pref_int_privacy_lastModeID), PrivacyMode.UNKNOWN.getID());
        PrivacyMode mode = PrivacyMode.fromID(id);
        mPrivacyModeView.setMode(mode);
    }

}
