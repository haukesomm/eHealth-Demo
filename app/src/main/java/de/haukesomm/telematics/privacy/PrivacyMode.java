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
    /**
     * Unknown or missing mode.<br>
     * This mode should never actually be available to the user and only functions as some sort of
     * fallback in case somthing goes wrong.
     */
    UNKNOWN(-1),

    /**
     * Submit all data to the insurance (minimal privacy).<br>
     * The insurance plan might rise or lower in price depending completely on the driving habits of
     * the user.
     */
    MAXIMUM_DATA(0),

    /**
     * Submit only the data explicitly selected by the user.<br>
     * The insurance plan might rise or lower in price depending on the driving habits of
     * the user but with upper and lower limits as well as a higher initial price.
     */
    USER_DEFINED(45),

    /**
     * Submit only a bare minimum of data to the insurance.<br>
     * The insurance plan might slightly rise or lower in price depending on the driving habits of
     * the user. The initial price is slightly higher than default. Upper and lower limits apply.
     */
    MINIMUM_DATA(65),

    /**
     * Do not submit any data at all (maximum privacy).<br>
     * A default plan with a fixed price (higher than the general upper limit) applies.
     */
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
