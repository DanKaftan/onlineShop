package com.dan.kaftan.onlineshop.onlineshop;

import java.io.Serializable;

public class Item implements Serializable {

    private String name;
    private String price;
    private String itemImageFile;


    public void setName(String name){

        this.name = name;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public void setItemImageFile (String itemImageFile){

        this.itemImageFile = itemImageFile;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getItemImageFile() {
        return itemImageFile;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", itemImageFile='" + itemImageFile + '\'' +
                '}';
    }
}
