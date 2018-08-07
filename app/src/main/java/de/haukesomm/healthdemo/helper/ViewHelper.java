/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo.helper;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created on 20.12.17
 * <p>
 * This class provides different methods which might be helpful with {@link View}s.
 *
 * @author Hauke Sommerfeld
 */
public class ViewHelper {

    /**
     * This method animates a View's visibility change
     *
     * @param view          The {@link View} which visibility should be changed
     * @param duration      Duration of the animation
     * @param visibility    Visbility (View.VISIBLE or View.INVISIBLE)
     */
    public static void animateSetVisibility(View view, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }
}
