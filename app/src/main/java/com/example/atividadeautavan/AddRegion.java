package com.example.atividadeautavan;

import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import com.example.vitorautavan.Trinta;


public class AddRegion extends Thread {
    private LinkedList<Region> regionsQueue = new LinkedList<>();
    private int ordem = 1;
    private Semaphore semaphore = new Semaphore(2);



    public void addRegion(Region region, Context mContext) {
        try {
            semaphore.acquire();

            synchronized (regionsQueue) {

                if (!isTooClose(region)) {
                    regionsQueue.add(region);
                    System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                    Toast.makeText(mContext, "Região adicionada", Toast.LENGTH_SHORT).show();
                    System.out.println("A fila será impressa a seguir:");
                    printRegionsQueue();
                } else {
                    System.out.println("A região não pode ser adicionada devido à proximidade com outra região na fila.");
                    Toast.makeText(mContext, "Região não adicionada (muito próxima)", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); // Libera a permissão do semáforo
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
        try {
            semaphore.acquire();
            synchronized (regionsQueue) {
                for (Region region : regionsQueue) {
                    System.out.println("User: " +region.getUser()+ " Ordem: "+ ordem + "° - " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude()+ ", Time: " + region.getTimestamp());
                    ordem = ordem + 1;
                }
                ordem = 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); // Libera a permissão do semáforo
        }
    }


    private boolean isTooClose(Region newRegion) {
        for (Region region : regionsQueue) {
            Trinta mTrinta = new Trinta();
            double distance = mTrinta.trinta(newRegion.getLatitude(), newRegion.getLongitude(), region.getLatitude(), region.getLongitude());
            if (distance <= 30) {
                return true;
            }
        }
        return false;
    }
}