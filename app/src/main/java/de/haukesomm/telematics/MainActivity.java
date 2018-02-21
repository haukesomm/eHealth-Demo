/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created on 27.11.17
 * <p>
 * This Activity provides the main interface of the app where the user can select multiple sets of
 * data and see his current {@link de.haukesomm.telematics.privacy.PrivacyMode} settings.<br>
 * It mainly consists of multiple Fragments for future compatibility.
 *
 * @see OverviewFragment
 * @see TimelineFragment
 * @see de.haukesomm.telematics.data.Blackbox
 *
 * @author Hauke Sommerfeld
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindActivity();

        initSearch();
        initFragments();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.activity_main_menuAction_settings:
                Toast.makeText(this, "This feature is not yet implemented.",
                        Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private TabLayout mFragmentTabLayout;


    private ViewPager mFragmentPager;


    private OverviewFragment mOverviewFragment;


    private TimelineFragment mTimelineFragment;


    private FloatingActionButton mSearchButton;


    private void bindActivity() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        mFragmentTabLayout = findViewById(R.id.activity_main_tabs);
        mFragmentPager = findViewById(R.id.activity_main_pager);
        mOverviewFragment = new OverviewFragment();
        mTimelineFragment = new TimelineFragment();

        mSearchButton = findViewById(R.id.activity_main_searchButton);
    }



    private FragmentStatePagerAdapter mFragmentAdapter =
            new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mOverviewFragment;
                case 1:
                    return mTimelineFragment;
                default:
                    return new Fragment();
            }
        }


        @Override
        public int getCount() {
            return 2;
        }
    };


    private TabLayout.OnTabSelectedListener mFragmentTabListener =
            new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(final TabLayout.Tab tab) {
            mFragmentPager.setCurrentItem(tab.getPosition());

            mSearchButtonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tab == mTimelineTab) {
                        mSearchButton.show();
                    }
                }
            }, 400L);
        }


        @Override
        public void onTabUnselected(final TabLayout.Tab tab) {
            mSearchButtonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tab == mTimelineTab) {
                        mSearchButton.hide();
                    }
                }
            }, 200L);
        }


        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // Do nothing
        }
    };


    private TabLayout.Tab mOverviewTab;


    private TabLayout.Tab mTimelineTab;


    /**
     * This method initializes the Activity's ViewPager and TabLayout containing the Fragments
     * providing the actual UI as well as the needed navigation elements.
     */
    private void initFragments() {
        mOverviewTab = mFragmentTabLayout.newTab().setText(R.string.fragment_overview_title);
        mFragmentTabLayout.addTab(mOverviewTab);

        mTimelineTab = mFragmentTabLayout.newTab().setText(R.string.fragment_timeline_title);
        mFragmentTabLayout.addTab(mTimelineTab);


        mFragmentPager.setAdapter(mFragmentAdapter);
        mFragmentPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mFragmentTabLayout));


        mFragmentTabLayout.addOnTabSelectedListener(mFragmentTabListener);
    }



    private Handler mSearchButtonHandler = new Handler();


    private void initSearch() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "This feature is not yet implemented.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
