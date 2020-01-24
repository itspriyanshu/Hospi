package com.example.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser User;
    private FirebaseDatabase db;
    private static final int RC_SIGN_IN = 123;
    private GridView simpleGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("S1","App Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();


        User = firebaseAuth.getCurrentUser();
        if(User==null){
            StartSignIn();
        }
        DatabaseReference hostelref = db.getReference("Hostels");
        hostelref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long noh = dataSnapshot.getChildrenCount();
                Log.d("Number Of Hostels : ",Long.toString(noh));
                final String[] hostels = new String[(int)noh];
                int count = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String hostel_name = (String) messageSnapshot.getKey();
                    hostels[count] = hostel_name;
                    count++;
                }
                findViewById(R.id.loadingtv).setVisibility(View.GONE);
                simpleGrid = (GridView) findViewById(R.id.GridView);
                simpleGrid.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                        R.layout.hostel_name_layout,R.id.Hostel_name_screen,hostels));
                simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // set an Intent to Another Activity
                        Intent intent = new Intent(MainActivity.this, RoomScreen.class);
                        intent.putExtra("Hostel", hostels[position]); // put image data in Intent
                        startActivity(intent); // start Intent
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Toast.makeText(getApplicationContext(),"Some error occured while Loading Hostels!",Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                User = FirebaseAuth.getInstance().getCurrentUser();
                String msg = "Successfully Logged IN with User ID : " + User.getUid();
                Toast.makeText(getApplicationContext(),msg ,Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"Failed" ,Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }
    private void StartSignIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(User==null)finish();
    }
}
