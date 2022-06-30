package com.example.xmum_bus_app;

import static com.example.xmum_bus_app.GlobalVariable.trackFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.BusSchedule.BusStop;
import com.example.xmum_bus_app.Fragments.ContactFragment;
import com.example.xmum_bus_app.Fragments.MyBookingFragment;
import com.example.xmum_bus_app.Fragments.Route.RouteFragment;
import com.example.xmum_bus_app.Fragments.ScanQRCodeFragment;
import com.example.xmum_bus_app.Fragments.ScheduleFragment;
import com.example.xmum_bus_app.Fragments.TrackFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class PassengerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    private static final int REQUEST_CALL = 1;
    static final float END_SCALE = 0.7f;
    static final int TIME_INTERVAL = 2000;
    long backPressed;
    String data, phoneNo;

    LinearLayout contentView;
    ImageView menuIcon;
    TextView fragmentTitle, passengerId;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_passenger);

        //Retrieve username from LoginActivity or FingerprintActivity
        Intent intent = getIntent();
        data = intent.getExtras().getString("username");

        //Hooks
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);
        fragmentTitle = findViewById(R.id.fragment_title);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        passengerId = header.findViewById(R.id.tvPassengerId);
        passengerId.setText(data);

        //Update ticket status
        startUpdate();

        //Start Track Fragment when return from TrackBusMapActivity
        if (trackFragment) {
            fragmentTitle.setText(getString(R.string.title4));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TrackFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_track);
        }

        //Start Booking Fragment by default
        else if (savedInstanceState == null) {
            fragmentTitle.setText(getString(R.string.title1));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MyBookingFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_booking);
        }
        navigationDrawer();

    }

    private void startUpdate() {

        AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(this,TicketStatusAlarm.class);
        i.putExtra("userID", getMyData().getString("username"));
        PendingIntent alarmIntent=PendingIntent.getBroadcast(this,0,i,0);

        // Set the alarm to start at 1 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    //Pass username to BookingDetails Fragment
    public Bundle getMyData() {
        Bundle hm = new Bundle();
        hm.putString("username",data);
        return hm;
    }

    //Navigation Drawer Functions
    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //Scale the view based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //Translate the view, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            if (backPressed + TIME_INTERVAL > System.currentTimeMillis()){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }else{
                Toast.makeText(getBaseContext(), getString(R.string.backPressedToast), Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_booking:
                fragmentTitle.setText(getString(R.string.title1));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyBookingFragment()).commit();
                break;
            case R.id.nav_route:
                fragmentTitle.setText(getString(R.string.title2));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RouteFragment()).commit();
                break;
            case R.id.nav_schedule:
                fragmentTitle.setText(getString(R.string.title3));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScheduleFragment()).commit();
                break;
            case R.id.nav_track:
                fragmentTitle.setText(getString(R.string.title4));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TrackFragment()).commit();
                break;
            case R.id.nav_scan:
                fragmentTitle.setText(getString(R.string.title5));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScanQRCodeFragment()).commit();
                break;
            case R.id.nav_eContact:
                fragmentTitle.setText(getString(R.string.title6));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactFragment()).commit();
                break;
            case R.id.nav_signout:
                Intent i = new Intent(PassengerActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.signoutMessage), Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragmentTitle(String title){
        fragmentTitle.setText(title);
    }

    public void makePhoneCall(String number) {

        phoneNo = number;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(phoneNo);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}