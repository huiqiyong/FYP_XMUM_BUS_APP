package com.example.xmum_bus_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

public class TicketStatusAlarm extends BroadcastReceiver {

    private DatabaseReference userDbRef, friDbRef, satDbRef, sunDbRef;

    @Override
    public void onReceive(Context context, Intent intent) {

        String username=intent.getStringExtra("userID");
        userDbRef = FirebaseDatabase.getInstance().getReference("Users").child("Passenger").child(username);

        friDbRef = FirebaseDatabase.getInstance().getReference("Schedule").child("Friday Special");
        satDbRef = FirebaseDatabase.getInstance().getReference("Schedule").child("Saturday Special");
        sunDbRef = FirebaseDatabase.getInstance().getReference("Schedule").child("Sunday Special");

        String dayName = LocalDate.now().getDayOfWeek().name();

        if (dayName.equals("MONDAY")) {
            userDbRef.child("ticketFri").setValue("false");
            userDbRef.child("ticketFriBack").setValue("false");
            userDbRef.child("ticketSat").setValue("false");
            userDbRef.child("ticketSatBack").setValue("false");
            userDbRef.child("ticketSun").setValue("false");
            userDbRef.child("ticketSunBack").setValue("false");

            friDbRef.child("1Afternoon").child("AvailableSeat").setValue("36");
            friDbRef.child("2Night").child("AvailableSeat").setValue("36");
            satDbRef.child("1Morning").child("AvailableSeat").setValue("36");
            satDbRef.child("2Afternoon").child("AvailableSeat").setValue("36");
            sunDbRef.child("1Morning").child("AvailableSeat").setValue("36");
            sunDbRef.child("2Afternoon").child("AvailableSeat").setValue("36");
        }

    }
}
