package com.example.sanggon.synaptopchallenge;

public class PointOfInterest {
    public String title;
    public String lng;
    public String lat;
    public String url;

    public boolean needsViewChange;

    public PointOfInterest(String title, String lng, String lat, String url) {
        this.title = title;
        this.lng = lng;
        this.lat = lat;
        this.url = url;

        this.needsViewChange = false;
    }
}
