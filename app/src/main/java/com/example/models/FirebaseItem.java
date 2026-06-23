package com.example.models;

import java.io.Serializable;

public class FirebaseItem implements Serializable {
    private String id;
    private String name;
    private double price;
    private int quantity;

    public FirebaseItem() {
        // Required for Firebase Realtime Database
    }

    public FirebaseItem(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name + " - Price: $" + price + " (Qty: " + quantity + ")";
    }
}
