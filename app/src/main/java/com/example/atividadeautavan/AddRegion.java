package com.example.atividadeautavan;

import java.util.LinkedList;
import java.util.Queue;


public class AddRegion extends Thread {
    private LinkedList<Region> regionsQueue = new LinkedList<>();
    private int ordem = 1;

    public void addRegion(Region region) {
        synchronized (regionsQueue) {

            if (!isTooClose(region)) {
                regionsQueue.add(region);
                System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude());
                System.out.println("A fila será impressa a seguir:");
                printRegionsQueue();
            } else {
                System.out.println("A região não pode ser adicionada devido à proximidade com outra região na fila.");
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (regionsQueue) {
                if (!regionsQueue.isEmpty()) {
                    Region region = regionsQueue.peekLast();
                    // Aqui você pode processar a região, por exemplo, salvar em um banco de dados
                }
            }
            try {
                Thread.sleep(1000); // Aguarda um segundo antes de verificar novamente a fila
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printRegionsQueue() {
        synchronized (regionsQueue) {
            for (Region region : regionsQueue) {
                System.out.println(ordem + "° - " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude());
                ordem = ordem + 1;
            }
            ordem = 1;
        }
    }

    private boolean isTooClose(Region newRegion) {
        for (Region region : regionsQueue) {
            double distance = calculateDistance(newRegion.getLatitude(), newRegion.getLongitude(), region.getLatitude(), region.getLongitude());
            if (distance <= 30) {
                return true;
            }
        }
        return false;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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