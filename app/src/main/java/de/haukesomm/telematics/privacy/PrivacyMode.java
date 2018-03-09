/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017-2018, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import de.haukesomm.telematics.R;

/**
 * Created on 28.11.17
 * <p>
 * This enum defines all available privacy modes.
 *
 * @author Hauke Sommerfeld
 */
public enum PrivacyMode {
    /**
     * Unknown or missing mode.<br>
     * This mode should never actually be available to the user and only functions as some sort of
     * fallback in case somthing goes wrong.
     */
    UNKNOWN(-1, R.string.privacy_mode_unknown_title, R.drawable.ic_privacy_mode_unknown),

    /**
     * Submit all data to the insurance (minimal privacy).<br>
     * The insurance plan might rise or lower in price depending completely on the driving habits of
     * the user.
     */
    MAXIMUM_DATA(0, R.string.privacy_mode_maximum_title, R.drawable.ic_privacy_mode_maximumdata),

    /**
     * Submit only the data explicitly selected by the user.<br>
     * The insurance plan might rise or lower in price depending on the driving habits of
     * the user but with upper and lower limits as well as a higher initial price.
     */
    USER_DEFINED(45, R.string.privacy_mode_custom_title, R.drawable.ic_privacy_mode_userdefined),

    /**
     * Submit only a bare minimum of data to the insurance.<br>
     * The insurance plan might slightly rise or lower in price depending on the driving habits of
     * the user. The initial price is slightly higher than default. Upper and lower limits apply.
     */
    MINIMUM_DATA(65, R.string.privacy_mode_minimum_title, R.drawable.ic_privacy_mode_minimumdata),

    /**
     * Do not submit any data at all (maximum privacy).<br>
     * A default plan with a fixed price (higher than the general upper limit) applies.
     */
    OBFUSCATION(100, R.string.privacy_mode_obfuscation_title, R.drawable.ic_privacy_mode_obfuscation);


    /**
     * This method returns all user accessible PrivacyModes, which are all modes except those used
     * for error purposes or error handling. Their IDs are always greater equal than zero.
     *
     * @return  Array of user accessible modes
     */
    public static PrivacyMode[] userModes() {
        List<PrivacyMode> modes = new ArrayList<>();
        for (PrivacyMode m : values()) {
            if (m.getID() >= 0) {
                modes.add(m);
            }
        }
        return modes.toArray(new PrivacyMode[modes.size()]);
    }


    /**
     * This method tries to find a PrivacyMode by its ID an returns it if found.
     * If no mode can be found, this method returns {@link #UNKNOWN}.
     *
     * @param id                        ID of the PrivacyMode
     * @return                          Privacy mode with the specified ID
     */
    public static PrivacyMode fromID(int id) {
        for (PrivacyMode mode : PrivacyMode.values()) {
            if (mode.getID() == id) {
                return mode;
            }
        }

        return UNKNOWN;
    }



    private int mID;


    @StringRes
    private int mNameRes;


    @DrawableRes
    private int mDrawableRes;



    PrivacyMode(int id, @StringRes int nameRes, @DrawableRes int drawableRes) {
        mID = id;
        mNameRes = nameRes;
        mDrawableRes = drawableRes;
    }


    /**
     * This method returns the ID of a given PrivacyMode
     *
     * @return  ID
     */
    public int getID() {
        return mID;
    }


    /**
     * This method returns the ID of a given PrivacyMode
     *
     * @return  ID
     */
    @StringRes
    public int getNameRes() {
        return mNameRes;
    }


    /**
     * This method returns the ID of a given PrivacyMode
     *
     * @return  ID
     */
    @DrawableRes
    public int getDrawableRes() {
        return mDrawableRes;
    }
}
