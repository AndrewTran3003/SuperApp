package com.example.andrewtran.superapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrewtran.superapp.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map_Activity_SearchLocation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Map_Activity_SearchLocation";
    private GoogleMap mMap;
    private String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float DEFAULT_ZOOM = 15;
    private Boolean locationGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView searchText;
    private FloatingActionButton gpsButton,inforButton;
    private ImageView cancelButton;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private final LatLngBounds latLngBounds = new LatLngBounds(new LatLng(-56,-151),new LatLng(-40,-56));
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_findlocation);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_input);
        gpsButton = (FloatingActionButton) findViewById(R.id.gpsButton);
        cancelButton = findViewById(R.id.ic_cancel);
        inforButton = findViewById(R.id.inforButton);
        getLocationPermission();
        initSearchBar();
        initGpsButton();
        initInforButton();
        initCancelButton();

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

    private void initSearchBar(){

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        searchText.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,Places.getGeoDataClient(this,null),latLngBounds,null );
        searchText.setAdapter(mPlaceAutocompleteAdapter);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                        new GetSearchedLocation().execute(searchText.getText().toString());
                        hideKeyboard();

                }
                return false;
            }


        });



    }

    private void initCancelButton(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });
    }

    private void initGpsButton(){
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private void initInforButton(){
        inforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    }
                    else{
                        marker.showInfoWindow();
                    }
                }
                catch (NullPointerException e){

                }
            }
        });
    }

//This method initializes the map
    private void initMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(Map_Activity_SearchLocation.this, "Map is now created", Toast.LENGTH_SHORT).show();
                mMap = googleMap;
                if (locationGranted) {
                    getCurrentLocation();
                    if (ActivityCompat.checkSelfPermission(Map_Activity_SearchLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map_Activity_SearchLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    /*This function asks for accessing current location*/
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
    private void getCurrentLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(locationGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Map_Activity_SearchLocation.this, "Found location",Toast.LENGTH_SHORT).show();
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"Current Location");
                        }
                        else{
                            Toast.makeText(Map_Activity_SearchLocation.this, "Current location is unavailable",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Toast.makeText(Map_Activity_SearchLocation.this, "getLocationError: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
//Move the camera to desired place
    private void moveCamera (LatLng latLng,float zoom, String location){
        Log.d(TAG, "moveCamera: move the camera to: " + latLng.latitude+","+latLng.longitude+","+zoom);
        mMap.clear();
        CameraUpdate cuf = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        //mMap.moveCamera(cuf);
        MarkerOptions options = new MarkerOptions().position(latLng).title(location);
        mMap.addMarker(options);
        mMap.animateCamera(cuf);
        hideKeyboard();

    }

    private void moveCamera (LatLng latLng,float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: move the camera to: " + latLng.latitude+","+latLng.longitude+","+zoom);
        mMap.clear();
        CameraUpdate cuf = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.animateCamera(cuf);
        mMap.setInfoWindowAdapter(new CustomWindowInforAdapter(Map_Activity_SearchLocation.this,mPlace.getPlaceImage()));
        if(placeInfo != null){
            try {
                String snippet = "Address: "+placeInfo.getAddress()+"\n"+
                        "Phone Number: "+placeInfo.getPhoneNumber()+"\n"+
                        "Website: "+placeInfo.getUri()+"\n"+
                        "Price Rating: "+placeInfo.getRating()+"\n";
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);

                marker = mMap.addMarker(options);
            }
            catch (NullPointerException e){

            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideKeyboard();
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            String placeID = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeID);
            Task<PlacePhotoMetadataResponse> photoResult  =Places.getGeoDataClient(Map_Activity_SearchLocation.this).getPlacePhotos(placeID);
            loadPhotos(photoResult);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private void loadPhotos(Task<PlacePhotoMetadataResponse> photoResult) {
        photoResult.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                Task<PlacePhotoResponse> photoResponse = Places.getGeoDataClient(Map_Activity_SearchLocation.this).getPhoto(photoMetadata);
               photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        mPlace.setPlaceImage(bitmap);
                    }
                });

            }
        });
    }


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
            mPlace = new PlaceInfo();
            mPlace.setAddress(place.getAddress().toString());
           // mPlace.setAttributions(place.getAttributions().toString());
            mPlace.setLatLng(place.getLatLng());
            mPlace.setLocale(place.getLocale());
            mPlace.setName(place.getName().toString());
            mPlace.setPlaceTypes(place.getPlaceTypes());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            mPlace.setPriceLevel(place.getPriceLevel());
            mPlace.setRating(place.getRating());
            mPlace.setUri(place.getWebsiteUri());
            moveCamera(mPlace.getLatLng(),DEFAULT_ZOOM,mPlace);
            places.release();


        }
    };
    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    // this class is used to do the task of loading the result from user input in the background
    private class GetSearchedLocation extends AsyncTask<String, Void, Address> {


        @Override

        protected Address doInBackground(String... strings) {
            Geocoder geocoder = new Geocoder(Map_Activity_SearchLocation.this);
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
            if (address != null){
                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,mPlace);
            }
        }
    }
}
