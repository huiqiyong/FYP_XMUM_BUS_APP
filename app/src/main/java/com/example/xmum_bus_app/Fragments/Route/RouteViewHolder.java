package com.example.xmum_bus_app.Fragments.Route;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xmum_bus_app.R;
import com.google.android.material.button.MaterialButton;

public class RouteViewHolder extends RecyclerView.ViewHolder{

    TextView stopName, routeName, bNo, bStatus;
    MaterialButton stopSequence;
    LinearLayout item_layout;
    View v;

    public RouteViewHolder(@NonNull View itemView) {
        super(itemView);

        v = itemView;

        stopName = itemView.findViewById(R.id.stops_name);
        stopSequence = itemView.findViewById(R.id.stop_sequence);
        item_layout = itemView.findViewById(R.id.stop_list_layout);

        routeName = itemView.findViewById(R.id.txtRouteName);
        bNo = itemView.findViewById(R.id.txtBusNo);
        bStatus = itemView.findViewById(R.id.txtBusStatus);
    }
}
