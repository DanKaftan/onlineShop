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
        setContentView(R.layout.activity_list_view);

        final android.widget.ListView listView =(android.widget.ListView)findViewById(R.id.listView);
        DownloadCatalog.CustomAdapter customAdapter = new DownloadCatalog.CustomAdapter();

        downloadTheCatalog(listView, customAdapter);
    }

    private void downloadTheCatalog(final android.widget.ListView listView, final DownloadCatalog.CustomAdapter customAdapter) {

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
                    listView.setAdapter(customAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });

    }

    class CustomAdapter extends BaseAdapter {

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
            itemPrice.setText("מחיר:"+(items.get(i).getPrice()));
            downloadImage(ShopSpecificInfo.getShopInfo("shopBucket"), items.get(i).getItemImageFile(), imageView);

            return view;
        }
    }

    private void downloadImage(String folder, String imageName, final ImageView imageView) {

        System.out.println("going to download: " + folder + "/" +imageName);
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
}