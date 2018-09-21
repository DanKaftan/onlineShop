package com.dan.kaftan.common;

import java.io.Serializable;

public class Item implements Serializable {



    private int id;
    private String name = "";
    private String price = "";
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", itemImageFile='" + itemImageFile + '\'' +
                '}';
    }
}
