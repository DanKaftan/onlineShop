package com.dan.kaftan.onlineshop.onlineshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListView extends AppCompatActivity {

    ArrayList<Item> items;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://online-shop-a32c0.appspot.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        setItemsList();

        android.widget.ListView listView =(android.widget.ListView)findViewById(R.id.listView);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }


    class CustomAdapter extends BaseAdapter {

//check111111
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView)view.findViewById(R.id.item_iv);
            TextView itemName = (TextView)view.findViewById(R.id.tv_name);
            TextView itemPrice = (TextView)view.findViewById(R.id.tv_price);

            itemName.setText(items.get(i).getName());
            itemPrice.setText("מחיר:"+Integer.toString(items.get(i).getPrice()));
            downloadImage("items_images", items.get(i).getItemImageFile(), imageView);

            return view;
        }
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
    private void setItemsList (){

        Intent i = getIntent();
        items  = (ArrayList<Item>) i.getSerializableExtra("items");

    }
}
