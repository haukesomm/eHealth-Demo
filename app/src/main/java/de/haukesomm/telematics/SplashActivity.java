/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import de.haukesomm.telematics.data.Blackbox;

/**
 * Created on 03.12.17
 * <p>
 * This Activity shows a splash screen on each start of the application. It also tests the Blackbox
 * connection and creates it before launching the app if necessary.
 *
 * @author Hauke Sommerfeld
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final Blackbox blackbox = new Blackbox(this);

        final Blackbox.OpenListener openListener = new Blackbox.OpenListener() {
            @Override
            public void onBlackboxOpened() {
                blackbox.close();

                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                overridePendingTransition(android.R.anim.fade_in, R.anim.none);
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                blackbox.open(openListener);
            }
        }, 1500L);
    }
}
