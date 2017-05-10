package com.polito.madinblack.expandedmad;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.GroupForUser;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.UserForGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsGraphs extends AppCompatActivity {
    private Spinner spinner;
    private MyApplication ma;
    private List<String> groupArray = new ArrayList<>();
    private Map<String,String> groupMap = new HashMap<String,String>(); //key->groupName value->groupId
    private Map<Integer,Double> groupExpensesByMonth = new HashMap<>();
    //private DatabaseReference mDatabase;
    //private String firebaseId;
    //private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        ma = MyApplication.getInstance();
        String id = ma.getFirebaseId();
        String phone = ma.getUserPhoneNumber();

        populateSpinner();

        spinner = (Spinner)findViewById(R.id.group_spinner);

        groupArray.add(getString(R.string.all_groups));
        groupMap.put(getString(R.string.all_groups), getString(R.string.all_groups));

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupArray);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(groupAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupSelected = parent.getItemAtPosition(position).toString();
                GraphView graph = (GraphView)findViewById(R.id.graph1);
                if(groupMap.get(groupSelected) != null) {
                    //initGraph(graph, groupMap.get(groupSelected));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateSpinner() {
        FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                .child(ma.getFirebaseId()).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupForUser groupForUser = groupSnapshot.getValue(GroupForUser.class);
                    String groupName = groupForUser.getName();
                    groupArray.add(groupName);
                    String groupId = groupForUser.getId();
                    groupMap.put(groupName, groupId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //metodo per inizializzare i dati del grafico
    public void initGraph(GraphView graph, String groupId) {
        if(!groupId.equals(getString(R.string.all_groups))) {
            FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                    .child(ma.getFirebaseId()).child("groups").child(groupId).child("expenses").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (int i = 1; i <= 12; i++) {
                        groupExpensesByMonth.put(i, 0.0);
                    }
                    for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                        ExpenseForUser expenseForUser = expenseSnapshot.getValue(ExpenseForUser.class);
                        Double expenseBalance = expenseForUser.getMyBalance();
                        expenseBalance += groupExpensesByMonth.get(expenseForUser.getMonth().intValue());
                        groupExpensesByMonth.put(expenseForUser.getMonth().intValue(), expenseBalance);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                    .child(ma.getFirebaseId()).child("groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (int i = 1; i <= 12; i++) {
                        groupExpensesByMonth.put(i, 0.0);
                    }
                    for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                        //Double expenseBalance = expenseSnapshot.child("myBalance").getValue(Double.class);
                        //Double expenseBalance = expenseForUser.getMyBalance();
                        //expenseBalance += groupExpensesByMonth.get(expenseForUser.getMonth().intValue());
                        //groupExpensesByMonth.put(expenseForUser.getMonth().intValue(), expenseBalance);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        DataPoint[] dataPoints = new DataPoint[12];
        for(int i = 0; i < 12; i++){
            dataPoints[i] = new DataPoint(i,groupExpensesByMonth.get(i));
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);

        series.setColor(Color.BLUE); //colore della barra
        series.setSpacing(30); //percentuale di spazio tra le barre. 0->no spazio, 100->spazio tra una barra e l'altra e pari alla larghezza di una barra
        series.setAnimated(true);
        graph.addSeries(series); //aggiunge la serie di dati al grafico

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1.0);
        graph.getViewport().setMaxX(12.0);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);

        series.setTitle(ma.getUserName());  //etichetta della serie di dati
        graph.getLegendRenderer().setVisible(true); //visualizza legenda e decide dove posizionarla
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Month of the year");
        gridLabel.setNumHorizontalLabels(12);
        gridLabel.setHorizontalLabelsVisible(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return super.formatLabel(value, isValueX);
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //staticLabelsFormatter.setHorizontalLabels(new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        //staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
        //graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


    }

}