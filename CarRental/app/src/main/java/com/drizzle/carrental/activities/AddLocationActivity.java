package com.drizzle.carrental.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.google.android.libraries.places.widget.AutocompleteActivity.*;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    final int AUTOCOMPLETE_REQUEST_CODE = 1;

    /**
     * UI Control Handlers
     */
    private ImageButton buttonBack;

    private Button buttonAddLocation;

    private GoogleMap map;

    private Marker marker;

    SupportMapFragment mapView;

    private Boolean getLocation = false;
    /**
     * Vars for location address
     */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private Location currentLocation;
    private LocationCallback locationCallback;

    private EditText editTextSearch;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);

        buttonAddLocation = findViewById(R.id.button_submit);

        buttonBack.setOnClickListener(this);

        buttonAddLocation.setOnClickListener(this);

        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_location);
        mapView.getMapAsync(this);

//        editTextSearch = findViewById(R.id.edittext_search);
//        editTextSearch.setOnClickListener(this);

    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Log.i("tiny-debug", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("tiny-debug", "An error occurred: " + status);
            }
        });


        //String apiKey = getString(R.string.google_api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), apiKey);
//        }

// Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);



        getControlHandlersAndLinkActions();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                marker.setPosition(place.getLatLng());
                marker.setTitle(place.getName());

                editTextSearch.setText(place.getName());

            } else if (resultCode == RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_back:
                finish();
                break;

            case R.id.button_submit:
                if (getLocation) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

//            case R.id.edittext_search:
//                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//
//                // Start the autocomplete intent.
//                Intent intent = new Autocomplete.IntentBuilder(
//                        AutocompleteActivityMode.OVERLAY, fields)
//                        .build(AddLocationActivity.this);
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        getCurrentLocation();


        map.setOnMapClickListener(this);
    }

    private void getCurrentLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);

                marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Marker"));

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.DEFAULT_MAP_ZOOM_LEVEL));

                getLocation = true;
                Constants.selectedLocation = currentLocation;

                fusedLocationClient.removeLocationUpdates(locationCallback);

            }

        };
        startLocationUpdates();
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }


    @Override
    public void onMapClick(LatLng point) {

        Location location = new Location("addedLocation");
        location.setLatitude(point.latitude);
        location.setLongitude(point.longitude);

        Constants.selectedLocation = location;
    }

}
