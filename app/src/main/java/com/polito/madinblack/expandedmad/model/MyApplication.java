package com.polito.madinblack.expandedmad.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ale on 04/04/2017.
 */

public class MyApplication {

    public static User myself;

    /*groupId --> group*/
    private Map<Long, Group> groups = new LinkedHashMap<>();



    public MyApplication(){
        myself = new User("MyName", "MySurname");
        

            Group g1 = new Group("Group1");

            //groups.put(g.getId(), g);



    }



    public void addGroup(Group g){
        groups.put(g.getId(), g);
    }

    public List<Group> getGroup(){ return new ArrayList<Group>(groups.values()); }







}
