package com.example.ejercitadorrespiratoriodigital;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class registroManual extends AppCompatActivity {
    private ManejoDB manejoDB;
    FirebaseFirestore db;
    EditText eTFlujo,eTVolumen,eTOxi,eTPulso;
    Button btnRegistrar, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_manual);

        manejoDB=new ManejoDB();
        db=FirebaseFirestore.getInstance();
        eTFlujo=findViewById(R.id.eTFlujo);
        eTVolumen=findViewById(R.id.eTVolumen);
        eTOxi=findViewById(R.id.eTOxi);
        eTPulso=findViewById(R.id.eTPulso);

        btnRegistrar=findViewById(R.id.btnRegistrar);
        btnRegresar=findViewById(R.id.btnRegresar);

    }

    public void onClickRegistrar(View view) {

        registrarADB();
        eTFlujo.setText(""); // Limpia campos de datos
        eTVolumen.setText("");
        eTOxi.setText("");
        eTPulso.setText("");


    }

    private void registrarADB() {
        String Flujo =eTFlujo.getText().toString();
        String Volumen =eTVolumen.getText().toString();
        String Oxi =eTOxi.getText().toString();
        String Pulso =eTPulso.getText().toString();

        manejoDB.registrarEspEnDB(Flujo,Volumen);
        manejoDB.registrarPulsiOxEnDB(Oxi,Pulso);


    }


    public void onClickRegresar(View view) {
        Intent intentMenu =new Intent(getApplicationContext(),activity_menu.class);
        startActivity(intentMenu);
    }
}