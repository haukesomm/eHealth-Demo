/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.privacy.PrivacyMode;

/**
 * Created on 30.12.17
 * <p>
 * This Fragment provides a SettingsFragment used to customize the {@link PrivacyMode#USER_DEFINED}
 * privacy mode.
 *
 * @author Hauke Sommerfeld
 */
public class CustomPrivacyModeFragment extends PreferenceFragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_privacymode_custom);
    }



    /**
     * This method enables or disables all Preferences of this PreferenceFragment
     *
     * @param enabled   true to enable all Preferences
     */
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            getPreferenceScreen().getPreference(i).setEnabled(enabled);
        }
    }
}
