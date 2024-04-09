package com.example.atividadeautavan;

import com.example.vitorautavan.Cryptography;
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

    public static double dlat;
    public static double dlon;


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
                decryptRegionAttributes(region);
                    boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                    if(d){
                        Res.set(true);
                        AddRegion.setMainRegion(region);
                        break;
                    }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("Sub Região").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                SubRegion region = document.toObject(SubRegion.class);
                decryptRegionAttributes(region);

                boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                if(d){
                    Res5.set(true);
                    AddRegion.setMainRegion(region);
                    break;
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("Região Restrita").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                RestrictedRegion region = document.toObject(RestrictedRegion.class);
                decryptRegionAttributes(region);

                boolean d = region.calcularDistancia(lat , lon , dlat , dlon);
                if(d){
                    Res5.set(true);
                    break;
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void decryptRegionAttributes(Region region) throws Exception {
        dlat  = Double.parseDouble(Cryptography.decrypt(String.valueOf(region.getLatitude())));
        dlon  = Double.parseDouble(Cryptography.decrypt(String.valueOf(region.getLongitude())));
        // Você deve fazer o mesmo para os outros atributos que precisam ser descriptografados
    }



}