package com.example.andrewtran.superapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private String distanceText;
    private int distanceValue;
    private String durationText;
    private int durationValue;
    private String startAddress;
    private double startAddressLat;
    private double startAddressLng;
    private String endDestination;
    private double endDestinationLat;
    private double endDestinationLng;
    private List<Step> steps;
    private List<LatLng> overviewPolyline;
    private String summary;

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

    public int getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public double getStartAddressLat() {
        return startAddressLat;
    }

    public void setStartAddressLat(double startAddressLat) {
        this.startAddressLat = startAddressLat;
    }

    public double getStartAddressLng() {
        return startAddressLng;
    }

    public void setStartAddressLng(double startAddressLng) {
        this.startAddressLng = startAddressLng;
    }

    public String getEndDestination() {
        return endDestination;
    }

    public void setEndDestination(String endDestination) {
        this.endDestination = endDestination;
    }

    public double getEndDestinationLat() {
        return endDestinationLat;
    }

    public void setEndDestinationLat(double endDestinationLat) {
        this.endDestinationLat = endDestinationLat;
    }

    public double getEndDestinationLng() {
        return endDestinationLng;
    }

    public void setEndDestinationLng(double endDestinationLng) {
        this.endDestinationLng = endDestinationLng;
    }

    public List<LatLng> getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(List<LatLng> overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void addStep(String StepDistanceText, int StepDistanceValue,
                        String StepDurationText, int StepDurationValue,
                        double EndLat, double EndLng,
                        double StartLat, double StartLng,
                        String Instruction,
                        List<LatLng> Polyline, String Mode){
        steps = new ArrayList<Step>();
        Step step = new Step();
        step.setDistanceText(StepDistanceText);
        step.setDistanceValue(StepDistanceValue);
        step.setDurationText(StepDurationText);
        step.setDurationValue(StepDurationValue);
        step.setEndLat(EndLat);
        step.setEndLng(EndLng);
        step.setStartLat(StartLat);
        step.setStartLng(StartLng);
        step.setInstruction(Instruction);
        step.setPolyline(Polyline);
        step.setMode(Mode);
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }
}
