package com.example.passengerapp.activities;

import static com.example.passengerapp.activities.WalletActivity.getTotalBalance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.passengerapp.R;
import com.example.passengerapp.models.CurrentPosition;
import com.example.passengerapp.models.Driver;
import com.example.passengerapp.models.Transaction;
import com.example.passengerapp.models.Trip;
import com.example.passengerapp.models.Vehicle;
import com.example.passengerapp.tools.Const;
import com.example.passengerapp.tools.Tool;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitingPickUp extends AppCompatActivity implements OnMapReadyCallback {

    RelativeLayout layoutStatusUpdate, layoutBottom;
    TextView textTimeLeft, textDistanceLeft, textPlate, textVehicleName,
            textDriverName, textStatus, textMoney, textPaymentMethod;
    CircleImageView imgFocus, imgDriver;
    RelativeLayout btnCall, btnMessage;
    AppCompatButton btnCancel;
    CircleImageView buttonFocus;
    EditText edtPickUp, edtDropOff;
    public static ProgressDialog transferProgressDialog;

    boolean infoIsHided = true;

    GoogleMap maps;
    private FusedLocationProviderClient fusedLocationClient;

    public static Driver driver;
    public static Trip trip;
    public static CurrentPosition currentPosition;
    public static Vehicle vehicle;

    public static String markerIconName = "motor_marker_icon";
    public static final String taxiMarker = "taxi_marker";
    public static final String motorMarker = "motor_marker_icon";
    private static final String pickUpMarkerName = "pick_up_marker";
    private static final String desMarkerName = "flag";
    public static int driverMarkerSize = 160;
    public static int locationMarkerSize = 120;
    public static int zoomToDriver = 17;
    public static float polyWidth = 14;

    public static Marker driverMarker;
    public static String MAPS_API_KEY;
    public static String GOONG_API_KEY;
    public static ProgressDialog progressDialog;

    boolean polylineIsDrawn = false;
    boolean keyIsLoaded = false;
    boolean focusOnDriver = true;
    boolean pickUpPolyIsDrawn = false, dropOffPolyIsDrawn = false;

    public static boolean isRunning = false;

    public static Polyline pickUpPolyline, dropOffPolyline;

    int pickUpPolyOption = 0, dropOffPolyOption = 1;

    boolean pickUpAndDropOffIsMarked = false;

    private final int CALL_REQUEST_CODE = 123;
    private final int SMS_REQUEST_CODE = 234;

    boolean movedToSearching = false, movedToReview = false;
    boolean passengerArrivedToDropOff = false, cancelable = true, onlinePayment = false;
    static String currentWalletBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_pick_up);

        init();
        loadCurrentApiKey();
        hideInfo();
        listener();
        calculateLastBalance();
    }

    public void zoomToDriver() {
        if (currentPosition != null) {
            maps.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    Tool.getLatLngFromString(currentPosition.getPosition())
                    , zoomToDriver));
        }
    }

    public void zoomToPickUpRoute() {
        LatLng destination = Tool.getLatLngFromString(trip.getPickUpLocation());
        LatLng origin = Tool.getLatLngFromString(currentPosition.getPosition());

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(destination)
                .include(origin).build();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        maps.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 800, 250));
    }

    public void zoomToRoute(LatLng origin, LatLng dest) {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(dest)
                .include(origin).build();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        maps.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 800, 250));
    }

    private void updateDriverLocation(CurrentPosition currentPosition) {
        LatLng latLng = Tool.getLatLngFromString(currentPosition.getPosition());

        if (driverMarker != null) {
            driverMarker.remove();
        }
        driverMarker = maps.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Driver")
                .anchor(0.5f, 0.5f)
                .rotation(Float.parseFloat(currentPosition.getBearing()))
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(markerIconName, driverMarkerSize, driverMarkerSize))));

        if (focusOnDriver) {
            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomToDriver));
        }
    }

    private void markPickUpAndDropOff(Trip trip) {
        LatLng pickup = Tool.getLatLngFromString(trip.getPickUpLocation());
        LatLng dropOff = Tool.getLatLngFromString(trip.getDropOffLocation());

        maps.addMarker(new MarkerOptions()
                .position(pickup)
                .title("Pick-up")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(pickUpMarkerName, locationMarkerSize, locationMarkerSize))));

        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(pickup, zoomToDriver));

        maps.addMarker(new MarkerOptions()
                .position(dropOff)
                .title("Drop-off")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(desMarkerName, locationMarkerSize, locationMarkerSize))));

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void getDataFromFirebase() {
        Intent intent = getIntent();
        String tripId = intent.getStringExtra("tripId");
        String driverId = intent.getStringExtra("driverId");
        
        getTripInfo(tripId);
        getDriverInfo(driverId);
        getStatusUpdate(driverId);
    }

    private void getStatusUpdate(String driverId) {
        if (driverId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("CurrentPosition")
                    .child("Driver")
                    .child(driverId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currentPosition = snapshot.getValue(CurrentPosition.class);
                            if (currentPosition != null) {
                                setStatusView();
                                updateDriverLocation(currentPosition);

                                //draw a polyline between driver and pick-up point
                                if (trip != null && !pickUpPolyIsDrawn && !dropOffPolyIsDrawn) {
                                    pickUpPolyIsDrawn = true;
                                    LatLng origin = Tool.getLatLngFromString(currentPosition.getPosition());
                                    LatLng des = Tool.getLatLngFromString(trip.getPickUpLocation());
                                    try {
                                        direction(origin, des, pickUpPolyOption);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setStatusView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (currentPosition == null) {

                }
                while (trip == null) {

                }

                while (currentPosition.getSpeed() == null) {

                }

                while (currentPosition.getSpeed().trim().isEmpty()) {

                }

                final float[] distance = {(float) Tool.calculateDistance(
                        Tool.getLatLngFromString(trip.getPickUpLocation()),
                        Tool.getLatLngFromString(currentPosition.getPosition())
                )};

                distance[0] = distance[0] / 1000;

                float speed = Float.parseFloat(currentPosition.getSpeed());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (speed != 0) {
                            float time = (float) ((distance[0] / speed));
                            time *= 60;
                            textTimeLeft.setText(String.format("%s min", Math.round(time)));
                        } else {
                            textTimeLeft.setText("N/A");
                        }
                        distance[0] = Math.round(distance[0] * 10);
                        distance[0] /= 10;
                        textDistanceLeft.setText(String.format("%s km", distance[0]));
                    }});

            }
        }).start();
    }

    private void getDriverInfo(String driverId) {
        if (driverId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child("drivers")
                    .child(driverId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            driver = snapshot.getValue(Driver.class);
                            if (driver != null) {
                                setDriverInfoView(driver);
                                getVehicleInfo(driver.getVehicleId());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void getVehicleInfo(String vehicleId) {
        if (vehicleId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Vehicles")
                    .child(vehicleId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            vehicle = snapshot.getValue(Vehicle.class);
                            if (vehicle != null) {
                                setVehicleView(vehicle);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setVehicleView(Vehicle vehicle) {
        textVehicleName.setText(vehicle.getName());
        textPlate.setText(vehicle.getPlateNumber());
    }

    private void setDriverInfoView(Driver driver) {
        Picasso.get().load(driver.getDriverImageUrl()).resize(1000, 1000)
                .centerCrop().placeholder(R.drawable.user_blue).into(imgDriver);
        textDriverName.setText(driver.getName());
    }

    private void getTripInfo(String tripId) {
        if (tripId != null) {
            FirebaseDatabase.getInstance().getReference().child("Trips")
                    .child(tripId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trip = snapshot.getValue(Trip.class);
                            if (trip != null) {
                                try {
                                    setTripInfoView(trip);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (!pickUpAndDropOffIsMarked) {
                                    markPickUpAndDropOff(trip);
                                    setVehicleIcon(trip);
                                    pickUpAndDropOffIsMarked = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setVehicleIcon(Trip trip) {
        if (trip.getVehicleType().equals(Const.car)) {
            markerIconName = taxiMarker;
        } else {
            markerIconName = motorMarker;
        }
    }

    private void setTripInfoView(Trip trip) throws IOException {
        textMoney.setText(trip.getCost());
        edtPickUp.setText(trip.getPickUpName());
        edtDropOff.setText(trip.getDropOffName());

        if (trip.getPaymentMethod() != null) {
            if (trip.getPaymentMethod().equals(Const.cash)) {
                textPaymentMethod.setText("Cash");
            } else if (trip.getPaymentMethod().equals(Const.online)){
                textPaymentMethod.setText("L-Wallet");
                onlinePayment = true;
            }
        }

        if (trip.getStatus().equals(Const.paymentSuccessful)) {
            textStatus.setText("Payment successfully done!");
            btnCancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
            btnCancel.setClickable(false);
        } else if (trip.getStatus().equals(Const.driverArrivedPickUp)) {
            textStatus.setText("Driver has arrived to pick-up point");
            textTimeLeft.setVisibility(View.GONE);
            textDistanceLeft.setVisibility(View.GONE);
        } else if (trip.getStatus().equals(Const.onGoing)) {
            textStatus.setText("Going to your destination");
            textTimeLeft.setVisibility(View.GONE);
            textDistanceLeft.setVisibility(View.GONE);

            btnCancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
            btnCancel.setText("Pay for driver");
            cancelable = false;
        } else if (trip.getStatus().equals(Const.arrivedDropOff)) {
            textStatus.setText("You have arrived to your destination");
            textTimeLeft.setVisibility(View.GONE);
            textDistanceLeft.setVisibility(View.GONE);

            btnCancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background));
            btnCancel.setText("Pay for driver");
            cancelable = false;
            passengerArrivedToDropOff = true;
        } else if (trip.getStatus().equals(Const.waitingPickUp)) {
            textStatus.setText("Driver is coming in ");
            textTimeLeft.setVisibility(View.VISIBLE);
            textDistanceLeft.setVisibility(View.VISIBLE);
            return;
        } else if (trip.getStatus().equals(Const.success) && !movedToReview) {
            movedToReview = true;
            Intent intent = new Intent(WaitingPickUp.this, ReviewTrip.class);
            Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else if (trip.getStatus().equals(Const.cancelByDriver) && !movedToSearching) {
            Intent intent = new Intent(WaitingPickUp.this, SearchForDriverActivity.class);
            intent.putExtra("tripId", trip.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Toast.makeText(this, "Trip has been canceled by driver!", Toast.LENGTH_SHORT).show();
            movedToSearching = true;
            startActivity(intent);
            finish();
        }

        if (!dropOffPolyIsDrawn) {
            dropOffPolyIsDrawn = true;
            direction(
                    Tool.getLatLngFromString(trip.getPickUpLocation()),
                    Tool.getLatLngFromString(trip.getDropOffLocation()),
                    dropOffPolyOption
            );
            if (pickUpPolyIsDrawn) {
                pickUpPolyline.remove();
            }
        }
     }

    private void listener() {
        layoutStatusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInfo();
            }
        });

        buttonFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusOnDriver) {
                    focusOnDriver = false;
                    if (!dropOffPolyIsDrawn) {
                        zoomToPickUpRoute();
                    } else {
                        zoomToRoute(
                                Tool.getLatLngFromString(trip.getPickUpLocation()),
                                Tool.getLatLngFromString(trip.getDropOffLocation()));
                    }
                    buttonFocus.setImageResource(R.drawable.unfocus);
                } else {
                    focusOnDriver = true;
                    zoomToDriver();
                    buttonFocus.setImageResource(R.drawable.focus);
                }
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driver != null) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WaitingPickUp.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                CALL_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + driver.getPhoneNumber()));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Driver is null!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driver != null) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WaitingPickUp.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                SMS_REQUEST_CODE);
                    } else {
                        String number = driver.getPhoneNumber();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number,null));
                        //intent.putExtra("sms_body", "Hehe");
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(WaitingPickUp.this, "Driver is null!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (cancelable) {
                    cancelTrip();
                }

                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passengerArrivedToDropOff && onlinePayment && currentWalletBalance != null) {
                    if (checkWalletBalance(currentWalletBalance, trip.getCost())) {
                        transferMoney(driver.getId(), trip.getCost());
                    } else {
                        Toast.makeText(WaitingPickUp.this, "Your L-Wallet balance smaller than this trip's cost!", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!onlinePayment) {
                    btnCancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
                    btnCancel.setClickable(false);
                    Toast.makeText(WaitingPickUp.this, "Successful!", Toast.LENGTH_SHORT).show();

                    trip.setStatus(Const.paymentSuccessful);
                    FirebaseDatabase.getInstance().getReference()
                            .child("Trips")
                            .child(trip.getId())
                            .setValue(trip);
                }
            }
        });
    }

    private void transferMoney(String receiverId, String amount) {
        transferProgressDialog = new ProgressDialog(this);
        transferProgressDialog.setMessage("Loading...");
        transferProgressDialog.show();

        String senderId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("L-Wallet")
                .child("Transactions");
        String transId = databaseReference.push().getKey();

        Transaction transaction = new Transaction(
                transId,
                senderId,
                receiverId,
                amount,
                Calendar.getInstance().getTime().toString(),
                Const.payForDriver);

        assert transId != null;
        FirebaseDatabase.getInstance().getReference().child("L-Wallet")
                .child("Transactions")
                .child(transId)
                .setValue(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btnCancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
                        btnCancel.setClickable(false);
                        transferProgressDialog.dismiss();
                        Toast.makeText(WaitingPickUp.this, "Successful!", Toast.LENGTH_SHORT).show();

                        trip.setStatus(Const.paymentSuccessful);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Trips")
                                .child(trip.getId())
                                .setValue(trip);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        transferProgressDialog.dismiss();
                        Toast.makeText(WaitingPickUp.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean checkWalletBalance(String currentWalletBalance, String amount) {

        return Tool.getDoubleFromFormattedMoney(currentWalletBalance) >= Tool.getDoubleFromFormattedMoney(amount);
    }

    private void hideInfo() {
        if (infoIsHided) {
            infoIsHided = false;
            layoutBottom.setVisibility(View.VISIBLE);
        } else {
            infoIsHided = true;
            layoutBottom.setVisibility(View.GONE);
        }
    }

    private void init() {
        layoutStatusUpdate = findViewById(R.id.layout_statusUpdate);
        layoutBottom = findViewById(R.id.layout_driverInfo);
        textTimeLeft = findViewById(R.id.text_timeLeft);
        textDistanceLeft = findViewById(R.id.text_distanceLeft);
        textPlate = findViewById(R.id.text_plateNumber);
        textVehicleName = findViewById(R.id.text_vehicleName);
        textDriverName = findViewById(R.id.textDriverName);
        btnCall = findViewById(R.id.layout_call);
        btnMessage = findViewById(R.id.layout_chat);
        btnCancel = findViewById(R.id.buttonCancel);
        imgFocus = findViewById(R.id.img_focusOnMe);
        imgDriver = findViewById(R.id.img_driver);
        buttonFocus = findViewById(R.id.img_focusOnMe);
        textStatus = findViewById(R.id.text_status);
        textMoney = findViewById(R.id.text_money);
        textPaymentMethod = findViewById(R.id.text_paymentMethod);
        edtPickUp = findViewById(R.id.edt_pickUpPos);
        edtDropOff = findViewById(R.id.edt_dropOfPos);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);
    }

    private void markCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (maps != null) {
                            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        } else {

                        }
                    }
                });

    }

    private void direction(LatLng origin, LatLng destination, int option) throws IOException {
        String strOrigin = origin.latitude + ", " + origin.longitude;
        String strDestination = destination.latitude + ", " + destination.longitude;
        String vehicleType;
        if (trip.getVehicleType().equals(Const.car)) {
            vehicleType = "car";
        } else {
            vehicleType = "bike";
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://rsapi.goong.io/Direction")
                .buildUpon()
                .appendQueryParameter("origin", strOrigin)
                .appendQueryParameter("destination", strDestination)
                .appendQueryParameter("vehicle", vehicleType)
                .appendQueryParameter("api_key", GOONG_API_KEY)
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i=0;i<routes.length();i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j=0;j<legs.length();j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k=0;k<steps.length();k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l=0;l<list.size();l++){
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(polyWidth);
                            polylineOptions.geodesic(true);
                            polylineOptions.color(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                        }

                        assert polylineOptions != null;

                        if (option == pickUpPolyOption) {
                            pickUpPolyline = maps.addPolyline(polylineOptions);
                        } else {
                            dropOffPolyline = maps.addPolyline(polylineOptions);
                        }

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(destination.latitude, destination.longitude))
                                .include(new LatLng(origin.latitude, origin.longitude)).build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);

