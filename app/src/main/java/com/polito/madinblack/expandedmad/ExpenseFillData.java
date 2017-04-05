package com.polito.madinblack.expandedmad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by Ale on 05/04/2017.
 */

public class ExpenseFillData extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_expense) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            ///Ale qua cambia tu e metti quello che ti serve

            /*Intent intent = new Intent(this, UserDebts.class);
            intent.putExtra(EXTRA_MESSAGE, userID);

            startActivity(intent);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_expense, menu);
        return true;
    }











}
