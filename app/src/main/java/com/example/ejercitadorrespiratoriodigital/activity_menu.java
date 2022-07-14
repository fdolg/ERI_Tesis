package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class activity_menu extends AppCompatActivity {

    private Button btnEspirometria;
    String sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnEspirometria = findViewById(R.id.btnEspirometria);
    }



    public void onClickEspirometria(View view)
    {
        sensor="esp";
        Intent intentVincular =new Intent(getApplicationContext(),activity_vincular.class);
        intentVincular.putExtra(getResources().getString(R.string.str_sensor),sensor);
        startActivity(intentVincular);
    }
    public void onClickPox(View view)
    {
        sensor="pox";
        Intent intentVincular =new Intent(getApplicationContext(),activity_vincular.class);
        intentVincular.putExtra(getResources().getString(R.string.str_sensor),sensor);
        startActivity(intentVincular);
    }
    public void onClickRegistroManual(View view)
    {
        Intent intentRegistro =new Intent(getApplicationContext(),registroManual.class);
        startActivity(intentRegistro);
    }
    public void onClickHistorial(View view)
    {
        Intent intentSelecHistorial =new Intent(getApplicationContext(),seleccionHistorial.class);
        startActivity(intentSelecHistorial);
    }
}