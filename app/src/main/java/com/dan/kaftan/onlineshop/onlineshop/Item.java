package com.dan.kaftan.onlineshop.onlineshop;

import java.io.Serializable;

public class Item implements Serializable {

    private String name;
    private int price;
    private String itemImageFile;


    public void setName(String name){

        this.name = name;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public void setItemImageFile (String itemImageFile){

        this.itemImageFile = itemImageFile;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getItemImageFile() {
        return itemImageFile;
    }
}
