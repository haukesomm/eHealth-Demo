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

package de.haukesomm.healthdemo.data;

/**
 * Created on 23.08.18
 * <p>
 * This is a data class representing a single fitness measurement consisting of different attributes
 * such as a timestamp, location and vital functions.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class Measurement {

    /**
     * Timestamp in the ISO-8601 date format
     */
    public final String timestamp;

    /**
     * Latitude of the user's location at the time of the measurement
     */
    public final double latitude;

    /**
     * Longitude of the user's location at the time of the measurement
     */
    public final double longitude;

    /**
     * The user's heartrate at the time of the measurement
     */
    public final int heartrate;


    /**
     * Creates a new Measurement object from the given attributes.
     *
     * @param timestamp Timestamp in the ISO-8601 date format
     * @param latitude  Latitude of the user's location at the time of the measurement
     * @param longitude Longitude of the user's location at the time of the measurement
     * @param heartrate The user's heartrate at the time of the measurement
     */
    // TODO Use a regex to validate the timestamp format
    Measurement(String timestamp, double latitude, double longitude, int heartrate) {
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heartrate = heartrate;
    }
}
