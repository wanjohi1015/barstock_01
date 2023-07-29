package com.blueprint211.poslegendz;

import java.util.Date;

public class Credit {
    String refname,category,size ;
    float items;
    int sales,itemsale,profit;
    Date timestamp;

    public Credit() {
    }

    public Credit (String refname, String category, Date timestamp ,float items,int sales,int itemsale,int profit){
        this.refname = refname;
        this.category = category;
        this.timestamp = timestamp;
        this.items = items;
        this.sales = sales;
        this.profit = profit;
        this.itemsale = itemsale;

    }

    public String getRefname() {
        return refname;
    }
    public void setRefname( String refname){
        this.refname = refname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getItems() {
        return items;
    }

    public void setItems(float items) {
        this.items = items;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getItemsale() {
        return itemsale;
    }

    public void setItemsale(int itemsale) {
        this.itemsale = itemsale;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }


}
