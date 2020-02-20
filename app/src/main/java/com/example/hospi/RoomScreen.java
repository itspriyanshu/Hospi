package com.example.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class RoomScreen extends AppCompatActivity {

    private String Hostel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser User;
    private FirebaseDatabase db;
    protected RecyclerView rv;
    protected FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_screen);

        // Get Hostel From Intent
        Bundle bundle = getIntent().getExtras();
        if(bundle==null)finish();
        Hostel = bundle.getString("Hostel");
        //

        // Check wheather user is Authorized
        firebaseAuth = FirebaseAuth.getInstance();
        User = firebaseAuth.getCurrentUser();
        if(User==null)finish();
        //

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog ad = new addDialog(RoomScreen.this,Hostel);
                ad.show();
            }
        });

        //Start Transaction from Database
        db = FirebaseDatabase.getInstance();
        DatabaseReference hostelref = db.getReference("Hostels/"+Hostel);
        hostelref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                TextView capacity_tv = findViewById(R.id.Capacity_tv);
                TextView name_of_hostel = findViewById(R.id.Hostel_name_tv);
                TextView filled_tv = findViewById(R.id.filled_tv);
                name_of_hostel.append(Hostel);
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String Key = (String) messageSnapshot.getKey();
                    if(Key.equals("Capacity")){
                        long tc = (long)messageSnapshot.getValue();
                        capacity_tv.append(Long.toString(tc));
                    }else if(Key.equals("Filled")){
                        long fill = (long)messageSnapshot.getValue();
                        filled_tv.append(Long.toString(fill));
                    }
                }
                getRoomlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Number Of Hostels : ","0");
                Toast.makeText(getApplicationContext(),"Some error occured while loading rooms!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRoomlist(){
        DatabaseReference rooms = db.getReference("Hostels/"+Hostel+"/Rooms");
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<cfdata> rooms_list = new ArrayList<>();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String Key = (String) messageSnapshot.getKey();
                    long cap = (long)messageSnapshot.child("Capacity").getValue();
                    long fil = (long)messageSnapshot.child("Filled").getValue();
                    rooms_list.add(new cfdata(Key,fil,cap));
                }

                // Sort by number of vacants
                Collections.sort(rooms_list, new Comparator<cfdata>() {
                    @Override
                    public int compare(cfdata o1, cfdata o2) {
                        return (int)((o2.getCapacity() - o2.getFilled()) - (o1.getCapacity() - o1.getFilled()));
                    }
                });

                rv = findViewById(R.id.room_rv);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setAdapter(new adapter(rooms_list,Hostel));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Something went wrong in getting rooms",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }
}
