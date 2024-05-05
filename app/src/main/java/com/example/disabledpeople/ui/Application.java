package com.example.disabledpeople.ui;

public class Application {
    public String userName;
    public String applicationName;
    public String applicationDescription;

    public Application(String nameOfUser, String nameOfApplication, String descriptionOfApplication) {
        this.userName = nameOfUser;
        this.applicationName = nameOfApplication;
        this.applicationDescription = descriptionOfApplication;
    }
}
