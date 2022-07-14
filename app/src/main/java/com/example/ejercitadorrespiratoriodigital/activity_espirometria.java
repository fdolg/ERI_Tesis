package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

public class activity_espirometria extends AppCompatActivity {

    private static final int STATE_LISTENING = 1;
    private static final int STATE_MESSAGE_RECIEVED = 2;
    private String direccion;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int handlerState=0;
    private Handler hBTcomunica;
    private Switch swRegistro;
    private ComunicaThread comunicaThreadBT;
    private TextView tvFlujo,tvOxi,tvVolumen,tvStatus,tvCDown,tvIE;
    private int numBytes,progressRR=0,min,seg,tInsp;
    private long  segTot,segInsp,segEsp;
    private byte[] mmBuffer = new byte[1024];
    private ProgressBar pbFlujo;
    private ProgressBar pbVolumen;
    private ProgressBar pbRR;
    private String flujoMax,volMax,minObj,segObj,rIEobj,aux[];
    private ManejoDB manejoDB;
    private CountDownTimer countDownTimer2,countDownTimerUp,countDownTimerDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espirometria);

        swRegistro= findViewById(R.id.swRegistro);
        tvFlujo=findViewById(R.id.tvFlujo);
        tvStatus=findViewById(R.id.tvStatus);
        tvVolumen=findViewById(R.id.tvVolumen);
        tvOxi=findViewById(R.id.tvOxi);
        tvCDown=findViewById(R.id.tvCDown);
        tvIE=findViewById(R.id.tvIE);
        pbFlujo=findViewById(R.id.pbFlujo);
        pbVolumen=findViewById(R.id.pbVolumen);
        pbRR=findViewById(R.id.pbRR);
        btAdaptador= BluetoothAdapter.getDefaultAdapter();


        Bundle bundleextras= getIntent().getExtras();
        flujoMax=bundleextras.getString(getResources().getString(R.string.str_flujo_obj));
        volMax=bundleextras.getString(getResources().getString(R.string.str_vol_obj));
        minObj=bundleextras.getString(getResources().getString(R.string.str_min));
        segObj=bundleextras.getString(getResources().getString(R.string.str_seg));
        rIEobj=bundleextras.getString(getResources().getString(R.string.str_IE_obj));
        tInsp= Integer.parseInt(bundleextras.getString(getResources().getString(R.string.str_t_insp)));


        manejoDB = new ManejoDB();

        swRegistro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    countDownTimer1.start();
                    countDownTimer2.start();
                }
                else {
                    countDownTimer1.cancel();

                }
            }
        });
        //Se inicializa el cronómetro

        min= Integer.parseInt(minObj);
        seg=Integer.parseInt(segObj);

        segTot = (min*60+seg)*1000;
        updateCounDownText();


        //Progress bar relación IE
        pbsetRatio();

    }

    private void pbsetRatio() {
        segInsp=tInsp*1000;

        if (rIEobj.equals("1/2")){
            segEsp=tInsp*2*1000;
            pbRR.setMax(123*tInsp);
        }
        else {
            segEsp=tInsp*3*1000;
            pbRR.setMax(185*tInsp);
        }
        pbRR.setProgress(0);
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
        startChronometer();
        start_pbIE();

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
        tvFlujo.setText(aux[0]);
        tvVolumen.setText(aux[1]);
        tvOxi.setText(aux[3]);

        setpbFlujo(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]));
    }

    public void setpbFlujo(int flujo,int volumen)
    {

        pbFlujo.setProgress( flujo);
        pbFlujo.setMax(Integer.parseInt(flujoMax));

        pbVolumen.setProgress(volumen);
        pbVolumen.setMax(Integer.parseInt(volMax));

    }

    //Contadores de la barra de relación IE
    private void start_pbIE() {
        countDownTimerUp= new CountDownTimer(segInsp,10) {
            @Override
            public void onTick(long l){
                if (rIEobj.equals("1/2")){
                    progressRR+=2;
                }
                else{
                    progressRR+=3;
                }

                pbRR.setProgress(progressRR);

            }

            @Override
            public void onFinish() {
                countDownTimerDown.start();
                tvIE.setText("Espira");
            }
        }.start();

        countDownTimerDown=new CountDownTimer(segEsp,10) {
            @Override
            public void onTick(long l){
                progressRR --;
                pbRR.setProgress(progressRR);

            }

            @Override
            public void onFinish() {
                progressRR=0;
                countDownTimerUp.start();
                tvIE.setText("Inspira");
            }
        };


    }



    //Contador de los registros en la BD
    CountDownTimer countDownTimer1=new CountDownTimer(1000*60,10*1000) {
        @Override
        public void onTick(long l){
            String Flujo = (String) tvFlujo.getText();
            String Volumen = (String) tvVolumen.getText();
            manejoDB.registrarEspEnDB(Flujo,Volumen);
        }

        @Override
        public void onFinish() {
            countDownTimer1.start();
        }
    };

    //Cronómetro
    private void startChronometer(){
        countDownTimer2=new CountDownTimer(segTot,1000) {
            @Override
            public void onTick(long l) {
                segTot=l;
                updateCounDownText();
            }

            @Override
            public void onFinish() {
                mostrarDialogTermino();

            }
        }.start();
    }

    private void mostrarDialogTermino() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setMessage("Sesión terminada");
        builder.show();
        try {
            Thread.sleep(1500);
            Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
            startActivity(intentMenu);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void updateCounDownText() {
        int minutes = (int) (segTot/1000) /60;
        int seconds = (int) (segTot/1000) %60;
        String timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvCDown.setText(timeLeftFormat);
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