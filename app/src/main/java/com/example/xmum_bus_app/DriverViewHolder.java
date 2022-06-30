package com.example.xmum_bus_app;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DriverViewHolder extends RecyclerView.ViewHolder{

    View v;
    TextView routeName, startTime;

    public DriverViewHolder(@NonNull View itemView) {
        super(itemView);

        v = itemView;

        routeName = itemView.findViewById(R.id.route_name);
        startTime = itemView.findViewById(R.id.schedule_time);
    }
}
