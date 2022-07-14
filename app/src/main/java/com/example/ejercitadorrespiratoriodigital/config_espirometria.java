package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class config_espirometria extends AppCompatActivity {
    private EditText eTFlujoObj, eTVolObj,eTMin,eTSeg,eTTInsp;
    private Button  btnComenzar;
    private Spinner spinner;
    private String[] rIE={"1/2","1/3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_espirometria);
        eTFlujoObj=findViewById(R.id.eTFlujoObj);
        eTVolObj=findViewById(R.id.eTVolObj);
        eTMin=findViewById(R.id.eTMin);
        eTSeg=findViewById(R.id.eTSeg);
        eTTInsp=findViewById(R.id.eTTInsp);
        btnComenzar=findViewById(R.id.btnComenzar);
        spinner=findViewById(R.id.spinner);

        // Configuración spinner
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,rIE);
        spinner.setAdapter(adapter);
    }

    public void onClickComenzar(View view) {
        String FlujoObj = eTFlujoObj.getText().toString();
        String VolObj = eTVolObj.getText().toString();
        String min= eTMin.getText().toString();
        String seg= eTSeg.getText().toString();
        String IESelec = spinner.getSelectedItem().toString();
        String tInsp = eTTInsp.getText().toString();

        Intent intentEsp =new Intent(getApplicationContext(),activity_espirometria.class);
        intentEsp.putExtra(getResources().getString(R.string.str_flujo_obj),FlujoObj);
        intentEsp.putExtra(getResources().getString(R.string.str_vol_obj),VolObj);
        intentEsp.putExtra(getResources().getString(R.string.str_min),min);
        intentEsp.putExtra(getResources().getString(R.string.str_seg),seg);
        intentEsp.putExtra(getResources().getString(R.string.str_IE_obj),IESelec);
        intentEsp.putExtra(getResources().getString(R.string.str_t_insp),tInsp);
        startActivity(intentEsp);
    }

}