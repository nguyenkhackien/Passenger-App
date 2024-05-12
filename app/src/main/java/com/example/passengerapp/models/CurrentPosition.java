package com.example.passengerapp.models;

public class CurrentPosition {
    private String driverId;
    private String position;
    private String bearing;
    private String speed;
    private String vehicleType;
    private String time;

    public CurrentPosition(String driverId, String position, String bearing, String speed, String vehicleType, String time) {
        this.driverId = driverId;
        this.position = position;
        this.bearing = bearing;
        this.speed = speed;
        this.vehicleType = vehicleType;
        this.time = time;
    }

    public CurrentPosition() {
    }

    public CurrentPosition(String driverId, String position, String time) {
        this.driverId = driverId;
        this.position = position;
        this.time = time;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
