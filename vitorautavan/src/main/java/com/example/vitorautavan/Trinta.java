package com.example.vitorautavan;

public class Trinta {

    public Trinta() {

    }

    public double trinta(double lat1, double lon1, double lat2, double lon2) {


        // Fórmula haversine para calcular a distância entre dois pontos na Terra
        double R = 6371000; // raio da Terra em metros
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}