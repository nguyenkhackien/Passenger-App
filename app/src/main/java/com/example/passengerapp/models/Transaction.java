package com.example.passengerapp.models;

public class Transaction {
    private String transId;
    private String senderId;
    private String receiverId;
    private String amount;
    private String time;
    private String note;

    public Transaction(String transId, String senderId, String receiverId, String amount, String time, String note) {
        this.transId = transId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.time = time;
        this.note = note;
    }

    public Transaction() {
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
