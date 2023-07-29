package com.blueprint211.poslegendz;

import java.util.List;

public class Credited {
        String name;
        float items;
        int credit;
    List<Cart> titleitem;



    public Credited() {
    }


    public Credited(String name,float items,int credit, List<Cart> titleitem){
        this.name = name;
        this.items = items;
        this.credit = credit;
        this.titleitem = titleitem;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getItems() {
        return items;
    }

    public void setItems(float items) {
        this.items = items;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public List<Cart> getTitleitem() {
        return titleitem;
    }

    public void setTitleitem(List<Cart> titleitem) {
        this.titleitem = titleitem;
    }
}
