package com.example.ejercitadorrespiratoriodigital;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Historial extends AppCompatActivity {
    private ManejoDB manejoDB;
    private ListView lstRegistros;
    private String mood;
    private TextView titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        lstRegistros=findViewById(R.id.lstRegistros);
        titulo=findViewById(R.id.tvTitulo);
        Bundle bundle=getIntent().getExtras();
        mood=bundle.getString(getResources().getString(R.string.str_mood));
        setTitulo(mood);

        manejoDB=new ManejoDB();
        manejoDB.setMood(mood);
        manejoDB.setVariables(getApplicationContext(),lstRegistros);
        manejoDB.ConsultarPorEmail();

    }

    private void setTitulo(String mood) {
        if (mood.equals("esp")){
            titulo.setText("Historial del paciente: Espirometría ");
        }
        else
        {
            titulo.setText("Historial del paciente: Pulsioximetría");
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        manejoDB.ConsultarPorEmail();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentSeleccionH = new Intent(getApplicationContext(), seleccionHistorial.class);
            startActivity(intentSeleccionH);
        }
        return super.onKeyDown(keyCode, event);
    }
}