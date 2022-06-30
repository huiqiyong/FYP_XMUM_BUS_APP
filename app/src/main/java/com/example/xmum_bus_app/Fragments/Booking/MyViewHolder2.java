package com.example.xmum_bus_app.Fragments.Booking;

import android.animation.LayoutTransition;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xmum_bus_app.BusSchedule.Schedule;
import com.example.xmum_bus_app.R;

import java.util.ArrayList;

public class MyViewHolder2 extends RecyclerView.ViewHolder {

    ImageView bookingImage;
    TextView bookingDay, bookingTime, bookingRoute, bookingSeat, stopName, historySession, historyDay, historyTime, historyDate, historyUID, historySeat, historyStopName;
    View v;

    RelativeLayout expandedText;
    LinearLayout itemHLayout;
    CardView cardView;

    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);

        v = itemView;   // for schedule onClick listener

        //Expand cardView
        expandedText = itemView.findViewById(R.id.expanded_card);
        itemHLayout = itemView.findViewById(R.id.history_itemLayout);
        cardView = itemView.findViewById(R.id.card_history);

        bookingImage = itemView.findViewById(R.id.booking_image1);
        bookingDay = itemView.findViewById(R.id.booking_title1);
        bookingTime = itemView.findViewById(R.id.depart_time);
        bookingRoute = itemView.findViewById(R.id.route_title1);
        bookingSeat = itemView.findViewById(R.id.seat_capacity);
        stopName = itemView.findViewById(R.id.tv_stops);

        historySession = itemView.findViewById(R.id.session_history);
        historyDay = itemView.findViewById(R.id.day_history);
        historyDate = itemView.findViewById(R.id.date_history);
        historyTime = itemView.findViewById(R.id.depart_time_history);
        historyUID = itemView.findViewById(R.id.userID_history);
        historySeat = itemView.findViewById(R.id.seat_history);
        historyStopName = itemView.findViewById(R.id.stopName_history);

    }
}
