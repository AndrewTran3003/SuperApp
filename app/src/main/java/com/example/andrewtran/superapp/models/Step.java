package com.example.andrewtran.superapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Step {
    private String distanceText;
    private int distanceValue;
    private String durationText;
    private int durationValue;
    private double endLat;
    private double endLng;
    private double startLat;
    private double startLng;
    private String instruction;
    private List<LatLng> polyline;
    private String mode;
    public Step(){

    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public int getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<LatLng> getPolyline() {
        return polyline;
    }

    public void setPolyline(List<LatLng> polyline) {
        this.polyline = polyline;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
