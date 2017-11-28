/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Mukisa Kibirige.
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

import android.support.annotation.NonNull;

/**
 * Created on 28.11.17
 *
 * This enum defines all available privacy modes.
 *
 * @author Hauke Sommerfeld
 */
public enum PrivacyMode {
    UNKNOWN(-1),
    MAXIMUM_DATA(0),
    USER_DEFINED(45),
    MINIMUM_DATA(65),
    OBFUSCATION(100);



    public static PrivacyMode fromPercentage(int percentage) throws ClassNotFoundException {
        for (PrivacyMode mode : PrivacyMode.values()) {
            if (mode.getPercentage() == percentage) {
                return mode;
            }
        }

        throw new ClassNotFoundException("There is no privacy mode with percentage " + percentage);
    }



    int mPercentage;



    PrivacyMode(int percentage) {
        mPercentage = percentage;
    }



    public int getPercentage() {
        return mPercentage;
    }
}
