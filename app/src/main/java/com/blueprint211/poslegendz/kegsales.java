package com.blueprint211.poslegendz;

public class kegsales {
    String name;
    int sales;
    public kegsales(){

    }
    public kegsales(String name, int sales){
        this.name = name;
        this.sales = sales;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }
}
