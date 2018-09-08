package com.dan.kaftan.onlineshop.onlineshop;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class get_image extends AppCompatActivity {

    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView [] imageViews = new ImageView[4];
    TextView tv;
    String imageNamesFile = "items_details_heb.txt";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://online-shop-a32c0.appspot.com");
    ArrayList<Item> items = new ArrayList<Item>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);




        downloadImages();
    }

    private void downloadImages(){


        storageRef.child(imageNamesFile).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

                    while (true) {
                        String line;
                        try {
                            line = bufferedReader.readLine();
                            if (line == null){
                                break;
                            }
                            System.out.println("line: " + line);
                        } catch (NullPointerException e) {
                            bufferedReader.close();
                            break;
                        }

                        Item item = createItem(line);
                    }

                    Intent i = new Intent(get_image.this, com.dan.kaftan.onlineshop.onlineshop.ListView.class);
                    i.putExtra("items",items);
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });

    }


    private void downloadImage(String folder, String imageName, final ImageView imageView) {

        storageRef.child(folder + "/" +imageName).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                try {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private Item createItem (String line){

        String [] lineSplit =line.split(",");

        Item item = new Item();

        System.out.println ("0: " + lineSplit[0]);
        item.setName(lineSplit[0]);

        System.out.println ("1: " + lineSplit[1]);
        item.setPrice(Integer.valueOf(lineSplit[1]));

        System.out.println ("2: " + lineSplit[2]);
        item.setItemImageFile(lineSplit[2].trim());

        items.add(item);
        return item;

    }


}