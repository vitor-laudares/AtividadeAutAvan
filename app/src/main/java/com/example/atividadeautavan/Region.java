package com.example.atividadeautavan;

public class Region {

    private String name;
    private double latitude;
    private double longitude;
    private int user;
    private long timestamp;

    public Region(String name, double latitude, double longitude, int user){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = System.nanoTime();
    }

    public String getNome() {

        return name;
    }

    public void setNome(String nome) {

        this.name = nome;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
