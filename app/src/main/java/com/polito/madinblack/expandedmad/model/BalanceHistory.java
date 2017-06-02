package com.polito.madinblack.expandedmad.model;

import java.util.Date;

/**
 * Created by Ale on 01/06/2017.
 */

enum Type{NEW_EXPENSE, SETTLE_UP, STORNED};

public class BalanceHistory {

    private String expenseName;
    private Type type;
    private Long value;
    private Date date;

    public BalanceHistory(String expenseName, Type type, Long value, Date date) {
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
