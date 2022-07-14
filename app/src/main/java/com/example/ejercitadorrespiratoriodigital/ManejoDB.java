package com.example.ejercitadorrespiratoriodigital;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManejoDB {
    private FirebaseFirestore db;
    private ArrayList<String> registros;
    private ArrayList<String> registroFecha;
    private ArrayList<String> registroValor;
    private ArrayList<String> registroID;
    private ListView lstRegistros;
    private Context context;
    private String Mood;


    public ManejoDB(){
        db=FirebaseFirestore.getInstance();

    } //Constructor


    public void setVariables(Context context,ListView lstRegistros){
        this.context=context;
        this.lstRegistros=lstRegistros;
    }
    public void setMood(String mood)
    {
        this.Mood=mood;
    }

    public void ConsultarPorEmail(){

        Query query = db.collection("lecturasERI").whereEqualTo("email", usuario.email); // Hace búsqueda con email de la clase email

        if (query!=null){

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {// Se emplean los registros de la búsqueda

                            if(task.isSuccessful()){
                                registros=new ArrayList<>();
                                registroFecha=new ArrayList<>();
                                registroValor=new ArrayList<>();
                                registroID=new ArrayList<>();
                               if(Mood.equals("esp")){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if((String)document.get("flujo")!=null){
                                            registros.add("Fecha: "+(String) document.get("fecha")+"\n"+"Flujo(cc/s): "+
                                                    (String) document.get("flujo")+"\n"+
                                                    "Volumen(mL): "+(String) document.get("volumen")+"\n"); // Se concatenan los campos en el formato requerido
                                            registroFecha.add((String) document.get("fecha"));
                                            registroValor.add((String) document.get("flujo")+","+(String) document.get("volumen"));
                                            registroID.add(document.getId());
                                        }
                                    }
                              }

                               if (Mood.equals("oxi"))
                                {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if ((String)document.get("oxigenacion")!=null){
                                            registros.add("Fecha: "+(String) document.get("fecha")+"\n"+"Oxigenación(%): "+
                                                    (String) document.get("oxigenacion")+"\n"+
                                                    "Pulso(PPM): "+(String) document.get("pulso")+"\n"); // Se concatenan los campos en el formato requerido
                                            registroFecha.add((String) document.get("fecha"));
                                            registroValor.add((String) document.get("oxigenacion")+","+(String) document.get("pulso"));
                                            registroID.add(document.getId());
                                        }

                                    }
                                }

                                ArrayAdapter<String > adapter =new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,registros);
                                lstRegistros.setAdapter(adapter); // Se emplea el adaptador para visualizar registros
                            }else{
                                Log.w(TAG, "Error getting documents. Tienes un error", task.getException());

                            }

                        }
                    });


        }


    }
    public void borrar(String IDborrar){

        db.collection("lecturas").
                document(IDborrar).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,"Se borró éxitosamente", Toast.LENGTH_LONG);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"No se pudo borrar éxitosamente", Toast.LENGTH_LONG);
            }
        });

    }

    public void registrarEspEnDB(String flujo, String volumen){
        CollectionReference lecturas=db.collection("lecturasERI"); // Se señala la colección a usar
        Map<String,Object> data =new HashMap<>();

        String fecha = new Date().toString();

       if(fecha.equals("")==false && flujo.equals("")==false && volumen.equals("")==false){ // Verificación de datos
            data.put("email", usuario.email);
            data.put("fecha", fecha);
            data.put("flujo", flujo);
            data.put("volumen", volumen);
            lecturas.document().set(data); // Se registran datos

       }

    }
    public void registrarPulsiOxEnDB(String oxigenacion, String pulso){
        CollectionReference lecturas=db.collection("lecturasERI"); // Se señala la colección a usar
        Map<String,Object> data =new HashMap<>();

        String fecha = new Date().toString();

        if(fecha.equals("")==false && oxigenacion.equals("")==false && pulso.equals("")==false){ // Verificación de datos
            data.put("email", usuario.email);
            data.put("fecha", fecha);
            data.put("oxigenacion", oxigenacion);
            data.put("pulso", pulso);
            lecturas.document().set(data); // Se registran datos
        }

    }

    public ArrayList<String> getRegistroFecha() {
        return registroFecha;
    }

    public ArrayList<String> getRegistroValor() {
        return registroValor;
    }

    public ArrayList<String> getRegistros() {
        return registros;
    }

    public ArrayList<String> getRegistroID() {
        return registroID;
    }

    public FirebaseFirestore getDb() {
        return db;
    }


}