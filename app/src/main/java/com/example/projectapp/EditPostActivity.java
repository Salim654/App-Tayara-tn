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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class EditPostActivity extends AppCompatActivity {

    private Button btnSelect, btnEdit;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseAuth mAuth;
    EditText title,prix,descr,phone;
    String imgl,idpt;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Edit Post");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        mAuth = FirebaseAuth.getInstance();

        btnSelect = findViewById(R.id.select);
        btnEdit = findViewById(R.id.editbtn);
        imageView = findViewById(R.id.imgPost);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        title = findViewById(R.id.titlep);
        descr = findViewById(R.id.desc);
        prix = findViewById(R.id.prix);
        phone = findViewById(R.id.phone);

        String titlep = getIntent().getStringExtra("title");
        String descriptionp = getIntent().getStringExtra("description");
        String pricep = getIntent().getStringExtra("price");
        String phonep = getIntent().getStringExtra("phone");
        String imglinkp = getIntent().getStringExtra("imglink");
        String postidp = getIntent().getStringExtra("postid");
        try{
            Picasso.get().load(imglinkp).into(imageView);

        }catch(Exception e){

        }
        title.setText(titlep);
        descr.setText(descriptionp);
        prix.setText(pricep);
        phone.setText(phonep);
        imgl=imglinkp;
        idpt=postidp;



        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String t1=title.getText().toString();
                String d1=descr.getText().toString();
                String p1=prix.getText().toString();
                String ph1=phone.getText().toString();
                if (TextUtils.isEmpty(t1) || TextUtils.isEmpty(d1) || TextUtils.isEmpty(p1) || TextUtils.isEmpty(ph1)) {

                    Toast.makeText(EditPostActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Loaded",Toast.LENGTH_SHORT).show();
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
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        if (filePath != null) {

            progressDialog.show();
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
                                hashMap.put("postid",idpt);
                                hashMap.put("description",d1);
                                hashMap.put("imglink",downloadUri);
                                hashMap.put("price",p1);
                                hashMap.put("phone",ph1);
                                hashMap.put("userid",uid);
                                updatepost(hashMap,idpt);
                                progressDialog.dismiss();
                            }

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(EditPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Updating " + (int)progress + "%");
                        }
                    });
        }
        else
        {
            progressDialog.show();
            final String timestamp=String.valueOf(System.currentTimeMillis());
            String t1=title.getText().toString();
            String d1=descr.getText().toString();
            String p1=prix.getText().toString();
            String ph1=phone.getText().toString() ;
            FirebaseUser user = mAuth.getCurrentUser();
            String uid=user.getUid();
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("title",t1);
            hashMap.put("postid",idpt);
            hashMap.put("description",d1);
            hashMap.put("imglink",imgl);
            hashMap.put("price",p1);
            hashMap.put("phone",ph1);
            hashMap.put("userid",uid);
            updatepost(hashMap,idpt);
            progressDialog.dismiss();
        }
    }
    public boolean onSupportNavigateUp(){

        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void updatepost(HashMap hashMap,String idp)
    {
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference reference= database.getReference("Posts");
        reference.child(idp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reference.child(idp).updateChildren(hashMap);
                Toast.makeText(EditPostActivity.this, "Post Edited!!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(EditPostActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
