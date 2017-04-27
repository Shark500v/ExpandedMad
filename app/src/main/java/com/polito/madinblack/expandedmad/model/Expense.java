package com.polito.madinblack.expandedmad.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Expense {

    /*
    public enum State{CONTEST, ACCEPTED}
    public enum Tag{FOOD, WATER_BILL, GAS_BILL, LIGHT_BILL, FLIGHT, HOTEL, FUEL, DRINK, OTHER}
    public enum Currency{YEN, EURO, DOLLAR, GBP}
    */

    private String  id;
    private String  name;
    private String  tag;
    private String  paidByName;
    private String  paidBySurname;
    private String  paidById;
    private Double  cost;
    private String  currencyName;
    private String  currencySymbol;
    //private String state
    private String  groupId;
    private Long    year;
    private Long    month;
    private Long    day;
    private String  description;


    private Map<String, PaymentFirebase>payments;

    //a map showing for each user the cost of the Payment
    //private Map<String, Payment> userCost = new HashMap<>();

    public Expense(){

    }

    public Expense(String name, String tag, String paidByName, String paidBySurname, String paidById, Double cost, String currencyName, String currencySymbol, Long year, Long month, Long day, String description) {
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidById = paidById;
        this.cost = cost;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
    }

    public Expense(String id, String name, String tag, String paidByName, String paidBySurname, String paidById, Double cost, String currencyName, String currencySymbol, Long year, Long month, Long day, String description, Map<String, PaymentFirebase> payments) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidById = paidById;
        this.cost = cost;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
        this.payments = payments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public String getPaidBySurname() {
        return paidBySurname;
    }

    public void setPaidBySurname(String paidBySurname) {
        this.paidBySurname = paidBySurname;
    }

    public String getPaidById() {
        return paidById;
    }

    public void setPaidById(String paidById) {
        this.paidById = paidById;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, PaymentFirebase> getPayments() {
        return payments;
    }

    public void setPayments(Map<String, PaymentFirebase> payments) {
        this.payments = payments;
    }


    public void addUserCost(User u, Payment payment){
    //    userCost.put(u.getId(), payment);

    }




    public static int dateCompare(Expense e1, Expense e2){
        /*if(e1.getYear()>e2.getYear())
            return 1;
        else if(e1.getMonth())
    */
        return 1;
    }



    /*
    public Payment getPayment(Long id){ return userCost.get(id); }

    public Float getMyBalance(){
        if(!userCost.containsKey(MyApplication.myself.getId()))
            return -777f;
        return userCost.get(MyApplication.myself.getId()).getBalance();
    }



    public void addPayment(User user, Float paid, Float toPaid){
        Payment p = new Payment(user, this, paid, toPaid);
        userCost.put(user.getId(), p);

        if(paying.getId()==MyApplication.myself.getId() && user.getId()!=MyApplication.myself.getId()){
            group.uplMyCreditDebit(user, (toPaid-paid));
        }else if(paying.getId()!=MyApplication.myself.getId() && user.getId()==MyApplication.myself.getId()){
            group.uplMyCreditDebit(paying, (paid-toPaid));
        }
    }

    public void addPayment(Payment p){

        userCost.put(p.getUser().getId(), p);

        if(paying.getId()==MyApplication.myself.getId() && p.getUser().getId()!=MyApplication.myself.getId()){
            group.uplMyCreditDebit(p.getUser(), (p.getToPaid()-p.getPaid()));
        }else if(paying.getId()!=MyApplication.myself.getId() && p.getUser().getId()==MyApplication.myself.getId()){
            group.uplMyCreditDebit(paying, (p.getPaid()-p.getToPaid()));
        }
    }




    public String toString(){
        return userCost.get(MyApplication.myself.getId()).toString();
    }
    */

}
