package com.example.divvipay;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText Name;
    EditText PhnNo;
    EditText Password1;
    EditText Password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Name = (EditText)findViewById(R.id.SignUpTF);
        PhnNo = (EditText)findViewById(R.id.SignUpPhnTF);
        Password1 = (EditText)findViewById(R.id.SignUpPswd1TF);
        Password2 = (EditText)findViewById(R.id.SignUpPswd2TF);

    }
    public void onSignUpClick(View v)
    {

        String Namestr = Name.getText().toString();
        String PhnNostr = PhnNo.getText().toString();
        String Passwords1tr = Password1.getText().toString();
        String Passwords2tr = Password2.getText().toString();

        if(PhnNostr.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Phone Number Cannot be Empty",Toast.LENGTH_LONG).show();
        }

        else if(Passwords1tr.equals(Passwords2tr))
        {
            Toast.makeText(getApplicationContext(), "Thanks for Signing Up..!!!!",Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getApplicationContext(), "Password doesn't Match..!!!",Toast.LENGTH_LONG).show();

        }

    }
}
