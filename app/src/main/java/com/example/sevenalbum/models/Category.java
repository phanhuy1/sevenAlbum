package com.example.sevenalbum.models;

import java.util.List;

public class Category {
    private String nameCategory;
    private List<Image> listImage;

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<Image> getListImage() {
        return listImage;
    }

    public void setListImage(List<Image> listImage) {
        this.listImage = listImage;
    }

    public void addListImage(Image img){this.listImage.add(img);}

    public Category(String nameCategory, List<Image> listImage) {
        this.nameCategory = nameCategory;
        this.listImage = listImage;
    }

    public Category(List<Image> listImage) {
        this.listImage = listImage;
    }
}
