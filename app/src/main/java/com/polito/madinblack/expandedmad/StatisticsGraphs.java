package com.polito.madinblack.expandedmad;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.polito.madinblack.expandedmad.model.ExpenseForUser;
import com.polito.madinblack.expandedmad.model.GroupForUser;
import com.polito.madinblack.expandedmad.model.MyApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class StatisticsGraphs extends AppCompatActivity {
    private Spinner groupSpinner;
    private Spinner yearSpinner;
    //private Button show_button;
    private MyApplication ma;
    private GraphView graph;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mDatabaseGroupReference;
    private DatabaseReference mDatabaseExpenseReference;
    private DatabaseReference mDatabaseAllExpenseReference;
    private List<String> groupArray = new ArrayList<>(); //groupName
    private Map<String,String> groupMap = new HashMap<String,String>(); //key->groupName value->groupId
    private Map<Double,Double> groupExpensesByMonth = new HashMap<>();
    private DataPoint[] dataPoints;
    private String groupSelected;
    private String yearSelected;
    private String groupId;
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

        graph = (GraphView) findViewById(R.id.graph1);
        setGraph(graph);

        resetMap(groupExpensesByMonth);

        groupSelected = getString(R.string.select_group);
        yearSelected = getString(R.string.select_year);

        ma = MyApplication.getInstance();
        mDatabaseGroupReference = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber()).child(ma.getFirebaseId()).child("groups");

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupArray.add(getString(R.string.select_group)); //aggiungo all'array e alla mappa <groupName,groupId> il valore di default
                groupArray.add(getString(R.string.all_groups));
                groupMap.put(getString(R.string.select_group), getString(R.string.select_group));
                groupMap.put(getString(R.string.all_groups), getString(R.string.all_groups));

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupForUser groupForUser = groupSnapshot.getValue(GroupForUser.class);
                    String groupName = groupForUser.getName(); //prendo il groupForUser dal db e aggiungo i campi che mi servono all'array e alla mappa
                    groupArray.add(groupName);
                    String groupId = groupForUser.getId();
                    groupMap.put(groupName, groupId);
                }

                groupSpinner = (Spinner) findViewById(R.id.group_spinner);
                yearSpinner = (Spinner) findViewById(R.id.year_spinner);

                ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(StatisticsGraphs.this, android.R.layout.simple_spinner_item, groupArray);
                groupAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                groupSpinner.setAdapter(groupAdapter);

                groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        groupSelected = parent.getItemAtPosition(position).toString();
                        groupId = groupMap.get(groupSelected);
                        if ((!groupMap.get(groupSelected).equals(getString(R.string.select_group))) && (!yearSelected.equals(getString(R.string.select_year)))) {
                            if(groupMap.get(groupSelected).equals(getString(R.string.all_groups))){
                                mDatabaseAllExpenseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                                        .child(ma.getFirebaseId()).child("groups");
                                initGraph(graph, groupMap.get(groupSelected), yearSelected, groupSelected);   //una volta selezionati sia l'anno che il gruppo chiama il metodo
                            }else {
                                mDatabaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                                        .child(ma.getFirebaseId()).child("groups").child(groupId).child("expenses");
                                initGraph(graph, groupMap.get(groupSelected), yearSelected, groupSelected);   //una volta selezionati sia l'anno che il gruppo chiama il metodo

                                //if(groupMap.get(groupSelected) != null) {
                                //initGraph(graph, groupMap.get(groupSelected));
                                //}
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        yearSelected = parent.getItemAtPosition(position).toString();

                        if ((!groupMap.get(groupSelected).equals(getString(R.string.select_group))) && (!yearSelected.equals(getString(R.string.select_year)))) {
                            if (groupMap.get(groupSelected).equals(getString(R.string.all_groups))) {
                                mDatabaseAllExpenseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                                        .child(ma.getFirebaseId()).child("groups");
                                initGraph(graph, groupMap.get(groupSelected), yearSelected, groupSelected);   //una volta selezionati sia l'anno che il gruppo chiama il metodo

                            } else {
                                mDatabaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ma.getUserPhoneNumber())
                                        .child(ma.getFirebaseId()).child("groups").child(groupId).child("expenses");
                                initGraph(graph, groupMap.get(groupSelected), yearSelected, groupSelected);   //una volta selezionati sia l'anno che il gruppo chiama il metodo
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //show_button = (Button)findViewById(R.id.show_button);
        //show_button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        GraphView graph = (GraphView)findViewById(R.id.graph1);
        //        String groupSelected2 = groupSpinner.getSelectedItem().toString();
        //        String yearSelected2 = yearSpinner.getSelectedItem().toString();
        //        if ((!groupMap.get(groupSelected).equals(getString(R.string.choose_group))) && (!yearSelected.equals(getString(R.string.select_year)))) {
        //            initGraph(graph, groupMap.get(groupSelected2), yearSelected2, groupSelected2);
        //        }
        //    }

        //});

    }

    private void setGraph(GraphView graph){
        graph.getViewport().setMinX(0.0);       //setto i margini del grafico
        graph.getViewport().setMaxX(13.0);
        graph.getViewport().setMinY(0.0);       //setto i margini del grafico
        graph.getViewport().setMaxY(10.0);

        graph.getViewport().setXAxisBoundsManual(true);  //questo e' la riga che me lo permette
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.months)); //da il titolo all'asse delle x
        graph.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.expenses_in_euro));
    }

    //metodo per inizializzare i dati del grafico
    public void initGraph(final GraphView graph, String groupId, String year, final String groupName) {
        final String yearSelected = year;
        resetMap(groupExpensesByMonth);
        if(!groupName.equals(getString(R.string.all_groups))) {
            mDatabaseExpenseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                        ExpenseForUser expenseForUser = expenseSnapshot.getValue(ExpenseForUser.class);     //prendo l'expenseForUser
                        String yearStr = String.valueOf(expenseForUser.getYear());
                        if (yearStr.equals(yearSelected)) {                      //controllo che appartenga all'anno selezionato
                            Double expenseCost = expenseForUser.getCost();                                  //prendo il costo della expenseForUser
                            Double month = Double.valueOf(expenseForUser.getMonth());
                            expenseCost += groupExpensesByMonth.get(month);                                 //gli aggiungo il valore già presente nella mappa ai passi precedenti
                            groupExpensesByMonth.put(month, expenseCost);    //aggiorno la mappa alla posizione del mese della expense
                            //Toast.makeText(getApplicationContext(), String.valueOf(groupExpensesByMonth.get(5.0)), Toast.LENGTH_LONG).show();
                        }
                    }
                    printGraph(graph, groupName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            mDatabaseAllExpenseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(int i = 2; i < groupArray.size(); i++){             //parto da perche i primi 2 campi dell'array sono quello di default e "tutti i gruppi"
                        String groupIdSelected = groupMap.get(groupArray.get(i));
                        for(DataSnapshot expenseSnapshot : dataSnapshot.child(groupIdSelected).child("expenses").getChildren()){
                            ExpenseForUser expenseForUser = expenseSnapshot.getValue(ExpenseForUser.class);
                            String yearStr = String.valueOf(expenseForUser.getYear());
                            //Toast.makeText(getApplicationContext(), expenseForUser.getName(), Toast.LENGTH_LONG).show();
                            if (yearStr.equals(yearSelected)) {
                                Double expenseCost = expenseForUser.getCost();
                                Double month = Double.valueOf(expenseForUser.getMonth());
                                expenseCost += groupExpensesByMonth.get(month);
                                groupExpensesByMonth.put(month, expenseCost);
                            }
                        }
                    }
                    printGraph(graph,groupName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }





        //GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        //gridLabel.setHorizontalAxisTitle("Month of the year");
        //gridLabel.setNumHorizontalLabels(12);
        //gridLabel.setHorizontalLabelsVisible(true);

        //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //staticLabelsFormatter.setHorizontalLabels(new String[] {getString(R.string.january),getString(R.string.february), getString(R.string.march),
        //        getString(R.string.april), getString(R.string.may), getString(R.string.june), getString(R.string.july), getString(R.string.august),
        //        getString(R.string.september), getString(R.string.october), getString(R.string.november), getString(R.string.december)});
        //staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
        //graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


    }

    private void printGraph(GraphView graph, String groupName){
        graph.removeAllSeries();

        dataPoints = new DataPoint[12];                 //creo un array di DataPoint

        double max = 0.0;

        for (int i = 0; i < 12; i++) {                               // da 0 venga visualizzato male
            double cost = groupExpensesByMonth.get(Double.valueOf(i+1));
            if(cost > max) {
                max = cost;
            }
            dataPoints[i] = new DataPoint(i+1, cost);  //inserisco i valori della mappa nella forma <numero_mese, spesa_totale>
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints); //creo questo oggeto partendo dall'array precedentemente costruito

        series.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); //colore della barra
        series.setSpacing(20); //percentuale di spazio tra le barre. 0->no spazio, 100->spazio tra una barra e l'altra e pari alla larghezza di una barra
        series.setAnimated(true);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        //series.setTitle(groupName + "-" + yearSelected);                     //etichetta della serie di dati
        if(max == 0.0) {
            if(!groupName.equals(getString(R.string.all_groups))) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_expenses) + " " + groupName + " " + getString(R.string.in) + " " + yearSelected, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.no_expenses_in) + " " + yearSelected, Toast.LENGTH_LONG).show();
            }
        }
        graph.addSeries(series); //aggiunge la serie di dati al grafico
        if(max>10) {
            graph.getViewport().setMaxY(max + max / 4);
        }

        //graph.getLegendRenderer().setVisible(true);     //visualizza legenda e decide dove posizionarla
        //graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }

    private void resetMap(Map <Double,Double> map){
        for(int i = 1; i <= 12; i++){
            map.put(Double.valueOf(i), 0.0);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mValueEventListener!=null)
            mDatabaseGroupReference.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mValueEventListener!=null)
            mDatabaseGroupReference.removeEventListener(mValueEventListener);
    }
}

