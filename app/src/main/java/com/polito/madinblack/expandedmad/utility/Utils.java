package com.polito.madinblack.expandedmad.utility;

import android.content.Context;
import android.content.res.TypedArray;

import com.polito.madinblack.expandedmad.R;

//usato per celare il FAB nello scrolling della tabView
public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}