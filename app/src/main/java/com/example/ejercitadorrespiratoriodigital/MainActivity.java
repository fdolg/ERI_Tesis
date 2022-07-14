package com.example.ejercitadorrespiratoriodigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnMenu;
    private Button btnAcerca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMenu=findViewById(R.id.btnMenu);
        btnAcerca=findViewById(R.id.btnEspirometria);

    }
    public void onClickLogin(View view)
    {
        Intent intentLogin = new Intent(MainActivity.this,Login.class);
        startActivity(intentLogin);
    }

}