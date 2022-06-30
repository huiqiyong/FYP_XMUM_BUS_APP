package com.example.xmum_bus_app.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewScheduleFragment extends Fragment {

    private ImageView back;
    private String day, session;
    private TextView stop1, stop2, stop3, stop4, stop5, stop6, stop7, stop8, stop9, departTime1, departTime2, departTime3, departTime4, departTime5, departTime6, departTime7, departTime8, departTime9;
    private LinearLayout timeline2,timeline3,timeline4,timeline5,timeline6,timeline7,timeline8,timeline9;
    private DatabaseReference dataRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_schedule, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_content_background));

        Bundle bundle = this.getArguments();

        if(bundle != null){
            day = bundle.getString("day");
            session = bundle.getString("sessionId");
        }

        timeline2 = (LinearLayout) rootView.findViewById(R.id.timeline2);
        timeline3 = (LinearLayout) rootView.findViewById(R.id.timeline3);
        timeline4 = (LinearLayout) rootView.findViewById(R.id.timeline4);
        timeline5 = (LinearLayout) rootView.findViewById(R.id.timeline5);
        timeline6 = (LinearLayout) rootView.findViewById(R.id.timeline6);
        timeline7 = (LinearLayout) rootView.findViewById(R.id.timeline7);
        timeline8 = (LinearLayout) rootView.findViewById(R.id.timeline8);
        timeline9 = (LinearLayout) rootView.findViewById(R.id.timeline9);

        stop1 = (TextView) rootView.findViewById(R.id.stops1_name);
        stop2 = (TextView) rootView.findViewById(R.id.stops2_name);
        stop3 = (TextView) rootView.findViewById(R.id.stops3_name);
        stop4 = (TextView) rootView.findViewById(R.id.stops4_name);
        stop5 = (TextView) rootView.findViewById(R.id.stops5_name);
        stop6 = (TextView) rootView.findViewById(R.id.stops6_name);
        stop7 = (TextView) rootView.findViewById(R.id.stops7_name);
        stop8 = (TextView) rootView.findViewById(R.id.stops8_name);
        stop9 = (TextView) rootView.findViewById(R.id.stops9_name);
        departTime1 = (TextView) rootView.findViewById(R.id.stops1_time);
        departTime2 = (TextView) rootView.findViewById(R.id.stops2_time);
        departTime3 = (TextView) rootView.findViewById(R.id.stops3_time);
        departTime4 = (TextView) rootView.findViewById(R.id.stops4_time);
        departTime5 = (TextView) rootView.findViewById(R.id.stops5_time);
        departTime6 = (TextView) rootView.findViewById(R.id.stops6_time);
        departTime7 = (TextView) rootView.findViewById(R.id.stops7_time);
        departTime8 = (TextView) rootView.findViewById(R.id.stops8_time);
        departTime9 = (TextView) rootView.findViewById(R.id.stops9_time);

        dataRef = FirebaseDatabase.getInstance().getReference("Schedule").child(day).child(session);

        back = (ImageView) rootView.findViewById(R.id.btn_back);

        // Return to Schedule Fragment when back button is pressed
        back.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ScheduleFragment()).commit();
        });

        displayTimeline();

        return rootView;
    }

    private void displayTimeline() {

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String Stop1 = String.valueOf(snapshot.child("Stop1").getValue());
                    String Stop2 = String.valueOf(snapshot.child("Stop2").getValue());
                    String Stop3 = String.valueOf(snapshot.child("Stop3").getValue());
                    String Stop4 = String.valueOf(snapshot.child("Stop4").getValue());
                    String Stop5 = String.valueOf(snapshot.child("Stop5").getValue());
                    String Stop6 = String.valueOf(snapshot.child("Stop6").getValue());
                    String Stop7 = String.valueOf(snapshot.child("Stop7").getValue());
                    String Stop8 = String.valueOf(snapshot.child("Stop8").getValue());
                    String Stop9 = String.valueOf(snapshot.child("Stop9").getValue());
                    String DepartTime1 = String.valueOf(snapshot.child("DepartTime1").getValue());
                    String DepartTime2 = String.valueOf(snapshot.child("DepartTime2").getValue());
                    String DepartTime3 = String.valueOf(snapshot.child("DepartTime3").getValue());
                    String DepartTime4 = String.valueOf(snapshot.child("DepartTime4").getValue());
                    String DepartTime5 = String.valueOf(snapshot.child("DepartTime5").getValue());
                    String DepartTime6 = String.valueOf(snapshot.child("DepartTime6").getValue());
                    String DepartTime7 = String.valueOf(snapshot.child("DepartTime7").getValue());
                    String DepartTime8 = String.valueOf(snapshot.child("DepartTime8").getValue());
                    String DepartTime9 = String.valueOf(snapshot.child("DepartTime9").getValue());

                    stop1.setText(Stop1);
                    departTime1.setText(DepartTime1);
                    if (!"null".equals(Stop2)){
                        stop2.setText(Stop2);
                        departTime2.setText(DepartTime2);
                    }else {
                        timeline2.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop3)){
                        stop3.setText(Stop3);
                        departTime3.setText(DepartTime3);
                    }else {
                        timeline3.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop4)){
                        stop4.setText(Stop4);
                        departTime4.setText(DepartTime4);
                    }else {
                        timeline4.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop5)){
                        stop5.setText(Stop5);
                        departTime5.setText(DepartTime5);
                    }else {
                        timeline5.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop6)){
                        stop6.setText(Stop6);
                        departTime6.setText(DepartTime6);
                    }else {
                        timeline6.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop7)){
                        stop7.setText(Stop7);
                        departTime7.setText(DepartTime7);
                    }else {
                        timeline7.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop8)){
                        stop8.setText(Stop8);
                        departTime8.setText(DepartTime8);
                    }else {
                        timeline8.setVisibility(View.GONE);
                    }
                    if (!"null".equals(Stop9)){
                        stop9.setText(Stop9);
                        departTime9.setText(DepartTime9);
                    }else {
                        timeline9.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}