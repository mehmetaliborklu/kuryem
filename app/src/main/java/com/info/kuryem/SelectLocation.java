package com.info.kuryem;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.info.kuryem.databinding.ActivitySelectLocationBinding;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SelectLocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivitySelectLocationBinding binding;
    private Geocoder geocoder;
    private String street;
    private Dialog dialog;
    private TextView firstAddress;
    private TextView lastAddress;
    private Boolean isFirsAddressSelected = false;
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firstAddress = findViewById(R.id.selectedfirstAddress);
        lastAddress = findViewById(R.id.selectedlastAddress);
        searchView = findViewById(R.id.sv_location);

        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(SelectLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrenLocation();
        } else {
            ActivityCompat.requestPermissions(SelectLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        geocoder = new Geocoder(this);
        firstAddress = findViewById(R.id.selectedfirstAddress);
        lastAddress = findViewById(R.id.selectedlastAddress);
        searchView = findViewById(R.id.sv_location);
        searchView.setQueryHint(getResources().getString(R.string.searchAdress));

        getDialog();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals((""))){
                    Geocoder geocoder = new Geocoder(SelectLocation.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void getCurrenLocation() {
        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder= new LocationSettingsRequest.Builder().addAllLocationRequests(Collections.singleton(locationRequest));
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result= LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response=task.getResult(ApiException.class);
                }
                catch (ApiException e){
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException= (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(SelectLocation.this,1);

                            } catch (IntentSender.SendIntentException sendIntentException) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });


        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Buradayım");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        street = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                street = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions().position(latLng).title(street).draggable(true));
                getSelectedLocation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mMap.clear();
        street = "";
        LatLng latLng = marker.getPosition();

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                street = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions().position(latLng).title(street).draggable(true));
                getSelectedLocation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSelectedLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectLocation.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SelectLocation.this).inflate(R.layout.check_selected_location, (ConstraintLayout)findViewById(R.id.selected_location_layout));

        builder.setView(view);

        ((TextView) view.findViewById(R.id.check_message)).setText(street + "\nOnaylıyor musunuz?");

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.check_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                street = "";
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.check_okey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstAddress.getText() == "") {
                    firstAddress.setText(street);
                    isFirsAddressSelected = true;
                    getDialog();
                } else if ( firstAddress.getText() != "" && lastAddress.getText() == "") {
                    lastAddress.setText(street);
                    sendIntent();
                }
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
    private void getDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectLocation.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SelectLocation.this).inflate(R.layout.layout_dialog, (ConstraintLayout)findViewById(R.id.dialogContainer));

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        if(isFirsAddressSelected){
            ((TextView) view.findViewById(R.id.message)).setText(getResources().getString(R.string.lastLocation));
        } else {
            ((TextView) view.findViewById(R.id.message)).setText(getResources().getString(R.string.firstLocation));
        }

        view.findViewById(R.id.okey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void sendIntent() {
        Intent intent = new Intent(SelectLocation.this, AddAdvert.class);
        intent.putExtra("sender", "SelectLocation");
        intent.putExtra("firstAddress", firstAddress.getText());
        intent.putExtra("lastAddress", lastAddress.getText());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrenLocation();
            }
        }
    }
}