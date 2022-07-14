package com.example.ejercitadorrespiratoriodigital;

import  androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class activity_pulsioximetria extends AppCompatActivity {


    private static final int STATE_LISTENING = 1;
    private static final int STATE_MESSAGE_RECIEVED = 2;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Switch swRegistro;
    private TextView tvBpm,tvSpO2,tvStatus;
    private int numBytes,progressRR=0,min,seg,tInsp;
    private long  segTot,segInsp,segEsp;
    private byte[] mmBuffer = new byte[1024];
    private String aux[];
    private ProgressBar pbBpm,pbSpO2;
    private String flujoMax,volMax;
    private ManejoDB manejoDB;
    private ComunicaThread comunicaThreadBT;
    private CountDownTimer countDownTimer2,countDownTimerUp,countDownTimerDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsioximetria);

        tvBpm=findViewById(R.id.tvBpm);
        tvSpO2=findViewById(R.id.tvSpO2);
        tvStatus=findViewById(R.id.tvStatus);
        pbBpm=findViewById(R.id.pbBPM);
        pbSpO2=findViewById(R.id.pbSpO2);
        swRegistro= findViewById(R.id.swRegistro);
        manejoDB = new ManejoDB();
        btAdaptador= BluetoothAdapter.getDefaultAdapter();

        //Configuración PB
        pbBpm.setMax(210);
        pbSpO2.setMax(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothDevice dispositivo=btAdaptador.getRemoteDevice(usuario.BTdirec);

        try {

            btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(btUUID);
            Toast.makeText(getApplicationContext(),"Direccion:" +usuario.BTdirec,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Falló la creación de soccket",Toast.LENGTH_SHORT).show();
        }
        try {
            btSocket.connect();

        } catch (IOException e) {
            Log.d("ErrorContect","Error de conexión: " +e.toString());
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

        public void write(String input)  {

            try {
                moutputStream.write(input.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
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
                    tvStatus.setText("Desconectado");
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
                    Desplegar(tempMsg);

                    break;

            }
            return  true;
        }

    });

    private void Desplegar(String data) {
        aux= data.split(",",4);
        tvBpm.setText(aux[2]);
        tvSpO2.setText(aux[3]);
        setpb(Integer.parseInt(aux[2]), Integer.parseInt(aux[3]));
    }

    public void setpb(int Bpm,int SpO2)
    {

        pbBpm.setProgress(Bpm);
        pbSpO2.setProgress(SpO2);

    }


}