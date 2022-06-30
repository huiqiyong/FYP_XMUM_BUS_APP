package com.example.xmum_bus_app.Fragments.Booking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.Fragments.MyBookingFragment;
import com.example.xmum_bus_app.PassengerActivity;
import com.example.xmum_bus_app.R;
import com.example.xmum_bus_app.TicketStatusAlarm;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingDetails extends Fragment {

    private ImageView btn_back;
    private String day, time, route, seat, stopName, session, uID, bookDate;
    private TextView tvDay, tvStopName, etTime, etRoute, etSeat;
    private MaterialButton btnBook;
    private FirebaseDatabase rootNode;
    private DatabaseReference dbRef, userDBRef, dbRefRecord;
    PassengerActivity passengerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_booking_details, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_content_background));

        //Retrieve username from Passenger Activity
        passengerActivity = (PassengerActivity) getActivity();
        Bundle results = passengerActivity.getMyData();
        uID = results.getString("username");

        tvDay = (TextView) rootView.findViewById(R.id.tv_day);
        tvStopName = (TextView) rootView.findViewById(R.id.tv_stops);
        etTime = (TextView) rootView.findViewById(R.id.edtTime);
        etRoute = (TextView) rootView.findViewById(R.id.edtRoute);
        etSeat = (TextView) rootView.findViewById(R.id.edtSeat);
        btnBook = (MaterialButton) rootView.findViewById(R.id.btnBookSeat);

        btn_back = (ImageView) rootView.findViewById(R.id.btn_back);

        // Return to Booking Fragment when back button is pressed
        btn_back.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MyBookingFragment()).commit();
        });

        //Retrieve day, departTime, routeName, AvailableSeat, stopName from BookingMenu Fragment
        Bundle bundle = this.getArguments();
        if(bundle != null){
            day = bundle.getString("day");
            time = bundle.getString("time");
            route = bundle.getString("route");
            seat = bundle.getString("seat");
            stopName = bundle.getString("stopName");
            session = bundle.getString("sessionId");
        }

        tvDay.setText(day);
        tvStopName.setText(stopName);
        etTime.setText(time);
        etRoute.setText(route);
        etSeat.setText(seat);

        dbRef = FirebaseDatabase.getInstance().getReference("Schedule").child(day).child(session);
        dbRefRecord = FirebaseDatabase.getInstance().getReference().child("BookingRecord");

        btnBook.setOnClickListener(view -> {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String dayName = LocalDate.now().getDayOfWeek().name();
                        rootNode = FirebaseDatabase.getInstance();
                        userDBRef = rootNode.getReference("Users").child("Passenger").child(uID);

                        if (dayName.equals("MONDAY") || dayName.equals("TUESDAY") || dayName.equals("WEDNESDAY") || dayName.equals("THURSDAY")){
                            Integer seatNo = Integer.parseInt(snapshot.child("AvailableSeat").getValue().toString());
                            if (seatNo > 0){
                                if (day.equals("Friday Special") && route.equals("Route 1")) {
                                    AvailableSeat("ticketFri", seatNo, "FRIDAY");
                                }else if (day.equals("Friday Special") && route.equals("Route 2")) {
                                    AvailableSeat("ticketFriBack", seatNo, "FRIDAY");
                                }

                                if (day.equals("Saturday Special") && route.equals("Route 1")){
                                    AvailableSeat("ticketSat", seatNo, "SATURDAY");
                                }else if (day.equals("Saturday Special") && route.equals("Route 2")) {
                                    AvailableSeat("ticketSatBack", seatNo, "SATURDAY");
                                }

                                if (day.equals("Sunday Special") && route.equals("Route 1")) {
                                    AvailableSeat("ticketSun", seatNo, "SUNDAY");
                                }else if (day.equals("Sunday Special") && route.equals("Route 2")) {
                                    AvailableSeat("ticketSunBack", seatNo, "SUNDAY");
                                }
                            }else {
                                Toast.makeText(requireContext(), getString(R.string.toast1), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(requireContext(), getString(R.string.toast2), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        Toast.makeText(requireContext(), getString(R.string.toast3), Toast.LENGTH_SHORT).show();

        return rootView;
    }

    public void AvailableSeat(String ticket, Integer number, String dayName){
        userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String t = snapshot.child(ticket).getValue().toString();
                if (t.equals("false")){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.confirmBooking))
                            .setCancelText(getString(R.string.no))
                            .setConfirmText(getString(R.string.yes))
                            .showCancelButton(true)
                            .setCancelClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                            })
                            .setConfirmClickListener(sweetAlertDialog -> {
                                userDBRef.child(ticket).setValue("true");
                                Integer s = number - 1;
                                String seat = Integer.toString(s);
                                dbRef.child("AvailableSeat").setValue(seat);
                                etSeat.setText(seat);

                                // Set notification alarm
                                AlarmManager alarmManager=(AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                                Intent i=new Intent(requireContext(), BookingAlarm.class);
                                i.putExtra("day", dayName);
                                PendingIntent alarmIntent=PendingIntent.getBroadcast(requireContext(),0,i,0);
                                // Convert string to long
                                long alarmStartTime=setNotificationTime(time);
                                alarmManager.set(AlarmManager.RTC_WAKEUP,alarmStartTime,alarmIntent);

                                // Save booking record into firebase
                                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (day.equals("Sunday Special")){
                                            bookDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).toString();
                                        }else if (day.equals("Saturday Special")){
                                            bookDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).toString();
                                        }else {
                                            bookDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toString();
                                        }
                                        String sessionName = String.valueOf(snapshot.child("Session").getValue());

                                        HashMap<String, String> recordMap = new HashMap<>();
                                        recordMap.put("route", route);
                                        recordMap.put("date", bookDate);
                                        recordMap.put("session", sessionName);
                                        recordMap.put("time", time);
                                        recordMap.put("studentID", uID);
                                        recordMap.put("stops", stopName);

                                        dbRefRecord.push().setValue(recordMap);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                sweetAlertDialog.setTitleText(getString(R.string.successBooking))
                                        .setContentText(getString(R.string.contentSuccessBooking))
                                        .setConfirmText(getString(R.string.ok))
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(sweetAlertDialog1 -> {
                                            sweetAlertDialog.dismiss();
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //convert time from 24hrs based to hour and minute
    private long setNotificationTime(String t){  //time in 24hrs eg.1320 for 1.20pm
        String[] strings = t.split(":");

        int hour=Integer.parseInt(strings[0]);  //convert to hour eg.13hr
        int min=Integer.parseInt(strings[1]);   //convert to min eg.20min
        min=min-10;     //change the time to remind user 10min before the schedule met

        if(min<0){      //convert time to proper value if the time get deducted to negative value
            hour--;     //assume time 13hr 20min will become eg. 12hr
            min=min+60;     //will become eg. 50min
        }

        Calendar startTime=Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,hour);   //24hrs system
        startTime.set(Calendar.MINUTE,min);
        startTime.set(Calendar.SECOND,0);
        long alarmStartTime=startTime.getTimeInMillis();

        return alarmStartTime;
    }
}