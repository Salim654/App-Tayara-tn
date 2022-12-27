package com.example.projectapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{
    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
     String titre=postList.get(i).getTitle();
     String description=postList.get(i).getDescription();
     String price=postList.get(i).getPrice();
     String phone=postList.get(i).getPhone();
     String imglink=postList.get(i).getImglink();


     holder.posttitre.setText(titre);
     holder.post_desc.setText(description);
     holder.post_prix.setText(price+" DT");
     holder.postphone.setText(phone);

        try{
            Picasso.get().load(imglink).into(holder.postimg);

        }catch(Exception e){

        }




    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView postimg;
        TextView posttitre,post_prix,post_desc,postphone;
        Button btndial;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            postimg=itemView.findViewById(R.id.postimg);
            posttitre=itemView.findViewById(R.id.tp);
            post_prix=itemView.findViewById(R.id.post_prix);
            post_desc=itemView.findViewById(R.id.post_desc);
            postphone=itemView.findViewById(R.id.post_phone);
            btndial=itemView.findViewById(R.id.btndial);

            btndial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone=postphone.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    context.startActivity(intent);
                }
            });


        }


    }
}
