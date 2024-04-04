package com.example.atividadeautavan;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vitorautavan.Trinta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultaBD30 extends Thread{

    private final AtomicBoolean Res;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean regionFound = false;
    public Region newRegion;



    public ConsultaBD30(Region newRegion, FirebaseFirestore db, AtomicBoolean Res) {
        this.newRegion = newRegion;
        this.db = db;
        this.Res = Res;
    }

    @Override
    public void run(){
        varrerDb();
    }

    public boolean varrerDb() {

        try {
            QuerySnapshot querySnapshot = Tasks.await(db.collection("DadosAutAvan").get()); // Realiza a consulta síncrona ao Firestore
            for (QueryDocumentSnapshot document : querySnapshot) {
                Region dbRegion = document.toObject(Region.class); // Converte o documento em um objeto Regiao

                // Calcula a distância entre a região candidata e a região atual
                Trinta mTrinta = new Trinta();
                Double distancia = mTrinta.trinta(newRegion.getLatitude() , newRegion.getLongitude() , dbRegion.getLatitude() , dbRegion.getLongitude());

                if(distancia <= 30){ // Verifica se a distância é menor ou igual a 30
                    Res.set(true); // Define que existe uma região próxima
                    return true; // Interrompe a consulta
                }
            }
            return false;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }



}