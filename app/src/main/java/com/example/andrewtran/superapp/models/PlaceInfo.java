package com.example.andrewtran.superapp.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class PlaceInfo
{
    private String address;
    private String attributions;
    private LatLng latLng;


    private Locale locale;
    private String name;
    private String phoneNumber;
    private List<Integer> placeTypes;
    private float rating;
    private Uri uri;
    private int priceLevel;
    private Bitmap placeImage;

    public PlaceInfo(String address, String attributions, LatLng latLng, Locale locale, String name, String phoneNumber, List<Integer> placeTypes, float rating, Uri uri, int priceLevel, Bitmap PlaceImage) {
        this.address = address;
        this.attributions = attributions;
        this.latLng = latLng;
        this.locale = locale;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.placeTypes = placeTypes;
        this.rating = rating;
        this.uri = uri;
        this.priceLevel = priceLevel;
        this.placeImage = PlaceImage;
    }

    public PlaceInfo() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    @Override
    public String toString() {
        return "Place{" +
                "address='" + address + '\'' +
                ", attributions='" + attributions + '\'' +
                ", latLng=" + latLng +
                ", locale=" + locale +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", placeTypes=" + placeTypes +
                ", rating=" + rating +
                ", uri=" + uri +
                ", priceLevel=" + priceLevel +
                '}';
    }

    public Bitmap getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(Bitmap placeImage) {
        this.placeImage = placeImage;
    }
}
