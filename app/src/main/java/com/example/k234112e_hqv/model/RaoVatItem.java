package com.example.k234112e_hqv.model;

public class RaoVatItem {
    private String title;
    private String thumb;
    private String url;
    private String price;
    private String location;

    public RaoVatItem() {}

    public RaoVatItem(String title, String thumb, String url, String price, String location) {
        this.title = title;
        this.thumb = thumb;
        this.url = url;
        this.price = price;
        this.location = location;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
