package com.example.xmum_bus_app;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.Routes;
import com.example.xmum_bus_app.Driver.BusStatusUpdate;
import com.example.xmum_bus_app.Driver.VehicleTracking;
import com.example.xmum_bus_app.Fragments.Booking.BookingDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DriverActivity extends AppCompatActivity {

    String uId;
    private TextView date, message, routeDTitle, routeSTitle;
    private ImageView btn_signOut;
    private RecyclerView recyclerView, recyclerViewS;
    private FirebaseRecyclerOptions<Routes> options;
    private FirebaseRecyclerAdapter<Routes, DriverViewHolder>adapter;
    private DatabaseReference dbRef, dbRefFri, dbRefFriS, dbRefSat, dbRefSatS, dbRefSun, dbRefSunS, driverDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_driver);

        //Retrieve username from LoginActivity or FingerprintActivity
        Intent intent = getIntent();
        uId = intent.getExtras().getString("username");
        driverDbRef = FirebaseDatabase.getInstance().getReference("Users").child("Driver").child(uId);

        btn_signOut = findViewById(R.id.signOut);
        message = findViewById(R.id.driver_message);
        routeSTitle = findViewById(R.id.routeS_title);
        routeDTitle = findViewById(R.id.routeD_title);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Routes").child("Monday-Thursday");
        dbRefFri = FirebaseDatabase.getInstance().getReference().child("Routes").child("Friday");
        dbRefSat = FirebaseDatabase.getInstance().getReference().child("Routes").child("Saturday");
        dbRefSun = FirebaseDatabase.getInstance().getReference().child("Routes").child("Sunday");
        dbRefFriS = FirebaseDatabase.getInstance().getReference().child("Routes").child("Friday Special");
        dbRefSatS = FirebaseDatabase.getInstance().getReference().child("Routes").child("Saturday Special");
        dbRefSunS = FirebaseDatabase.getInstance().getReference().child("Routes").child("Sunday Special");
        recyclerView = findViewById(R.id.recyclerView_driver);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewS = findViewById(R.id.recyclerView_driver2);
        recyclerViewS.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //Display current date & time
        date = findViewById(R.id.current_dateTime);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        String d = dateFormat.format(calendar.getTime());
        date.setText(d);

        getWorkingDay();

        btn_signOut.setOnClickListener(view -> {
            new SweetAlertDialog(DriverActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.exit))
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .setCancelClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setConfirmClickListener(sweetAlertDialog -> {
                        Intent i = new Intent(DriverActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .show();
        });
    }

    private void getWorkingDay(){
        driverDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dayName = LocalDate.now().getDayOfWeek().name();

                String d1 = snapshot.child("MonThursday").getValue().toString();
                String d2 = snapshot.child("Friday").getValue().toString();
                String d3 = snapshot.child("Saturday").getValue().toString();
                String d4 = snapshot.child("Sunday").getValue().toString();
                String d5 = snapshot.child("FriSpecial").getValue().toString();
                String d6 = snapshot.child("SatSpecial").getValue().toString();
                String d7 = snapshot.child("SunSpecial").getValue().toString();

                if (d1.equals("false") && d2.equals("false") && d3.equals("false") && d4.equals("false") && d5.equals("false") && d6.equals("false") && d7.equals("false")) {
                    message.setVisibility(View.VISIBLE);
                }else{
                    if (d1.equals("true") && (dayName.equals("MONDAY") || dayName.equals("TUESDAY") || dayName.equals("WEDNESDAY") || dayName.equals("THURSDAY"))) {
                        getDriverSchedule(dbRef, recyclerView, "Monday-Thursday");
                    } else if (d2.equals("true") && dayName.equals("FRIDAY")) {
                        getDriverSchedule(dbRefFri, recyclerView, "Friday");
                    } else if (d3.equals("true") && dayName.equals("SATURDAY")) {
                        getDriverSchedule(dbRefSat, recyclerView, "Saturday");
                    } else if (d4.equals("true") && dayName.equals("SUNDAY")) {
                        getDriverSchedule(dbRefSun, recyclerView, "Sunday");
                    } else {
                        routeDTitle.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    if (d5.equals("true") && dayName.equals("FRIDAY")) {
                        getDriverSchedule(dbRefFriS, recyclerViewS, "Friday Special");
                        routeSTitle.setVisibility(View.VISIBLE);
                    } else if (d6.equals("true") && dayName.equals("SATURDAY")) {
                        getDriverSchedule(dbRefSatS, recyclerViewS, "Saturday Special");
                        routeSTitle.setVisibility(View.VISIBLE);
                    } else if (d7.equals("true") && dayName.equals("SUNDAY")) {
                        getDriverSchedule(dbRefSunS, recyclerViewS, "Sunday Special");
                        routeSTitle.setVisibility(View.VISIBLE);
                    } else {
                        routeSTitle.setVisibility(View.GONE);
                        recyclerViewS.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDriverSchedule(DatabaseReference ref, RecyclerView view, String day) {

        options = new FirebaseRecyclerOptions.Builder<Routes>().setQuery(ref, Routes.class).build();
        adapter = new FirebaseRecyclerAdapter<Routes, DriverViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull DriverViewHolder holder, int position, @NonNull Routes model) {
                holder.routeName.setText(model.getRouteName());
                holder.startTime.setText(model.getStartTime());

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(DriverActivity.this, VehicleTracking.class);
                        i.putExtra("route", model.getRouteName());
                        i.putExtra("time", model.getStartTime());
                        i.putExtra("day", day);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_schedule_card_design, parent, false);
                return new DriverViewHolder(v);
            }
        };
        adapter.startListening();
        view.setAdapter(adapter);
    }
}