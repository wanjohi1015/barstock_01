package com.blueprint211.poslegendz;

public class reports {

    int sales,profit;
    float items;
    String define;
    public reports(){

    }
    public reports(String define,int sales,int items){
        this.sales = sales;
        this. items = items;
        this.define = define;

    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public float getItems() {
        return items;
    }

    public void setItems(float items) {
        this.items = items;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
}