//                        maps.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 800, 250));
                    zoomToDriver();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded){

        return PolyUtil.decode(encoded);
    }

    private void loadCurrentApiKey() {
        FirebaseDatabase.getInstance().getReference().child("Current-API-KEY")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MAPS_API_KEY = snapshot.getValue(String.class);
                        keyIsLoaded = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("GOONG_API_KEY")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GOONG_API_KEY = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                            Toast.makeText(WaitingPickUp.this, "Your trip has been canceled!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(WaitingPickUp.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WaitingPickUp.this, "Can not cancel your trip, try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void calculateLastBalance() {
        List<Transaction> listReceiveTrans = new ArrayList<>();
        List<Transaction> listSendTrans = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("L-Wallet")
                .child("Transactions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listReceiveTrans.clear();
                        listSendTrans.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Transaction transaction = dataSnapshot.getValue(Transaction.class);
                            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            if (transaction != null) {
                                if (transaction.getNote().equals(Const.addMoney) && transaction.getSenderId().equals(userId)) {
                                    listReceiveTrans.add(transaction);
                                } else if (transaction.getSenderId().equals(userId)) {
                                    listSendTrans.add(transaction);
                                } else if (transaction.getReceiverId() != null) {
                                    if (transaction.getReceiverId().equals(userId))
                                        listReceiveTrans.add(transaction);
                                }
                            }
                        }

                        currentWalletBalance = getTotalBalance(listReceiveTrans, listSendTrans);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;
        maps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        maps.getUiSettings().setRotateGesturesEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        maps.setMyLocationEnabled(true);
        markCurrentLocation();
        getDataFromFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        isRunning = false;
    }
}