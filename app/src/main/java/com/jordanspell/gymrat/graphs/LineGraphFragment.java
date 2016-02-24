package com.jordanspell.gymrat.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.jordanspell.gymrat.R;

/**
 * Created by elfrank on 3/3/15.
 */
public class LineGraphFragment extends BaseFragment{

    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.linegraph_fragment, container, false);

        Bundle bundle = this.getArguments();
        int pos = bundle.getInt("pos");

        mChart = (LineChart) v.findViewById(R.id.chart);

        mChart.setHighlightEnabled(true);


        mChart.setDescription("");
        mChart.setNoDataTextDescription("There is no data logged for this exercise");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

        mChart.setHighlightIndicatorEnabled(false);

        mChart.setData(getLineData(pos));
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setValueFormatter(getWholeNumberValueFormatter());
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mChart.getLegend().setTextSize(8);
        mChart.getXAxis().setTextSize(7);
        mChart.getAxisLeft().setTextSize(7);
        mChart.getLineData().setValueTextSize(7);
        return v;
    }

}
