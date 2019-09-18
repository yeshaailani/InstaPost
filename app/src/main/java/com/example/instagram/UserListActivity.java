package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ListView listView;
    FirebaseDatabase database;
    ArrayList<User> list;
    Menu menu;
    User user;
    //ArrayAdapter<String> adapter;
    UserListAdapter userlistadapter;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        listView = (ListView) findViewById(R.id.userlist);
        list=new ArrayList<>();
        user=new User();
        database=FirebaseDatabase.getInstance();
        //adapter = new ArrayAdapter<String>(UserListActivity.this, R.layout.user_info,R.id.userinfo,list);
        //listView.setAdapter(adapter);
        userlistadapter=new UserListAdapter(this,R.layout.user_info,list);
        mDatabaseRef=database.getReference("User");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                        user=ds.getValue(User.class);
                        list.add(user);
                }
                listView.setAdapter(userlistadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u=list.get(position);
                String email=u.getEmail();
               
                Bundle bundle=new Bundle();
                bundle.putString("EMAIL",email);
                Intent intent = new Intent(UserListActivity.this, UserListImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }



}
