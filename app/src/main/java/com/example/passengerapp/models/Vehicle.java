package com.example.passengerapp.models;

public class Vehicle {
    private String id;
    private String driverId;
    private String name;
    private String type;
    private String plateNumber;
    private String vehicleImageUrl;

    public Vehicle(String id, String driverId, String name, String type, String plateNumber, String vehicleImageUrl) {
        this.id = id;
        this.driverId = driverId;
        this.name = name;
        this.type = type;
        this.plateNumber = plateNumber;
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public Vehicle() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", vehicleImageUrl='" + vehicleImageUrl + '\'' +
                '}';
    }
}
