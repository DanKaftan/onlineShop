package com.dan.kaftan.shopconsole;

import android.content.Intent;
import android.os.Bundle;

import com.dan.kaftan.common.DownloadCatalogActivity;
import com.dan.kaftan.common.MyService;
import com.dan.kaftan.common.ShopSpecificInfo;

public class ConsoleDownloadCatalogActivity extends DownloadCatalogActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ShopSpecificInfo.setShopInfo("isConsole", "TRUE");
        super.onCreate(savedInstanceState);

    }}
