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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;

import de.haukesomm.healthdemo.R;

/**
 * Created on 27.12.17
 * <p>
 * This View is a more powerful {@link GraphView} featuring an additional title, headline as well as
 * maximum and minimum value statistics.
 *
 * @author Hauke Sommerfeld
 */
public class TelematicsGraphView extends LinearLayout {

    /**
     * Ust this constructor to create the View programmatically.
     *
     * @param context   The app's context
     * @param icon      Icon to use for the graph
     * @param title     The title of the Graph
     * @param data      The actual data to use for the graph
     */
    public TelematicsGraphView(@NonNull Context context, @Nullable Drawable icon, @Nullable String title,
                               @NonNull LineGraphSeries<DataPoint> data) {
        super(context);

        init(icon, title);
        setData(data);
    }


    /**
     * This constructor is used when the View is created from XML.
     *
     * @param context   The app's context
     * @param attrs     {@link AttributeSet} containing the XML-attributes
     */
    public TelematicsGraphView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TelematicsGraphView);
        Drawable icon = typedArray.getDrawable(R.styleable.TelematicsGraphView_graphIcon);
        String title = typedArray.getString(R.styleable.TelematicsGraphView_graphTitle);
        typedArray.recycle();

        init(icon, title);
    }



    private void init(Drawable icon, String title) {
        inflate(getContext(), R.layout.graph_telematics, this);
        bindView();

        mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        mTitle.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.margin_small));
        mTitle.setText(title);
    }



    private TextView mTitle;



    private GraphView mGraph;


    private TextView mMaximum;


    private TextView mMinimum;


    private void bindView() {
        mTitle = findViewById(R.id.graph_telematics_title);
        mGraph = findViewById(R.id.graph_telematics_graph);
        mMaximum = findViewById(R.id.graph_telematics_maximum_value);
        mMinimum = findViewById(R.id.graph_telematics_minimum_value);
    }



    private final DecimalFormat mNumberFormat = new DecimalFormat("###.##");



    private Series<DataPoint> mData;


    /**
     * Use this method to provide the grpah's data in form of a {@link LineGraphSeries} in case you
     * created the View from XML or want to update the data.
     *
     * @param data  The graph's data
     */
    public void setData(@NonNull Series<DataPoint> data) {
        mData = data;


        mGraph.removeAllSeries();
        mGraph.addSeries(mData);

        // Setup Viewport
        Viewport viewport = mGraph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMaxX(viewport.getMaxX(true));
        viewport.setYAxisBoundsManual(true);
        viewport.setMaxY(viewport.getMaxY(true));

        // Setup labels
        GridLabelRenderer labelRenderer = mGraph.getGridLabelRenderer();
        labelRenderer.setHorizontalLabelsVisible(false);

        mMaximum.setText(mNumberFormat.format(mData.getHighestValueY()));
        mMinimum.setText(mNumberFormat.format(mData.getLowestValueY()));
    }
}
