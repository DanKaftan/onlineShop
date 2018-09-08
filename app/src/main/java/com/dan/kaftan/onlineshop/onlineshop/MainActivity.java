package com.dan.kaftan.onlineshop.onlineshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(MainActivity.this, get_image.class);
        startActivity(i);
//        btn = (Button)findViewById(R.id.btn);
//        onClick();
        startService(new Intent(this, MyService.class));
    }




    private void onClick (){

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, get_image.class);
                startActivity(i);
            }
        });




    }
}
