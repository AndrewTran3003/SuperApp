package com.example.andrewtran.superapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewtran.superapp.models.Route;
import com.example.andrewtran.superapp.models.RouteFinder;
import com.example.andrewtran.superapp.models.RouteFinderListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map_Activity_FindWays extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,RouteFinderListener {

    private GoogleMap mMap;
    private AutoCompleteTextView startingPointInput;
    private AutoCompleteTextView destinationInput;
    private Button searchButton;
    private Boolean locationGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private final LatLngBounds latLngBounds = new LatLngBounds(new LatLng(-56,-151),new LatLng(-40,-56));
    private GoogleApiClient mGoogleApiClient;
    private List<Marker> originMarkers;
    private List<Marker> destinationMarkers ;
    private List<Polyline> polylinePaths;
    private final float DEFAULT_ZOOM = 15;
    private ImageView deleteStartingPoint;
    private ImageView deleteDestination;
    private FloatingActionButton toggleButton;
    private LinearLayout searchBar;



    //private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_findways);
        startingPointInput = findViewById(R.id.startingPoint);
        destinationInput = findViewById(R.id.destination);
        searchButton = findViewById(R.id.searchButton);
        searchBar = findViewById(R.id.searchBar);
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        polylinePaths = new ArrayList<>();
        deleteStartingPoint = findViewById(R.id.deleteStartingPoint);
        deleteDestination = findViewById(R.id.deleteDestination);
        toggleButton = findViewById(R.id.toggleButton);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, Places.getGeoDataClient(this),latLngBounds,null );
        getLocationPermission();
        initStartingPointInput();
        initDestinationInput();
        initSearchButton();
        initDeleteStartingPoint();
        initDeleteDestination();
        initToggleButton();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void initMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(Map_Activity_FindWays.this, "Map is now created", Toast.LENGTH_SHORT).show();
                mMap = googleMap;
                if (locationGranted) {
                    getCurrentLocation();
                    if (ActivityCompat.checkSelfPermission(Map_Activity_FindWays.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map_Activity_FindWays.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                    mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
                }
            }
        });
    }
    private void initStartingPointInput(){


        startingPointInput.setOnItemClickListener(mAutocompleteClickListener);

        startingPointInput.setAdapter(mPlaceAutocompleteAdapter);
        startingPointInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    new GetSearchedLocation().execute(startingPointInput.getText().toString());
                    checkToFindRoute();

                }
                return false;
            }


        });



    }

    private void initDeleteStartingPoint(){
        deleteStartingPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startingPointInput.setText("");
            }
        });
    }
    private void initDeleteDestination(){
        deleteDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationInput.setText("");
            }
        });

    }
    private void initToggleButton(){

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBar.getVisibility() == View.VISIBLE){
                    searchBar.setVisibility(View.INVISIBLE);
                }
                else{
                    searchBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initDestinationInput(){

        destinationInput.setOnItemClickListener(mAutocompleteClickListener);
        destinationInput.setAdapter(mPlaceAutocompleteAdapter);
        destinationInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    new GetSearchedLocation().execute(destinationInput.getText().toString());
                    checkToFindRoute();
                }
                return false;
            }


        });
    }

    private void initSearchButton(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startingPointText = startingPointInput.getText().toString();
                String destinationPointText = destinationInput.getText().toString();
                if(startingPointText == null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter starting point!",Toast.LENGTH_SHORT);
                    toast.show();
                    return;

                }
                else if (destinationPointText == null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter destination!",Toast.LENGTH_SHORT);
                    toast.show();
                    return;

                }
                else{
                    new RouteFinder(Map_Activity_FindWays.this, startingPointText,destinationPointText,getApplicationContext()).execute();
                }
            }
        });
    }
    private void checkToFindRoute() {
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            String placeID = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeID);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                /*When called, this method will erase the buffer.
                if we have a Place object set to be equal to this place object,
                when release is called, both object will be erased.
                Need a way to store it somehow
                */
                places.release();
                return;
            }
            final Place place = places.get(0);
            places.release();


        }
    };

    private void getCurrentLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(locationGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Map_Activity_FindWays.this, "Found location",Toast.LENGTH_SHORT).show();
                            Location currentLocation = (Location) task.getResult();

                        }
                        else{
                            Toast.makeText(Map_Activity_FindWays.this, "Current location is unavailable",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Toast.makeText(Map_Activity_FindWays.this, "getLocationError: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    private void getLocationPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length;i++){
                        if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                            locationGranted = false;
                            return;
                        }
                    }
                    locationGranted = true;
                    initMap();
                }
            }
        }
    }

    @Override
    public void onDirectionFinderStart() {


        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }
    @Override
    public void onDirectionFinderSuccess(Route route) {
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .title(route.getStartAddress())
                                    .position(new LatLng(route.getStartAddressLat(),route.getStartAddressLng()))));
        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .title(route.getEndDestination())
                                    .position(new LatLng(route.getEndDestinationLat(),route.getEndDestinationLng()))));

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(Color.BLUE)
                .width(10);
        for (int i = 0; i < route.getOverviewPolyline().size();i++){
            polylineOptions.add(route.getOverviewPolyline().get(i));
        }
        polylinePaths.add(mMap.addPolyline(polylineOptions));
        moveCamera(new LatLng(route.getStartAddressLat(),route.getStartAddressLng()),DEFAULT_ZOOM,"StartingPoint");
    }

    private class GetSearchedLocation extends AsyncTask<String, Void, Address> {


        private static final String TAG = "Blah";

        @Override

        protected Address doInBackground(String... strings) {
            Geocoder geocoder = new Geocoder(Map_Activity_FindWays.this);
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(strings[0],3);
            }
            catch (IOException e){
                Log.d(TAG, "getSearchedLocation: ERROR!: " + e.getMessage());
            }
            if(list.size()>0){
                Address address = list.get(0);
                return address;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {

        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void moveCamera (LatLng latLng,float zoom, String location){

        CameraUpdate cuf = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        //mMap.moveCamera(cuf);
        mMap.animateCamera(cuf);

    }
}
