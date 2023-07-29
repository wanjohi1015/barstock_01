package com.blueprint211.poslegendz;

public class Cart {

    String refname,category,size,purpose ;
    float items;
    int sales,itemsale,profit;

    public Cart() {
    }

    public Cart (String refname, String category, String size ,String purpose,float items,int sales,int itemsale,int profit){
        this.refname = refname;
        this.category = category;
        this.size = size;
        this.items = items;
        this.sales = sales;
        this.profit = profit;
        this.itemsale = itemsale;
        this.purpose = purpose;

    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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
