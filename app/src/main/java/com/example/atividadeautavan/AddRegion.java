package com.example.atividadeautavan;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.vitorautavan.Cryptography;
import com.example.vitorautavan.Region;
import com.example.vitorautavan.RestrictedRegion;
import com.example.vitorautavan.SubRegion;
import com.example.vitorautavan.JsonUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;


public class AddRegion extends Thread {
    private LinkedList<Region> regionsQueue = new LinkedList<>();
    private int ordem = 1;
    private Semaphore semaphore = new Semaphore(2);
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private static int alterna = 1;
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

        ConsultaBD30 consultaBd = new ConsultaBD30(lat, lon, db, ResBd, Res5);
        ConsultaFila30 consultaFila = new ConsultaFila30(lat, lon, regionsQueue, ResFila, Res5);
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
                String lat1 = String.valueOf(lat);
                String lon1 = String.valueOf(lon);
                String user1 = String.valueOf(201911007);
                if (!ResFila.get()) {
                    if(!ResBd.get()) {

                        Region region = new Region("Região", lat1, lon1, user1);
                        serialize(region);

                        regionsQueue.add(region);
                            System.out.println("Processando: " + "Localização Atual" + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Região adicionada", Toast.LENGTH_SHORT).show();
                            System.out.println("A fila será impressa a seguir:");
                            printRegionsQueue();
                    } else {
                        if (!Res5.get()) {
                            if(alterna == 1) {
                                SubRegion region = new SubRegion(mainRegion, "Sub Região", lat1, lon1, user1);
                                serialize(region);
                                regionsQueue.add(region);
                                System.out.println("Processando região: " + "Sub Região" + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                                Toast.makeText(mContext, "Adicionada " + "Sub Região", Toast.LENGTH_SHORT).show();
                                System.out.println("A fila será impressa a seguir:");
                                printRegionsQueue();
                                alterna = 0;
                            }else {
                                RestrictedRegion region = new RestrictedRegion(mainRegion, "Região Restrita", lat1, lon1, user1);
                                serialize(region);
                                regionsQueue.add(region);
                                System.out.println("Processando região: " + "Região Restrita" + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                                Toast.makeText(mContext, "Adicionada " + "Região Restrita", Toast.LENGTH_SHORT).show();
                                System.out.println("A fila será impressa a seguir:");
                                printRegionsQueue();
                                alterna = 1;
                            }

                        }else{
                        System.out.println("A região não pode ser adicionada devido à proximidade com outra região no BD.");
                        Toast.makeText(mContext, "Região não adicionada (muito próxima)", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    if (!Res5.get()) {
                        if(alterna == 1) {
                            SubRegion region = new SubRegion(mainRegion, "Sub Região", lat1, lon1, user1);
                            serialize(region);
                            regionsQueue.add(region);
                            System.out.println("Processando região: " + "Sub Região" + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Adicionada " + "Sub Região", Toast.LENGTH_SHORT).show();
                            System.out.println("A fila será impressa a seguir:");
                            printRegionsQueue();
                            alterna = 0;
                        }else {
                            RestrictedRegion region = new RestrictedRegion(mainRegion, "Região Restrita", lat1, lon1, user1);
                            serialize(region);
                            alterna = 1;
                            regionsQueue.add(region);
                            System.out.println("Processando região: " + "Região Restrita" + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude() + ", Time: " + region.getTimestamp());
                            Toast.makeText(mContext, "Adicionada " + "Região Restrita", Toast.LENGTH_SHORT).show();
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

                                data.put("name", region.getName());
                                data.put("latitude", region.getLatitude());
                                data.put("longitude", region.getLongitude());
                                data.put("user", region.getUser());
                                data.put("timestamp", region.getTimestamp());


                                String colecao;

                                if(region instanceof SubRegion){
                                    colecao = "Sub Região";
                                    data.put("mainRegion", ((SubRegion) region).getMainRegion());

                                } else if(region instanceof RestrictedRegion){
                                    colecao = "Região Restrita";
                                    data.put("mainRegion", ((RestrictedRegion) region).getMainRegion());

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
                    System.out.println("User: " +region.getUser()+ " Ordem: "+ ordem + "° - " + region.getName() + ", Latitude: " + region.getLatitude() + ", Longitude: " + region.getLongitude()+ ", Time: " + region.getTimestamp());
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

    public void serialize(Region region) {
        JsonUtil gson = new JsonUtil();
        String regionJson = gson.toJson(region);
        try {
            region.setNome(Cryptography.encrypt(region.getName()));
            region.setLatitude(Cryptography.encrypt(String.valueOf(region.getLatitude())));
            region.setLongitude(Cryptography.encrypt(String.valueOf(region.getLongitude())));
            region.setTimestamp(Cryptography.encrypt(String.valueOf(region.getTimestamp())));
            region.setUser(Cryptography.encrypt(String.valueOf(region.getUser())));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}