package com.example.passengerapp.models;

public class Driver {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String driverImageUrl;
    private String vehicleId;

    public Driver(String id, String name, String phoneNumber, String email, String driverImageUrl, String vehicleId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.driverImageUrl = driverImageUrl;
        this.vehicleId = vehicleId;
    }

    public Driver() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDriverImageUrl() {
        return driverImageUrl;
    }

    public void setDriverImageUrl(String driverImageUrl) {
        this.driverImageUrl = driverImageUrl;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", driverImageUrl='" + driverImageUrl + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                '}';
    }
}
