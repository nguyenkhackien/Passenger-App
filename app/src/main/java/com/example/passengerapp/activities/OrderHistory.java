package com.example.passengerapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passengerapp.R;
import com.example.passengerapp.adapters.OrderAdapter;
import com.example.passengerapp.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistory extends AppCompatActivity {

    RecyclerView recyclerOrders;
    OrderAdapter orderAdapter;
    List<Trip> listTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        init();
        getListTrips();
    }

    private void getListTrips() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Trips")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listTrips.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            if (trip != null) {
                                if (trip.getPassengerId().equals(userId)) {
                                    listTrips.add(trip);
                                }
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void init() {
        recyclerOrders = findViewById(R.id.recycler_orders);
        recyclerOrders.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerOrders.setLayoutManager(linearLayoutManager);

        listTrips = new ArrayList<>();
        orderAdapter = new OrderAdapter(listTrips, this);
        recyclerOrders.setAdapter(orderAdapter);
    }
}