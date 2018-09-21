package com.dan.kaftan.common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DownloadCatalogActivity extends AppCompatActivity {

    // init firebase storage
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReferenceFromUrl("gs://online-shop-a32c0.appspot.com");

    // list of items to fill from the catalog
    static ArrayList<Item> items = new ArrayList<Item>();

    static Bitmap tmpBitmap;

    Button editBtn;
    Button addBtn;
    Boolean isEdited;
    boolean isWantToDelete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        final android.widget.ListView listView =(android.widget.ListView)findViewById(R.id.listView);
        DownloadCatalogActivity.CustomAdapter customAdapter = new DownloadCatalogActivity.CustomAdapter();
        Intent intent = getIntent();
        isEdited = (intent != null && intent.getBooleanExtra("fromEditItem",false));
        addBtn = (Button)findViewById(R.id.add_item_btn);

        if (!isEdited){
            // start it as a service so it always run in background and gets notifications
            startService(new Intent(this, MyService.class));

            // init specific shop params
            ShopSpecificInfo.setShopInfo("shopBucket", "DemoShop");
            ShopSpecificInfo.setShopInfo("catalogFile","catalog.txt");

            downloadTheCatalog(listView, customAdapter);
        } else {
            downloadTheCatalog(listView, customAdapter);
            listView.setAdapter(customAdapter);
        }
    }

    private void downloadTheCatalog(final android.widget.ListView listView, final DownloadCatalogActivity.CustomAdapter customAdapter) {

        // reset items list
        items.clear();
        // get catalog file from storage
        storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket") +"/"+ ShopSpecificInfo.getShopInfo("catalogFile")).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            // once loaded - create item list out of it and start next activity
            @Override
            public void onSuccess(byte[] bytes) {
                try {

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    // walk through file items
                    int i = 0;
                    while (true) {
                        // create new item
                        Item item = new Item();
                        try {
                            //fill it from catalog file lines
                            item.setName(bufferedReader.readLine().trim());
                            item.setPrice(bufferedReader.readLine().trim());
                            item.setItemImageFile(bufferedReader.readLine().trim());
                            item.setId(i=i+100);
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

            final ImageView imageView = (ImageView)view.findViewById(R.id.item_iv);
            TextView itemName = (TextView)view.findViewById(R.id.tv_name);
            TextView itemPrice = (TextView)view.findViewById(R.id.tv_price);
            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn) ;
            editBtn = (Button)view.findViewById(R.id.edit_btn);
            TextView idTv = (TextView)view.findViewById(R.id.id_tv);

            Item item = items.get(i);
            itemName.setText(item.getName());
            itemPrice.setText("מחיר:"+(item.getPrice()));
            idTv.setText(Integer.valueOf(item.getId()).toString());

            downloadImage(ShopSpecificInfo.getShopInfo("shopBucket"), item.getItemImageFile(), imageView);

            if ("TRUE".equals(ShopSpecificInfo.getShopInfo("isConsole"))){

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View parent = (View)view.getParent();
                        TextView itemId = (TextView)parent.findViewById(R.id.id_tv);
                        ImageView imageView1 = (ImageView)parent.findViewById(R.id.item_iv);
                        imageView1.setDrawingCacheEnabled(true);
                        imageView1.buildDrawingCache(true);
                        Bitmap bitmap = Bitmap.createBitmap(imageView1.getDrawingCache());
                        imageView.setDrawingCacheEnabled(false);
                        Intent intent = new Intent(DownloadCatalogActivity.this, EditItem.class);
                        System.out.println(itemId.getText().toString());
                        intent.putExtra("id",Integer.parseInt(itemId.getText().toString()));
//                        intent.putExtra("bitmap",bitmap);
                        tmpBitmap = bitmap;
                        System.out.println("moved to edit item");
                        startActivity(intent);

                    }
                });


                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        View parent = (View) view.getParent();
                        TextView itemId = (TextView) parent.findViewById(R.id.id_tv);
                        final int id = Integer.valueOf(itemId.getText().toString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(DownloadCatalogActivity.this);
                        builder.setMessage("האם אתה בטוח שברצונך למחוק את הפריט?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // loop on item list look for item with same name.
                                for (int a = 0; a < items.size(); a++) {

                                    if (items.get(a).getId() == id) {
                                        System.out.println(items.get(a));
                                        items.remove(a);
                                        break;
                                    }
                                }

                                uploadCatalogToStorage();
                                notifyDataSetChanged();

                            }
                        });
                        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
            } else {
                deleteBtn.setVisibility(View.INVISIBLE);
                editBtn.setVisibility(View.INVISIBLE);
                addBtn.setVisibility(View.INVISIBLE);


            }

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

    // find item in items list by id

    static public Item findItem(int id) {
        Item item = null;
        for (int i = 0;i<items.size();i++){
            if (items.get(i).getId()==id){
                item=items.get(i);
                return item;
            }
        }
        return item;
    }

    static public int findItemIndex(int id) {
        for (int i = 0;i<items.size();i++){
            if (items.get(i).getId()==id){
                return i;
            }
        }
        return -1;
    }

    public void onAddBtnClick(View view){
        Intent intent = new Intent(DownloadCatalogActivity.this, EditItem.class);
        Boolean isAdd = true;
        intent.putExtra("isAdd", isAdd);
        startActivity(intent);

    }
    public void setCategoryToFile(){

        File catalogFile = new File("catalog.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("catalog.txt"));
            for (int i = 0; i < items.size(); i++){
                writer.write(items.get(i).getName());
                writer.write(items.get(i).getPrice());
                writer.write(items.get(i).getItemImageFile());
                writer.write("-----------------------");

            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    static public void uploadCatalogToStorage(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        byte [] catalog = DownloadCatalogActivity.buildCatalogFile();
        {

            String fileName = "catalog.txt";

            StorageReference ref = storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket")+"/" + fileName);
            ref.putBytes(catalog)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {



                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Success uploading catalog");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failure uploading catalog");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        }
                    });
        }
    }

    static public byte [] buildCatalogFile(){

        File catalogFile = new File("catalog.txt");
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            for (int i = 0; i < items.size(); i++){
                writer.write(items.get(i).getName());
                writer.newLine();
                writer.write(items.get(i).getPrice());
                writer.newLine();
                writer.write(items.get(i).getItemImageFile());
                writer.newLine();
                writer.write("-----------------------");
                writer.newLine();
            }

            writer.flush();
            writer.close();
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadFileToFireBase(){


        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        StorageReference riversRef = storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket")+ "/"+ ShopSpecificInfo.getShopInfo("catalogFile"));

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
}
