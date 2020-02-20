package com.example.hospi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

class addDialog extends Dialog {

    private String hostel;
    private Activity activity;
    private EditText roomname, roomcap, password;
    private Button add;
    private FirebaseDatabase db;
    private ProgressBar pb;

    public addDialog(Activity a, String hostel) {
        super(a);
        this.activity = a;
        this.hostel = hostel;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addroomlayout);
        roomname = findViewById(R.id.roomnametv);
        roomcap = findViewById(R.id.caproomtv);
        password = findViewById(R.id.password);
        add = findViewById(R.id.addButton);

        db = FirebaseDatabase.getInstance();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    add.setVisibility(View.GONE);
                    DatabaseReference mref = db.getReference("Password");
                    mref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String passcode = (String)dataSnapshot.getValue();
                            String passinput = password.getText().toString();
                            if(passcode.equals(passinput)){

                               // Toast.makeText(activity,"Password is correct!",Toast.LENGTH_LONG).show();
                                //String hostel,Activity ctx,addDialog ad,long chngatroot

                                final DatabaseReference nref = db.getReference("Hostels/"+hostel);
                                nref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        long updateatroot = (long) dataSnapshot.child("Capacity").getValue();
                                        long cap = 0;
                                        String name = "";
                                        try{
                                            cap = Long.parseLong(roomcap.getText().toString());
                                            name = roomname.getText().toString();
                                            if(!dataSnapshot.child("Rooms").hasChild(name)){
                                                updater up = new updater(hostel,activity,addDialog.this,(updateatroot+cap));
                                                up.addRoom(cap,name);
                                            }else{
                                                Toast.makeText(getContext(),"Room Already Exist!",Toast.LENGTH_LONG).show();
                                                add.setVisibility(View.VISIBLE);
                                            }

                                        }catch (Exception e){
                                            Toast.makeText(getContext(),"Please Input Correct Data!",Toast.LENGTH_LONG).show();
                                            add.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(),"Some Error Occurred!",Toast.LENGTH_LONG).show();
                                        add.setVisibility(View.VISIBLE);
                                    }
                                });
                            }else{
                                Toast.makeText(activity,"Password is not correct!",Toast.LENGTH_LONG).show();
                                add.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(activity,"Database Connectivity Error!",Toast.LENGTH_LONG).show();
                            add.setVisibility(View.VISIBLE);
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(activity,"Some Error Occurred!",Toast.LENGTH_LONG).show();
                    add.setVisibility(View.VISIBLE);
                    dismiss();
                }
            }
        });
    }

    protected void refresh(){
        refreshclass r = new refreshclass(activity);
        r.refresh();
    }
}
