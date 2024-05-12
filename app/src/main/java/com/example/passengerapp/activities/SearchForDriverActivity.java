package com.example.passengerapp.activities;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.example.passengerapp.R;
import com.example.passengerapp.models.CurrentPosition;
import com.example.passengerapp.models.Trip;
import com.example.passengerapp.services.MyLocationServices;
import com.example.passengerapp.tools.Const;
import com.example.passengerapp.tools.Tool;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SearchForDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText edtPickUp, edtDes;
    ImageView imgVehicle;
    TextView textVehicleType, textTimeCost, textMoneyCost;
    AppCompatButton buttonCancel;

    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 123;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 234;
    public final int maxLength = 34;

    public static GoogleMap map;
    public static Marker currentLocationMarker;
    public static Circle radarCircle;
    public static String markerIconName = "your_are_here";
    public static String driverMarkerIconName = "motor_marker_icon";
    public static String motorMarker = "motor_marker_icon";
    public static String carMarker = "taxi_marker";
    public static int driverIconSize = 160;
    public static int userIconSize = 160;
    public static LatLng currentLatLng;

    MyLocationServices mLocationService;
    Intent mServiceIntent;
    LatLng UET;

    private String tripId;
    public static Trip trip;
    private FusedLocationProviderClient fusedLocationClient;

    List<Marker> listDriverMarkers;
    List<CurrentPosition> listNearByDrivers;


    static SearchForDriverActivity instance;

    public static SearchForDriverActivity getInstance() {
        return instance;
    }

    boolean statusIsUpdated = false;

    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_driver);

        init();
        listener();
    }

    private void listener() {
        buttonCancel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancelTrip();

                return false;
            }
        });
    }

    private void cancelTrip() {
        if (trip != null) {
            trip.setStatus(Const.cancelByPassenger);
            FirebaseDatabase.getInstance().getReference()
                    .child("Trips")
                    .child(trip.getId())
                    .setValue(trip)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SearchForDriverActivity.this, "Your trip has been canceled!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SearchForDriverActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SearchForDriverActivity.this, "Can not cancel your trip, try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setTripInfo() {
        edtDes.setText(trip.getDropOffName());
        edtPickUp.setText(trip.getPickUpName());
        textTimeCost.setText(trip.getTimeCost());
        textMoneyCost.setText(trip.getCost());

        if (trip.getVehicleType().equals(Const.car)) {
            imgVehicle.setImageResource(R.drawable.car);
            textVehicleType.setText("Car");
        } else {
            imgVehicle.setImageResource(R.drawable.shipper);
            textVehicleType.setText("Motorbike");
        }
    }

    private void init() {
        instance = this;

        edtDes = findViewById(R.id.edt_dropOfPos);
        edtPickUp = findViewById(R.id.edt_pickUpPos);
        textMoneyCost = findViewById(R.id.text_moneyCost);
        textTimeCost = findViewById(R.id.text_timeCost);
        textVehicleType = findViewById(R.id.text_vehicleType);
        imgVehicle = findViewById(R.id.imgVehicle);
        buttonCancel = findViewById(R.id.buttonCancel);

        edtDes.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtPickUp.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

        UET = new LatLng(21.038902482537342, 105.78296809797327); //Dai hoc Cong Nghe Lat Lng

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (intent.getStringExtra("tripId") == null) {
            getLastedTrip();
        } else {
            tripId = intent.getStringExtra("tripId");
            getTripInfo(tripId);
        }

        isRunning = true;
    }

    private void getLastedTrip() {
        List<Trip> listTrips = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Trips")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            if (trip != null)
                                if (trip.getPassengerId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                    if (!trip.getStatus().equals(Const.cancelByPassenger)
                                        && !trip.getStatus().equals(Const.cancelByDriver)
                                        && !trip.getStatus().equals(Const.success)) {
                                        listTrips.add(trip);
                                    }
                                }
                        }
                        if (listTrips.size() > 0) {
                            trip = listTrips.get(listTrips.size() - 1);
                            setTripInfo();
                            checkDriverFound();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getTripInfo(String tripId) {
        FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(tripId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trip = snapshot.getValue(Trip.class);
                        if (trip != null && !statusIsUpdated) {
                            updateTripStatus(Const.searching, trip);
                            statusIsUpdated = true;
                        }
                        setTripInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateTripStatus(String status, Trip trip) {
        trip.setStatus(status);
        FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(trip.getId())
                .setValue(trip);
    }

    public void updateLocationOnFirebase(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CurrentPosition currentPosition = new CurrentPosition(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                latLng.toString(),
                Calendar.getInstance().getTime().toString()
        );

        FirebaseDatabase.getInstance().getReference().child("CurrentPosition")
                .child("Passenger")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(currentPosition);
    }

    private void markCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermission();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (map != null) {
                        } else {
                            Toast.makeText(SearchForDriverActivity.this, "Map is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_FINE_LOCATION_REQUEST_CODE
        );

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                ACCESS_COARSE_LOCATION_REQUEST_CODE
        );
    }

    private void startServiceFunc() {
        mLocationService = new MyLocationServices();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (!isMyServiceRunning(mLocationService.getClass(), this)) {
            startService(mServiceIntent);
            Toast.makeText(this, "Service start successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopServiceFunc() {
        mLocationService = new MyLocationServices();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (isMyServiceRunning(mLocationService.getClass(), this)) {
            stopService(mServiceIntent);
            Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Activity mActivity) {
        ActivityManager manager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setRotateGesturesEnabled(false);
       
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        map.setMyLocationEnabled(true);

        startServiceFunc();
        getListDriverNearby();
    }

    private void getListDriverNearby() {
        //get list of drivers nearby
        FirebaseDatabase.getInstance().getReference().child("CurrentPosition")
                .child("Driver")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listNearByDrivers = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CurrentPosition currentPosition = dataSnapshot.getValue(CurrentPosition.class);
                            if (currentPosition != null) {
                                listNearByDrivers.add(currentPosition);
                            }
                        }

                        try {
                            markAllNearbyDriver(listNearByDrivers);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void markAllNearbyDriver(List<CurrentPosition> listNearByDrivers) throws InterruptedException {
        //clear all previous driver marker
        if (listDriverMarkers != null) {
            for (Marker marker : listDriverMarkers) {
                marker.remove();
            }
        }

        listDriverMarkers = new ArrayList<>();

        if (!(listNearByDrivers.isEmpty())) {
            for (CurrentPosition position : listNearByDrivers) {
                LatLng latLng = Tool.getLatLngFromString(position.getPosition());
                float bearing = Float.parseFloat(position.getBearing());
                if (position.getVehicleType().equals(Const.car)) {
                    driverMarkerIconName = carMarker;
                } else {
                    driverMarkerIconName = motorMarker;
                }

                Marker tempMarker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Driver")
                        .anchor(0.5f, 0.5f)
                        .rotation(bearing)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(driverMarkerIconName, driverIconSize, driverIconSize))));

                listDriverMarkers.add(tempMarker);
            }
        }
    }

    public static void updateRadarCircle(LatLng latLng, int colorId) {
        if (radarCircle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)   //set center
                    .radius(100)   //set radius in meters
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(colorId)
                    .strokeWidth(5);

            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setIntValues(0, 1000);
            valueAnimator.setDuration(3000);
            valueAnimator.setEvaluator(new IntEvaluator());
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    if (radarCircle != null)
                        radarCircle.setRadius(animatedFraction * 300);
                }
            });

            valueAnimator.start();

            radarCircle = map.addCircle(circleOptions);
        } else {
            radarCircle.setCenter(latLng);
        }
    }

    private void radarAnimation() {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(0, 1000);
        valueAnimator.setDuration(3000);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (radarCircle != null)
                    radarCircle.setRadius(animatedFraction * 300);
            }
        });

        valueAnimator.start();
    }

    private void checkDriverFound() {
        FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(trip.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trip trip = snapshot.getValue(Trip.class);
                        if (trip != null) {
                            if (!trip.getStatus().equals(Const.searching)
                                    && !trip.getStatus().equals(Const.driverFound)
                                    && !trip.getStatus().equals(Const.waitingForAccept)
                                    && !trip.getStatus().equals(Const.canceled)
                                    && !trip.getStatus().equals(Const.success)
                                    && !trip.getStatus().equals(Const.cancelByDriver)
                                    && !trip.getStatus().equals(Const.cancelByPassenger)
                                    && trip.getDriverId() != null
                                    && !WaitingPickUp.isRunning) {
                                Intent intent = new Intent(SearchForDriverActivity.getInstance(), WaitingPickUp.class);
                                intent.putExtra("tripId", trip.getId());
                                intent.putExtra("driverId", trip.getDriverId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        radarCircle = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       // stopServiceFunc();
    }

    @Override
    protected void onStop() {
        super.onStop();

        isRunning = false;
    }

}