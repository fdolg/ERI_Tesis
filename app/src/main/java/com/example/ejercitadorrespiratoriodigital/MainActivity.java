package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button btnMenu,btnAcerca;
    private BluetoothAdapter btAdaptador;
    private static final int REQUEST_ENABLE_BT =10;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket btSocket=null;
    Boolean dispFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMenu=findViewById(R.id.btnIngresar);
        btnAcerca=findViewById(R.id.btnAcercaDe);

    }

    @Override
    protected void onResume() {
        super.onResume();
        btAdaptador= BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBt();
    }

    private void VerificarEstadoBt() {
        if(btAdaptador== null)
        {
            Toast.makeText(getApplicationContext(),"Su equipo no cuenta con Bluetooth",Toast.LENGTH_SHORT).show();
        }else {
            if(btAdaptador.isEnabled()){
                GetBtDireccion();
            }else
            {
                //Pedir al usuario que active Bluetooth
                Intent haBTint = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(haBTint,REQUEST_ENABLE_BT);
            }
        }
    }

    private void GetBtDireccion() {
        Set<BluetoothDevice> DispEmp= btAdaptador.getBondedDevices();
        if(DispEmp.size() > 0)
        {
            for(BluetoothDevice dispositivo:DispEmp){

                if (dispositivo.getName().equals("ESP32_ERI")){
                    usuario.BTdirec=dispositivo.getAddress();
                    dispFound=true;
                }
            }
        }
        if (!dispFound){
            Toast.makeText(getApplicationContext(),"Vincule el dispositivo ERI antes de continuar",Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickLogin(View view)
    {
        if(dispFound){
            Intent intentLogin = new Intent(MainActivity.this,Login.class);
            startActivity(intentLogin);
        }
        else {
            Toast.makeText(getApplicationContext(),"Vincule el dispositivo ERI antes de continuar",Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickAcercaDe(View view) {
        Intent intentAcercaDe = new Intent(getApplicationContext(),AcercaDe.class);
        startActivity(intentAcercaDe);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}