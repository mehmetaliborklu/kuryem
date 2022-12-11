package com.info.kuryem;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GetLocation {

    private static Geocoder geocoder;

    public static Address getFromLocation(double latitude, double longitude, int i) throws IOException {

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, i);
        Address address = null;

        if (addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;
    }

    public static Address getFromLocationName(String location, int i) throws IOException {

        List<Address> addresses = geocoder.getFromLocationName(location, i);
        Address address = null;

        if (addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;
    }
}
