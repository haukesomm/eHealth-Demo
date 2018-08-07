/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.healthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import de.haukesomm.healthdemo.data.Blackbox;

/**
 * Created on 03.12.17
 * <p>
 * This Activity shows a splash screen on each start of the application. It also tests the Blackbox
 * connection and creates it before launching the app if necessary.
 *
 * @author Hauke Sommerfeld
 */
public class SplashActivity extends AppCompatActivity {

    private static final long DURATION = 1500L;



    private Blackbox.OpenListener mLaunchListener = new Blackbox.OpenListener() {
        @Override
        public void onBlackboxOpened() {
            mBlackbox.close();

            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            overridePendingTransition(android.R.anim.fade_in, R.anim.none);
        }
    };



    private final Runnable mLaunchRunnable = new Runnable() {
        @Override
        public void run() {
            mBlackbox.open(mLaunchListener);
        }
    };



    private Blackbox mBlackbox;


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

        mBlackbox = new Blackbox(this);
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
}
