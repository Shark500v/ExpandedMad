package com.polito.madinblack.expandedmad.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ale on 04/04/2017.
 */

public class MyApplication {

    /*groupId --> group*/
    private Map<Long, Group> groups = new HashMap<>();

    public MyApplication(){

    }


    public void addGroup(Group g){
        groups.put(g.getId(), g);
    }









}
