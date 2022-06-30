package com.example.xmum_bus_app.Driver;

import static com.example.xmum_bus_app.GlobalVariable.endRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.DriverActivity;
import com.example.xmum_bus_app.FingerprintActivity;
import com.example.xmum_bus_app.LoginActivity;
import com.example.xmum_bus_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VehicleTracking extends AppCompatActivity {

    private String routeName, departTime, day;
    private ImageView btn_signOut, btn_back;
    private TextView title, route, time;
    private MaterialButton btn_start;
    private DatabaseReference dbRef;
    private boolean isLocationPermissionOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vehicle_tracking);

        endRoute = true;

        //Retrieve routeName and departTime from DriverActivity
        Intent intent = getIntent();
        routeName = intent.getExtras().getString("route");
        departTime = intent.getExtras().getString("time");
        day = intent.getExtras().getString("day");

        title = findViewById(R.id.driver_route);
        route = findViewById(R.id.tv_route);
        time = findViewById(R.id.tv_time);
        btn_signOut = findViewById(R.id.signOut);
        btn_back = findViewById(R.id.driver_btnback);
        btn_start = findViewById(R.id.btn_track);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Routes").child(day).child(routeName);

        title.setText(routeName);
        route.setText(routeName);
        time.setText(departTime);

        btn_signOut.setOnClickListener(view -> {
            new SweetAlertDialog(VehicleTracking.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.exit))
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .setCancelClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setConfirmClickListener(sweetAlertDialog -> {
                        Intent i = new Intent(VehicleTracking.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .show();
        });

        btn_back.setOnClickListener(view -> {
            onBackPressed();
        });

        checkLocationPermission();

        btn_start.setOnClickListener(view -> {

            if (isLocationPermissionOk){
                dbRef.child("TrackStatus").setValue("1");
                Intent i = new Intent(VehicleTracking.this, BusStatusUpdate.class);
                i.putExtra("route", routeName);
                i.putExtra("time", departTime);
                i.putExtra("day", day);
                i.putExtra("permissionOk", isLocationPermissionOk);
                startActivity(i);
            }else {
                Toast.makeText(this, getString(R.string.enableLocation), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.locationPermission))
                .setMessage(getString(R.string.contentLocationPermission))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocation();
                    }
                })
                .create().show();
        }else{
            requestLocation();
        }
    }

    private void requestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    ,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2000);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    ,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }

}