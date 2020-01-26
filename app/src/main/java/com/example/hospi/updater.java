package com.example.hospi;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

class updater {
    private String hostel, roomno, userid, key, deletepath;
    private long chngatroot, chngatdepth;
    private colleges clg;
    private FirebaseDatabase db;
    private Activity ctx;
    private customdialog cd;
    private DialogClass dc;
    private addDialog ad;

    public updater(colleges clg, String hostel, String roomno, long chngatroot, long chngatdepth, Activity ctx, customdialog cd) {
        this.hostel = hostel;
        this.roomno = roomno;
        this.chngatroot = chngatroot;
        this.chngatdepth = chngatdepth;
        this.clg = clg;
        this.ctx = ctx;
        this.cd = cd;
    }

    public updater(String key, String hostel, String roomno, long chngatroot, long chngatdepth, Activity ctx, DialogClass dc, String dp) {
        this.key = key;
        this.hostel = hostel;
        this.roomno = roomno;
        this.chngatroot = chngatroot;
        this.chngatdepth = chngatdepth;
        this.ctx = ctx;
        this.dc = dc;
        this.deletepath = dp;
    }

    public updater(String hostel,Activity ctx,addDialog ad,long chngatroot){
        this.hostel = hostel;
        this.ad = ad;
        this.ctx = ctx;
        this.chngatroot = chngatroot;
    }

    protected void update(){
        final Map<String,Object> updatechild = new HashMap<>();
        String randomKey = generateKey();
        Log.d("info",randomKey);
        updatechild.put("Hostels/"+hostel+"/Filled",chngatroot);
        updatechild.put("Hostels/"+hostel+"/Rooms/"+roomno+"/Filled",chngatdepth);
        updatechild.put("Hostels/"+hostel+"/Rooms/"+roomno+"/"+randomKey,clg);
        db = FirebaseDatabase.getInstance();
        final DatabaseReference mref = db.getReference();
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               mref.updateChildren(updatechild).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       Toast.makeText(ctx,"Data Successfully Updated. Please refresh the window!",Toast.LENGTH_LONG).show();
                       cd.dismiss();
                       cd.refresh();
                   }
               });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ctx,"Some error occurred!",Toast.LENGTH_LONG).show();
                cd.dismiss();
                cd.refresh();
            }
        });


    }

    protected void delete() {
        final Map<String,Object> updatechild = new HashMap<>();
        updatechild.put("Hostels/"+hostel+"/Filled",chngatroot);
        updatechild.put("Hostels/"+hostel+"/Rooms/"+roomno+"/Filled",chngatdepth);
        updatechild.put(deletepath,null);

        db = FirebaseDatabase.getInstance();
        final DatabaseReference mref = db.getReference();
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mref.updateChildren(updatechild).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ctx,"Data Successfully Deleted. Please refresh the window!",Toast.LENGTH_LONG).show();
                        dc.dismiss();
                        dc.refresh();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ctx,"Some error occurred while deleting!",Toast.LENGTH_LONG).show();
                dc.dismiss();
                dc.refresh();
            }
        });
    }

    private String generateKey(){
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for(int i = 0;i<15;i++){
            sb.append((char)(97+r.nextInt(26)));
        }
        return sb.toString();
    }


    protected void addRoom(long root, String roomname){
        final Map<String,Object> updatechild = new HashMap<>();
        updatechild.put("Hostels/"+hostel+"/Capacity",chngatroot);
        updatechild.put("Hostels/"+hostel+"/Rooms/"+roomname,new Room(root));

        db = FirebaseDatabase.getInstance();
        Log.d("Name",roomname);
        final DatabaseReference mref = db.getReference();
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mref.updateChildren(updatechild).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(ctx,"Data Successfully Updated. Please refresh the window!",Toast.LENGTH_LONG).show();
                        ad.dismiss();
                        ad.refresh();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(ctx,"Some error occurred while Adding!",Toast.LENGTH_LONG).show();
                ad.dismiss();
                ad.refresh();
            }
        });

    }
}
