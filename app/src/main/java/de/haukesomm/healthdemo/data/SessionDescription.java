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
 * Created on 25.08.18
 * <p>
 * This is a data class providing basic information about a {@link Session} such as it's ID and
 * {@link SessionType}.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class SessionDescription {

    /**
     * This Sessions (unique) ID
     */
    public final int id;

    /**
     * Session type (e.g. running, biking, etc.)
     */
    public final SessionType type;


    /**
     * Creates a new SessionDescription from an ID and {@link SessionType}
     *
     * @param id    The Session's ID
     * @param type  The Session's Type
     */
    public SessionDescription(int id, SessionType type) {
        this.id = id;
        this.type = type;
    }
}
