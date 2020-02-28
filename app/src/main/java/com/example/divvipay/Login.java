package com.example.divvipay;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button button;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

         button = (Button)findViewById(R.id.btn_signup);
         button.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        run();
    }
    public void run()
    {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
        finish();
    }
}

