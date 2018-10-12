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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.privacy.PrivacyMode;

/**
 * Created on 21.02.18
 * <p>
 * This Activity contains both the {@link PrivacyModeChooserFragment} and
 * {@link CustomPrivacyModeFragment} which together make up the privacy settings.
 *
 * @author Hauke Sommerfeld
 */
public class PrivacyPreferenceActivity extends AppCompatActivity
        implements PrivacyModeChooserFragment.ModeChangedListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindActivity();
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
        }

        return super.onOptionsItemSelected(item);
    }



    private PrivacyModeChooserFragment mModeChooser;


    private CustomPrivacyModeFragment mCustomPreferences;


    private void bindActivity() {
        setContentView(R.layout.activity_preferences_privacy);

        Toolbar toolbar = findViewById(R.id.activity_preferences_privacy_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Privacy Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mModeChooser = (PrivacyModeChooserFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_preferences_privacy_fragment);
        mModeChooser.setCollapsible(true);
        mCustomPreferences = (CustomPrivacyModeFragment)
                getFragmentManager().findFragmentById(R.id.activity_preferences_privacy_fragment_userdefined);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void onModeChanged(PrivacyMode mode) {
        mCustomPreferences.setEnabled(mode.equals(PrivacyMode.USER_DEFINED));
    }
}
