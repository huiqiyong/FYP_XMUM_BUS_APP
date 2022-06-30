package com.example.xmum_bus_app.Fragments.Booking;

import android.animation.LayoutTransition;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingHistory extends Fragment {

    private String userID, bookingStatus, dayName;
    private TextView message;
    private RecyclerView recyclerViewFri, recyclerViewSat, recyclerViewSun;
    private FirebaseRecyclerOptions<Schedule> options;
    private FirebaseRecyclerAdapter<Schedule, MyViewHolder2> adapter;
    private DatabaseReference dataRef, dataRefSat, dataRefSun, userDBRef;
    PassengerActivity passengerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_booking_history, container, false);

        message = (TextView) rootView.findViewById(R.id.booking_message);

        //Retrieve username from Passenger Activity
        passengerActivity = (PassengerActivity) getActivity();
        Bundle results = passengerActivity.getMyData();
        userID = results.getString("username");

        userDBRef = FirebaseDatabase.getInstance().getReference("Users").child("Passenger").child(userID);

        dataRef = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday Special");
        recyclerViewFri = (RecyclerView) rootView.findViewById(R.id.recyclerView_historyFri);
        recyclerViewFri.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        dataRefSat = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday Special");
        recyclerViewSat = (RecyclerView) rootView.findViewById(R.id.recyclerView_historySat);
        recyclerViewSat.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        dataRefSun = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Sunday Special");
        recyclerViewSun = (RecyclerView) rootView.findViewById(R.id.recyclerView_historySun);
        recyclerViewSun.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        checkBookingStatus();

        return rootView;
    }

    private void checkBookingStatus() {

        userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String fri1 = snapshot.child("ticketFri").getValue().toString();
                String fri2 = snapshot.child("ticketFriBack").getValue().toString();
                String sat1 = snapshot.child("ticketSat").getValue().toString();
                String sat2 = snapshot.child("ticketSatBack").getValue().toString();
                String sun1 = snapshot.child("ticketSun").getValue().toString();
                String sun2 = snapshot.child("ticketSunBack").getValue().toString();

                if (fri1.equals("false") && fri2.equals("false") && sat1.equals("false") && sat2.equals("false") && sun1.equals("false") && sun2.equals("false")){
                    bookingStatus = "close";
                }else {
                    bookingStatus = "open";
                }

                switch (bookingStatus){
                    case "close":
                        message.setVisibility(View.VISIBLE);
                        break;
                    case "open":
                        if ((fri1.equals("true") && fri2.equals("true"))) {
                            recyclerViewBookingHistory(dataRef, recyclerViewFri, "Friday Special", 1);
                        } else if (fri1.equals("false") && fri2.equals("true")) {
                            recyclerViewBookingHistory(dataRef, recyclerViewFri, "Friday Special", 2);
                        } else if (fri1.equals("true") && fri2.equals("false")) {
                            recyclerViewBookingHistory(dataRef, recyclerViewFri, "Friday Special", 3);
                        } else if (fri1.equals("false") && fri2.equals("false")) {
                            recyclerViewFri.setVisibility(View.GONE);
                        }

                        if (sat1.equals("true") && sat2.equals("true")) {
                            recyclerViewBookingHistory(dataRefSat, recyclerViewSat, "Saturday Special", 1);
                        } else if (sat1.equals("false") && sat2.equals("true")) {
                            recyclerViewBookingHistory(dataRefSat, recyclerViewSat, "Saturday Special", 2);
                        } else if (sat1.equals("true") && sat2.equals("false")) {
                            recyclerViewBookingHistory(dataRefSat, recyclerViewSat, "Saturday Special", 3);
                        } else if (sat1.equals("false") && sat2.equals("false")) {
                            recyclerViewSat.setVisibility(View.GONE);
                        }

                        if (sun1.equals("true") && sun2.equals("true")) {
                            recyclerViewBookingHistory(dataRefSun, recyclerViewSun, "Sunday Special", 1);
                        } else if (sun1.equals("false") && sun2.equals("true")) {
                            recyclerViewBookingHistory(dataRefSun, recyclerViewSun, "Sunday Special", 2);
                        } else if (sun1.equals("true") && sun2.equals("false")) {
                            recyclerViewBookingHistory(dataRefSun, recyclerViewSun, "Sunday Special", 3);
                        } else if (sun1.equals("false") && sun2.equals("false")) {
                            recyclerViewSun.setVisibility(View.GONE);
                        }
                        break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recyclerViewBookingHistory(DatabaseReference dbRef, RecyclerView recyclerView, String day, Integer status) {

        options = new FirebaseRecyclerOptions.Builder<Schedule>().setQuery(dbRef, Schedule.class).build();
        adapter = new FirebaseRecyclerAdapter<Schedule, MyViewHolder2>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder2 holder, int position, @NonNull Schedule model) {
                if (day.equals("Sunday Special")){
                    dayName = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).toString();
                }else if (day.equals("Saturday Special")){
                    dayName = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).toString();
                }else {
                    dayName = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toString();
                }
                String p = String.valueOf(position);
                holder.historySession.setText(model.getSession());
                holder.historyDay.setText(day);
                holder.historyDate.setText(dayName);
                holder.historyTime.setText(model.getDepartTime1());
                holder.historyStopName.setText(model.getStop1());
                holder.historyUID.setText(userID);
                if (status == 1){
                    holder.historySeat.setText("1");
                }else if (status == 2){
                    switch (p){
                        case "0":
                            holder.historySeat.setText("0");
                            holder.historySeat.setTextColor(Color.parseColor("#ff0000"));
                            break;
                        case "1":
                            holder.historySeat.setText("1");
                            break;
                    }
                }else if (status == 3){
                    switch (p){
                        case "0":
                            holder.historySeat.setText("1");
                            break;
                        case "1":
                            holder.historySeat.setText("0");
                            holder.historySeat.setTextColor(Color.parseColor("#ff0000"));
                            break;
                    }
                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TransitionManager.beginDelayedTransition(holder.itemHLayout, new AutoTransition());
                        if (holder.expandedText.getVisibility() == View.GONE){
                            holder.expandedText.setVisibility(View.VISIBLE);
                        }else {
                            holder.expandedText.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_history_item_design, parent, false);
                return new MyViewHolder2(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}