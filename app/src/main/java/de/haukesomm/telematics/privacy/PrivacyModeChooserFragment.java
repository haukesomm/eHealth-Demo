/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.haukesomm.telematics.R;

/**
 * Created on 21.02.18
 * <p>
 *
 * @author Hauke Sommerfeld
 */
public class PrivacyModeChooserFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return setupFragment(inflater.inflate(R.layout.fragment_privacymodechooser, container, false));
    }



    /**
     * {@inheritDoc}
     * <br>
     * This method also sets the {@link ModeChangedListener} and throws an exception if the
     * respective Activity does not implement the interface.
     *
     * @see ClassCastException
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d("PrivacyPrefFragment", "onAttach called!");

        try {
            mModeListener = (ModeChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "PrivacyModeFragment parent needs to implement ModeChangedListener!");
        }
    }



    private Spinner mPrivacyModes;


    private View setupFragment(View view) {
        mPrivacyModes = view.findViewById(R.id.fragment_preferences_privacy_modes);

        initModes();

        return view;
    }



    /**
     * This Interface is used to notify the attached Activity about {@link PrivacyMode} changes.
     */
    public interface ModeChangedListener {
        void onModeChanged(PrivacyMode mode);
    }


    private ModeChangedListener mModeListener;


    // Under development!
    private void initModes() {
        assert getContext() != null : "Fragment not attached to any Activity!";

        final PrivacyMode[] modes = PrivacyMode.values();

        mPrivacyModes.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, modes));
        mPrivacyModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mModeListener != null) {
                    mModeListener.onModeChanged(modes[position]);
                }
                // To-do: Update settings
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}
