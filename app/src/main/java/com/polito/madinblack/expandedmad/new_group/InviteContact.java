package com.polito.madinblack.expandedmad.new_group;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

public class InviteContact extends DialogFragment {

    List<SelectUser> invite;
    String phrase = "Do you want to invite new members ?";
    String list = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog;

        //retriving the list of contacts I need to invite
        invite = (List<SelectUser>) getArguments().getSerializable("invite");

        for(int i=0; i<invite.size(); i++){
            SelectUser contact= invite.get(i);
            if (i!=0){
                list +=", ";
            }
            list += contact.getName();
        }
        list += " should be invited to join the Application in order to be added to a group.";

        builder.setTitle(phrase).setMessage(list);
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //manage here the invitation activity

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                getActivity().onBackPressed();  //ritorno all'activity precedente che ha chiamato il fragment
            }
        });

        alertDialog = builder.create();
        return alertDialog;
    }
}