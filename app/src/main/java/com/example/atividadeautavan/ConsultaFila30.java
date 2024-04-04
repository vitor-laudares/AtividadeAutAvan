package com.example.atividadeautavan;

import com.example.vitorautavan.Trinta;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultaFila30 extends Thread{

    private static LinkedList<Region> regionsQueue = new LinkedList<>();
    private static Region newRegion;
    public static AtomicBoolean Res;

    public ConsultaFila30(Region newRegion, LinkedList<Region> regionsQueue, AtomicBoolean Res){

        this.newRegion = newRegion;
        this.regionsQueue = regionsQueue;
        this.Res = Res;


    }

    @Override
    public void run(){

        isTooClose();

    }


    public static void isTooClose() {
        for (Region region : regionsQueue) {
            Trinta mTrinta = new Trinta();
            double distance = mTrinta.trinta(newRegion.getLatitude(), newRegion.getLongitude(), region.getLatitude(), region.getLongitude());
            if (distance <= 30) {
                Res.set(true);
            }
        }
    }

}
