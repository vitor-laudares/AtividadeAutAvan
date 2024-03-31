package com.example.atividadeautavan;

public class Region {

    private String name;
    private double latitude;
    private double longitude;

    public Region(String name, double latitude, double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
