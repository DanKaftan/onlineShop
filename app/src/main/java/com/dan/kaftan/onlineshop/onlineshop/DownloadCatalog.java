package com.dan.kaftan.onlineshop.onlineshop;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DownloadCatalog extends AppCompatActivity {


    // init firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://online-shop-a32c0.appspot.com");

    // list of items to fill from the catalog
    ArrayList<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // start it as a service so it always run in background and gets notifications
        startService(new Intent(this, MyService.class));

        // init specific shop params
        ShopSpecificInfo.setShopInfo("shopBucket", "DemoShop");
        ShopSpecificInfo.setShopInfo("catalogFile","catalog.txt");
        setContentView(R.layout.activity_get_image);

        downloadTheCatalog();
    }

    private void downloadTheCatalog() {

        // get catalog file from storage
        storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket") +"/"+ ShopSpecificInfo.getShopInfo("catalogFile")).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            // once loaded - create item list out of it and start next activity
            @Override
            public void onSuccess(byte[] bytes) {
                try {

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    // walk through file items
                    while (true) {
                        // create new item
                        Item item = new Item();
                        try {
                            //fill it from catalog file lines
                            item.setName(bufferedReader.readLine().trim());
                            item.setPrice(bufferedReader.readLine().trim());
                            item.setItemImageFile(bufferedReader.readLine().trim());
                            System.out.println(item.toString());

                            // add it to the list of items
                            items.add(item);

                            // if reached end of file --> BREAK
                            if(bufferedReader.readLine()==null){
                                break;
                            }
                        } catch (NullPointerException e) {
                            bufferedReader.close();
                            break;
                        }
                    }

                    // now that we have the list of items from the catalog, pass it on to the next activity
                    Intent i = new Intent(DownloadCatalog.this, com.dan.kaftan.onlineshop.onlineshop.ListView.class);
                    i.putExtra("items", items);
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });

    }
}