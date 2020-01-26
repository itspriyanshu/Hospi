package com.example.hospi;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

class customdialog extends Dialog {

    private Activity c;
    private Button add;
    private EditText teamname, leadername, leadernumber, teamsize;
    final private String hostel, room;
    private FirebaseDatabase db;

    public customdialog(Activity a,String hostel, String room) {
        super(a);
        this.c = a;
        this.hostel = hostel;
        this.room = room;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adddialog);
        db = FirebaseDatabase.getInstance();
        teamname = findViewById(R.id.clgnametv);
        leadername = findViewById(R.id.leadernametv);
        leadernumber = findViewById(R.id.leadernotv);
        teamsize = findViewById(R.id.sizeteamtv);
        add = findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    final String tn = teamname.getText().toString();
                    final String ln = leadername.getText().toString();
                    final String num = leadernumber.getText().toString();
                    final long size = Long.parseLong(teamsize.getText().toString());
                    DatabaseReference mref = db.getReference("Hostels/"+hostel);
                    mref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long updateatroot = (long) dataSnapshot.child("Filled").getValue();
                            long cap = (long) dataSnapshot.child("Rooms/"+room+"/Capacity").getValue();
                            long fill = (long) dataSnapshot.child("/Rooms/"+room+"/Filled").getValue();


                            if(fill>=cap){
                                //Update the same at the root
                            }else{
                                updateatroot = Math.min(cap-fill,size) + updateatroot;
                            }

                            updater up = new updater(new colleges(tn,ln,num,size), hostel,
                                    room, updateatroot, (fill + size), c, customdialog.this);

                            up.update();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(),"Some Error Occurred!",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(),"Please input correct data!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validate(){
        String tn = teamname.getText().toString();
        String ln = leadername.getText().toString();
        String num = leadernumber.getText().toString();
        String size = teamsize.getText().toString();

        boolean isvalid = true;
        try{
            int len = Integer.parseInt(size);
            if(len==0 || len>100){
                isvalid = false;
            }
        }catch (Exception e){
            isvalid = false;
        }
        if(num.length()!=10)isvalid = false;
        else{
            for(int i=0;i<10;i++){
                if(num.charAt(i)<'0' || num.charAt(i)>'9')isvalid = false;
            }
        }
        if(tn.equals("") || ln.equals(""))isvalid = false;

        return isvalid;

    }

    protected void refresh(){
        refreshclass r = new refreshclass(c);
        r.refresh();
    }
}
