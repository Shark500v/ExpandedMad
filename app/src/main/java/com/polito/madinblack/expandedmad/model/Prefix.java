package com.polito.madinblack.expandedmad.model;


import com.polito.madinblack.expandedmad.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Ale on 02/05/2017.
 */

public class Prefix {
    private static final Map<String, String> prefixs = new LinkedHashMap<String, String>() {{
       // String [] prefix = getResources().getStringArray(R.array.nations_prefix);
        put("Australia", "+61");
        put("Austria", "+43");
        put("Canada",  "+1");
        put("Francia",  "+33");
        put("Germania", "+49");
        put("Gran", "+61");
        put("Austria", "+43");
        put("Canada",  "+1");
        put("Francia",  "+33");
        put("Germania", "+49");
        put("Australia", "+61");
        put("Austria", "+43");
        put("Canada",  "+1");
        put("Francia",  "+33");
        put("Germania", "+49");

    }};





    public Prefix(){

    }
    public static Set<String> getStates (){

        return prefixs.keySet();

    }

    public String getPrefix(String nation){return prefixs.get(nation);}
    public Map<String, String> getCurrency() {
        return prefixs;
    }


}
