package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Ale on 27/04/2017.
 */

public class Tag {

    String [] strings = {"Food", "Water Bill", "Gas Bill", "Light Bill", "Flight", "Hotel", "Fuel", "Drink", "Other"};
    private List<String> tag = new ArrayList<String>( Arrays.asList(strings));



    public Tag(){

    }


    public List<String> getTag() {
        return tag;
    }
}
