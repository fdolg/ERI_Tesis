package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PC6M_1 extends AppCompatActivity {

    private static final int STATE_LISTENING = 1,REQUEST_ENABLE_BT =10,STATE_MESSAGE_RECIEVED = 2;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private TextView tvStatus,tvCDown,tvOxi,tvSp;
    private int numBytes,progress,i;
    private byte[] mmBuffer = new byte[1024];
    private ProgressBar pbTiempo;
    private ComunicaThread comunicaThreadBT;
    private Button btnComenzar;
    private float oxiAcum;
    CountDownTimer countDownTimer;
    private long segTot=60000;
    private boolean start=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc6_m1);

        btnComenzar=findViewById(R.id.btnComenzar);
        pbTiempo=findViewById(R.id.pbTiempo);
        tvStatus=findViewById(R.id.tvStatus);
        tvCDown=findViewById(R.id.tvCDown); tvCDown.setVisibility(View.INVISIBLE);
        tvOxi=findViewById(R.id.tvOxi); tvOxi.setVisibility(View.INVISIBLE);
        tvSp=findViewById(R.id.tvSp); tvSp.setVisibility(View.INVISIBLE);

        btAdaptador= BluetoothAdapter.getDefaultAdapter();
        i = 0;
        progress=0;
        pbTiempo.setMax(60);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothDevice dispositivo=btAdaptador.getRemoteDevice(usuario.BTdirec);

        try {

            btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(btUUID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            btSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error al conectar con dispositivo" ,Toast.LENGTH_LONG).show();

            try {
                btSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        comunicaThreadBT= new ComunicaThread(btSocket);
        comunicaThreadBT.start();
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

        public void run() {

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = minputStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED,numBytes,-1,mmBuffer).sendToTarget();
                } catch (IOException e) {
                    Log.d("eInputStream", "Input stream was disconnected", e);
                    break;
                }
            }

        }

    }
    Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what){
                case STATE_LISTENING:
                    tvStatus.setText("Escuchando");
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readBuffer= (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer,0,msg.arg1);
                    Promediar(tempMsg);
                    Desplegar(tempMsg);
                    break;

            }
            return  true;
        }

    });

    private void Desplegar(String data) {
       String aux1[]= data.split(",",4);
        tvOxi.setText(aux1[3]);

    }

    private void Promediar(String data) {
        String aux []= data.split(",",4); // Se obtiene dato de concentración y tasa separados por coma
        float acum=Float.parseFloat(aux[3]);
        if (i<55 && acum<101 && acum!=0)
        {
            oxiAcum += acum;
            i++;
        }

    }


    public void onClickComenzar(View view) {
        //countDownTimer.start();
        startChronometer();
        tvCDown.setVisibility(View.VISIBLE);
        btnComenzar.setVisibility(View.INVISIBLE);
        tvOxi.setVisibility(View.VISIBLE);
        tvSp.setVisibility(View.VISIBLE);
        tvStatus.setText("Medición en curso...");
        start=true;
    }

    //Cronómetro
    private void startChronometer(){
        countDownTimer=new CountDownTimer(segTot,1000) {
            @Override
            public void onTick(long l) {
                progress++;
                pbTiempo.setProgress(progress);
                segTot=l;
                updateCounDownText();
            }

            @Override
            public void onFinish() {
                usuario.basalOx= (int) (oxiAcum/i);
                Intent intentPC6M2 = new Intent(getApplicationContext(),PC6M_2.class);
                startActivity(intentPC6M2);

            }
        }.start();
    }
    private void updateCounDownText() {
        int minutes = (int) (segTot/1000) /60;
        int seconds = (int) (segTot/1000) %60;
        String timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvCDown.setText(timeLeftFormat);
    }

    public void onClickAtras(View view) {
        Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
        startActivity(intentMenu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
            startActivity(intentMenu);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (start){
            startChronometer();
        }

    }

   @Override
    protected void onStop() {
        super.onStop();
        if (start){
            countDownTimer.cancel();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (start){
            countDownTimer.cancel();
        }

    }
}