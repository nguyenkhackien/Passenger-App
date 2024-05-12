package com.example.passengerapp.models;

public class Trip {
    private String id;
    private String passengerId;
    private String driverId;
    private String pickUpName;
    private String pickUpLocation;
    private String dropOffName;
    private String dropOffLocation;
    private String distance;
    private String cost;
    private String timeCost;
    private String vehicleType;
    private String paymentMethod;
    private String createTime;
    private String endTime;
    private String status;

    public Trip(String id, String passengerId, String driverId, String pickUpLocation, String dropOffLocation, String distance, String cost, String timeCost, String vehicleType, String paymentMethod, String createTime, String endTime, String status) {
        this.id = id;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.distance = distance;
        this.cost = cost;
        this.timeCost = timeCost;
        this.vehicleType = vehicleType;
        this.paymentMethod = paymentMethod;
        this.createTime = createTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Trip(String passengerId, String pickUpLocation, String dropOffLocation, String distance, String cost, String timeCost, String vehicleType, String createTime) {
        this.passengerId = passengerId;
        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.distance = distance;
        this.cost = cost;
        this.timeCost = timeCost;
        this.vehicleType = vehicleType;
        this.createTime = createTime;
    }

    public Trip(String passengerId, String pickUpLocation, String dropOffLocation, String distance, String cost, String timeCost, String vehicleType, String paymentMethod, String createTime) {
        this.passengerId = passengerId;
        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.distance = distance;
        this.cost = cost;
        this.timeCost = timeCost;
        this.vehicleType = vehicleType;
        this.paymentMethod = paymentMethod;
        this.createTime = createTime;
    }

    public Trip() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStatus() {
        return status;
    }

    public String getPickUpName() {
        return pickUpName;
    }

    public void setPickUpName(String pickUpName) {
        this.pickUpName = pickUpName;
    }

    public String getDropOffName() {
        return dropOffName;
    }

    public void setDropOffName(String dropOffName) {
        this.dropOffName = dropOffName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", passengerId='" + passengerId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", pickUpLocation='" + pickUpLocation + '\'' +
                ", dropOffLocation='" + dropOffLocation + '\'' +
                ", distance='" + distance + '\'' +
                ", cost='" + cost + '\'' +
                ", timeCost='" + timeCost + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", createTime='" + createTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
