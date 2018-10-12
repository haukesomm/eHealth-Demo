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

package de.haukesomm.healthdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.data.SessionDatabase;

/**
 * Created on 03.12.17
 * <p>
 * This Activity shows a splash screen on each start of the application.
 *
 * @author Hauke Sommerfeld
 */
public class SplashActivity extends AppCompatActivity {

    private static final long DURATION = 1500L;



    private final Runnable mLaunchRunnable = new Runnable() {
        @Override
        public void run() {
            initSessionDatabase();

            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            overridePendingTransition(android.R.anim.fade_in, R.anim.none);
        }
    };



    private Handler mLaunchHandler = new Handler();


    private void startDelayedLaunch() {
        mLaunchHandler.postDelayed(mLaunchRunnable, DURATION);
    }


    private void cancelDelayedLaunch() {
        mLaunchHandler.removeCallbacks(mLaunchRunnable);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startDelayedLaunch();
    }



    private boolean mFirstLaunch = true;



    /**
     * Cancels the delayed application launch on Activity pause.
     */
    @Override
    public void onPause() {
        cancelDelayedLaunch();
        super.onPause();
    }


    /**
     * Resumes the delayed application launch on Activity pause.
     */
    @Override
    public void onResume() {
        if (mFirstLaunch) {
            mFirstLaunch = false;
        } else {
            startDelayedLaunch();
        }
        super.onResume();
    }



    private void initSessionDatabase() {
        SessionDatabase database = new SessionDatabase(getApplicationContext());
        database.close();
    }
}
