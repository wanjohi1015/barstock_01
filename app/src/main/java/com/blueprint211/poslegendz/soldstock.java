package com.blueprint211.poslegendz;

import java.util.Date;

public class soldstock {
    int sales;
    String purpose;
    Date time;
    public soldstock (){

    }
    public soldstock (String purpose, int sales){
        this.purpose = purpose;
        this.sales = sales;

    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }
}
