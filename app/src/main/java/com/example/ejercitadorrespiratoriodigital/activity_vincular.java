package com.example.ejercitadorrespiratoriodigital;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class activity_vincular extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT =10 ;
    private ListView lstDispositivos;
    private BluetoothAdapter BtAdaptador; //Adaptador para manejo de bluetooth
    private TextView txtvTitulo;
    private ArrayList<String> dispositivos;
    String sensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincular);
        lstDispositivos = findViewById(R.id.lstdVinculados);
        txtvTitulo= findViewById(R.id.txtvTitulo);
        lstDispositivos.setOnItemClickListener(ItemClickLista);

        Bundle bundleextras= getIntent().getExtras(); // Clase que permite recibir datos de activity previo
        sensor = bundleextras.getString(getResources().getString(R.string.str_sensor)); // Recepción de dato
    }
    @Override
    protected void onResume() {
        super.onResume();
        VerificarEstadoBt();


    }

    private void VerificarEstadoBt() {
        BtAdaptador= BluetoothAdapter.getDefaultAdapter();

        if(BtAdaptador== null)
        {
            Toast.makeText(getApplicationContext(),"Su equipo no cuenta con bluetooth",Toast.LENGTH_SHORT).show();
        }else {
            if(BtAdaptador.isEnabled() && BtAdaptador.isEnabled()){
                Log.d("DispBluetoooth","....Bluetooth Activiado");
                MuestraDispositivos();

            }else
            {
                //Pedir al usuario que active Bluetooth
                Intent haBTint = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(haBTint,REQUEST_ENABLE_BT);
            }
        }
    }

    private void MuestraDispositivos() {
        Set<BluetoothDevice> DispEmp= BtAdaptador.getBondedDevices();
        if(DispEmp.size() > 0)
        {
            dispositivos= new ArrayList<>();
            for(BluetoothDevice dispositivo:DispEmp){

                dispositivos.add(dispositivo.getName()+"\n"+dispositivo.getAddress());
            }
            ArrayAdapter<String > adapter =new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dispositivos);
            lstDispositivos.setAdapter(adapter);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            MuestraDispositivos();

        }
    }

    AdapterView.OnItemClickListener ItemClickLista= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String cadAux= dispositivos.get(i).toString();
            String [] cau2= cadAux.split("\n",2);
            //intentEspirometria.putExtra(getResources().getString(R.string.str_direccion_dispositivo),cau2[1]);
            usuario.BTdirec=cau2[1];

            if (sensor.equals("esp")){
                Intent intentconfigEsp = new Intent(getApplicationContext(),config_espirometria.class);
                startActivity(intentconfigEsp);
            }
            if (sensor.equals("pox")){
                Intent intentPox = new Intent(getApplicationContext(),activity_pulsioximetria.class);
                startActivity(intentPox);
            }
        }
    };

}