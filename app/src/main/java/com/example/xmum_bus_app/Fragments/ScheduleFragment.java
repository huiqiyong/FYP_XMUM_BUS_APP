package com.example.xmum_bus_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerViewMon, recyclerViewFri, recyclerViewSatSun, recyclerViewFriSpecial, recyclerViewSatSpecial, recyclerViewSun;
    private FirebaseRecyclerOptions<Schedule> options;
    private FirebaseRecyclerAdapter<Schedule,MyViewHolder>adapter;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseReference4, databaseReference5, databaseReference6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_schedule, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_background));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Monday-Thursday");
        recyclerViewMon = (RecyclerView) rootView.findViewById(R.id.recyclerView_Monday);
        recyclerViewMon.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday");
        recyclerViewFri = (RecyclerView) rootView.findViewById(R.id.recyclerView_Friday);
        recyclerViewFri.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday-Sunday");
        recyclerViewSatSun = (RecyclerView) rootView.findViewById(R.id.recyclerView_Sat_Sun);
        recyclerViewSatSun.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Friday Special");
        recyclerViewFriSpecial = (RecyclerView) rootView.findViewById(R.id.recyclerView_Friday_Special);
        recyclerViewFriSpecial.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference5 = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Saturday Special");
        recyclerViewSatSpecial = (RecyclerView) rootView.findViewById(R.id.recyclerView_Sat_Special);
        recyclerViewSatSpecial.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference6 = FirebaseDatabase.getInstance().getReference().child("Schedule").child("Sunday Special");
        recyclerViewSun = (RecyclerView) rootView.findViewById(R.id.recyclerView_Sunday);
        recyclerViewSun.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewSchedule(databaseReference, "Monday-Thursday", recyclerViewMon);
        recyclerViewSchedule(databaseReference2, "Friday", recyclerViewFri);
        recyclerViewSchedule(databaseReference3, "Saturday-Sunday", recyclerViewSatSun);
        recyclerViewSchedule(databaseReference4, "Friday Special", recyclerViewFriSpecial);
        recyclerViewSchedule(databaseReference5, "Saturday Special", recyclerViewSatSpecial);
        recyclerViewSchedule(databaseReference6, "Sunday Special", recyclerViewSun);

        return rootView;
    }

    private void recyclerViewSchedule(DatabaseReference dbRef, String day, RecyclerView recyclerView) {

        options = new FirebaseRecyclerOptions.Builder<Schedule>().setQuery(dbRef, Schedule.class).build();
        adapter = new FirebaseRecyclerAdapter<Schedule, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Schedule model) {
                holder.stopsTitle.setText(model.getRoute());
                holder.session.setText(model.getSession());
                Picasso.get().load(model.getImageUrl()).into(holder.stopsImage);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("day",day);
                        bundle.putString("sessionId", model.getSessionId());

                        ViewScheduleFragment fragment2 = new ViewScheduleFragment();
                        fragment2.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragment2).commit();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_card_design, parent, false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}