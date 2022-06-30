package com.example.xmum_bus_app.Fragments.Route;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.BusStop;
import com.example.xmum_bus_app.Fragments.ViewScheduleFragment;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import okhttp3.Route;

public class RouteFragment extends Fragment {

    private CardView card1,card2,card3,card4,card5, card6,card7,card8, card9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_route, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_background));

        card1 = (CardView) rootView.findViewById(R.id.card1);
        card2 = (CardView) rootView.findViewById(R.id.card2);
        card3 = (CardView) rootView.findViewById(R.id.card3);
        card4 = (CardView) rootView.findViewById(R.id.card4);
        card5 = (CardView) rootView.findViewById(R.id.card5);
        card6 = (CardView) rootView.findViewById(R.id.card6);
        card7 = (CardView) rootView.findViewById(R.id.card7);
        card8 = (CardView) rootView.findViewById(R.id.card8);
        card9 = (CardView) rootView.findViewById(R.id.card9);

        card1.setOnClickListener(view -> {
            nextFragment("Monday-Thursday Daily Route 1");
        });

        card2.setOnClickListener(view -> {
            nextFragment("Monday-Thursday Daily Route 2");
        });

        card3.setOnClickListener(view -> {
            nextFragment("Friday Morning Daily Route");
        });

        card4.setOnClickListener(view -> {
            nextFragment("Friday-Sunday Daily Route 1");
        });

        card5.setOnClickListener(view -> {
            nextFragment("Friday-Sunday Daily Route 2");
        });

        card6.setOnClickListener(view -> {
            nextFragment("Friday & Sunday Special Route 1");
        });

        card7.setOnClickListener(view -> {
            nextFragment("Friday & Sunday Special Route 2");
        });

        card8.setOnClickListener(view -> {
            nextFragment("Saturday Special Route 1");
        });

        card9.setOnClickListener(view -> {
            nextFragment("Saturday Special Route 2");
        });

        return rootView;
    }

    private void nextFragment(String route){
        Bundle bundle = new Bundle();
        bundle.putString("route", route);

        RouteMapView fragment2 = new RouteMapView();
        fragment2.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment2).commit();
    }

}