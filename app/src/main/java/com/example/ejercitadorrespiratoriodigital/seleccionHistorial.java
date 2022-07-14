package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class seleccionHistorial extends AppCompatActivity {
    Button btnHEsp, btnHOxi;
    String mood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_historial);
        btnHEsp=findViewById(R.id.btnHEsp);
        btnHOxi=findViewById(R.id.btnHOxi);

    }

    public void onClick(View view) {

        int seleccion= view.getId();
        switch (seleccion){
            case R.id.btnHEsp:
                this.mood="esp";
                break;
            case R.id.btnHOxi:
                this.mood="oxi";
                break;
        }
        Intent intentHistorial = new Intent(getApplicationContext(), Historial.class);
        intentHistorial.putExtra(getResources().getString(R.string.str_mood), mood);
        startActivity(intentHistorial);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
            startActivity(intentMenu);
        }
        return super.onKeyDown(keyCode, event);
    }
}