package com.example.hospi;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

class DialogClass extends Dialog {

    private Activity c;
    private Button deletebutton;
    private EditText teamno;
    private String path, hostel, roomno;
    private FirebaseDatabase db;
    private ArrayList<model> list;
    private long size;

    public DialogClass(Activity a, String path,ArrayList<model> list, String hostel, String roomno,long size) {
        super(a);
        this.c = a;
        this.path = path;
        this.list = list;
        this.hostel = hostel;
        this.roomno = roomno;
        this.size = size;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deletedialog);
        db = FirebaseDatabase.getInstance();
        teamno = findViewById(R.id.deletetv);
        deletebutton = findViewById(R.id.deleteButton);
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    try{
                        final int value = Integer.parseInt(teamno.getText().toString());

                        final String paths = path+"/"+list.get(value-1).getKey();
                        DatabaseReference mref = db.getReference("Hostels/"+hostel);
                        mref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long teamsize = list.get(value-1).getClg().getSize();
                                long updateatroot = (long) dataSnapshot.child("Filled").getValue();
                                long cap = (long) dataSnapshot.child("Rooms/"+roomno+"/Capacity").getValue();
                                long fill = (long) dataSnapshot.child("/Rooms/"+roomno+"/Filled").getValue();


                                if ((fill - teamsize) >= cap){
                                    // Do nothing
                                }else {
                                    long fv = fill - teamsize;
                                    updateatroot =  updateatroot - Math.min(cap - fv,teamsize);
                                }
                                updater up = new updater(list.get(value-1).getKey(), hostel,
                                        roomno, updateatroot, (fill - teamsize), c,DialogClass.this ,paths);

                                up.delete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(),"Some Error Occurred!",Toast.LENGTH_LONG).show();
                            }
                        });

                    }catch (Exception e){
                        Toast.makeText(getContext(), "Please input which falls in range!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    dismiss();
                }
            }
        });
    }

    private boolean validate(){
        int value = 0;
        try{
            value = Integer.parseInt(teamno.getText().toString());
        }catch (Exception e){
            Toast.makeText(getContext(), "Please enter a valid input", Toast.LENGTH_LONG).show();
        }
        if(value<=0 || value>size) {
            Toast.makeText(getContext(), "Please enter a valid input", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }

    }

    protected void refresh(){
        refreshclass r = new refreshclass(c);
        r.refresh();
    }
}
