package com.polito.madinblack.expandedmad.model;


import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.load.engine.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ale on 27/04/2017.
 */

public class Currency {

    double [][] association = new double[4][4];

    public static final Map<String, Integer> currencySymbolInt = new HashMap<String, Integer>() {{
        put("€", 0);
        put("$", 1);
        put("¥", 2);
        put("£", 3);
    }};

    public static final Map<String, String> currencyNameSymbol = new HashMap<String, String>() {{
        put("Euro", "€");
        put("Dollar", "$");
        put("Yen",  "¥");
        put("Pound",  "£");
    }};
    public static final Map<String, String> currencyNameSymbol = new HashMap<String, String>() {{
        put("Euro", "€");
        put("Dollar", "$");
        put("Yen",  "¥");
        put("Pound",  "£");
    }};



    public static final Map<String, String> currencyNameSymbol = new HashMap<String, String>() {{
        put("Euro", "€");
        put("Dollar", "$");
        put("Yen",  "¥");
        put("Pound",  "£");
    }};


    public static final Map<String, String> currencyNameValue = new HashMap<String, String>() {{
        put("Euro", "€");
        put("Dollar", "$");
        put("Yen",  "¥");
        put("Pound",  "£");
    }};



    public Currency(){

    }

    public static Map<String, String> getCurrency() {
        return currency;
    }
}
