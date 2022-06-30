package com.example.xmum_bus_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.time.LocalDate;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScannerActivity extends AppCompatActivity {

    String username, resultData;
    CodeScanner codeScanner;
    CodeScannerView scannerView;

    private DatabaseReference userDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scanner);

        scannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, scannerView);

        //Retrieve username from ScanQRCodeFragment
        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        userDBRef = FirebaseDatabase.getInstance().getReference("Users").child("Passenger").child(username);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData = result.getText();  // ticket name
                        if (resultData.equals("ticketFri") || resultData.equals("ticketFriBack") || resultData.equals("ticketSat") || resultData.equals("ticketSatBack") || resultData.equals("ticketSun") || resultData.equals("ticketSunBack"))
                        {
                            processData(resultData);
                        }else{
                            invalidCode();
                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });

    }

    private void processData(String ticket){
        userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String t = snapshot.child(ticket).getValue().toString();
                switch (t){
                    case "true":
                        successMessage();
                        break;
                    case "false":
                        failedMessage();
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(ScannerActivity.this, getString(R.string.camPermission), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void successMessage(){
        new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.scanTitle1))
                .setContentText(getString(R.string.scanContent1))
                .show();
    }

    private void failedMessage(){
        new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.scanTitle2))
                .setContentText(getString(R.string.scanContent2))
                .show();
    }

    private void invalidCode(){
        new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.invalidCode))
                .setContentText(getString(R.string.scanContent2))
                .show();
    }

}