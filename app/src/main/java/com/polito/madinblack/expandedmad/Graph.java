package com.polito.madinblack.expandedmad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by AleCatto on 05/04/2017.
 */

public class Graph extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);
        GraphView graphView = (GraphView)findViewById(R.id.graph1);
        DataPoint[] dataPoints=new DataPoint[10]; //creo un vettore di DataPoints da passare a series (di prova)
        for(int i=0;i<10;i++)
        {
            dataPoints[i]=new DataPoint(i,10-1);
        }
        LineGraphSeries<DataPoint> series =new LineGraphSeries<>(dataPoints);
        graphView.addSeries(series); //aggiungo series alla graphview
    }
}
