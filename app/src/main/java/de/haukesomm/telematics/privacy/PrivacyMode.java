/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

/**
 * Created on 28.11.17
 * <p>
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


    /**
     * This method tries to find a PrivacyMode by its ID an returns it if found.
     * <br>
     * A {@link ClassNotFoundException} will be thrown if no PrivacyMode was found.
     *
     * @param id                        ID of the PrivacyMode
     * @return                          Privacy mode with the specified ID
     * @throws ClassNotFoundException   in case no PrivacyMode was found
     */
    public static PrivacyMode fromID(int id) throws ClassNotFoundException {
        for (PrivacyMode mode : PrivacyMode.values()) {
            if (mode.getID() == id) {
                return mode;
            }
        }

        throw new ClassNotFoundException("There is no privacy mode with percentage " + id);
    }



    int mID;



    PrivacyMode(int id) {
        mID = id;
    }


    /**
     * This method returns the ID of a given PrivacyMode
     *
     * @return  ID
     */
    public int getID() {
        return mID;
    }
}
