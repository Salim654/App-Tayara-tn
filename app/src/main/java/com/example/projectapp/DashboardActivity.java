package com.example.projectapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth= FirebaseAuth.getInstance();
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Tayara");
        HomeFragment fragment1= new HomeFragment();
        FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();


        BottomNavigationView navigationView=(BottomNavigationView)findViewById(R.id.navbar);
        navigationView.setOnItemSelectedListener(item -> {
            // do stuff
            switch (item.getItemId()) {
                case R.id.nav_home:
                    actionBar.setTitle("Tayara");
                    HomeFragment fragment= new HomeFragment();
                    FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content,fragment,"");
                    ft.commit();
                    return true;
                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragment2= new ProfileFragment();
                    FragmentTransaction ft2= getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,fragment2,"");
                    ft2.commit();
                    return true;
                case R.id.nav_posts:
                    actionBar.setTitle("MyPosts");
                    PostsFragment fragment3= new PostsFragment();
                    FragmentTransaction ft3= getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,fragment3,"");
                    ft3.commit();
                    return true;

            }
            return true;
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_nav1, menu);

        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(DashboardActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.addpost:


                Intent intent1=new Intent(DashboardActivity.this, AddPostActivity.class);
                startActivity(intent1);
                return true;
        }
        return false;
    }

}