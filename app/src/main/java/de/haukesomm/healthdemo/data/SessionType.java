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

import android.support.annotation.StringRes;

import de.haukesomm.healthdemo.R;

/**
 * Created on 25.08.18
 * <p>
 * This enum consists of all available activity/session types.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public enum SessionType {

    /**
     * Default type if no other was assigned
     */
    DEFAULT("default", R.string.session_type_description_default),

    /**
     * Walking
     */
    WALK("walk", R.string.session_type_description_walk),

    /**
     * Running
     */
    RUN("run", R.string.session_type_description_run),

    /**
     * Cycling
     */
    BICYCLE("bicycle", R.string.session_type_description_bicycle);


    /**
     * Alias to represent the type in an SQL database such as {@link SessionDatabase}.
     */
    public final String alias;

    /**
     * String resource with type description
     */
    @StringRes
    public final int descriptionRes;


    SessionType(String databaseAlias, @StringRes int descriptionRes) {
        this.alias = databaseAlias;
        this.descriptionRes = descriptionRes;
    }


    /**
     * This method finds a type by it's alias and returns it.
     *
     * @param alias Alias
     * @return      SessionType
     */
    public static SessionType get(String alias) {
        for (SessionType type : SessionType.values()) {
            if (type.alias.equals(alias)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
