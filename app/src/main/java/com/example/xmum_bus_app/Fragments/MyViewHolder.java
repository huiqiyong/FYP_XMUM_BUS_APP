package com.example.xmum_bus_app.Fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xmum_bus_app.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView stopsImage, bookingImage;
    TextView stopsTitle, session, bookingDay, bookingTime, bookingRoute, bookingSeat, trackRouteName;
    View v;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        v = itemView;   // for schedule onClick listener

        bookingImage = itemView.findViewById(R.id.booking_image1);
        bookingDay = itemView.findViewById(R.id.booking_title1);
        bookingTime = itemView.findViewById(R.id.depart_time);
        bookingRoute = itemView.findViewById(R.id.route_title1);
        bookingSeat = itemView.findViewById(R.id.seat_capacity);

        stopsImage = itemView.findViewById(R.id.stops_image1);
        stopsTitle = itemView.findViewById(R.id.stops_title1);
        session = itemView.findViewById(R.id.session1);

        trackRouteName = itemView.findViewById(R.id.tv_route_name);
    }
}
