package com.polito.madinblack.expandedmad.Utility;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.ExpenseFillData;
import com.polito.madinblack.expandedmad.ExpenseFillData_2;
import com.polito.madinblack.expandedmad.GroupManaging.GroupDetailActivity;
import com.polito.madinblack.expandedmad.GroupManaging.GroupDetailFragment;
import com.polito.madinblack.expandedmad.GroupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.group_members.GroupMemebersActivity;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.Group;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.List;

public class TabView extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private MyApplication ma;

    private static String index = "index";

    private static Group groupSelected;

    private static List<Expense> eItem;    //usato per la lista di spese da mostrare in una delle tre schede a schermo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_details);

        ma = MyApplication.getInstance();   //retrive del DB

        Intent beginner = getIntent();
        groupSelected = ma.getSingleGroup(Long.valueOf(beginner.getStringExtra("index"))); //recupero l'id del gruppo selezionato, e quindi il gruppo stesso
        //eItem = groupSelected.getExpenses();
        index = beginner.getStringExtra("index");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupSelected.getName());
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //this is the tab under the toolbar
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent intent = new Intent(c, ExpenseFillData.class);     //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra("index", index);                            //passo l'indice del gruppo
                startActivityForResult(intent,1);
            }
        });

        // Show the Up button in the action bar. (bottone indietro nella pagina 2)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tabview_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, GroupDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra(GroupDetailFragment.ARG_G_ID, index);   //il tutto viene passato come stringa
                startActivity(intent);
                return true;

            case R.id.home:
                navigateUpTo(new Intent(this, GroupListActivity.class));    //definisco il parente verso cui devo tornare indietro
                return true;

            case R.id.action_members:
                //insert here the connection
                Intent intent2 = new Intent(this, GroupMemebersActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent2.putExtra("GROUP_ID", index);
                startActivity(intent2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * questo è un semplice fragment, in questo caso un esempio
     */
    public static class PlaceholderFragment extends Fragment {

        //memorizzo dentro questa variabile il fragment che sto visualizzando a schermo
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //da qui in poi bisogna differenziare la righe di codice da eseguire in base al tab di interesse
            //le posizioni che arrivano quì sono 1, 2, 3, non comincia dallo 0

            View rootView = null;
            rootView = inflater.inflate(R.layout.tab_activity_fragment, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Coming soon!");

            return rootView;
        }
    }

    //gestisco la lista delle spese nella tab di mezzo
    public static class ExpensesListFragment extends Fragment {

        public ExpensesListFragment() {
            // Required empty public constructor
        }

        //usato per instanziare un ogetto fragment
        public static ExpensesListFragment newInstance() {
            ExpensesListFragment fragment = new ExpensesListFragment();
            Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.expense_list_fragment, container, false);

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(eItem, index);    //come argomento devo passare la lista di elementi che voglio mostrare a schermo

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.expense_list2);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            return rootView;
        }
    }

    //gestisco la lista delle spese nella tab di mezzo (My Balance)
    public static class MyBalanceFragment extends Fragment {

        RecyclerViewAdapterUsers adapter;

        public MyBalanceFragment() {
            // Required empty public constructor
        }

        //usato per instanziare un ogetto fragment
        public static MyBalanceFragment newInstance() {
            MyBalanceFragment fragment = new MyBalanceFragment();
            //Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.user_list_fragment, container, false);

            adapter = new RecyclerViewAdapterUsers(getContext(),
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupSelected.getId()).child("members").
                            child("balance"), groupSelected);    //come argomento devo passare la lista di elementi che voglio mostrare a schermo

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            return rootView;
        }

        @Override
        public void onStop() {
            super.onStop();

            // Clean up comments listener
            if(adapter!=null)
                adapter.cleanupListener();
        }
    }

    //questa classe gestisce le singole pagine del tab view dentro le quali instazio fragment diversi
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //usato per instanziare fragment diversi per ogni pagina del tabview
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            //used to instantiate one different fragment for each tabs
            switch (position) {
                case 0:
                    return MyBalanceFragment.newInstance();
                case 1:
                    //return new BlankFragment();
                    return ExpensesListFragment.newInstance();
                case 2:
                    //return new BlankFragment();
                    return PlaceholderFragment.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MY BALANCE";
                case 1:
                    return "EXPENSES";
                case 2:
                    return "CHAT";
            }
            return null;
        }
    }
}