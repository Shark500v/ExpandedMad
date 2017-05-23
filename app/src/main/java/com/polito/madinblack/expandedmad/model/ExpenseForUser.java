package com.polito.madinblack.expandedmad.model;


public class ExpenseForUser {
    private String name;
    private String paidByName;
    private String paidBySurname;
    private String paidByFirebaseId;
    private String paidByPhoneNumber;
    private String id;
    private Double myBalance;
    private Double cost;
    private Currency.CurrencyISO currencyISO;
    private String groupId;
    private Long day;
    private Long month;
    private Long year;
    private Long timestamp;
    private String tag;


    public ExpenseForUser(){

    }

    public ExpenseForUser(Expense expense, Double myBalance){
        this.name               = expense.getName();
        this.myBalance          = CostUtil.round(myBalance, 2);
        this.paidByName         = expense.getPaidByName();
        this.paidBySurname      = expense.getPaidBySurname();
        this.paidByFirebaseId   = expense.getPaidByFirebaseId();
        this.paidByPhoneNumber  = expense.getPaidByPhoneNumber();
        this.currencyISO        = expense.getCurrencyISO();
        this.id                 = expense.getId();
        this.groupId            = expense.getGroupId();
        this.day                = expense.getDay();
        this.month              = expense.getMonth();
        this.year               = expense.getYear();
        this.cost               = expense.getCost();
        this.tag                = expense.getTag();

    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getPaidByFirebaseId() {
        return paidByFirebaseId;
    }

    public void setPaidByFirebaseId(String paidByFirebaseId) {
        this.paidByFirebaseId = paidByFirebaseId;
    }

    public String getPaidByPhoneNumber() {
        return paidByPhoneNumber;
    }

    public void setPaidByPhoneNumber(String paidByPhoneNumber) {
        this.paidByPhoneNumber = paidByPhoneNumber;
    }

    public Double getMyBalance() {
        return myBalance;
    }

    public void setMyBalance(Double myBalance) {
        this.myBalance = CostUtil.round(myBalance, 2);
    }


    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Currency.CurrencyISO getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(Currency.CurrencyISO currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = -1*System.currentTimeMillis();
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
