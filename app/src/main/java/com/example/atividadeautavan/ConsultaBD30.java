package com.example.atividadeautavan;

import com.example.vitorautavan.Region;
import com.example.vitorautavan.RestrictedRegion;
import com.example.vitorautavan.SubRegion;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultaBD30 extends Thread{

    private final AtomicBoolean Res;
    private final AtomicBoolean Res5;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean regionFound = false;
    private double lat;
    private double lon;


    public ConsultaBD30(Region mainRegion, double Lat, double Lon, FirebaseFirestore db, AtomicBoolean Res, AtomicBoolean Res5) {
        this.db = db;
        this.Res = Res;
        this.Res5 = Res5;
        this.lat = Lat;
        this.lon = Lon;
    }

    @Override
    public void run(){

        varrerDb();
    }

    public void varrerDb() {

        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("Região").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                Region region = document.toObject(Region.class);
                    boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                    if(d){
                        Res.set(true);
                    }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }



        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("Sub Região").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                SubRegion region = document.toObject(SubRegion.class);
                boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                if(d){
                    Res5.set(true);
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("Região Restrita").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                RestrictedRegion region = document.toObject(RestrictedRegion.class);
                boolean d = region.calcularDistancia(lat , lon , region.getLatitude() , region.getLongitude());
                if(d){
                    Res5.set(true);
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }



}