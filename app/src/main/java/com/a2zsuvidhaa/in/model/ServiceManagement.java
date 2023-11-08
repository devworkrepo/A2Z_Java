package com.a2zsuvidhaa.in.model;

public class ServiceManagement {

    private String id;
    private String name;
    private String category;
    private String message;
    private String status;

    public ServiceManagement(){}

    public ServiceManagement(String id, String name, String category, String message, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.message = message;
        this.status = status;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
