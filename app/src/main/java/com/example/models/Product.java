package com.example.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String productName;
    private int quantity;
    private double prices;
    private double coupon;
    private double VAT;
    //bổ sung thêm chỉ thêm mới vào không sửa trực tiếp những cái đang có
    private String categoryId;

    // Firebase compatibility fields
    private double price;
    private int stock;
    private String imageUrl;
    private boolean isActive;

    public Product() {
    }
    // tạo thêm constructor đầy đủ đối so bổ sung thêm vào hàm cũ
    public Product(String productId, String productName, int quantity, double prices, double coupon, double VAT, String categoryId) {
        this(productId, productName, quantity, prices, coupon, VAT);// tái sử dụng
        this.categoryId = categoryId;
    }

    public Product(String productId, String productName, int quantity, double prices, double coupon, double VAT) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.prices = prices;
        this.coupon = coupon;
        this.VAT = VAT;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrices() {
        return prices;
    }

    public void setPrices(double prices) {
        this.prices = prices;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // Firebase getters/setters
    public double getPrice() {
        return price > 0 ? price : prices;
    }

    public void setPrice(double price) {
        this.price = price;
        this.prices = price;
    }

    public int getStock() {
        return stock > 0 ? stock : quantity;
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.quantity = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    @com.google.firebase.database.Exclude
    public boolean isActive() {
        return isActive;
    }

    @com.google.firebase.database.Exclude
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", prices=" + prices +
                ", coupon=" + coupon +
                ", VAT=" + VAT +
                ", categoryId='" + categoryId + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", imageUrl='" + imageUrl + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
