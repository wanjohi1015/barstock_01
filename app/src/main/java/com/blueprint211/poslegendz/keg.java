package com.blueprint211.poslegendz;

import java.util.Date;

public class keg {
    Date boughttime;
    String name,barrelref,donetime;
    int buyprice;
     public keg (){

     }

     public keg (String name, String barrelref, Date boughttime, String donetime, int buyprice){
         this.name = name;
         this.barrelref = barrelref;
         this.boughttime = boughttime;
         this.donetime = donetime;
         this.buyprice = buyprice;

     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBuyprice() {
        return buyprice;
    }

    public void setBuyprice(int buyprice) {
        this.buyprice = buyprice;
    }

    public Date getBoughttime() {
        return boughttime;
    }

    public void setBoughttime(Date boughttime) {
        this.boughttime = boughttime;
    }

    public String getDonetime() {
        return donetime;
    }

    public void setDonetime(String donetime) {
        this.donetime = donetime;
    }

    public String getBarrelref() {
        return barrelref;
    }

    public void setBarrelref(String barrelref) {
        this.barrelref = barrelref;
    }
}
