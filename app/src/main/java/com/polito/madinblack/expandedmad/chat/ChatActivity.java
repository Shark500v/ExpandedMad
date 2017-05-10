package com.polito.madinblack.expandedmad.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String groupId = "52";
    private EditText inputMessage;
    private MyApplication ma;
    private RecyclerView recyclerView;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputMessage = (EditText)findViewById(R.id.input_message);
        ma = MyApplication.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.message_list);

        ref = FirebaseDatabase.getInstance().getReference()
                                    .child("messages/"+groupId);

        SimpleItemRecyclerViewAdapter mAdapter = new SimpleItemRecyclerViewAdapter(
                Message.class,
                R.layout.list_message_item_left,
                RecyclerView.ViewHolder.class,
                ref
                );
        recyclerView.setAdapter(mAdapter);

    }

    public void sendMessage(View view) {

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        ref.push().setValue(
                new Message(ma.getUserName() + " " + ma.getUserSurname(), inputMessage.getText().toString())
                );

        // Clear the input
        inputMessage.setText("");
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends FirebaseRecyclerAdapter<Message,RecyclerView.ViewHolder> {
        /**
         * @param modelClass      Firebase will marshall the data at a location into
         *                        an instance of a class that you provide
         * @param modelLayout     This is the layout used to represent a single item in the list.
         *                        You will be responsible for populating an instance of the corresponding
         *                        view with the data from an instance of modelClass.
         * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
         * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
         *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
         */
        public SimpleItemRecyclerViewAdapter(Class<Message> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }


        @Override
        protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Message model, int position) {
            //da cambiare il controllo, è solo per ricordarmi che se mio mess va a destra
            if(model.getSentBy().equals("ME")){
                ViewHolderRight holder = (ViewHolderRight)viewHolder;
                holder.mContentView.setText(model.getMessage());
            }
            else{
                ViewHolderLeft holder = (ViewHolderLeft)viewHolder;
                holder.mContentView.setText(model.getMessage());
                //devo poi settare la foto
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = null;
            if(viewType == 0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_item_right, parent, false);
                return new ViewHolderRight(view);
            }
            else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_item_left, parent, false);
                return new ViewHolderLeft(view);
            }
        }

        @Override
        public int getItemViewType(int position){
            Message message = getItem(position);
            // 0 : right
            // 1 : left
            //da cambiare il controllo, è solo per ricordarmi che se mio mess va a destra
            if(message.getSentBy().equals("ME"))
                return 0;
            else
                return 1;
        }

        //questa è una classe di supporto che viene usata per creare la vista a schermo
        public class ViewHolderRight extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Message mItem;

            public ViewHolderRight(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.textView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        //questa è una classe di supporto che viene usata per creare la vista a schermo
        public class ViewHolderLeft extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mIdView;
            public final TextView mContentView;
            public Message mItem;

            public ViewHolderLeft(View view) {
                super(view);
                mView = view;
                mIdView = (ImageView) view.findViewById(R.id.imageUser);
                mContentView = (TextView) view.findViewById(R.id.textView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
