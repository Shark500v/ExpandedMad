package com.polito.madinblack.expandedmad.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CostUtil {

    public static double round(double number, double scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        //return (double) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
        return (double) (int) ((tmp - (int) tmp) > 0.0f ? tmp + 1 : tmp) / pow;
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
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }


}
