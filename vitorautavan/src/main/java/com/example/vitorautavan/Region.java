package com.example.vitorautavan;

public class Region {

    protected String name;
    protected String latitude;
    protected String longitude;
    protected String user;
    protected String timestamp;


    public Region(String name, String latitude, String longitude, String user){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = String.valueOf(System.nanoTime());
    }

    public Region(){

    }

    public String getNome() {

        return name;
    }

    public void setNome(String nome) {

        this.name = nome;
    }

    public String getLatitude() {

        return String.valueOf(latitude);
    }

    public void setLatitude(String latitude) {

        this.latitude = latitude;
    }

    public String getLongitude() {

        return longitude;
    }

    public void setLongitude(String longitude) {

        this.longitude = longitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }

    public boolean calcularDistancia(double lat1, double lon1, double lat2, double lon2) {


        // Fórmula haversine para calcular a distância entre dois pontos na Terra
        double R = 6371000; // raio da Terra em metros
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d =  R * c;
        if (d<=30){
            return true;

        }else {
            return false;
        }


    }
}
