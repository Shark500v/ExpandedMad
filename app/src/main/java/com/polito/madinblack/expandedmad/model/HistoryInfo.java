package com.polito.madinblack.expandedmad.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryInfo {
    /**
     *
     *  0: New Expense
     *  1: New Payment
     *  2: New User In Group
     */
    private String name;
    private Long content;
    private Double cost;
    private String currencyISO;
    private String paidTo;
    private Date date;

    public HistoryInfo(){

    }

    public HistoryInfo(String name, Long content, Double cost, String currencyISO, String paidTo){
        this.name = name;
        this.content = content;
        this.cost = cost;
        this.currencyISO = currencyISO;
        this.paidTo = paidTo;
        date = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getContent() {
        return content;
    }

    public void setContent(Long content) {
        this.content = content;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(String paidTo) {
        this.paidTo = paidTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String convertDateToString(){
        //SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        return df.format(date);
    }
}
