package com.dan.kaftan.onlineshop.onlineshop;

import java.util.HashMap;

public class ShopSpecificInfo {

    static protected HashMap<String,String> shopInfo = new HashMap<>();

    public static void setShopInfo(String key, String value){
        shopInfo.put(key,value);
    }

    static public String getShopInfo(String key){
        return shopInfo.get(key);
    }
}
