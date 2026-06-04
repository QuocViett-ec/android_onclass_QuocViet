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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        String data = orderId + " \t " + sdf.format(this.orderDate) +" \t "+  DataWareHouse.sumOfMoney(this);
        return data;

    }
}

