package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

     FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    TextView res;
    List<ModelPost> postList;
    AdapterMyPosts adapterMyPosts;
    FirebaseUser user;
    String uid;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_posts, container, false);
        firebaseAuth =FirebaseAuth.getInstance();
        recyclerView= view.findViewById(R.id.myposts);
        res=view.findViewById(R.id.result);
        user=firebaseAuth.getCurrentUser();
        uid=user.getUid();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        adapterMyPosts= new AdapterMyPosts(getContext(), postList);
        recyclerView.setAdapter(adapterMyPosts);

        loadPosts();


        return view;
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                int i=0 ;
                for(DataSnapshot ds: snapshot.getChildren())
                {

                    ModelPost modelPost=ds.getValue(ModelPost.class);
                    if(modelPost.getUserid().toLowerCase().contains(uid.toLowerCase())){
                        postList.add(modelPost);
                        i++;
                    }
                    adapterMyPosts= new AdapterMyPosts(getActivity(),postList);
                    recyclerView.setAdapter(adapterMyPosts);
                    adapterMyPosts.notifyDataSetChanged();



                }
                res.setText("You Have "+i+" Post(s) ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater) {

        MenuItem item = menu.findItem(R.id.searchp);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim()))
                {
                    loadPostsneeded(s);
                }
                else
                {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim()))
                {
                    loadPostsneeded(s);
                }
                else
                {
                    loadPosts();
                }
                return false;
            }
        });


    }

    private void loadPostsneeded(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                int i=0;
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelPost modelPost=ds.getValue(ModelPost.class);
                    if(modelPost.getUserid().toLowerCase().contains(uid.toLowerCase())){


                    if(modelPost.getTitle().toLowerCase().contains(s.toLowerCase()))
                    {
                        postList.add(modelPost);
                        i++;
                    }
                    }


                    adapterMyPosts= new AdapterMyPosts(getActivity(),postList);
                    recyclerView.setAdapter(adapterMyPosts);
                    adapterMyPosts.notifyDataSetChanged();

                }
                res.setText("You Have "+i+" Post(s) ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });

    }




}