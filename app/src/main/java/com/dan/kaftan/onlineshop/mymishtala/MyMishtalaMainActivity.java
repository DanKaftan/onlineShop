package com.dan.kaftan.onlineshop.mymishtala;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dan.kaftan.common.DownloadCatalogActivity;
import com.dan.kaftan.common.ShopSpecificInfo;

public class MyMishtalaMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this is the specific shop code
        ShopSpecificInfo.setShopInfo("shopName", "mymishtala");
        ShopSpecificInfo.setShopInfo("catalogFile","catalog.txt");
        Intent intent = new Intent(MyMishtalaMainActivity.this,DownloadCatalogActivity.class);
        startActivity(intent);
    }
}
