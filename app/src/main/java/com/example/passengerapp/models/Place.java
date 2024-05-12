package com.example.passengerapp.models;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private String placeId;
    private String mainText;
    private String secondaryText;
    private LatLng latLng;
    private String placeName;

    public Place(String placeId, String mainText, String secondaryText) {
        this.placeId = placeId;
        this.mainText = mainText;
        this.secondaryText = secondaryText;
    }

    public Place(String placeId, String mainText, String secondaryText, LatLng latLng) {
        this.placeId = placeId;
        this.mainText = mainText;
        this.secondaryText = secondaryText;
        this.latLng = latLng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public String toString() {
        return "Place{" +
                "mainText='" + mainText + '\'' +
                ", secondaryText='" + secondaryText + '\'' +
                '}';
    }
}
