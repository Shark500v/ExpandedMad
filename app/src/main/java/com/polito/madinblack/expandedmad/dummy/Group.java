package com.polito.madinblack.expandedmad.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 03/04/2017.
 * creo la classe gruppo che mi faccia vedere i gruppi nella schermata principale
 */

public class Group {

    public static final List<GroupElements> Groups = new ArrayList<GroupElements>();

    public static final Map<String, GroupElements> Group_MAP = new HashMap<String, GroupElements>();

    private static final int COUNT = 30;    //mi dice quanti elementi mostrare nella lista che vado a creare

    public static int CountNewGrup = COUNT; //da usare quando aggiungo un nuovo gruppo

    static {
        // Add some sample groups.
        for (int i = 1; i <= COUNT; i++) {
            addGroup(createNewGroup(i));
        }
    }

    private static void addGroup(GroupElements g) {
        Groups.add(g);
        Group_MAP.put(g.id, g);
    }

    private static GroupElements createNewGroup(int position) {
        return new GroupElements(String.valueOf(position), "Group " + position, makeDetails(position));
    }

    //questa funzione va a creare le informazioni che orgni gruppo deve avere, quindi qui dentro suppongo bisogna inserire
    //le spese, cliccando su di una spesa arriviamo alla vista della spesa
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();    //costruisce una stringa appendendo elementi
        builder.append("Details about Group: ").append(position);

        builder.append("\n\nInsert here the Expenses list");

        return builder.toString();
    }

    //aggiungo un nuovo elemento alla lista
    public static void AddNewGroup(){
        addGroup(createNewGroup(++CountNewGrup));
    }

    //informazioni per ogni singolo gruppo
    public static class GroupElements {
        public final String id;
        public final String content;
        public final String details;
        private Expense expensesList;

        public Expense getList() {
            return expensesList;
        }

        //costruttore
        public GroupElements(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.expensesList = new Expense();
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
