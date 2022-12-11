package com.info.kuryem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class AddAdvert extends AppCompatActivity {

    Button buttonFirstAddress, buttonLastAddress;
    Toolbar toolbar;
    EditText firsAddress, lastAddress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);


        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar = findViewById(R.id.toolbar);

        buttonFirstAddress = findViewById(R.id.buttonAddFirstAddress);
        buttonLastAddress = findViewById(R.id.buttonAddAddress);
        firsAddress = findViewById(R.id.firsAddressText);
        lastAddress = findViewById(R.id.lastAddressText);

        buttonFirstAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AddAdvert.this, SelectLocation.class));
            }
        });

        buttonLastAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAdvert.this, SelectLocation.class));
            }
        });

        getLocationInfo();

    }

    private void getLocationInfo() {
        String sender = getIntent().getStringExtra("sender");

        if (sender.equals("SelectLocation")) {

            String firstLocation = getIntent().getStringExtra("firstAddress");
            String lastLocation = getIntent().getStringExtra("lastAddress");

            firsAddress.setVisibility(View.VISIBLE);
            buttonFirstAddress.setVisibility(View.INVISIBLE);
            firsAddress.setText(firstLocation); // first location set edilecek
            lastAddress.setVisibility(View.VISIBLE);
            buttonLastAddress.setVisibility(View.INVISIBLE);
            lastAddress.setText(lastLocation); // last adres set edilecek
        }
    }
}