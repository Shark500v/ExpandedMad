package com.polito.madinblack.expandedmad.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

;

public class BalanceHistory {

    private String expenseName;
    private Type type;
    private Double value;
    private Date date;

    public BalanceHistory(){}


    public BalanceHistory(String expenseName, Type type, Double value, Date date) {
        this.expenseName = expenseName;
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String convertDateToString(){

        SimpleDateFormat dateFormat;
        if(Locale.getDefault().equals(Locale.ITALIAN)){
            dateFormat = new SimpleDateFormat("dd/MM/yy kk:mm", Locale.getDefault());
        }
        else{
            dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm aa", Locale.getDefault());
        }

        return dateFormat.format(date);
    }
}
