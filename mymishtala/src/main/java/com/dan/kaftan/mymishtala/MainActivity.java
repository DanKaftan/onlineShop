package com.dan.kaftan.mymishtala;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dan.kaftan.common.DownloadCatalogActivity;
import com.dan.kaftan.common.ShopSpecificInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // this is the specific shop code
            ShopSpecificInfo.setShopInfo("shopName", "mymishtala");
            ShopSpecificInfo.setShopInfo("catalogFile","catalog.txt");
            ShopSpecificInfo.setShopInfo("storage_bucket", "gs://online-shop-a32c0.appspot.com");
            Intent intent = new Intent(MainActivity.this,DownloadCatalogActivity.class);
            startActivity(intent);

    }
}
