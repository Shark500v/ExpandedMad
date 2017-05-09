package com.polito.madinblack.expandedmad.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creata come la classe gruppo
 */

enum State{constest, accepted}

public class Expense {

    //elementi per ogni lista di expenses
    public  static List<Expense.ExpenseElement> list;
    public  static Map<String, Expense.ExpenseElement> map;
    private static int COUNT;    //mi dice quanti elementi mostrare nella lista che vado a creare
    private static int CountNewExpense; //da usare quando aggiungo un nuovo gruppo

    //constructor, in questo caso mi serve poichè per ogni gruppo ci sarà una ista di spese, quindi devo inizializzare il tutto
    public Expense() {

        this.list = new ArrayList<Expense.ExpenseElement>();
        this.map = new HashMap<String, ExpenseElement>();
        this.COUNT = 13;
        this.CountNewExpense = COUNT;
        //now I have to initialize the list with random elemets
        Initialize();

    }

    private static void Initialize() {
        // Add some sample expenses.
        for (int i = 1; i <= COUNT; i++) {
            addExpense(createNewExpense(i));
        }
    }

    private static void addExpense(ExpenseElement e) {
        list.add(e);
        map.put(e.id, e);
    }

    //constructor
    private static Expense.ExpenseElement createNewExpense(int position) {
        return new Expense.ExpenseElement(String.valueOf(position), "Expense " + position, makeDetails(position), (float) 15.6, "Euro");
    }

    //detail about the expense
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();    //costruisce una stringa appendendo elementi
        builder.append("Details about Expense: ").append(position);

        builder.append("\n\nInsert here the Expense information.");

        return builder.toString();
    }

    //aggiungo un nuovo elemento alla lista
    public static void AddNewExpense(){
        addExpense(createNewExpense(++CountNewExpense));
    }

    //informazioni per ogni singola spesa
    public static class ExpenseElement {
        public final String id;         //tag
        public final String content;    //name
        public final String details;    //description
        public final float cost;
        public final String currency;
        //public final State state;



        //costruttore
        public ExpenseElement(String id, String content, String details, float cost, String currency) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.cost = cost;
            this.currency = currency;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}
