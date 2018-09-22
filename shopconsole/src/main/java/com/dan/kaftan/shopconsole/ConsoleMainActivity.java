package com.dan.kaftan.shopconsole;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dan.kaftan.common.DownloadCatalogActivity;
import com.dan.kaftan.common.ShopSpecificInfo;

public class ConsoleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this is the specific shop code
        ShopSpecificInfo.setShopInfo("shopName", "DemoShop");
        ShopSpecificInfo.setShopInfo("catalogFile", "catalog.txt");
        ShopSpecificInfo.setShopInfo("storage_bucket", "gs://online-shop-a32c0.appspot.com");

        ShopSpecificInfo.setShopInfo("isConsole", "TRUE");
        Intent intent = new Intent(ConsoleMainActivity.this, DownloadCatalogActivity.class);
        startActivity(intent);
    }
}
