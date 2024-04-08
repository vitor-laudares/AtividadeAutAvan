package com.example.atividadeautavan;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.vitorautavan.Region;
import com.example.vitorautavan.RestrictedRegion;
import com.example.vitorautavan.SubRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddRegion extends Thread {
    private LinkedList<Region> regionsQueue = new LinkedList<>();
    private int ordem = 1;
    private Semaphore semaphore = new Semaphore(2);
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private static int alterna = 1;
    private double lat;
    private double lon;
    private static Region mainRegion;


    public AddRegion(Context mContext){
        this.mContext = mContext;

    }



    public void addRegion(double lat, double lon, Context mContext) {
        AtomicBoolean ResBd = new AtomicBoolean();
        ResBd.set(false);
        AtomicBoolean ResFila = new AtomicBoolean();
        ResFila.set(false);
        AtomicBoolean Res5 = new AtomicBoolean();
        Res5.set(false);

        ConsultaBD30 consultaBd = new ConsultaBD30(mainRegion, lat, lon, db, ResBd, Res5);
        ConsultaFila30 consultaFila = new ConsultaFila30(mainRegion, lat, lon, regionsQueue, ResFila, Res5);
        consultaFila.start();
        consultaBd.start();

        try {
            consultaFila.join();
            consultaBd.join();// Espera a thread ConsultaFila30 terminar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            semaphore.acquire();
            synchronized (regionsQueue) {
                if (!ResFila.get()) {
                    if(!ResBd.get()) {

                        Region region = new Region("Região", lat, lon, 201911007);
                        regionsQueue.add(region);
                            System.out.println("Processando: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Região adicionada", Toast.LENGTH_SHORT).show();
                            System.out.println("A fila será impressa a seguir:");
                            printRegionsQueue();
                    } else {
                        if (!Res5.get()) {
                            if(alterna == 1) {
                                SubRegion region = new SubRegion(mainRegion, "Sub Região", lat, lon, 201911007);
                                regionsQueue.add(region);
                                System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                                Toast.makeText(mContext, "Adicionada " + region.getNome(), Toast.LENGTH_SHORT).show();
                                System.out.println("A fila será impressa a seguir:");
                                printRegionsQueue();
                                alterna = 0;
                            }else {
                                RestrictedRegion region = new RestrictedRegion(mainRegion, "Região Restrita", lat, lon, 201911007);
                                alterna = 1;
                                regionsQueue.add(region);
                                System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                                Toast.makeText(mContext, "Adicionada " + region.getNome(), Toast.LENGTH_SHORT).show();
                                System.out.println("A fila será impressa a seguir:");
                                printRegionsQueue();
                            }

                        }else{
                        System.out.println("A região não pode ser adicionada devido à proximidade com outra região no BD.");
                        Toast.makeText(mContext, "Região não adicionada (muito próxima)", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    if (!Res5.get()) {
                        if(alterna == 1) {
                            SubRegion region = new SubRegion(mainRegion, "Sub Região", lat, lon, 201911007);
                            regionsQueue.add(region);
                            System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Adicionada " + region.getNome(), Toast.LENGTH_SHORT).show();
                            System.out.println("A fila será impressa a seguir:");
                            printRegionsQueue();
                            alterna = 0;
                        }else {
                            RestrictedRegion region = new RestrictedRegion(mainRegion, "Região Restrita", lat, lon, 201911007);
                            alterna = 1;
                            regionsQueue.add(region);
                            System.out.println("Processando região: " + region.getNome() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Adicionada " + region.getNome(), Toast.LENGTH_SHORT).show();
                            System.out.println("A fila será impressa a seguir:");
                            printRegionsQueue();
                            alterna = 1;
                        }
                    }else {
                        System.out.println("A região não pode ser adicionada devido à proximidade com outra região na fila.");
                        Toast.makeText(mContext, "Região não adicionada (muito próxima)", Toast.LENGTH_SHORT).show();
                    }
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
                    Button myButton2 =  ((Activity) mContext).findViewById(R.id.myButton2);
                    myButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Gravar todos os elementos da fila no Firestore
                            for (Region region : regionsQueue) {
                                // Criar um mapa de dados para o objeto Region
                                Map<String, Object> data = new HashMap<>();
                                data.put("name", region.getNome());
                                data.put("latitude", region.getLatitude());
                                data.put("longitude", region.getLongitude());
                                data.put("user", region.getUser());
                                data.put("timestamp", region.getTimestamp());

                                String colecao = null;

                                if (region instanceof SubRegion){
                                    colecao = "Sub Região";
                                } else if(region instanceof RestrictedRegion){
                                    colecao = "Região Restrita";
                                } else {
                                    colecao = "Região";
                                }


                                // Adicionar os dados ao Firestore
                                db.collection(colecao)
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // Se a adição for bem-sucedida, você pode remover o elemento da fila
                                                synchronized (regionsQueue) {
                                                    regionsQueue.remove(region);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Lidar com falha ao adicionar dados
                                                Toast.makeText(mContext, "Erro ao adicionar região ao Firestore", Toast.LENGTH_SHORT).show();
                                                Log.e("Firestore", "Erro ao adicionar região ao Firestore", e);
                                            }
                                        });
                            }
                            Toast.makeText(mContext, "Regiões Adicionadas no BD e removidas da fila", Toast.LENGTH_SHORT).show();

                        }

                    });
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

    public static void setMainRegion(Region mainRegion1){
        mainRegion = mainRegion1;
    }



}