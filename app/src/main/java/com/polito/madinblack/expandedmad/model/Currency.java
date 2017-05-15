package com.polito.madinblack.expandedmad.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ale on 27/04/2017.
 */

public class Currency {

    private static final double [][] association = new double[4][4];

    static {
        association[0][0] = 1;
        association[1][1] = 1;
        association[2][2] = 1;
        association[3][3] = 1;
        association[0][1] = 0.91084;
        association[0][2] = 0.77395;
        association[0][3] = 113.354;
        association[1][0] = 1.09789;
        association[1][2] = 0.84971;
        association[1][3] = 124.450;
        association[2][0] = 0.00882;
        association[2][1] = 0.00804;
        association[2][3] = 0.00683;
    }



    private static final Map<String, Integer> currencySymbolInt = new HashMap<String, Integer>() {{
        put("$", 0);
        put("€", 1);
        put("¥", 3);
        put("£", 2);
    }};

    private static final Map<String, String> currencyISOCodeSymbol = new HashMap<String, String>() {{
        put("USD", "$");
        put("EUR", "€");
        put("GBP", "£");
        put("JPY", "¥");
    }};
    private static final Map<String, String> currencySymbolISOCode = new HashMap<String, String>() {{
        put("$", "USD");
        put("€", "EUR");
        put("£", "GBP");
        put("¥", "JPY");
    }};



    public static double convertCurrency(Double quantity, String currency1, String currency2){

        if(currencySymbolInt.containsKey(currency1) && currencySymbolInt.containsKey(currency2)){
            double resultToRound = quantity * association[currencySymbolInt.get(currency1)][currencySymbolInt.get(currency1)];
            return new BigDecimal(resultToRound).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        else
            return -1;

    }

    public static String getSymbol(String name){
        return currencyISOCodeSymbol.get(name);

    }

    public static String getName(String symbol){
        return currencySymbolISOCode.get(symbol);

    }

    public static Set<String> getCurrencyNames(){
        return currencyISOCodeSymbol.keySet();
    }

    public static Set<String> getCurrencySymbols(){
        return currencySymbolISOCode.keySet();
    }




}
