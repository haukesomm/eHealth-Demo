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

package de.haukesomm.healthdemo.privacy;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.haukesomm.healthdemo.R;
import de.haukesomm.healthdemo.ui.PrivacyPreferenceActivity;

/**
 * Created on 28.11.17
 * <p>
 * This View can be used to display the 'Privacy Mode/Score' of the user.
 * All available Privacy modes are listed in the {@link PrivacyMode} enum and can either be set
 * programmatically via {@link #setMode(PrivacyMode)} or in the XML file using the 'score' attribute.
 * <br>
 * In case no mode was set the View displays a placeholder without any information.
 *
 * @author Hauke Sommerfeld
 */
public class PrivacyModeView extends LinearLayout {

    /**
     * This Activity request code is used to launch the {@link PrivacyPreferenceActivity} and
     * notify the launching Activity (only if the provided {@link Context} is an Activity-Context!)
     * that the settings were closed.
     */
    public static final int REQUEST_SELECT_PRIVACY_MODE = 1;



    /**
     * Use this constructor to create an instance of PrivacyModeView programmatically.
     *
     * @param context   The app's Context
     */
    public PrivacyModeView(Context context) {
        super(context);
        init();
    }


    /**
     * This constructor is used to create a PrivacyModeView from XML.
     *
     * @param context   The app's Context
     * @param attrs     XML-Attributes in form of an AttributeSet
     */
    public PrivacyModeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray typedArray  = context.obtainStyledAttributes(attrs, R.styleable.PrivacyModeView);
        setMode(PrivacyMode.fromID(typedArray.getInt(R.styleable.PrivacyModeView_mode, -1)));
        typedArray.recycle();
    }



    private boolean mActivityContext;



    private ImageView mIcon;


    private TextView mText;


    private Button mPrivacySettingsButton;



    private void init() {
        mActivityContext = getContext() instanceof AppCompatActivity;

        inflate(getContext(), R.layout.privacy_mode_overview, this);
        mIcon                    = findViewById(R.id.privacy_mode_overview_icon);
        mText                    = findViewById(R.id.privacy_mode_overview_text);
        mPrivacySettingsButton   = findViewById(R.id.privacy_mode_overview_settings);
        mPrivacySettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacySettings = new Intent(getContext(), PrivacyPreferenceActivity.class);
                if (mActivityContext) {
                    ((AppCompatActivity) getContext()).startActivityForResult(privacySettings, REQUEST_SELECT_PRIVACY_MODE);
                } else {
                    getContext().startActivity(privacySettings);
                }
            }
        });
    }



    /**
     * Use this method to set the {@link PrivacyMode} the PrivacyModeView should display. If the
     * specified PrivacyMode is invalid, this method will do nothing.
     *
     * @param mode  {@link PrivacyMode} that should be displayed
     */
    public void setMode(@NonNull PrivacyMode mode) {
        mIcon.setImageDrawable(getContext().getDrawable(mode.getDrawableRes()));
        mText.setText(mode.getNameRes());
    }

}
