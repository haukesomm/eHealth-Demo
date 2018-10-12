/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017-2018, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.ui;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.privacy.PrivacyMode;
import de.haukesomm.healthdemo.privacy.PrivacyModeAdapter;

/**
 * Created on 21.02.18
 * <p>
 * This Fragment lets the user choose his preferred {@link PrivacyMode}.
 *
 * @author Hauke Sommerfeld
 */
public class PrivacyModeChooserFragment extends Fragment {

    private SharedPreferences mPrefs;



    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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



    private LinearLayout mContent;


    private ImageView mContentToggle;


    private Spinner mPrivacyModes;


    private View setupFragment(final View view) {
        mContent = view.findViewById(R.id.fragment_privacyModeChooser_content);

        mContentToggle =
                view.findViewById(R.id.fragment_privacyModeChooser_content_toggle);
        mContentToggle.setOnClickListener(new View.OnClickListener() {
            private boolean _visible = true;

            private TextView _contentHidden =
                    view.findViewById(R.id.fragment_privacyModeChooser_content_hidden);

            @Override
            public void onClick(View v) {
                _visible = !_visible;

                mContent.setVisibility(_visible
                        ? View.VISIBLE
                        : View.GONE);
                _contentHidden.setVisibility(!_visible
                        ? View.VISIBLE
                        : View.GONE);

                if (getContext() != null) {
                    mContentToggle.setImageDrawable(getContext().getDrawable(_visible
                            ? R.drawable.ic_less
                            : R.drawable.ic_more));
                }
            }
        });

        mPrivacyModes = mContent.findViewById(R.id.fragment_privacyModeChooser_modes);
        initModes();

        return view;
    }



    /**
     * This Interface is used to notify the attached Activity about {@link PrivacyMode} changes.
     */
    public interface ModeChangedListener {

        /**
         * This method is called when the user selects a new {@link PrivacyMode}.
         *
         * @param mode  New mode
         */
        void onModeChanged(PrivacyMode mode);
    }


    private ModeChangedListener mModeListener;


    // Under development!
    private void initModes() {
        assert getContext() != null : "Fragment not attached to any Activity!";

        final PrivacyMode[] modes = PrivacyMode.userModes();

        mPrivacyModes.setAdapter(new PrivacyModeAdapter(getContext(), modes));

        int lastID = mPrefs.getInt(getString(R.string.pref_int_privacy_lastModeID), modes[0].getID());
        PrivacyMode lastMode = PrivacyMode.fromID(lastID);
        mPrivacyModes.setSelection(Arrays.asList(modes).indexOf(lastMode));

        mPrivacyModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PrivacyMode mode = modes[position];

                prefs.edit()
                        .putInt(getString(R.string.pref_int_privacy_lastModeID), mode.getID())
                        .apply();

                if (mModeListener != null) {
                    mModeListener.onModeChanged(mode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    /**
     * this method enables/disables the optional collapse of this Fragment in order to save space.
     * A dedicated button will appear.
     *
     * @param collapsible True if collapse allowed
     */
    public void setCollapsible(boolean collapsible) {
        mContentToggle.setVisibility(collapsible ? View.VISIBLE : View.GONE);
    }
}
