package com.example.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
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
    private final String CHANNEL_ID = "45";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("S1","App Started");
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Welcome")
                .setContentText("Thanks for Selecting this app to use")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("We hope that you will be going to use this app. Go ahead and keep posting about this."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(4, builder.build());


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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
