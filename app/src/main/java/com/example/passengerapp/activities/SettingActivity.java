package com.example.passengerapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passengerapp.R;
import com.example.passengerapp.models.Passenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView imgProfile;
    TextView textName;
    RelativeLayout layoutCurrentOrder, layoutHistory, layoutWallet, layoutBack;
    AppCompatButton buttonSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
        loadUserData();
        listener();
    }

    private void listener() {
        layoutCurrentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SearchForDriverActivity.isRunning && !WaitingPickUp.isRunning) {
                    Intent intent = new Intent(SettingActivity.this, SearchForDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

        layoutHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, OrderHistory.class);
                startActivity(intent);
            }
        });

        layoutWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingActivity.this, WelcomeActivity.class);
                startActivity(intent);
                Toast.makeText(SettingActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadUserData() {
        FirebaseDatabase.getInstance().getReference()
                .child("Passengers")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Passenger passenger = snapshot.getValue(Passenger.class);
                        if (passenger != null) {
                            textName.setText(passenger.getName());

                            Picasso.get().load(passenger.getPassengerImageUrl())
                                    .placeholder(R.drawable.user_blue)
                                    .into(imgProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void init() {
        imgProfile = findViewById(R.id.img_profile);
        textName = findViewById(R.id.text_userName);
        layoutCurrentOrder = findViewById(R.id.layout_currentOrder);
        layoutHistory = findViewById(R.id.layout_History);
        layoutWallet = findViewById(R.id.layout_lWallet);
        buttonSignOut = findViewById(R.id.button_sign_out);
        layoutBack = findViewById(R.id.relative_back);
    }
}