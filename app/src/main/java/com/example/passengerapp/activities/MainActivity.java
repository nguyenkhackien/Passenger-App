package com.example.passengerapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.passengerapp.R;
import com.example.passengerapp.models.Passenger;
import com.example.passengerapp.tools.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    EditText edtSearchPlace;
    FrameLayout btnYourLocation, btnHistory, btnCar, btnMotor, btnWallet, btnSettings;
    CircleImageView imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColor();
        init();
        getPassengerInfo();
        listener();

    }

    private void listener() {

        edtSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseDestinationActivity.class);
                startActivity(intent);
            }
        });

        btnYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SearchForDriverActivity.isRunning && !WaitingPickUp.isRunning) {
                    Intent intent = new Intent(MainActivity.this, SearchForDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderHistory.class);
                startActivity(intent);
            }
        });

        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseDestinationActivity.class);
                intent.putExtra("vehicleType", Const.car);
                startActivity(intent);
            }
        });

        btnMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseDestinationActivity.class);
                intent.putExtra("vehicleType", Const.motor);
                startActivity(intent);
            }
        });

        imageProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        edtSearchPlace = findViewById(R.id.edtSearch);
        btnYourLocation = findViewById(R.id.buttonYourLocation);
        btnHistory = findViewById(R.id.buttonHistory);
        btnCar = findViewById(R.id.buttonCar);
        btnMotor = findViewById(R.id.buttonMotor);
        btnWallet = findViewById(R.id.buttonWallet);
        btnSettings = findViewById(R.id.buttonSettings);
        imageProfile = findViewById(R.id.img_profile);
    }

    private void getPassengerInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("Passengers")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Passenger passenger = snapshot.getValue(Passenger.class);
                        if (passenger != null) {
                            Picasso.get().load(passenger.getPassengerImageUrl())
                                    .resize(1000, 1000)
                                    .centerCrop()
                                    .into(imageProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue_toolbar));
    }
}