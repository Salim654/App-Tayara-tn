package com.example.projectapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMyPosts extends RecyclerView.Adapter<AdapterMyPosts.MyHolder>{
    Context context;
    List<ModelPost> postList;

    public AdapterMyPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_mypost,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        String titre=postList.get(i).getTitle();
        String description=postList.get(i).getDescription();
        String price=postList.get(i).getPrice();
        String phone=postList.get(i).getPhone();
        String imglink=postList.get(i).getImglink();
        String postid=postList.get(i).getPostid();


        holder.posttitre.setText(titre);
        holder.post_desc.setText(description);
        holder.post_prix.setText(price+" TD");
        holder.postphone.setText(phone);
        holder.btnsupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Are You sure");
                builder.setMessage("Are You sure");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                deletemypost(postid,imglink);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context.getApplicationContext(),EditPostActivity.class);
                intent.putExtra("title", titre);
                intent.putExtra("description", description);
                intent.putExtra("price", price);
                intent.putExtra("phone", phone);
                intent.putExtra("imglink", imglink);
                intent.putExtra("postid", postid);
                context.startActivity(intent);

            }
        });

        try{
            Picasso.get().load(imglink).into(holder.postimg);

        }catch(Exception e){

        }




    }

    private void deletemypost( String postid, String imglink) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query postQuery = ref.child("Posts").orderByChild("postid").equalTo(postid);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imglink);


        postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post: dataSnapshot.getChildren()) {
                    post.getRef().removeValue();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(imglink!="")
        {
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent=new Intent(context.getApplicationContext(),DashboardActivity.class);
                    Toast.makeText(context.getApplicationContext(), "Post Deleted",Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView postimg;
        TextView posttitre,post_prix,post_desc,postphone;
        Button btnedit,btnsupp;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            postimg=itemView.findViewById(R.id.postimg);
            posttitre=itemView.findViewById(R.id.tp);
            post_prix=itemView.findViewById(R.id.post_prix);
            post_desc=itemView.findViewById(R.id.post_desc);
            postphone=itemView.findViewById(R.id.post_phone);
            btnedit=itemView.findViewById(R.id.btnedit);
            btnsupp=itemView.findViewById(R.id.btnsupp);
           




        }


    }

}
