package com.polito.madinblack.expandedmad.newGroup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    ContentResolver resolver;

    RecyclerView recyclerView;
    UserRecyclerViewAdapter adapter;
    View rootView;

    // ArrayList
    ArrayList<SelectUser> selectUsers = new ArrayList<>();;
    List<SelectUser> groupMembers;
    // Cursor to load contacts list
    Cursor phones;

    SearchView search;

    String id_prev = "00000";   //id che non dovrebbe avere nessun contatto


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupMembers = (List<SelectUser>) getArguments().getSerializable("LIST");       //lista che ricevo dall'activity

        RequestPerm();  //I need to ask for permission in order to access the contact list stored in the device
    }

    private void executeRetrieve(){
        resolver = getContext().getContentResolver();
        phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        //dopo aver fatto il retrieve dei contatti posso procedere
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    public void RequestPerm() {
        //l'if verifica che i permessi non siamo già stati concessi all'applicazione
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST); //se i permessi non sono stati concessi allora li richiedo direttamente all'utente
            //non basta inserire dentro il manifest file il permission per leggere i contatti, ma devo anche chiedere all'utente
        }else {
            //se ho tutti i permessi posso fare subito il retrieve dei contatti
            executeRetrieve();
        }
    }

    @Override       //questa funzione gestisce la risposta dell'utente alla richiesta di accesso dei contatti
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //se l'utente mi dà i permessi allora accedo ai contatti facendo il retrieve
                    executeRetrieve();
                } else {
                    // permission denied
                    // allora torno alla pagina precedente
                    getActivity().navigateUpTo(new Intent(getContext(), GroupListActivity.class));
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getContext(), getString(R.string.no_contacts_in_list), Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    if((id.compareTo(id_prev) != 0) && (phoneNumber != null)){     //elimina i contatti che possiedono più di un numero memorizzato, privileggiando solo il primo e mostrando solo quello
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                        //retrieving and setting the contact image
                        String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        try {
                            if (image_thumb != null) {
                                bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                            } else {
                                Log.e("No Image Thumb", "--------------");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }   //il retrieve delle foto contatto è corretto, non ci sono errori, verificato tramite debbug

                        SelectUser selectUser = new SelectUser();
                        if (image_thumb != null) {
                            try {
                                selectUser.setThumb(bit_thumb);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        selectUser.setName(name);
                        selectUser.setPhone(phoneNumber);
                        selectUser.setEmail(EmailAddr);
                        selectUser.setCheckedBox(false);
                        selectUsers.add(selectUser);

                        id_prev = id;
                    }
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new UserRecyclerViewAdapter(selectUsers, getContext(), groupMembers);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.contact_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        search = (SearchView) rootView.findViewById(R.id.search_view);

        return rootView;
    }
}