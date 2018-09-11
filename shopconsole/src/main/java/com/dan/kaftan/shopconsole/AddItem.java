package com.dan.kaftan.shopconsole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class AddItem extends AppCompatActivity {

    EditText nameEt;
    EditText priceEt;
    Button uploadImageBtn;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        nameEt = (EditText) findViewById(R.id.name_et);
    }
}
