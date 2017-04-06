package com.polito.madinblack.expandedmad;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by AleCatto on 05/04/2017.
 */

public class MultipleBarGraph extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);
        GraphView graphView = (GraphView) findViewById(R.id.graph1);
        initGraph(graphView);
    }
    //metodo per inizializzare i dati del grafico
    public void initGraph(GraphView graph) {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 3),
                new DataPoint(1, 5),
                new DataPoint(2, 6),
                new DataPoint(3, 2),
                new DataPoint(4, 5),
        });
        series.setColor(Color.BLUE); //colore della barra
        series.setSpacing(30); //percentuale di spazio tra le barre. 0->no spazio, 100->spazio tra una barra e l'altra e pari alla larghezza di una barra
        series.setAnimated(true);
        graph.addSeries(series); //aggiunge la serie di dati al grafico

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 5),
                new DataPoint(1, 4),
                new DataPoint(2, 1),
                new DataPoint(3, 5),
                new DataPoint(4, 3),
        });
        series2.setColor(Color.RED);
        series2.setSpacing(30);
        series2.setAnimated(true);
        graph.addSeries(series2);

        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 3),
                new DataPoint(1, 2),
                new DataPoint(2, 5),
                new DataPoint(3, 6),
                new DataPoint(4, 1),
        });
        series2.setColor(Color.GREEN);
        series2.setSpacing(30);
        series2.setAnimated(true);
        graph.addSeries(series3);

        graph.getViewport().setXAxisBoundsManual(true); //queste 3 righe permettono di visualizzare solo un intervallo sull'asse delle X
        graph.getViewport().setMinX(-1);
        graph.getViewport().setMaxX(5);

        series.setTitle("Food");  //etichetta della serie di dati
        series2.setTitle("Water bill");
        series3.setTitle("Light bill");
        graph.getLegendRenderer().setVisible(true); //visualizza legenda e decide dove posizionarla
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }
}