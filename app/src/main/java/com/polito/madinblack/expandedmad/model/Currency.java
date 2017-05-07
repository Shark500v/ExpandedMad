package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ale on 27/04/2017.
 */

public class Currency {

    public static final Map<String, String> currency = new HashMap<String, String>() {{
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
