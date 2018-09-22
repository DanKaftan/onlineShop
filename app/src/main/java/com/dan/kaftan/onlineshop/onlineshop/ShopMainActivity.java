package com.dan.kaftan.onlineshop.onlineshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dan.kaftan.common.DownloadCatalogActivity;
import com.dan.kaftan.common.EditItem;
import com.dan.kaftan.common.ShopSpecificInfo;

public class ShopMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this is the specific shop code
        ShopSpecificInfo.setShopInfo("shopName", "DemoShop");
        ShopSpecificInfo.setShopInfo("catalogFile","catalog.txt");
        ShopSpecificInfo.setShopInfo("storage_bucket", "gs://online-shop-a32c0.appspot.com");
        Intent intent = new Intent(ShopMainActivity.this,DownloadCatalogActivity.class);
        startActivity(intent);
    }
}
