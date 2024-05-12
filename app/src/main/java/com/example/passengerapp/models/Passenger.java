package com.example.passengerapp.models;

public class Passenger {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String passengerImageUrl;

    public Passenger(String id, String name, String phoneNumber, String email, String passengerImageUrl) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passengerImageUrl = passengerImageUrl;
    }

    public Passenger() {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassengerImageUrl() {
        return passengerImageUrl;
    }

    public void setPassengerImageUrl(String passengerImageUrl) {
        this.passengerImageUrl = passengerImageUrl;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", passengerImageUrl='" + passengerImageUrl + '\'' +
                '}';
    }
}
