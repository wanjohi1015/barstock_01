package com.blueprint211.poslegendz;

import com.google.firebase.Timestamp;

import java.util.Date;

public class expensesmodel {

    Date timestamp;
    int amount;
    String name,reason;

    public expensesmodel(){

    }
    public expensesmodel(String name, String reason , Date timestamp, int amount){
        this.name = name;
        this.reason = reason;
        this.amount = amount;
        this.timestamp = timestamp;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
