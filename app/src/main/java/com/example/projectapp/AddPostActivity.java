package com.example.projectapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    private Button btnSelect, btnUpload;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseAuth mAuth;
    EditText title,prix,descr,phone;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Add Post");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        mAuth = FirebaseAuth.getInstance();

        btnSelect = findViewById(R.id.select);
        btnUpload = findViewById(R.id.upbtn);
        imageView = findViewById(R.id.imgPost);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        title = findViewById(R.id.titlep);
        descr = findViewById(R.id.desc);
        prix = findViewById(R.id.prix);
        phone = findViewById(R.id.phone);




        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String t1=title.getText().toString();
                String d1=descr.getText().toString();
                String p1=prix.getText().toString();
                String ph1=phone.getText().toString() ;
                if (TextUtils.isEmpty(t1) || TextUtils.isEmpty(d1) || TextUtils.isEmpty(p1) || TextUtils.isEmpty(ph1)) {

                    Toast.makeText(AddPostActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }


            }
        });
    }

    private void SelectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage()
    {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());


            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    String downloadUri =uriTask.getResult().toString();
                                    if(uriTask.isSuccessful())
                                    {
                                        final String timestamp=String.valueOf(System.currentTimeMillis());
                                        String t1=title.getText().toString();
                                        String d1=descr.getText().toString();
                                        String p1=prix.getText().toString();
                                        String ph1=phone.getText().toString() ;
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid=user.getUid();
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("title",t1);
                                        hashMap.put("postid",timestamp);
                                        hashMap.put("description",d1);
                                        hashMap.put("imglink",downloadUri);
                                        hashMap.put("price",p1);
                                        hashMap.put("phone",ph1);
                                        hashMap.put("userid",uid);
                                        uploadpost(hashMap,timestamp);
                                        progressDialog.dismiss();
                                    }

                                    Toast.makeText(AddPostActivity.this, "Post Uploaded!!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(AddPostActivity.this,DashboardActivity.class);
                                    startActivity(intent);

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }
    public boolean onSupportNavigateUp(){

        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void uploadpost(HashMap hashMap,String idp)
    {



        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference reference= database.getReference("Posts");
        reference.child(idp).setValue(hashMap);
    }
}
