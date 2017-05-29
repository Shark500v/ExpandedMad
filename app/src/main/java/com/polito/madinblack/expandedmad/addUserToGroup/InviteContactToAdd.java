package com.polito.madinblack.expandedmad.addUserToGroup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.newGroup.SelectUser;

import java.io.Serializable;
import java.util.List;

/*Questa classe mostra un semplice dialogo quando vai a selezionare i contatti che vuoi inserire dentro il nuovo gruppo
* nel caso in cui il contatto non fosse presente nel DB.
* questa classe NON invia l'invito
* */
public class InviteContactToAdd extends DialogFragment {

    private List<SelectUser> invite;
    //private List<SelectUser> groupM;
    private String groupId;
    private String groupName;
    private String list = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog;

        //retriving the list of contacts I need to invite
        invite = (List<SelectUser>) getArguments().getSerializable("invite");
        groupId = getArguments().getString("groupIndex");
        groupName = getArguments().getString("groupName");
        //groupM = (List<SelectUser>) getArguments().getSerializable("Group Members");

        for(int i=0; i<invite.size(); i++){
            SelectUser contact= invite.get(i);
            if (i!=0){
                if(i==(invite.size()-1)){
                    list += " " + getString(R.string.and) + " ";
                }else {
                    list += ", ";
                }
            }
            list += contact.getName();
        }
        if(invite.size()>1){
            list += " " + getString(R.string.dialog_multiple_invite_to_add_request) + " \"" + groupName + "\".";
        }else
            list += " " + getString(R.string.dialog_single_invite_to_add_request) + " \"" + groupName + "\".";

        builder.setCancelable(false);
        builder.setTitle(getString(R.string.invite_members)).setMessage(list);
        // Add the buttons
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //manage here the invitation activity
                Intent intent1=new Intent(getContext(), InviteActivityToAdd.class);
                //intent1.putExtra("Group Members", (Serializable) groupM);
                intent1.putExtra("INVITE_LIST", (Serializable) invite);
                intent1.putExtra("groupIndex", groupId);
                intent1.putExtra("groupName", groupName);
                startActivity(intent1);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                getActivity().onBackPressed();  //ritorno all'activity precedente che ha chiamato il fragment
            }
        });

        alertDialog = builder.create();
        return alertDialog;
    }
}