package com.example.disabledpeople.ui;

public class Application {
    public String id;
    public String userName;
    public String description;
    public String region;
    public String email;

    public Application(String id, String userName, String description, String region, String email) {
        this.id = id;
        this.userName = userName;
        this.description = description;
        this.region = region;
        this.email =  email;
    }
}
