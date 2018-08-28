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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 24.08.18
 * <p>
 * This is a data class representing a fitness session consisting of {@link SessionDescription}'s
 * attributes and a list of measurements.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class Session extends SessionDescription {

    private final List<Measurement> mMeasurements = new LinkedList<>();


    /**
     * Creates a new Session from an id and a {@link SessionType}.
     *
     * @param id    Session ID
     * @param type  Session Type
     */
    Session(int id, SessionType type) {
        super(id, type);
    }


    /**
     * This method returns an unmodifiable List of all Measurements.
     *
     * @return  Unmodifiable List of measurements
     */
    public List<Measurement> getMeasurements() {
        return Collections.unmodifiableList(mMeasurements);
    }

    /**
     * Use this method to add a single Measurement to the Session.
     *
     * @param measurement   Measurement to add
     */
    public void add(Measurement measurement) {
        mMeasurements.add(measurement);
    }

    /**
     * Use this method to add a List of multiple Measurements to the Session.
     *
     * @param measurements  Measurements to add
     */
    public void addAll(List<Measurement> measurements) {
        mMeasurements.addAll(measurements);
    }
}
