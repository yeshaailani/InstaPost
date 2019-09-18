package com.example.instagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

public class HashTagListActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayadapter;
    FirebaseDatabase database;
    ArrayList<String> list1;
    Menu menu;
    String hash;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tag_list);
        listView = (ListView) findViewById(R.id.hashtagList);
        list1=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        arrayadapter=new ArrayAdapter<String>(this,R.layout.hashtag_info,R.id.hashtaginfo,list1);

        mDatabaseRef=database.getReference("hashtaging");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                  String  hash = String.valueOf(ds.getValue());
                    list1.add(hash);
                }
                listView.setAdapter(arrayadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tag=list1.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("HASHTAG",tag);
                Log.i("YR","HTLA"+tag);
                Intent intent = new Intent(HashTagListActivity.this, HashTagListImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }



}
