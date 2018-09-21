package com.dan.kaftan.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.DOMError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class EditItem extends AppCompatActivity {

    EditText newNameEt;
    EditText newPriceEt;
    TextView idTv;
    Button newImageBtn;
    Button btnConfirm;
    String newName;
    String newPrice;
    Item item1;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageRef;
    String fileName = null;
    Bitmap bitmap = null;
    Intent intent = null;
    Boolean isAdd = false;
    Spinner spinner;
    TextView subTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        intent = getIntent();
        int itemId = 0;
        isAdd =  intent.getBooleanExtra("isAdd",false);
//        Bitmap bitmap = intent.getParcelableExtra("bitmap");

        Bitmap tmpBitmap = DownloadCatalogActivity.tmpBitmap;
        newNameEt = (EditText)findViewById(R.id.name_et);
        newPriceEt = (EditText)findViewById(R.id.price_et);
        newImageBtn= (Button) findViewById(R.id.new_image_btn);
        btnConfirm= (Button) findViewById(R.id.btn_confirm);
        imageView = (ImageView)findViewById(R.id.imageView);
        idTv = (TextView)findViewById(R.id.tv_id);
        subTv = (TextView)findViewById(R.id.textView);

        if (isAdd){
            subTv.setText("הוספת מוצר");
        }

        // relevant for edit only, not for add
        if(!isAdd){
            itemId = intent.getIntExtra("id",0);
            idTv.setText(Integer.toString(itemId));
            final Item item = DownloadCatalogActivity.findItem(itemId);
            System.out.println("Found: "+ item);
            newNameEt.setText(item.getName());
            newPriceEt.setText(item.getPrice());
            imageView.setImageBitmap(tmpBitmap);
        }

        setSpinner(itemId);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageView==null|| (isAdd && bitmap == null) || "".equals(newNameEt.getText().toString())|| "".equals(newPriceEt.getText().toString())){
                    Toast toast=Toast.makeText(getApplicationContext(),"אינך יכול לפרסם מוצר ללא שם מחיר ותמונה",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                View parent = (View)view.getParent();
                TextView itemId = (TextView)parent.findViewById(R.id.tv_id);
                TextView price = (TextView)parent.findViewById(R.id.price_et);
                TextView name = (TextView)parent.findViewById(R.id.name_et);
                Spinner locationSpinner = (Spinner) findViewById(R.id.spinner);
                int location = Integer.valueOf(locationSpinner.getSelectedItem().toString());

                uploadImageToStorage();

                if (isAdd){

                    Item newItem = new Item();

                    // find existing highest id to create a higher one for the new item
                    int highestId = 0;
                    for (int i = 0; i < DownloadCatalogActivity.items.size(); i ++){

                        if (DownloadCatalogActivity.items.get(i).getId() > highestId){
                            highestId = DownloadCatalogActivity.items.get(i).getId();
                        }
                    }
                    newItem.setId(highestId+1);
                    newItem.setPrice(price.getText().toString());
                    newItem.setName(name.getText().toString());
                    newItem.setItemImageFile(fileName);

                    DownloadCatalogActivity.items.add(location-1, newItem);
                }
                // Edit
                else {
                    // extract item id
                    int id = Integer.parseInt(itemId.getText().toString());

                    // find it in list of items
                    Item editItem = DownloadCatalogActivity.findItem(id);
                    // update new values
                    editItem.setName(name.getText().toString());
                    editItem.setPrice(price.getText().toString());
                    if (fileName!=null){
                        editItem.setItemImageFile(fileName);
                    }
                    // remove from list
                    DownloadCatalogActivity.items.remove(DownloadCatalogActivity.findItemIndex(id));
                    // and add in selected location
                    DownloadCatalogActivity.items.add(location-1, editItem);
                }

                // build and upload the catalog to fire base
                // and go back to list view
                uploadCatalogToStorage();
            }
        });
    }


    public void onNewImgeBtnClick(View v){

        chooseImage();



    }
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToStorage(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            fileName = UUID.randomUUID().toString() + ".jpg";


            StorageReference ref = storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket")+"/" + fileName);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {



                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

    }

    private void uploadCatalogToStorage(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        byte [] catalog = DownloadCatalogActivity.buildCatalogFile();
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            fileName = "catalog.txt";

            StorageReference ref = storageRef.child(ShopSpecificInfo.getShopInfo("shopBucket")+"/" + fileName);
            ref.putBytes(catalog)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {



                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            // move back to list view
                            Intent intent = new Intent(EditItem.this,DownloadCatalogActivity.class);
                            boolean fromEditItem = true;
                            intent.putExtra("fromEditItem",fromEditItem);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

    }

    public void setSpinner(int itemId){
        ArrayList <Integer> arraySpinner = new ArrayList(DownloadCatalogActivity.items.size());

        int numberOfItemds = DownloadCatalogActivity.items.size();
        if (isAdd){
            numberOfItemds++;
        }
        for (int i = 0; i < numberOfItemds; i++){
            arraySpinner.add(i + 1);
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(DownloadCatalogActivity.findItemIndex(itemId));
        System.out.println("spinner");
    }
}
