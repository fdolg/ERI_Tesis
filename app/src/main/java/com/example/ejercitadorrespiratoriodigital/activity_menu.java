package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class activity_menu extends AppCompatActivity {

    private Button btnEspirometria;
    private BluetoothAdapter btAdaptador;
    private static final int REQUEST_ENABLE_BT =10;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket btSocket=null;

    private ComunicaThread comunicaThreadBT;
    private TextView tvUsuario;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnEspirometria = findViewById(R.id.btnEspirometria);
        btAdaptador= BluetoothAdapter.getDefaultAdapter();
        tvUsuario=findViewById(R.id.tvUsuario);

        tvUsuario.setText("ID: "+usuario.email);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothDevice dispositivo=btAdaptador.getRemoteDevice(usuario.BTdirec);

        try {

            btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(btUUID);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Encienda el dispositivo",Toast.LENGTH_SHORT).show();

        }
        try {
            btSocket.connect();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error al conectar con dispositivo" ,Toast.LENGTH_LONG).show();
            Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
            startActivity(intentMenu);

            try {
                btSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        comunicaThreadBT= new ComunicaThread(btSocket);
        comunicaThreadBT.start();
        comunicaThreadBT.write("A");
    }



    @Override
    protected void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Comunicación con Bluetooth por medio del protocolo serie
    private class ComunicaThread extends Thread{
        private InputStream minputStream;
        private OutputStream moutputStream;


        private ComunicaThread(BluetoothSocket socket) {
            try {
                this.minputStream= socket.getInputStream();
                this.moutputStream= socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.d("eSocket","Error: " +e.toString());
            }
        }

        public void write(String input)  {

            try {
                moutputStream.write(input.getBytes());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }



    public void onClickEspirometria(View view)
    {
        Intent intentConfigEsp =new Intent(getApplicationContext(),config_espirometria.class);
        startActivity(intentConfigEsp);

    }
    public void onClickPox(View view)
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intentPox =new Intent(getApplicationContext(),activity_pulsioximetria.class);
            startActivity(intentPox);
    }
    public void onClickPC6M(View view)
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intentPC6M1 =new Intent(getApplicationContext(),PC6M_1.class);
        startActivity(intentPC6M1);
    }
    public void onClickHistorial(View view)
    {
        Intent intentSelecHistorial =new Intent(getApplicationContext(),seleccionHistorial.class);
        startActivity(intentSelecHistorial);
    }

    public void onClickMain(View view) {
        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intentMain);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentMain);
        }
        return super.onKeyDown(keyCode, event);
    }
}