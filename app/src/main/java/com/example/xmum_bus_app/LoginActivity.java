package com.example.xmum_bus_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmum_bus_app.Fragments.Booking.BookingDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etname, etpass;
    private Button btnpassenger, btndriver, btn_login;
    private String usertype, uname, password;
    private DatabaseReference reference;

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;

    ProgressBar progressBar;
    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Dropdown language menu
        currentLanguage = getIntent().getStringExtra(currentLang);

        spinner = (Spinner) findViewById(R.id.spinner);

        List<String> list = new ArrayList<String>();

        list.add(getString(R.string.language));
        list.add(getString(R.string.english));
        list.add(getString(R.string.mandarin));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        break;
                    case 2:
                        setLocale("zh");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users");

        etname = findViewById(R.id.username);
        etpass = findViewById(R.id.password);

        btnpassenger = findViewById(R.id.btn_passenger);
        btndriver = findViewById(R.id.btn_driver);

        usertype = "Passenger";
        btnpassenger.setBackgroundColor(getResources().getColor(R.color.black));
        btndriver.setBackgroundColor(getResources().getColor(R.color.non_selected));

        btnpassenger.setOnClickListener(view -> {
            usertype = "Passenger";
            btnpassenger.setBackgroundColor(getResources().getColor(R.color.black));
            btndriver.setBackgroundColor(getResources().getColor(R.color.non_selected));
        });

        btndriver.setOnClickListener(view -> {
            usertype = "Driver";
            btnpassenger.setBackgroundColor(getResources().getColor(R.color.non_selected));
            btndriver.setBackgroundColor(getResources().getColor(R.color.black));
        });
    }

    public void login(View view) {
        btn_login = findViewById(R.id.btn_login);
        btn_login.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        uname = etname.getText().toString().toLowerCase(Locale.ROOT);
        password = etpass.getText().toString();
        if (!uname.isEmpty() && !password.isEmpty()) {
            reference.child(usertype).child(uname).addListenerForSingleValueEvent(listener);
        }
        else {
            btn_login.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, getString(R.string.inputCheck), Toast.LENGTH_SHORT).show();
        }
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                String pass = snapshot.child("password").getValue(String.class);
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), pass);

                if (result.verified) {
                    if (usertype.equals("Driver")) {
                        Intent i = new Intent(LoginActivity.this, FingerprintActivity.class);
                        i.putExtra("username", uname);
                        i.putExtra("driver", true);
                        startActivity(i);
                        finish();
                    }
                    else if (usertype.equals("Passenger")) {
                        Intent i = new Intent(LoginActivity.this, FingerprintActivity.class);
                        i.putExtra("username", uname);
                        i.putExtra("driver", false);
                        startActivity(i);
                        finish();
                    }
                }
                else {
                    btn_login.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, getString(R.string.passwordError), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                btn_login.setClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, getString(R.string.noRecord), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
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

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, LoginActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.langToast), Toast.LENGTH_SHORT).show();
        }
    }

}