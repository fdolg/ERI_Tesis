package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PC6M_3 extends AppCompatActivity {
    private TextView tvBasal, tvAfter,tvResultado,tv;
    private ProgressBar pbPC6M,pbBasal;
    private ManejoDB manejoDB;
    private Button btnRegistrar;
    private int dif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc6_m3);

        tvBasal=findViewById(R.id.tvBasal);
        tvAfter=findViewById(R.id.tvAfter);
        tvResultado=findViewById(R.id.tvResultado);
        tv=findViewById(R.id.tv);
        pbPC6M=findViewById(R.id.pbPCM6); pbPC6M.setMax(100);
        pbBasal=findViewById(R.id.pbBasal); pbBasal.setMax(100);
        manejoDB = new ManejoDB();
        btnRegistrar=findViewById(R.id.btnRegistrar);
        checkResults();
    }

    private void checkResults() {
        if (usuario.basalOx>100 || usuario.afterOx>100){
            pbPC6M.setVisibility(View.INVISIBLE);
            pbBasal.setVisibility(View.INVISIBLE);
            btnRegistrar.setVisibility(View.INVISIBLE);
            tvBasal.setText("ERROR DE MEDICIÓN");
            tv.setText("REPITA LA PRUEBA...");
        }
        else{
            setResults();
        }
    }

    private void setResults() {
        tvBasal.setText("Oximetría Basal:"+usuario.basalOx);
        pbBasal.setProgress(usuario.basalOx);
        tvAfter.setText("Oximetría PCM6:"+usuario.afterOx);
        pbPC6M.setProgress(usuario.afterOx);
        dif = usuario.basalOx-usuario.afterOx;
        if (dif>0){
            tvResultado.setText("-"+dif+"%");
        }
        if (dif==0){
            tvResultado.setText(dif+"%");
        }
        if (dif<0){
            dif=dif*-1;
            tvResultado.setText("+"+dif+"%");
        }

    }

    public void onClickAtras(View view) {
        Intent intentMenu = new Intent(getApplicationContext(), activity_menu.class);
        startActivity(intentMenu);
    }

    public void onClickRegistrar(View view) {
        btnRegistrar.setVisibility(View.INVISIBLE);
        manejoDB.registrarPC6MEnDB(String.valueOf(usuario.basalOx),String.valueOf(usuario.afterOx),String.valueOf(dif));
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