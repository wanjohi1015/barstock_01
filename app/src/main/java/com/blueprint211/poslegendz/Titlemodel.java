package com.blueprint211.poslegendz;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Titlemodel {

    @ServerTimestamp
    Date timestamp;


    String   name,purpose,define;
    float items;
    int totalprice,profits;
    List<Credit> titleitems;




    public Titlemodel() {
    }

    public Titlemodel( String name,String define,String purpose, Date timestamp, int totalprice,float items,int profits, List<Credit> titleitems) {

        this.name = name;
        this.timestamp = timestamp;
        this.totalprice = totalprice;
        this.titleitems = titleitems;
        this.items = items;
        this.profits = profits;
        this.purpose = purpose;
        this.define = define;

    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getProfits() {
        return profits;
    }

    public void setProfits(int profits) {
        this.profits = profits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }
    public float getItems() {
        return items;
    }

    public void setItems(float items) {
        this.items = items;
    }

    public List<Credit> getTitleItems(){
        return titleitems;
    }
    public void setTitleItems(List<Credit> titleitems){
        this.titleitems = titleitems;
    }


}
