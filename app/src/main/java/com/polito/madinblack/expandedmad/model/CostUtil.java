package com.polito.madinblack.expandedmad.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CostUtil {
    /*
    public static double round(double number, double scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        //return (double) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
        int result = (int) ((tmp - (int) tmp) > 0 ? tmp + 1 : tmp);
        double division = (double) result / pow;
        return division;
    }*/

    public static Double round(Double val, int scale) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.UP).doubleValue();
    }

    public static double round2(double number, double scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        return (double) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public static Double roundDown(Double val, int scale) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.DOWN).doubleValue();
    }


    public static boolean validateTelFaxNumber(String number)
    {
        if (number == null || number.isEmpty())
        {
            return false;
        }

        String patternSpace = "\\s+";
        String replaceStr = "";
        Pattern pattern = Pattern.compile(patternSpace);
        Matcher matcher = pattern.matcher(number);
        number = matcher.replaceAll(replaceStr);

        patternSpace = "\\-+";
        replaceStr = "";
        pattern = Pattern.compile(patternSpace);
        matcher = pattern.matcher(number);
        number = matcher.replaceAll(replaceStr);

//i caratteri / non vengono presi in considerazione
        patternSpace = "\\/+";
        replaceStr = "";
        pattern = Pattern.compile(patternSpace);
        matcher = pattern.matcher(number);
        number = matcher.replaceAll(replaceStr);

        Pattern p = Pattern.compile("^(\\+)?[0-9]+$");
        Matcher m = p.matcher(number);
        boolean matchFound = m.matches();
        return matchFound;
    }

    public static boolean isParsableAsDouble(final String s) {
        try {
            Double.valueOf(s);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String replaceDecimalComma(String string){
        int counter = 0;
        int i;
        for( i=0; i<string.length(); i++ ) {
            if( string.charAt(i) == '.' || string.charAt(i) == ',') {
                if(counter < 1)
                    counter++;
                else{
                    counter++;
                    break;
                }
            }
        }
        string = string.substring(0, i);
        string = string.replace(',', '.');
        if(string.indexOf('.') !=-1) {
            int diff = string.length() - string.indexOf('.');
            if (diff > 3) {
                string = string.substring(0, string.length() - 1);
                counter++; //so it return false
            }
        }
        return counter < 2 ? string : null;
    }


}
