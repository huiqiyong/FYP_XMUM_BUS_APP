package com.example.xmum_bus_app.Driver;

import static com.example.xmum_bus_app.GlobalVariable.endRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.FingerprintActivity;
import com.example.xmum_bus_app.LoginActivity;
import com.example.xmum_bus_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BusStatusUpdate extends AppCompatActivity implements LocationListener {

    private LocationManager manager;
    private final int MIN_TIME = 1000;  // 1 sec
    private final int MIN_DISTANCE = 1;     // 1 meter
    private String routeName, departTime, day, currentTime, status, busNo, driver;
    private ImageView btn_signOut, btn_back;
    private TextView title;
    private boolean isLocationPermissionOk;
    private RadioButton onSchedule, delayed, busBreakDown;
    private DatabaseReference dbRef, dbRefBusStop, dbRefRecord;
    private MaterialButton btn_stop;
    Date now, original, dateMax, dateMin;
    List<LatLng> points = new ArrayList<LatLng>();
    String dLat1,dLat2,dLat3,dLat4,dLat5,dLat6,dLat7,dLat8,dLat9,dLng1,dLng2,dLng3,dLng4,dLng5,dLng6,dLng7,dLng8,dLng9;
    LatLng location1,location2,location3,location4,location5,location6,location7,location8,location9;

    public BusStatusUpdate (){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bus_status_update);

        endRoute = false;

        //Retrieve routeName and departTime from VehicleTracking
        Intent intent = getIntent();
        routeName = intent.getExtras().getString("route");
        departTime = intent.getExtras().getString("time");
        day = intent.getExtras().getString("day");
        isLocationPermissionOk = intent.getExtras().getBoolean("permissionOk");
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        title = findViewById(R.id.driver_route);
        btn_signOut = findViewById(R.id.signOut);
        btn_back = findViewById(R.id.driver_back);
        onSchedule = findViewById(R.id.RadioButton1);
        delayed = findViewById(R.id.RadioButton2);
        busBreakDown = findViewById(R.id.RadioButton3);
        btn_stop = findViewById(R.id.btn_stopTrack);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Routes").child(day).child(routeName);
        dbRefRecord = FirebaseDatabase.getInstance().getReference().child("TravelRecord");

        title.setText(routeName);

        setBusStatus();

        btn_signOut.setOnClickListener(view -> {
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String track_status = String.valueOf(snapshot.child("TrackStatus").getValue());

                    if(track_status.equals("0")){
                        new SweetAlertDialog(BusStatusUpdate.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.exit))
                            .setCancelText(getString(R.string.no))
                            .setConfirmText(getString(R.string.yes))
                            .showCancelButton(true)
                            .setCancelClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                            })
                            .setConfirmClickListener(sweetAlertDialog -> {
                                Intent i = new Intent(BusStatusUpdate.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            })
                            .show();
                    }else{
                        Toast.makeText(BusStatusUpdate.this, getString(R.string.errorSignOut), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        btn_back.setOnClickListener(view -> {
            btn_stop.callOnClick();
        });

        //RadioGroup radioButton
        onSchedule.setOnClickListener(view -> {
            dbRef.child("BusStatus").setValue("On Schedule");
        });
        delayed.setOnClickListener(view -> {
            dbRef.child("BusStatus").setValue("Delayed");
        });
        busBreakDown.setOnClickListener(view -> {
            dbRef.child("BusStatus").setValue("Bus Breakdown");
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        String stime = timeFormat.format(calendar.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String date = dateFormat.format(calendar.getTime());

        btn_stop.setOnClickListener(view -> {
            endRoute = true;
            // Set default value
            dbRef.child("TrackStatus").setValue("0");   // 0 equal stop, 1 equal start
            dbRef.child("NextStop").setValue("0");
            dbRef.child("BusLatitude").setValue("2.82917");
            dbRef.child("BusLongitude").setValue("101.70445");
            dbRef.child("EndLatitude").setValue("2.82917");
            dbRef.child("EndLongitude").setValue("101.70445");
            Toast.makeText(BusStatusUpdate.this, getString(R.string.stopTrackingToast), Toast.LENGTH_SHORT).show();
            // Stop location update
            manager.removeUpdates(this);
            
            // Save travel record into firebase
            String etime = new SimpleDateFormat("HH:mm").format(new Date());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    status = String.valueOf(snapshot.child("BusStatus").getValue());
                    busNo = String.valueOf(snapshot.child("BusNo").getValue());
                    driver = String.valueOf(snapshot.child("Driver").getValue());

                    HashMap<String, String> recordMap = new HashMap<>();
                    recordMap.put("route", routeName);
                    recordMap.put("driver", driver);
                    recordMap.put("busNumber", busNo);
                    recordMap.put("date", date);
                    recordMap.put("scheduledTime", departTime);
                    recordMap.put("startTime", stime);
                    recordMap.put("endTime", etime);
                    recordMap.put("status", status);

                    dbRefRecord.push().setValue(recordMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish();
        });

        // Check Location Permission
        if (isLocationPermissionOk && !endRoute){
            getLocationUpdates();
        }
    }

    private void getLocationUpdates() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else {
                    Toast.makeText(this, "No provider enabled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdates();
            } else {
                Toast.makeText(this, getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Calculate distance between two places
    private static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    private void setBusStatus() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        currentTime = dateFormat.format(calendar.getTime());

        try {
            now = dateFormat.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            original = dateFormat.parse(departTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = original.getTime() - now.getTime();

        if(difference<0){
            try {
                dateMax = dateFormat.parse("24:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                dateMin = dateFormat.parse("00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            difference=(dateMax.getTime() -now.getTime() )+(original.getTime()-dateMin.getTime());
        }

        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

        if (hours == 0 && min < 10){
            onSchedule.setChecked(true);
            dbRef.child("BusStatus").setValue("On Schedule");
        }else {
            delayed.setChecked(true);
            dbRef.child("BusStatus").setValue("Delayed");
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null){
            Toast.makeText(this, "location change", Toast.LENGTH_SHORT).show();
            saveLocation(location);
        }
    }

    private void saveLocation(Location location) {
        //Store bus location in Firebase
        dbRef.child("BusLatitude").setValue(String.valueOf(location.getLatitude()));
        dbRef.child("BusLongitude").setValue(String.valueOf(location.getLongitude()));

        LatLng busLocation = new LatLng(location.getLatitude(), location.getLongitude());
        // Get location of the bus stop along the route
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String trackStatus = String.valueOf(snapshot.child("TrackStatus").getValue());
                int nextStop = Integer.parseInt(snapshot.child("NextStop").getValue().toString());
                String routeType = String.valueOf(snapshot.child("RouteType").getValue());
                dbRefBusStop = FirebaseDatabase.getInstance().getReference().child("BusStop").child(routeType);
                dbRefBusStop.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dLat1 = String.valueOf(snapshot.child("Stop1").child("Latitude").getValue());
                        dLng1 = String.valueOf(snapshot.child("Stop1").child("Longitude").getValue());
                        location1 = new LatLng(Double.parseDouble(dLat1), Double.parseDouble(dLng1));
                        points.add(location1);
                        if (snapshot.child("Stop2").exists()) {
                            dLat2 = String.valueOf(snapshot.child("Stop2").child("Latitude").getValue());
                            dLng2 = String.valueOf(snapshot.child("Stop2").child("Longitude").getValue());
                            location2 = new LatLng(Double.parseDouble(dLat2), Double.parseDouble(dLng2));
                            points.add(location2);
                        }
                        if (snapshot.child("Stop3").exists()) {
                            dLat3 = String.valueOf(snapshot.child("Stop3").child("Latitude").getValue());
                            dLng3 = String.valueOf(snapshot.child("Stop3").child("Longitude").getValue());
                            location3 = new LatLng(Double.parseDouble(dLat3), Double.parseDouble(dLng3));
                            points.add(location3);
                        }
                        if (snapshot.child("Stop4").exists()) {
                            dLat4 = String.valueOf(snapshot.child("Stop4").child("Latitude").getValue());
                            dLng4 = String.valueOf(snapshot.child("Stop4").child("Longitude").getValue());
                            location4 = new LatLng(Double.parseDouble(dLat4), Double.parseDouble(dLng4));
                            points.add(location4);
                        }
                        if (snapshot.child("Stop5").exists()) {
                            dLat5 = String.valueOf(snapshot.child("Stop5").child("Latitude").getValue());
                            dLng5 = String.valueOf(snapshot.child("Stop5").child("Longitude").getValue());
                            location5 = new LatLng(Double.parseDouble(dLat5), Double.parseDouble(dLng5));
                            points.add(location5);
                        }
                        if (snapshot.child("Stop6").exists()) {
                            dLat6 = String.valueOf(snapshot.child("Stop6").child("Latitude").getValue());
                            dLng6 = String.valueOf(snapshot.child("Stop6").child("Longitude").getValue());
                            location6 = new LatLng(Double.parseDouble(dLat6), Double.parseDouble(dLng6));
                            points.add(location6);
                        }
                        if (snapshot.child("Stop7").exists()) {
                            dLat7 = String.valueOf(snapshot.child("Stop7").child("Latitude").getValue());
                            dLng7 = String.valueOf(snapshot.child("Stop7").child("Longitude").getValue());
                            location7 = new LatLng(Double.parseDouble(dLat7), Double.parseDouble(dLng7));
                            points.add(location7);
                        }
                        if (snapshot.child("Stop8").exists()) {
                            dLat8 = String.valueOf(snapshot.child("Stop8").child("Latitude").getValue());
                            dLng8 = String.valueOf(snapshot.child("Stop8").child("Longitude").getValue());
                            location8 = new LatLng(Double.parseDouble(dLat8), Double.parseDouble(dLng8));
                            points.add(location8);
                        }
                        if (snapshot.child("Stop9").exists()) {
                            dLat9 = String.valueOf(snapshot.child("Stop9").child("Latitude").getValue());
                            dLng9 = String.valueOf(snapshot.child("Stop9").child("Longitude").getValue());
                            location9 = new LatLng(Double.parseDouble(dLat9), Double.parseDouble(dLng9));
                            points.add(location9);
                        }
                        // when reach a bus stop, if not at last stop, get the location of the next bus stop
                        Double distance = distanceBetween(busLocation, points.get(nextStop));
                        int counter = nextStop + 1;
                        if (!"0".equals(trackStatus)) {
                            if ((distance < 200) && (counter < points.size())) {
                                dbRef.child("NextStop").setValue(String.valueOf(counter));
                                dbRef.child("EndLatitude").setValue(String.valueOf(points.get(counter).latitude));
                                dbRef.child("EndLongitude").setValue(String.valueOf(points.get(counter).longitude));
                            } else {
                                dbRef.child("EndLatitude").setValue(String.valueOf(points.get(nextStop).latitude));
                                dbRef.child("EndLongitude").setValue(String.valueOf(points.get(nextStop).longitude));
                            }
                            points.clear();     // clear data in points list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}