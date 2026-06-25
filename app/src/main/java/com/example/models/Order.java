package com.example.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order implements Serializable {
    private String orderId;
    private String employeeId;
    private String customerId;
    private Date orderDate;
    private OrderStatus orderStatus;

    // Firebase compatibility fields
    private String status;
    private double totalAmount;
    private String orderDateString;

    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Order() {
    }

    public Order(String orderId, String employeeId, String customerId, Date orderDate, OrderStatus orderStatus) {
        this(orderId,employeeId,customerId,orderDate);
        this.orderStatus=orderStatus;
    }

    public Order(String orderId, String employeeId, String customerId, Date orderDate) {
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.orderDate = orderDate;
    }

    @com.google.firebase.database.Exclude
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @com.google.firebase.database.Exclude
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        Order.sdf = sdf;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @com.google.firebase.database.Exclude
    public Date getOrderDate() {
        return orderDate;
    }

    @com.google.firebase.database.Exclude
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @com.google.firebase.database.PropertyName("orderDate")
    public String getOrderDateString() {
        return orderDateString;
    }

    @com.google.firebase.database.PropertyName("orderDate")
    public void setOrderDateString(String orderDateString) {
        this.orderDateString = orderDateString;
        try {
            // Try to parse the ISO string or default formatting to date
            // e.g. "2026-06-15T08:30:00Z"
            if (orderDateString != null) {
                // simple quick parsing for ISO
                String datePart = orderDateString.split("T")[0];
                SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd");
                this.orderDate = parseSdf.parse(datePart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String dateStr = "";
        if (this.orderDate != null) {
            dateStr = sdf.format(this.orderDate);
        } else if (orderDateString != null) {
            dateStr = orderDateString;
        }
        double sum = totalAmount > 0 ? totalAmount : DataWareHouse.sumOfMoney(this);
        return orderId + " \t " + dateStr + " \t " + sum;
    }
}

