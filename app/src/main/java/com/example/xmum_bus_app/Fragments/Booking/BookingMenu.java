package com.example.xmum_bus_app.Fragments.Booking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.Fragments.MyViewHolder;
import com.example.xmum_bus_app.Fragments.ViewScheduleFragment;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;

public class BookingMenu extends Fragment {

    private RecyclerView recyclerViewMenu, recyclerViewSat, recyclerViewSun;
    private FirebaseRecyclerOptions<Schedule> options;
    private FirebaseRecyclerAdapter<Schedule, MyViewHolder2> adapter;
    private DatabaseReference dataRef, dataRefSat, dataRefSun;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_booking_menu, container, false);

        dataRef = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday Special");
        recyclerViewMenu = (RecyclerView) rootView.findViewById(R.id.recyclerView_bookingFri);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        dataRefSat = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday Special");
        recyclerViewSat = (RecyclerView) rootView.findViewById(R.id.recyclerView_bookingSat);
        recyclerViewSat.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        dataRefSun = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Sunday Special");
        recyclerViewSun = (RecyclerView) rootView.findViewById(R.id.recyclerView_bookingSun);
        recyclerViewSun.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        recyclerViewBooking(dataRef, "Friday Special", recyclerViewMenu);
        recyclerViewBooking(dataRefSat, "Saturday Special", recyclerViewSat);
        recyclerViewBooking(dataRefSun, "Sunday Special", recyclerViewSun);

        return rootView;
    }

    private void recyclerViewBooking(DatabaseReference dbRef, String day, RecyclerView recyclerView) {

        options = new FirebaseRecyclerOptions.Builder<Schedule>().setQuery(dbRef, Schedule.class).build();
        adapter = new FirebaseRecyclerAdapter<Schedule, MyViewHolder2>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder2 holder, int position, @NonNull Schedule model) {
                holder.bookingDay.setText(day);
                holder.bookingTime.setText(model.getDepartTime1());
                holder.bookingRoute.setText(model.getRoute());
                holder.bookingSeat.setText(model.getAvailableSeat());
                Picasso.get().load(model.getImageUrl()).into(holder.bookingImage);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("day",day);
                        bundle.putString("time", model.getDepartTime1());
                        bundle.putString("route", model.getRoute());
                        bundle.putString("seat", model.getAvailableSeat());
                        bundle.putString("stopName", model.getStop1());
                        bundle.putString("sessionId", model.getSessionId());

                        BookingDetails fragment2 = new BookingDetails();
                        fragment2.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragment2).commit();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_menu_card_design, parent, false);
                return new MyViewHolder2(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}