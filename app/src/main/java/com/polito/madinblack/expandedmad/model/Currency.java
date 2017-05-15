package com.polito.madinblack.expandedmad.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        put("USD", 0);
        put("EUR", 1);
        put("GBP", 3);
        put("JPY", 2);
    }};

    private static final Map<String, String> currencyISOCodeSymbol = new HashMap<String, String>() {{
        put("USD", "$");
        put("GBP", "£");
        put("JPY", "¥");
        put("EUR", "€");
    }};
    private static final Map<String, String> currencySymbolISOCode = new HashMap<String, String>() {{
        put("$", "USD");
        put("£", "GBP");
        put("¥", "JPY");
        put("€", "EUR");
    }};



    public static double convertCurrency(Double quantity, String currency1, String currency2){

        if(currencySymbolInt.containsKey(currency1) && currencySymbolInt.containsKey(currency2)){
            double resultToRound = quantity * association[currencySymbolInt.get(currency1)][currencySymbolInt.get(currency1)];
            return new BigDecimal(resultToRound).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        else
            return -1;

    }

    public static String getSymbol(String ISOcode){
        return currencyISOCodeSymbol.get(ISOcode);

    }

    public static String getISOCode(String symbol){
        return currencySymbolISOCode.get(symbol);

    }

    public static Set<String> getCurrencyISOcode(){
        return currencyISOCodeSymbol.keySet();
    }

    public static Set<String> getCurrencySymbols(){
        return currencySymbolISOCode.keySet();
    }




}
