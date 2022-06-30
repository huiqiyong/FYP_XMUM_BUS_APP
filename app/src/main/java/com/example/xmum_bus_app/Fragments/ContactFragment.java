package com.example.xmum_bus_app.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;

public class ContactFragment extends Fragment {

    private ImageView officer1, officer2, hotline;
    PassengerActivity passengerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_background));

        officer1 = rootView.findViewById(R.id.imgV_officer1);
        officer2 = rootView.findViewById(R.id.imgV_officer2);
        hotline = rootView.findViewById(R.id.imgV_hotline);

        passengerActivity = (PassengerActivity) getActivity();

        officer1.setOnClickListener(view -> {
            String officer1 = "01113133348";
            passengerActivity.makePhoneCall(officer1);
        });

        officer2.setOnClickListener(view -> {
            String officer2 = "0137014969";
            passengerActivity.makePhoneCall(officer2);
        });

        hotline.setOnClickListener(view -> {
            String hotline = "999";
            passengerActivity.makePhoneCall(hotline);
        });

        return rootView;
    }

}