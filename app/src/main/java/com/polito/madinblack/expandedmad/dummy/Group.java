package com.polito.madinblack.expandedmad.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Francesco on 03/04/2017.
 * creo la classe gruppo che mi faccia vedere i gruppi nella schermata principale
 */

public class Group {

    public static final List<DummyContent.DummyItem> Groups = new ArrayList<DummyContent.DummyItem>();

    //informazioni per ogni singolo gruppo
    public static class GroupElements {
        public final String id;
        public final String name;
        public final String content;
        public final String details;

        //costruttore
        public GroupElements(String id, String content, String name, String details) {
            this.id = id;
            this.name = name;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
