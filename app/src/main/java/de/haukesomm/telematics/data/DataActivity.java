/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.haukesomm.telematics.R;
import de.haukesomm.telematics.helper.ViewHelper;

/**
 * Created on 09.12.17
 * <p>
 * This Activity displays detailed information about a {@link Blackbox} table in form of various
 * diagrams for speed, height, etc. as well as a map.
 *
 * @author Hauke Sommerfeld
 */
public class DataActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    /**
     * Use this String in the launch {@link android.content.Intent} to specify the table to display.
     * This is mandatory for the Activity to work. Missing data will result in the Activity
     * finishing.
     */
    public static final String EXTRA_BLACKBOX_TABLE = "blackbox_table";


    /**
     * Use this String in the launch {@link android.content.Intent} to specify a date/title to display.
     */
    public static final String EXTRA_DATE = "title";



    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_data);
        bindActivity();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String date = getIntent().getStringExtra(EXTRA_DATE);
        setDate(date != null ? date : getString(R.string.unknown));

        mAppBarLayout.addOnOffsetChangedListener(this);


        initData();
        initDetails();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_data, menu);
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.activity_data_menuAction_delete:
            case R.id.activity_data_menuAction_report:
                Toast.makeText(this, "This feature is not yet implemented.",
                        Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private AppBarLayout mAppBarLayout;


    private Toolbar mToolbar;


    private TextView mTitle;


    private boolean mTitleVisible;


    private TextView mToolbarTitle;


    private boolean mToolbarTitleVisible;


    private TelematicsGraphView mGraphSpeed;


    private void bindActivity() {
        mAppBarLayout = findViewById(R.id.activity_data_appbar);
        mToolbar = findViewById(R.id.activity_data_toolbar);
        mToolbarTitle = findViewById(R.id.activity_data_toolbar_title);
        mTitle = findViewById(R.id.activity_data_title);

        mGraphSpeed = findViewById(R.id.activity_data_graph_speed);
    }



    private void setDate(String date) {
        mTitle.setText(date);
        mToolbarTitle.setText(date);
    }



    private static final int LENGTH_TOGGLE_ANIMATION = 200;


    private static final float PERCENTAGE_TOGGLE_STATUSBAR_TRANSLUCENCY = 0.9f;


    private static final float PERCENTAGE_TOGGLE_TITLE = 0.7f;


    private static final float PERCENTAGE_TOGGLE_TOOLBAR_TITLE = 1.0f;


    /**
     * This method handles some custom scrolling behavior of the Activity's
     * {@link android.support.design.widget.CoordinatorLayout}.
     *
     * {@inheritDoc}
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleStatusBarTranslucency(percentage);

        handleTitleVisibility(percentage);
        handleToolbarTitleVisibility(percentage);
    }


    private void handleStatusBarTranslucency(float percentage) {
        Window window = getWindow();
        if (percentage >= PERCENTAGE_TOGGLE_STATUSBAR_TRANSLUCENCY) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    private void handleTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TOGGLE_TITLE) {
            if (!mTitleVisible) {
                ViewHelper.animateSetVisibility(mTitle, LENGTH_TOGGLE_ANIMATION, View.INVISIBLE);
                mTitleVisible = true;
            }
        } else {
            if (mTitleVisible) {
                ViewHelper.animateSetVisibility(mTitle, LENGTH_TOGGLE_ANIMATION, View.VISIBLE);
                mTitleVisible = false;
            }
        }
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TOGGLE_TOOLBAR_TITLE) {
            if (!mToolbarTitleVisible) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewHelper.animateSetVisibility(mToolbarTitle, LENGTH_TOGGLE_ANIMATION, View.VISIBLE);
                        mToolbarTitleVisible = true;
                    }
                }, 800L);
            }
        } else {
            if (mToolbarTitleVisible) {
                ViewHelper.animateSetVisibility(mToolbarTitle, LENGTH_TOGGLE_ANIMATION, View.INVISIBLE);
                mToolbarTitleVisible = false;
            }
        }
    }



    private Blackbox mBlackbox;


    private ArrayList<JSONObject> mData;


    private void initData() {
        String table = getIntent().getStringExtra(EXTRA_BLACKBOX_TABLE);

        if (table == null) {
            Log.e("DataActivity", "No Blackbox table specified.");
            Toast.makeText(this, R.string.data_notAvailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        mBlackbox = new Blackbox(this);
        try {
            mBlackbox.open();
            mData = mBlackbox.getEntireTable(table);
            mBlackbox.close();
        } catch (SQLiteException e) {
            Log.e("DataActivity", "Invalid Blackbox table specified.");
            Toast.makeText(this, R.string.data_notAvailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }



    private static final int GRAPH_DEFAULT_THICKNESS = 7;


    private void initDetails() {
        LineGraphSeries<DataPoint> speedValues = new LineGraphSeries<>();

        if (Build.VERSION.SDK_INT >= 23) {
            Paint color = new Paint();
            color.setColor(getResources().getColor(R.color.colorPrimary, getTheme()));
            color.setStrokeWidth((float) GRAPH_DEFAULT_THICKNESS);

            speedValues.setCustomPaint(color);
        } else {
            speedValues.setThickness(GRAPH_DEFAULT_THICKNESS);
        }

        for (int i = 0; i < mData.size(); i++) {
            JSONObject data = mData.get(i);

            try {
                double speed = data.getDouble(Blackbox.DATA_SPEED);
                speed = Data.convert(speed, Blackbox.UNIT_SPEED, Data.PREFERRED_SPEED_UNIT);
                speedValues.appendData(new DataPoint(i, speed), true, mData.size(), true);
            } catch (JSONException e) {
                Log.w("DataActivity", "Unable to add value to graph: " + e.getMessage());
            }
        }

        mGraphSpeed.setData(speedValues, Data.PREFERRED_SPEED_UNIT);
    }
}
