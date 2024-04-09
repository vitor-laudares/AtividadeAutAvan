package com.example.atividadeautavan;

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

    public ConsultaFila30(Region mainRegion, double Lat, double Lon, LinkedList<Region> regionsQueue, AtomicBoolean Res , AtomicBoolean Res5){

        this.newRegion = newRegion;
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
            if(region instanceof SubRegion){
                boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                if (d) {
                    Res5.set(true);

                }
            } else if (region instanceof RestrictedRegion){
                boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                if (d) {
                    Res5.set(true);
                }
            } else {
                boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                if (d) {
                    Res.set(true);
                }
            }
        }

    }

}
