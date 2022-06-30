package com.example.xmum_bus_app.Fragments.Route;

import static com.example.xmum_bus_app.GlobalVariable.arriveNotification;
import static com.example.xmum_bus_app.GlobalVariable.busStatusNotification;
import static com.example.xmum_bus_app.GlobalVariable.endRoute;
import static com.example.xmum_bus_app.GlobalVariable.trackFragment;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.xmum_bus_app.BusSchedule.BusStop;
import com.example.xmum_bus_app.BusSchedule.Routes;
import com.example.xmum_bus_app.Fragments.TrackFragment;
import com.example.xmum_bus_app.LoginActivity;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrackBusMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private DatabaseReference dbRefBusStop, dbRefRoute, driverDbRef;
    private FirebaseRecyclerOptions<BusStop> options;
    private FirebaseRecyclerAdapter<BusStop, RouteViewHolder> adapter;
    private FloatingActionButton busLocation;
    private boolean isTrafficEnable;
    boolean clickStatus = false;
    private long timeLeft = 3000;
    private LatLng location1, location2, location3, location4, location5, location6, location7, location8, location9;
    private String route, ETA, username;
    private TextView estimateTime, driver, contact, busNo, bus_status;
    BottomSheetBehavior bottomSheetBehavior;
    private Double latitude, longitude, end_latitude, end_longitude, distance;
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_track_bus_map);

        trackFragment = false;

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(100);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackMap);
        mapFragment.getMapAsync(TrackBusMapActivity.this);

        //Retrieve username from TrackFragment
        Intent intent = getIntent();
        route = intent.getExtras().getString("route");
        String routeType = intent.getExtras().getString("routeType");
        String day = intent.getExtras().getString("day");
        username = intent.getExtras().getString("username");

        TextView routeName = findViewById(R.id.txtRouteName);
        driver = findViewById(R.id.txtDriverName);
        contact = findViewById(R.id.txtPhoneNo);
        estimateTime = findViewById(R.id.txtEstimatedTime);
        busNo = findViewById(R.id.txtBusNo);
        bus_status = findViewById(R.id.txtBusStatus);
        // Set route name
        routeName.setText(route);

        dbRefRoute = FirebaseDatabase.getInstance().getReference("Routes").child(day);
        RecyclerView stop_list_recyclerview = findViewById(R.id.stopRecyclerView);
        stop_list_recyclerview.setLayoutManager(new LinearLayoutManager(TrackBusMapActivity.this, LinearLayoutManager.VERTICAL, false));

        // Show bus stop list
        dbRefBusStop = FirebaseDatabase.getInstance().getReference().child("BusStop").child(routeType);
        getBusStopList(dbRefBusStop, stop_list_recyclerview);

        // Enable or Disable traffic
        FloatingActionButton traffic = findViewById(R.id.enableTraffic);
        traffic.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (gMap != null) {
                    gMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (gMap != null) {
                    gMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });

        // Move camera to bus location
        busLocation = findViewById(R.id.busLocation);
        busLocation.setOnClickListener(view -> {
            clickStatus = true;
            setBusInfo();
        });

        // check bus tracking status
        checkTrackingStatus(dbRefRoute);

        // set bus information
        setBusInfo();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng default_location = new LatLng(2.8324, 101.7070);
        currentMarker = gMap.addMarker(new MarkerOptions().position(default_location).title(getString(R.string.busMarker)));

        // Draw route on map
        dbRefBusStop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Stop1").exists()){
                    String t1 = String.valueOf(snapshot.child("Stop1").child("Title").getValue());
                    String lt1 = String.valueOf(snapshot.child("Stop1").child("Latitude").getValue());
                    String lg1 = String.valueOf(snapshot.child("Stop1").child("Longitude").getValue());
                    location1 = new LatLng(Double.parseDouble(lt1), Double.parseDouble(lg1));
                    addMarkers(googleMap, location1, t1);
                }
                if (snapshot.child("Stop2").exists()){
                    String t2 = String.valueOf(snapshot.child("Stop2").child("Title").getValue());
                    String lt2 = String.valueOf(snapshot.child("Stop2").child("Latitude").getValue());
                    String lg2 = String.valueOf(snapshot.child("Stop2").child("Longitude").getValue());
                    location2 = new LatLng(Double.parseDouble(lt2), Double.parseDouble(lg2));
                    new GetPathFromLocation(location1, location2, polyLine -> {
                        addMarkers(gMap, location2, t2);
                        gMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop3").exists()){
                    String t3 = String.valueOf(snapshot.child("Stop3").child("Title").getValue());
                    String lt3 = String.valueOf(snapshot.child("Stop3").child("Latitude").getValue());
                    String lg3 = String.valueOf(snapshot.child("Stop3").child("Longitude").getValue());
                    location3 = new LatLng(Double.parseDouble(lt3), Double.parseDouble(lg3));
                    new GetPathFromLocation(location2, location3, polyLine -> {
                        addMarkers(gMap, location3, t3);
                        gMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop4").exists()){
                    String t4 = String.valueOf(snapshot.child("Stop4").child("Title").getValue());
                    String lt4 = String.valueOf(snapshot.child("Stop4").child("Latitude").getValue());
                    String lg4 = String.valueOf(snapshot.child("Stop4").child("Longitude").getValue());
                    location4 = new LatLng(Double.parseDouble(lt4), Double.parseDouble(lg4));
                    new GetPathFromLocation(location3, location4, polyLine -> {
                        addMarkers(googleMap, location4, t4);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop5").exists()){
                    String t5 = String.valueOf(snapshot.child("Stop5").child("Title").getValue());
                    String lt5 = String.valueOf(snapshot.child("Stop5").child("Latitude").getValue());
                    String lg5 = String.valueOf(snapshot.child("Stop5").child("Longitude").getValue());
                    location5 = new LatLng(Double.parseDouble(lt5), Double.parseDouble(lg5));
                    new GetPathFromLocation(location4, location5, polyLine -> {
                        addMarkers(googleMap, location5, t5);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop6").exists()){
                    String t6 = String.valueOf(snapshot.child("Stop6").child("Title").getValue());
                    String lt6 = String.valueOf(snapshot.child("Stop6").child("Latitude").getValue());
                    String lg6 = String.valueOf(snapshot.child("Stop6").child("Longitude").getValue());
                    location6 = new LatLng(Double.parseDouble(lt6), Double.parseDouble(lg6));
                    new GetPathFromLocation(location5, location6, polyLine -> {
                        addMarkers(googleMap, location6, t6);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop7").exists()){
                    String t7 = String.valueOf(snapshot.child("Stop7").child("Title").getValue());
                    String lt7 = String.valueOf(snapshot.child("Stop7").child("Latitude").getValue());
                    String lg7 = String.valueOf(snapshot.child("Stop7").child("Longitude").getValue());
                    location7 = new LatLng(Double.parseDouble(lt7), Double.parseDouble(lg7));
                    new GetPathFromLocation(location6, location7, polyLine -> {
                        addMarkers(googleMap, location7, t7);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop8").exists()){
                    String t8 = String.valueOf(snapshot.child("Stop8").child("Title").getValue());
                    String lt8 = String.valueOf(snapshot.child("Stop8").child("Latitude").getValue());
                    String lg8 = String.valueOf(snapshot.child("Stop8").child("Longitude").getValue());
                    location8 = new LatLng(Double.parseDouble(lt8), Double.parseDouble(lg8));
                    new GetPathFromLocation(location7, location8, polyLine -> {
                        addMarkers(googleMap, location8, t8);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
                if (snapshot.child("Stop9").exists()){
                    String t9 = String.valueOf(snapshot.child("Stop9").child("Title").getValue());
                    String lt9 = String.valueOf(snapshot.child("Stop9").child("Latitude").getValue());
                    String lg9 = String.valueOf(snapshot.child("Stop9").child("Longitude").getValue());
                    location9 = new LatLng(Double.parseDouble(lt9), Double.parseDouble(lg9));
                    new GetPathFromLocation(location8, location9, polyLine -> {
                        addMarkers(googleMap, location9, t9);
                        googleMap.addPolyline(polyLine);
                    }).execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Get driver name and contact number
    private void setDriverInfo(String driverID){
        driverDbRef = FirebaseDatabase.getInstance().getReference("Users").child("Driver").child(driverID);
        driverDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driver.setText(String.valueOf(snapshot.child("name").getValue()));
                contact.setText(String.valueOf(snapshot.child("contact").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Get bus location, bus number and bus status
    private void setBusInfo() {
        dbRefRoute.child(route).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set bus number
                busNo.setText(String.valueOf(snapshot.child("BusNo").getValue()));

                // Set bus status
                String b = String.valueOf(snapshot.child("BusStatus").getValue());
                bus_status.setText(b);
                // if bus breakdown, pop notification
                if (b.equals("Bus Breakdown") && busStatusNotification){
                    popNotification(route, getString(R.string.breakdownContent));
                    busStatusNotification = false;
                }

                // set driver information
                String driver_id = String.valueOf(snapshot.child("Driver").getValue());
                setDriverInfo(driver_id);

                // Get bus location and next stop location
                latitude = Double.parseDouble(String.valueOf(snapshot.child("BusLatitude").getValue()));
                longitude = Double.parseDouble(String.valueOf(snapshot.child("BusLongitude").getValue()));
                end_latitude = Double.parseDouble(String.valueOf(snapshot.child("EndLatitude").getValue()));
                end_longitude = Double.parseDouble(String.valueOf(snapshot.child("EndLongitude").getValue()));

                // Pop up notification when reach destination
                distance = distanceBetween(new LatLng(latitude, longitude), new LatLng(end_latitude, end_longitude));
                if(distance>200 && !arriveNotification){
                    arriveNotification = true;
                }
                if (distance<200 && !clickStatus && arriveNotification && !trackFragment){
                    popNotification( route, getString(R.string.notificationMessage) + getString(R.string.notificationMessage2));
                    arriveNotification = false;
                }

                // Retrieve duration between two places using json
                requestServerUrl(latitude, longitude, end_latitude, end_longitude);

                // Move camera to bus location
                moveCameraToLocation(latitude, longitude);

                // Set location button click to false
                clickStatus = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Check tracking is on or off
    private void checkTrackingStatus(DatabaseReference RefRoute){
        RefRoute.child(route).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check bus status
                String bus_status = String.valueOf(snapshot.child("BusStatus").getValue());
                // Check bus tracking status
                String t = String.valueOf(snapshot.child("TrackStatus").getValue());
                if ((t.equals("0") || bus_status.equals("Bus Breakdown"))&& !trackFragment){
                    Toast.makeText(TrackBusMapActivity.this, getString(R.string.completeRoute), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Retrieve duration between two places using json
    private void requestServerUrl(Double lat, Double lng, Double end_lat, Double end_lng) {
        // Origin of route
        String str_origin = "origin=" + lat + "," + lng;
        // Destination of route
        String str_dest = "destination=" + end_lat + "," + end_lng;
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling mode enable
        String mode = "mode = transit";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"+ mode + "&key=" + "AIzaSyA5mK5ipHL4AQS5n9R-oGHXr9aYA5hmjPE";
        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject json = new JSONObject(response.toString());
                    ETA = json.getJSONArray("routes").getJSONObject(0)
                            .getJSONArray("legs").getJSONObject(0)
                            .getJSONObject("duration").getString("text");
                    estimateTime.setText(ETA);
                } catch (JSONException e) {
                    Log.d("SAMPLE", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });
        queue.add(objectRequest);
    }

    // Pop up notification when bus arrive or when bus breakdown
    private void popNotification(String title, String content) {
        String id = "my_channel_id";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = manager.getNotificationChannel(id);
            if (channel == null){
                channel = new NotificationChannel(id, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                //configure notification channel
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.bus_marker)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(false)   // swipe to dismiss, if true: automatically removes the notification when the user taps it
                .setTicker("Notification");
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        //id to generate new notification in list notifications menu
        m.notify(new Random().nextInt(), builder.build());
    }

    // Show bus stop list in recyclerview
    private void getBusStopList(DatabaseReference dbRef, RecyclerView recyclerView) {

        options = new FirebaseRecyclerOptions.Builder<BusStop>().setQuery(dbRef, BusStop.class).build();
        adapter = new FirebaseRecyclerAdapter<BusStop, RouteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RouteViewHolder holder, int position, @NonNull BusStop model) {
                holder.stopName.setText(model.getTitle());
                holder.stopSequence.setText(model.getSequence());
            }

            @NonNull
            @Override
            public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_map_view_item_design, parent, false);
                return new RouteViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void moveCameraToLocation(Double latitude, Double longitude) {
        LatLng bus_location = new LatLng(latitude, longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bus_location, 18));
        currentMarker.setPosition(new LatLng(latitude, longitude));
    }

    // Calculate distance between two places
    private static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    // Add custom marker on map
    private void addMarkers(GoogleMap map, LatLng location, String title){
        map.addMarker(new MarkerOptions().position(location).
                icon(BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(this,R.drawable.bus_marker)))).setTitle(title);
    }

    private static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_image_marker, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.circleImgView);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        trackFragment = true;
        finish();
        Intent i = new Intent(getApplicationContext(), PassengerActivity.class);
        i.putExtra("username", username);
        startActivity(i);
    }
}