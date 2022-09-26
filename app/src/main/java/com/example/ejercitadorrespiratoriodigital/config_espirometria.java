package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class config_espirometria extends AppCompatActivity {
    private EditText eTFlujoObj, eTVolObj,eTMin,eTSeg,eTTInsp;
    private Button  btnComenzar;
    private Spinner spinner;
    private String[] rIE={"1/2","1/3"};
    private Switch swRegistrarEsp;
    private String reg="off";
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
        swRegistrarEsp=findViewById(R.id.swRegistroEsp);


        // Configuración spinner
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,rIE);
        spinner.setAdapter(adapter);

        swRegistrarEsp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    reg="on";
                }
            }
        });

    }

    public void onClickComenzar(View view) {
        String FlujoObj = eTFlujoObj.getText().toString();
        String VolObj = eTVolObj.getText().toString();
        String min= eTMin.getText().toString();
        String seg= eTSeg.getText().toString();
        String IESelec = spinner.getSelectedItem().toString();
        String tResp = eTTInsp.getText().toString();




        if(FlujoObj.equals("")==false && VolObj.equals("")==false && (min.equals("")==false || seg.equals("")==false) && IESelec.equals("")==false && tResp.equals("")==false )
        {
            if(min.equals("")){
                min="0";
            }
            if (seg.equals("")){
                seg="0";
            }
            int auxTResp= Integer.parseInt(tResp);
            if (auxTResp>4){
                Intent intentEsp =new Intent(getApplicationContext(),activity_espirometria.class);
                intentEsp.putExtra(getResources().getString(R.string.str_flujo_obj),FlujoObj);
                intentEsp.putExtra(getResources().getString(R.string.str_vol_obj),VolObj);
                intentEsp.putExtra(getResources().getString(R.string.str_min),min);
                intentEsp.putExtra(getResources().getString(R.string.str_seg),seg);
                intentEsp.putExtra(getResources().getString(R.string.str_IE_obj),IESelec);
                intentEsp.putExtra(getResources().getString(R.string.str_t_resp),tResp);
                intentEsp.putExtra(getResources().getString(R.string.str_reg),reg);
                startActivity(intentEsp);
            }
            else {
                Toast.makeText(getApplicationContext(), "Establezca un tiempo respiratorio mayor a 4 segundos", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Llene todos los campos", Toast.LENGTH_LONG).show();
        }

    }


    public void onClickAtras(View view) {
        Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
        startActivity(intentMenu);
    }
}