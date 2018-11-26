package com.example.andrewtran.superapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.Serializable;


public class Map_Fragment extends Fragment implements Serializable {

    View view;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map,container,false);
        searchLocationButton();
        findWayButton();
        return view;
    }

    public void searchLocationButton(){
        Button searchLocation = view.findViewById(R.id.searchLocation);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkGoogleMapService()){
                    Intent startMapSearchLocation = new Intent(getActivity(),Map_Activity_SearchLocation.class);
                    startActivity(startMapSearchLocation);
                }
                else{
                    Toast toast=Toast.makeText(getContext(),"Google Map is not available. Try Again Later!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                }

            }
        });
    }
    public void findWayButton(){
        Button searchLocation = view.findViewById(R.id.findWays);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkGoogleMapService()){
                    Intent startMapSearchLocation = new Intent(getActivity(),Map_Activity_FindWays.class);
                    startActivity(startMapSearchLocation);
                }
                else{
                    Toast toast=Toast.makeText(getContext(),"Google Map is not available. Try Again Later!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                }

            }
        });
    }
    private boolean checkGoogleMapService(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else{
            return false;
        }
    }

}
