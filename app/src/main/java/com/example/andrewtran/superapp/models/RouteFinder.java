package com.example.andrewtran.superapp.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

public class RouteFinder {
    private final String ROUTEFINDER_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String API_KEY = "AIzaSyCzRd2w4tIYCDsXltazNR7M8P41XrUAizk";
    private String startingPoint;
    private String destination;
    private Route route;
    private RouteFinderListener rfl;
    Context context;


    public RouteFinder (RouteFinderListener routeFinderListener,String StartingPoint, String Destination, Context c){
        startingPoint = StartingPoint;
        destination = Destination;
        rfl = routeFinderListener;
        context = c;

    }

    public void execute(){
        rfl.onDirectionFinderStart();
        new LoadInformation().execute(createURL(startingPoint,destination));
    }

    private String createURL(String starting, String des) {
        try {
            String urlStarting = URLEncoder.encode(starting, "utf-8");
            String urlDestination = URLEncoder.encode(des, "utf-8");
            return ROUTEFINDER_URL + "origin=" + urlStarting + "&destination=" + urlDestination + "&key=" + API_KEY;
        }
        catch (UnsupportedEncodingException e){
            Log.d(TAG, "createURL: " + e.getMessage());
        }
        return null;
    }

    private class LoadInformation extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];
            try{
                URL url = new URL(link);
                InputStream inputStream = url.openConnection().getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(TAG, "doInBackground: Json come back!");
                String line;
                while ((line =  bufferedReader.readLine())!= null){
                    stringBuffer.append(line+"\n");
                }
                Log.d(TAG, "doInBackground: "+ stringBuffer.toString());
                return stringBuffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null){
                parseJsonIntoObject(s);
            }
            else{
                Toast toast = Toast.makeText(context, "Something goes wrong!",Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    private void parseJsonIntoObject(String s) {
        if (s == null){
            return;
        }
        route = new Route();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject jsonRoute = jsonObject.getJSONArray("routes").getJSONObject(0);
            JSONObject jsonLeg = jsonRoute.getJSONArray("legs").getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");



            route.setDistanceText(jsonDistance.getString("text"));
            route.setDistanceValue(jsonDistance.getInt("value"));

            route.setDurationText(jsonDuration.getString("text"));
            route.setDurationValue(jsonDistance.getInt("value"));


            route.setStartAddress(jsonLeg.getString("end_address"));
            route.setStartAddressLat(jsonStartLocation.getDouble("lat"));
            route.setStartAddressLng(jsonStartLocation.getDouble("lng"));

            route.setEndDestination((jsonLeg.getString("end_address")));
            route.setEndDestinationLat(jsonEndLocation.getDouble("lat"));
            route.setEndDestinationLng(jsonEndLocation.getDouble("lng"));

            for (int i = 0; i < jsonSteps.length();i++){
                JSONObject jsonStep = jsonSteps.getJSONObject(i);
                JSONObject jsonStepDistance = jsonStep.getJSONObject("distance");
                JSONObject jsonStepDuration = jsonStep.getJSONObject("duration");
                JSONObject jsonStepPolyline = jsonStep.getJSONObject("polyline");
                JSONObject jsonStepEndLocation = jsonStep.getJSONObject("end_location");
                JSONObject jsonStepStartLocation = jsonStep.getJSONObject("start_location");

                route.addStep(jsonStepDistance.getString("text"),jsonStepDistance.getInt("value"),
                        jsonStepDuration.getString("text"),jsonStepDuration.getInt("value"),
                        jsonStepEndLocation.getDouble("lat"),jsonEndLocation.getDouble("lng"),
                        jsonStepStartLocation.getDouble("lat"),jsonStartLocation.getDouble("lng"),
                        jsonStep.getString("html_instructions"),
                        PolyUtil.decode(jsonStepPolyline.getString("points")),jsonStep.getString("travel_mode"));


            }

            route.setOverviewPolyline(PolyUtil.decode(jsonRoute.getJSONObject("overview_polyline").getString("points")));
            route.setSummary(jsonRoute.getString("summary"));
            rfl.onDirectionFinderSuccess(route);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
