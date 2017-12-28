/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

import java.text.DecimalFormat;

/**
 * Created on 28.12.17
 *
 * This class represents the default decimal format of the app.
 *
 * @author Hauke Sommerfeld
 */
public class TelematicsDecimalFormat extends DecimalFormat {

    /**
     * Constructor.
     */
    public TelematicsDecimalFormat() {
        super("###.##");
    }
}
