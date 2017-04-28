package com.polito.madinblack.expandedmad.newGroup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    ContentResolver resolver;

    // ArrayList
    ArrayList<SelectUser> selectUsers = new ArrayList<SelectUser>();;
    List<SelectUser> groupMembers;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;

    SearchView search;
    SelectUserAdapter adapter;

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

    private void executeRetrive(){
        resolver = getContext().getContentResolver();
        phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        //dopo aver fatto il retrieve dei contatti posso procedere
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    //funzione creata da zero
    public void RequestPerm() {
        //l'if verifica che i permessi non siamo già stati concessi all'applicazione
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST); //se i permessi non sono stati concessi allora li richiedo direttamente all'utente
            //non basta inserire dentro il manifest file il permission per leggere i contatti, ma devo anche chiedere all'utente
        }else {
            //se ho tutti i permessi posso fare subito il retrieve dei contatti
            executeRetrive();
        }
    }

    @Override       //questa funzione gestisce la risposta dell'utente alla richiesta di accesso dei contatti
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //se l'utente mi dà i permessi allora accedo ai contatti facendo il retrieve
                    executeRetrive();
                } else {

                    // permission denied, boo! Disable the
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
                    Toast.makeText(getContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    if((id.compareTo(id_prev) != 0) && (phoneNumber != null)){     //elimina i contatti che possiedono più di un numero memorizzato, privileggiando solo il primo e mostrando solo quello
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        //email = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ", new String[] {id} , null);
                        //email.moveToNext();
                        String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                        //email.close();
                        /*String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        try {
                            if (image_thumb != null) {
                                bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                            } else {
                                Log.e("No Image Thumb", "--------------");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */

                        SelectUser selectUser = new SelectUser();
                        selectUser.setThumb(bit_thumb);
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
            adapter = new SelectUserAdapter(selectUsers, getContext(), groupMembers);
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectUser data = selectUsers.get(i);   //recupero il singolo utente che vado a clickare
                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.contact_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }
}