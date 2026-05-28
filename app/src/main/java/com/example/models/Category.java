package com.example.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String cateId;
    private String cateName;
    private String cateDescription;

    public Category() {
    }

    public Category(String cateId, String cateName, String cateDescription) {
        this.cateId = cateId;
        this.cateName = cateName;
        this.cateDescription = cateDescription;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCateDescription() {
        return cateDescription;
    }

    public void setCateDescription(String cateDescription) {
        this.cateDescription = cateDescription;
    }

    @Override
    public String toString() {
        return "Category{" +
                "cateId='" + cateId + '\'' +
                ", cateName='" + cateName + '\'' +
                ", cateDescription='" + cateDescription + '\'' +
                '}';
    }
}
