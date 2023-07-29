package com.blueprint211.poslegendz;

public class Products {

 String name,category,size,reference;
 float items;
 int price;


    public Products() {
    }

    public Products(String name, String category, String size, int price, String reference, float items){
        this.name = name;
        this.category = category;
        this.size = size;
        this.price = price;
        this.items = items;
        this.reference = reference;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getItems() {
        return items;
    }

    public void setItems(float items) {
        this.items = items;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
