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
    private int size;
    private String path, hostel, roomno;
    private FirebaseDatabase db;
    private ArrayList<model> list;

    public DialogClass(Activity a, String path,ArrayList<model> list,int size, String hostel, String roomno) {
        super(a);
        this.c = a;
        this.path = path;
        this.list = list;
        this.size = size;
        this.hostel = hostel;
        this.roomno = roomno;

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
//                        final DatabaseReference mref = db.getReference(path+"/"+list.get(value-1).getKey());
//                        mref.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                mref.removeValue();
//                                Toast.makeText(getContext(), "Successfully Deleted. Please Refresh the window!", Toast.LENGTH_LONG).show();
//                                dismiss();
//                                refreshclass r = new refreshclass(c);
//                                r.refresh();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Toast.makeText(getContext(), "Some error occured while deleting the entry!", Toast.LENGTH_LONG).show();
//                                dismiss();
//                            }
//                        });
                        final String paths = path+"/"+list.get(value-1).getKey();
                        DatabaseReference mref = db.getReference("Hostels/"+hostel);
                        mref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long updateatroot = (long) dataSnapshot.child("Filled").getValue();
                                long cap = (long) dataSnapshot.child("Rooms/"+roomno+"/Capacity").getValue();
                                long fill = (long) dataSnapshot.child("/Rooms/"+roomno+"/Filled").getValue();


                                if ((fill - size) >= cap){
                                    // Do nothing
                                }else {
                                    long fv = fill - size;
                                    updateatroot =  updateatroot - Math.min(cap - fv,size);
                                }
                                updater up = new updater(list.get(value-1).getKey(), hostel,
                                        roomno, updateatroot, (fill - size), c,DialogClass.this ,paths);

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
        int value = Integer.parseInt(teamno.getText().toString());
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
