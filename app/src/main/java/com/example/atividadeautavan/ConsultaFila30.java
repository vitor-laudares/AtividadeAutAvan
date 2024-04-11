package com.example.atividadeautavan;

import com.example.vitorautavan.Cryptography;
import com.example.vitorautavan.Region;
import com.example.vitorautavan.RestrictedRegion;
import com.example.vitorautavan.SubRegion;


import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultaFila30 extends Thread{

    private static LinkedList<Region> regionsQueue = new LinkedList<>();
    private static Region newRegion;
    public static AtomicBoolean Res;
    public static AtomicBoolean Res5;

    public static double lat;
    public static double lon;
    public static double dlat;
    public static double dlon;

    public ConsultaFila30( double Lat, double Lon, LinkedList<Region> regionsQueue, AtomicBoolean Res , AtomicBoolean Res5){


        this.regionsQueue = regionsQueue;
        this.Res = Res;
        this.Res5 = Res5;
        this.lat = Lat;
        this.lon = Lon;


    }

    @Override
    public void run(){

        isTooClose();

    }


    public static void isTooClose() {
        for (Region region : regionsQueue) {
            try {
                decryptRegionAttributes(region);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(region instanceof SubRegion){
                boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                if (d) {
                    Res5.set(true);
                    break;

                }
            } else if (region instanceof RestrictedRegion){
                boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                if (d) {
                    Res5.set(true);
                    break;
                }
            } else {
                boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                if (d) {
                    Res.set(true);
                    AddRegion.setMainRegion(region);
                }
            }
        }

    }

    private static void decryptRegionAttributes(Region region) throws Exception {
        dlat  = Double.parseDouble(Cryptography.decrypt(String.valueOf(region.getLatitude())));
        dlon  = Double.parseDouble(Cryptography.decrypt(String.valueOf(region.getLongitude())));
        // Você deve fazer o mesmo para os outros atributos que precisam ser descriptografados
    }

}
