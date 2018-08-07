/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.data;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import de.haukesomm.healthdemo.R;

/**
 * Created on 31.12.17
 * <p>
 * This class contains a standardized unit interface, a number of commonly used units as well as
 * methods for the conversion and formatting of data.
 *
 * @author Hauke Sommerfeld
 */
public class Data {

    private Data() {
        // Prevent this class from being instantiated.
    }


    /**
     * Use this interface to define custom units and use them with the {@link Data} class.
     */
    public interface Unit {

        /**
         * This method returns the reference unit which is used to evaluate the conversion factor.
         *
         * @return  Reference unit
         *
         * @see #getConversionFactor()
         */
        Unit getReferenceUnit();

        /**
         * This method returns the factor needed to convert this Unit to the specified reference
         * unit.
         *
         * @return  Conversion factor
         *
         * @see #getReferenceUnit()
         */
        double getConversionFactor();

        /**
         * This method returns the resource id of the short unit name (e.g. 'm' for Meter) in String
         * form.
         *
         * @return  Short unit name
         */
        @StringRes
        int getShortNameRes();

        /**
         * This method returns the resource id if the full unit name in String form.
         *
         * @return  Short unit name
         */
        @StringRes
        int getFullNameRes();
    }



    /**
     * This method converts a given value from one unit to another and returns the result as a
     * double.<p>
     * Important: The units must both implement the {@link Unit} interface and should belong to the
     * same category of units (e.g. {@link SpeedUnit}).
     *
     * @param value Original double value
     * @param <U>   Data type extending the Unit interface
     * @param from  Data of the original value
     * @param to    Data to convert the value to
     * @return      The converted value as a double
     *
     * @see Data
     * @see Unit
     */
    public static <U extends Unit> double convert(double value, @NonNull U from, @NonNull U to) {
        return value * (from.getConversionFactor() / to.getConversionFactor());
    }



    /**
     * This method formats a given value matching its unit and returns it in String form.
     *
     * @param value     The value to format
     * @param unit      The unit of the value
     * @return          The formatted value in String form
     */
    public static String format(@NonNull Context context, double value, @NonNull Unit unit) {
        return String.valueOf(value) + " " + context.getString(unit.getShortNameRes());
    }


    /**
     * Preferred speed unit.<br>
     * This does not force the app to use that unit globally but indicates which unit to use to any
     * part of the app that supports it.
     */
    public static final Unit PREFERRED_SPEED_UNIT = SpeedUnit.MILES_PER_HOUR;



    /**
     * This enum contains commonly used speed units.
     */
    public enum SpeedUnit implements Unit {
        /**
         * Miles per hour (mph)
         */
        MILES_PER_HOUR(null, 1.0d,
                R.string.data_unit_speed_mph_short, R.string.data_unit_speed_mph_full),

        /**
         * Kilometers per hour (kph)
         */
        KILOMETERS_PER_HOUR(MILES_PER_HOUR, 0.62137119223733d,
                R.string.data_unit_speed_kph_short, R.string.data_unit_speed_kph_full);



        SpeedUnit(Unit referenceUnit, double conversionFactor, int shortNameRes, int fullNameRes) {
            mReferenceUnit = referenceUnit;
            mConversionFactor = conversionFactor;
            mShortNameRes = shortNameRes;
            mFullNameRes = fullNameRes;
        }



        private final Unit mReferenceUnit;


        /**
         * {@inheritDoc}
         */
        @Override
        public Unit getReferenceUnit() {
            // Special behavior needed because an enum is used to store the Unit classes.
            return mReferenceUnit != null ? mReferenceUnit : this;
        }



        private final double mConversionFactor;


        /**
         * {@inheritDoc}
         */
        @Override
        public double getConversionFactor() {
            return mConversionFactor;
        }



        private final int mShortNameRes;


        /**
         * {@inheritDoc}
         */
        @Override
        public int getShortNameRes() {
            return mShortNameRes;
        }



        private final int mFullNameRes;


        /**
         * {@inheritDoc}
         */
        @Override
        public int getFullNameRes() {
            return mFullNameRes;
        }
    }
}
