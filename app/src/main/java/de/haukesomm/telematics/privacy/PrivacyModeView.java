/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.haukesomm.telematics.R;

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
        try {
            setMode(PrivacyMode.fromID(
                    typedArray.getInt(R.styleable.PrivacyModeView_mode, 0)));
        } catch (ClassNotFoundException e) {
            Log.d("PrivacyScoreView", "Invalid privacy mode: " + e.getMessage());
            setMode(PrivacyMode.MAXIMUM_DATA);
        } finally {
            typedArray.recycle();
        }
    }



    private ImageView mIcon;


    private TextView mText;


    private Button mPrivacySettingsButton;


    private void init() {
        inflate(getContext(), R.layout.view_privacy_mode, this);
        mIcon                    = findViewById(R.id.view_privacyMode_icon);
        mText                    = findViewById(R.id.view_privacyMode_text);
        mPrivacySettingsButton   = findViewById(R.id.view_privacyMode_settingsButton);
        mPrivacySettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacySettings = new Intent(getContext(), PrivacyPreferenceActivity.class);
                getContext().startActivity(privacySettings);
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
        switch (mode) {
            case UNKNOWN:
                mIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_privacy_unknown));
                mText.setText(R.string.unknown);
                break;
            case MAXIMUM_DATA:
                mIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_privacy_maximumdata));
                mText.setText(R.string.privacy_mode_maximumData);
                break;
            case USER_DEFINED:
                mIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_privacy_userdefined));
                mText.setText(R.string.privacy_mode_userDefined);
                break;
            case MINIMUM_DATA:
                mIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_privacy_minimumdata));
                mText.setText(R.string.privacy_mode_minimumData);
                break;
            case OBFUSCATION:
                mIcon.setImageDrawable(getContext().getDrawable(R.drawable.ic_privacy_obfuscation));
                mText.setText(R.string.privacy_mode_obfuscation);
                break;
        }
    }

}
