package com.example.xmum_bus_app.Fragments;

import static com.example.xmum_bus_app.GlobalVariable.arriveNotification;
import static com.example.xmum_bus_app.GlobalVariable.busStatusNotification;
import static com.example.xmum_bus_app.GlobalVariable.trackFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.example.xmum_bus_app.Fragments.Route.TrackBusMapActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

public class TrackFragment extends Fragment {

    private TextView dayTitle;
    private RecyclerView recyclerView_daily, recyclerView_special;
    private FirebaseRecyclerOptions<Schedule> options;
    private FirebaseRecyclerAdapter<Schedule,MyViewHolder> adapter;
    private DatabaseReference dbRefMon, dbRefFri, dbRefSatSun, dbRefFriS, dbRefSatS, dbRefSunS, dbRefTrackStatus;
    PassengerActivity passengerActivity;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_background));

        passengerActivity = (PassengerActivity) getActivity();
        passengerActivity.setFragmentTitle("Track Bus");
        trackFragment = true;
        busStatusNotification = true;
        arriveNotification = true;

        //Retrieve username from Passenger Activity
        Bundle results = passengerActivity.getMyData();
        userID = results.getString("username");

        dbRefMon = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Monday-Thursday");
        dbRefFri = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday");
        dbRefSatSun = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday-Sunday");
        dbRefFriS = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday Special");
        dbRefSatS = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday Special");
        dbRefSunS = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Sunday Special");

        recyclerView_daily = rootView.findViewById(R.id.recyclerView_trackRouteDaily);
        recyclerView_daily.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView_special = rootView.findViewById(R.id.recyclerView_trackRouteSpecial);
        recyclerView_special.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        String dayName = LocalDate.now().getDayOfWeek().name();
        switch (dayName) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
                getRouteList(dbRefMon, "Monday-Thursday", recyclerView_daily);
                dayTitle = rootView.findViewById(R.id.day_title);
                dayTitle.setVisibility(View.INVISIBLE);
                break;
            case "FRIDAY":
                getRouteList(dbRefFri, "Friday", recyclerView_daily);
                getRouteList(dbRefFriS, "Friday Special", recyclerView_special);
                break;
            case "SATURDAY":
                getRouteList(dbRefSatSun, "Saturday", recyclerView_daily);
                getRouteList(dbRefSatS, "Saturday Special", recyclerView_special);
                break;
            case "SUNDAY":
                getRouteList(dbRefSatSun, "Sunday", recyclerView_daily);
                getRouteList(dbRefSunS, "Sunday Special", recyclerView_special);
                break;
        }

        return rootView;
    }

    private void getRouteList(DatabaseReference dbRef, String day, RecyclerView recyclerView) {
        options = new FirebaseRecyclerOptions.Builder<Schedule>().setQuery(dbRef, Schedule.class).build();
        adapter = new FirebaseRecyclerAdapter<Schedule, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Schedule model) {
                holder.trackRouteName.setText(model.getRoute());

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //check bus tracking status
                        dbRefTrackStatus = FirebaseDatabase.getInstance().getReference().child("Routes")
                                .child(day).child(model.getRoute());

                        dbRefTrackStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String s = snapshot.child("TrackStatus").getValue().toString();
                                String route = snapshot.child("RouteName").getValue().toString();
                                String routeType = snapshot.child("RouteType").getValue().toString();
                                if (s.equals("0") && trackFragment){
                                    Toast.makeText(requireContext(), "No bus detected on this route", Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent i = new Intent(requireContext(), TrackBusMapActivity.class);
                                    i.putExtra("route", route);
                                    i.putExtra("routeType", routeType);
                                    i.putExtra("day", day);
                                    i.putExtra("username", userID);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_fragment_item_design, parent, false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}