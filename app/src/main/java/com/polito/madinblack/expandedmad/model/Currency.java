package com.polito.madinblack.expandedmad.model;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Currency {

    public enum CurrencyISO{EUR, USD, GBP, JPY};


    //Just to remember how to convert
    /*
    String name = CurrencyISO.USD.name();
    CurrencyISO mode = Currency.valueOf(name);
    */

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
        association[2][0] = 1.29207;
        association[2][1] = 1.17687;
        association[2][3] = 146.462;
        association[3][0] = 0.00882;
        association[3][1] = 0.00804;
        association[3][2] = 0.00683;
    }

    private static final Map<CurrencyISO, Integer> currencySymbolInt = new HashMap<CurrencyISO, Integer>() {{
        put(CurrencyISO.USD, 0);
        put(CurrencyISO.EUR, 1);
        put(CurrencyISO.GBP, 2);
        put(CurrencyISO.JPY, 3);

    }};

    private static final Map<CurrencyISO, String> currencyISOCodeSymbol = new HashMap<CurrencyISO, String>() {{
        put(CurrencyISO.USD, "$");
        put(CurrencyISO.EUR, "€");
        put(CurrencyISO.GBP, "£");
        put(CurrencyISO.JPY, "¥");
    }};
    private static final Map<String, CurrencyISO> currencySymbolISOCode = new HashMap<String, CurrencyISO>() {{
        put("$", CurrencyISO.USD);
        put("€", CurrencyISO.EUR);
        put("£", CurrencyISO.GBP);
        put("¥", CurrencyISO.JPY);
    }};



    public static double convertCurrency(Double quantity, Currency.CurrencyISO currencySource, Currency.CurrencyISO currencyDest){

        if(currencySymbolInt.containsKey(currencySource) && currencySymbolInt.containsKey(currencyDest)){
            double resultToRound = quantity * association[currencySymbolInt.get(currencySource)][currencySymbolInt.get(currencyDest)];
            return new BigDecimal(resultToRound).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        else
            return -1;

    }

    public static String getSymbol(CurrencyISO ISOcode){
        return currencyISOCodeSymbol.get(ISOcode);

    }

    public static CurrencyISO getISOCode(String symbol){
        return currencySymbolISOCode.get(symbol);

    }



    public static String toString(CurrencyISO currencyISO){

        return getSymbol(currencyISO) + " (" + currencyISO.name() + ")";

    }

    public static String[] getCurrencyValues() {
        return Arrays.toString(CurrencyISO.values()).replaceAll("^.|.$", "").split(", ");
    }

    public static String[] getCurrencyValues(CurrencyISO currencyISOPreferred) {
        CurrencyISO [] currencyISOs = new CurrencyISO[currencySymbolInt.size()];
        currencyISOs[0] = currencyISOPreferred;
        int i = 1;
        for(CurrencyISO currencyISO : CurrencyISO.values()){
            if(currencyISO.equals(currencyISOPreferred))
                continue;
            currencyISOs[i] = currencyISO;
            i++;
        }
        return Arrays.toString(currencyISOs).replaceAll("^.|.$", "").split(", ");
    }

    public static String[] getCurrencySymbols() {
        String [] symbols = new String[currencyISOCodeSymbol.size()];
        int i = 0;
        for(CurrencyISO currencyISO : CurrencyISO.values()){
            symbols[i] = currencyISOCodeSymbol.get(currencyISO);
            i++;
        }
        return symbols;
    }

    public static String[] getCurrencySymbols(CurrencyISO currencyISOPreferred) {
        String [] symbols = new String[currencyISOCodeSymbol.size()];
        symbols[0] = currencyISOCodeSymbol.get(currencyISOPreferred);
        int i = 1;
        for(CurrencyISO currencyISO : CurrencyISO.values()){
            if(currencyISO.equals(currencyISOPreferred))
                continue;
            symbols[i] = currencyISOCodeSymbol.get(currencyISO);
            i++;
        }
        return symbols;
    }



}
