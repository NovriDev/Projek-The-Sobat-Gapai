package com.example.the_sobat_gapai.Model;

public class Gift {
    private String name;
    private String price;
    private int imageResId; // Resource ID untuk gambar

    public Gift(String name, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}