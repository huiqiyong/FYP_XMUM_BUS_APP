package com.example.xmum_bus_app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.example.xmum_bus_app.ScannerActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

public class ScanQRCodeFragment extends Fragment {

    MaterialButton btnScan;
    private String userID;
    PassengerActivity passengerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_scan_q_r_code, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_background));

        //Retrieve username from Passenger Activity
        passengerActivity = (PassengerActivity) getActivity();
        Bundle results = passengerActivity.getMyData();
        userID = results.getString("username");

        btnScan = rootview.findViewById(R.id.btnScan);

        btnScan.setOnClickListener(view -> {
            Intent s = new Intent(requireContext(), ScannerActivity.class);
            s.putExtra("username", userID);
            startActivity(s);
        });

        return rootview;
    }
}