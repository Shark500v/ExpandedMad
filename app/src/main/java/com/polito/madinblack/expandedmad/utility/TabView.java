package com.polito.madinblack.expandedmad.utility;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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

import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.ExpenseFillData;
import com.polito.madinblack.expandedmad.chat.ChatRecyclerViewAdapter;
import com.polito.madinblack.expandedmad.groupManaging.GroupDetailActivity;
import com.polito.madinblack.expandedmad.groupManaging.GroupDetailFragment;
import com.polito.madinblack.expandedmad.groupManaging.GroupHistory;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupMembers.GroupMemebersActivity;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.GroupForUser;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.List;

public class TabView extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static MyApplication ma;

    private static String groupIndex;
    private static String groupName;

    private static List<Expense> eItem;    //usato per la lista di spese da mostrare in una delle tre schede a schermo

    private static DatabaseReference mDatabaseBalancesReference;
    private static DatabaseReference mDatabaseExpenseListReference;

    private MyBalanceFragment balanceFrag = MyBalanceFragment.newInstance();

    private ExpensesListFragment listFrag = ExpensesListFragment.newInstance();

    private static final int CONTACT_REQUEST = 1;

    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_details);

        ma = MyApplication.getInstance();   //retrive del DB
        groupIndex = getIntent().getExtras().getString("groupIndex");
        groupName  = getIntent().getExtras().getString("groupName");

        mDatabaseBalancesReference = FirebaseDatabase.getInstance().getReference().child("groups/"+groupIndex+"/users/"+ma.getFirebaseId()+"/balances");
        mDatabaseExpenseListReference = FirebaseDatabase.getInstance().getReference().child("users/"+ma.getUserPhoneNumber()+"/"+ma.getFirebaseId()+"/groups/"+groupIndex+"/expenses");

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupName);
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent intent = new Intent(c, ExpenseFillData.class);       //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra("groupIndex", groupIndex);                  //passo l'indice del gruppo
                startActivityForResult(intent, CONTACT_REQUEST);

            }
        });

        // Show the Up button in the action bar. (bottone indietro nella pagina 2)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //show the toolbar
    public void expandToolbar(){
        //setExpanded(boolean expanded, boolean animate)
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        appBarLayout.setExpanded(true, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //usato per mostrare il bottone di aggiunta spesa nella pagina giusta
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                expandToolbar();

                if (position == 0) {
                    fab.hide();
                } else if (position == 1) {
                    fab.show();
                } else if (position == 2){
                    fab.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tabview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, GroupDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                startActivity(intent);
                return true;

            case R.id.home:
                navigateUpTo(new Intent(this, GroupListActivity.class));    //definisco il parente verso cui devo tornare indietro
                return true;

            case R.id.action_members:
                //insert here the connection
                Intent intent2 = new Intent(this, GroupMemebersActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent2.putExtra("GROUP_ID", groupIndex);
                intent2.putExtra("GROUP_NAME", groupName);
                startActivity(intent2);
                return true;

            case R.id.action_history:
                //insert here the connection
                Intent intent3 = new Intent(this, GroupHistory.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent3.putExtra("GROUP_ID", groupIndex);
                startActivity(intent3);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public static class ChatFragment extends Fragment {

        private EditText inputMessage;
        private MyApplication ma;
        private RecyclerView recyclerView;
        private DatabaseReference ref;
        private View rootView = null;


        public ChatFragment() {
        }

        public static ChatFragment newInstance(int sectionNumber) {
            ChatFragment fragment = new ChatFragment();
            //Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.activity_chat, container, false);

            inputMessage = (EditText) rootView.findViewById(R.id.input_message);
            ma = MyApplication.getInstance();

            recyclerView = (RecyclerView) rootView.findViewById(R.id.message_list);

            ref = FirebaseDatabase.getInstance().getReference().child("messages/" + groupIndex);

            ChatRecyclerViewAdapter mAdapter = new ChatRecyclerViewAdapter(
                    Message.class,
                    R.layout.list_message_item_left,
                    RecyclerView.ViewHolder.class,
                    ref
            );
            recyclerView.setAdapter(mAdapter);

            ImageView send = (ImageView) rootView.findViewById(R.id.send_button);

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(v);
                }
            });

            return rootView;
        }

        public void sendMessage(View view) {

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            ref.push().setValue(new Message(ma.getUserName() + " " + ma.getUserSurname(), inputMessage.getText().toString()));
            //recyclerView.scrollBy(0, 1000);

            // Clear the input
            inputMessage.setText("");
        }
    }

    //gestisco la lista delle spese nella tab di mezzo
    public static class ExpensesListFragment extends Fragment {

        RecyclerViewAdapter adapter;
        RecyclerView recyclerView;
        View rootView;

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

            rootView = inflater.inflate(R.layout.expense_list_fragment, container, false);

            return rootView;
        }

        @Override
        public void onStart(){
            super.onStart();
            adapter = new RecyclerViewAdapter(getContext(), mDatabaseExpenseListReference, groupIndex);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.expense_list2);

            final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if(dy > 0){
                        fab.hide();
                    } else{
                        fab.show();
                    }

                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public void onStop() {
            super.onStop();

            // Clean up comments listener
            if(adapter!=null)
                adapter.cleanupListener();
        }
    }

    //gestisco la lista delle spese nella tab di mezzo (My Balance)
    public static class MyBalanceFragment extends Fragment {

        RecyclerViewAdapterUsers adapter;
        RecyclerView recyclerView;
        View rootView;

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

            rootView = inflater.inflate(R.layout.user_list_fragment, container, false);

            return rootView;
        }

        @Override
        public void onStart(){
            super.onStart();

            adapter = new RecyclerViewAdapterUsers(getContext(), mDatabaseBalancesReference);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                    return balanceFrag;
                case 1:
                    //return new BlankFragment();
                    return listFrag;
                case 2:
                    //return new BlankFragment();
                    return ChatFragment.newInstance(position + 1);
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
                    return getString(R.string.my_balance);
                case 1:
                    return getString(R.string.expenses);
                case 2:
                    return getString(R.string.chat);
            }
            return null;
        }
    }
}