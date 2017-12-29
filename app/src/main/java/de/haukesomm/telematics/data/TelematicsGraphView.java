/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

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

import de.haukesomm.telematics.R;

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
     * @param unit      Unit of the provided data
     * @param data      The actual data to use for the graph
     */
    public TelematicsGraphView(@NonNull Context context, @Nullable Drawable icon, @Nullable String title,
                               @NonNull String unit, @NonNull LineGraphSeries<DataPoint> data) {
        super(context);
        init(icon, title, unit);
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
        String unit = typedArray.getString(R.styleable.TelematicsGraphView_graphUnit);
        typedArray.recycle();

        init(icon, title, unit);
    }



    private void init(Drawable icon, String title, String unit) {
        inflate(getContext(), R.layout.view_graph_telematics, this);
        bindView();

        mUnit = unit;

        mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        mTitle.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.margin_small));
        mTitle.setText(title + " [" + unit + "]");
    }



    private TextView mTitle;


    private GraphView mGraph;


    private TextView mMaximum;


    private TextView mMinimum;


    private void bindView() {
        mTitle = findViewById(R.id.view_graph_telematics_title);
        mGraph = findViewById(R.id.view_graph_telematics_graph);
        mMaximum = findViewById(R.id.view_graph_telematics_maximum_value);
        mMinimum = findViewById(R.id.view_graph_telematics_minimum_value);
    }



    private String mUnit;


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

        TelematicsDecimalFormat format = new TelematicsDecimalFormat();
        mMaximum.setText(format.format(mData.getHighestValueY()) + " " + mUnit);
        mMinimum.setText(format.format(mData.getLowestValueY()) + " " + mUnit);
    }
}
