package com.example.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Contact implements Serializable {
    public static final String SYNCED = "SYNCED";
    public static final String PENDING_CREATE = "PENDING_CREATE";
    public static final String PENDING_UPDATE = "PENDING_UPDATE";
    public static final String PENDING_DELETE = "PENDING_DELETE";

    private String id;
    private String name;
    private String phone;
    private String email;
    private long updatedAt;
    private String syncStatus;

    public Contact() {
    }

    public Contact(String id, String name, String phone, String email, long updatedAt, String syncStatus) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.updatedAt = updatedAt;
        this.syncStatus = syncStatus;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Map<String, Object> toFirebaseMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("phone", phone);
        values.put("email", email);
        return values;
    }
}
