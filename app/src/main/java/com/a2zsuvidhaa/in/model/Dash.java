package com.a2zsuvidhaa.in.model;

public class Dash {

    private String name;
    private int img_resource;

    public Dash() {
    }

    public Dash(String name, int img_resource) {
        this.name = name;
        this.img_resource = img_resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg_resource() {
        return img_resource;
    }

    public void setImg_resource(int img_resource) {
        this.img_resource = img_resource;
    }
}
