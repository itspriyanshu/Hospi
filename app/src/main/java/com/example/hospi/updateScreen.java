package com.example.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class updateScreen extends AppCompatActivity implements View.OnClickListener, refreshable{

    private String hostel;
    private String roomno;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser User;
    private FirebaseDatabase db;
    private RecyclerView rv;
    private Button add,delete;
    private ArrayList<model> clgs;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_screen);

        Bundle bundle = getIntent().getExtras();
        hostel = bundle.getString("hostelname");
        roomno = bundle.getString("roomno");


        User = FirebaseAuth.getInstance().getCurrentUser();

        if(User==null)finish();

        TextView noteammsg = findViewById(R.id.noteamtext);
        noteammsg.setVisibility(View.VISIBLE);
        db = FirebaseDatabase.getInstance();

        add = findViewById(R.id.addButton);
        delete = findViewById(R.id.deleteButton);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);


        DatabaseReference mref = db.getReference("Hostels/"+hostel+"/Rooms/"+roomno);
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount() - 2L;
                size = (int)count + 2;
                Log.i("Hao",Long.toString(count));
                if(count<=0){
                    findViewById(R.id.noteamtext).setVisibility(View.VISIBLE);
                }else{
                    //pushdata();
                    findViewById(R.id.noteamtext).setVisibility(View.GONE);
                    clgs = new ArrayList<>();
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        String Key = (String) messageSnapshot.getKey();

                        if(!Key.equals("Capacity") && !Key.equals("Filled")){
                            colleges clg = messageSnapshot.getValue(colleges.class);
                           // Log.i("Team",clg.getNumber());
                            clgs.add(new model(clg,Key));
                        }
                    }
                    rv = findViewById(R.id.teams_window_rv);
                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv.setAdapter(new adapterteam(clgs));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.addButton : {
                customdialog cdd=new customdialog(updateScreen.this,hostel,roomno);
                cdd.show();
            }break;
            case R.id.deleteButton :{
                String path = "Hostels/"+hostel+"/Rooms/"+roomno;
                DialogClass cdd=new DialogClass(updateScreen.this, path, clgs,hostel,roomno,size);
                cdd.show();
            }break;
        }
    }

    @Override
    public void refresh() {
        this.recreate();
    }
}
