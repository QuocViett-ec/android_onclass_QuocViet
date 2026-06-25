package com.example.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String cateId;
    private String cateName;
    private String cateDescription;

    // Firebase compatibility fields
    private String categoryName;
    private String description;

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
        return cateDescription != null ? cateDescription : description;
    }

    public void setCateDescription(String cateDescription) {
        this.cateDescription = cateDescription;
        this.description = cateDescription;
    }

    public String getCategoryName() {
        return categoryName != null ? categoryName : cateName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        this.cateName = categoryName;
    }

    public String getDescription() {
        return description != null ? description : cateDescription;
    }

    public void setDescription(String description) {
        this.description = description;
        this.cateDescription = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "cateId='" + cateId + '\'' +
                ", cateName='" + cateName + '\'' +
                ", cateDescription='" + cateDescription + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
