package com.example.unimates;

public class User {
    private String email, name, surname, department, distance, clas, status, duration, url, userID, phone;

    public User(String email, String name, String surname, String url) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.url = url;
    }

    public User(String email, String name, String surname, String department, String distance, String clas, String status, String duration, String url, String userID, String phone) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.distance = distance;
        this.clas = clas;
        this.status = status;
        this.duration = duration;
        this.url = url;
        this.userID = userID;
        this.phone = phone;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }
    public String getClas() { return clas; }
    public void setClas(String clas) { this.clas = clas; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
