package com.dan.kaftan.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NotificationActivity extends AppCompatActivity {


    EditText titleEt;
    EditText bodyEt;
    Button confirmBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        titleEt = (EditText)findViewById(R.id.title_et);
        bodyEt = (EditText)findViewById(R.id.body_et);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushNotification pushNotification = new PushNotification();
                pushNotification.sendNotification(ShopSpecificInfo.getShopInfo("shopName"), titleEt.getText().toString(), bodyEt.getText().toString());
                Intent intent = new Intent(NotificationActivity.this, DownloadCatalogActivity.class);
                startActivity(intent);
            }
        });


    }


}
