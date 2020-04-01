package com.divvipay.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ViewTripDetail extends AppCompatActivity {

    LinearLayout expenses,balance,Header2,payment;
    String str,gid;
    TextView Expense,Balance,Heading,Payment;
    View Expence_v,Balance_v,Payment_v;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip_detail);
        getSupportActionBar().hide();
        checkInternet();
        Intent in=getIntent();
        Bundle bundle=in.getExtras();
         str=bundle.getString("str");
         gid=bundle.getString("gid");

         //Lineear Layout
        expenses=findViewById(R.id.expenses);
        balance=findViewById(R.id.balances);
        Header2=findViewById(R.id.header2);
        payment=findViewById(R.id.payments);

        //text View
        Expense=findViewById(R.id.expense);
        Balance =findViewById(R.id.balance);
        Heading =findViewById(R.id.heading);
        Payment =findViewById(R.id.payment);

        //View
        Expence_v=findViewById(R.id.expense_view);
        Balance_v=findViewById(R.id.balance_view);
        Payment_v=findViewById(R.id.payment_view);



        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected==true) {
                    Expense.setTextColor(getResources().getColor(R.color.colorPrimary));
                    Expence_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Balance.setTextColor(Color.LTGRAY);
                    Balance_v.setBackgroundColor(Color.LTGRAY);
                    Payment.setTextColor(Color.LTGRAY);
                    Payment_v.setBackgroundColor(Color.LTGRAY);
                    addFragment(new Fragment_show(str, gid), false, "Fragment_show");
                }
                else
                {
                    Toast.makeText(ViewTripDetail.this,"Please check internet connectivity",Toast.LENGTH_SHORT).show();

                }

            }
        });

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected==true) {
                    Balance.setTextColor(getResources().getColor(R.color.colorPrimary));
                    Balance_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Expense.setTextColor(Color.LTGRAY);
                    Expence_v.setBackgroundColor(Color.LTGRAY);
                    Payment.setTextColor(Color.LTGRAY);
                    Payment_v.setBackgroundColor(Color.LTGRAY);
                    addFragment(new Fragment_balance(str, gid), false, "Fragment_balance");
                }
                else
                {
                    Toast.makeText(ViewTripDetail.this,"Please check internet connectivity",Toast.LENGTH_SHORT).show();

                }
            }
        });


        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected)
                {
                    Balance.setTextColor(Color.LTGRAY);
                    Balance_v.setBackgroundColor(Color.LTGRAY);
                    Expense.setTextColor(Color.LTGRAY);
                    Expence_v.setBackgroundColor(Color.LTGRAY);
                    Payment.setTextColor(getResources().getColor(R.color.colorPrimary));
                    Payment_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    addFragment(new Fragment_Payment(str, gid), false, "Fragment_Payment");

                }
                else
                {
                    Toast.makeText(ViewTripDetail.this,"Please check internet connectivity",Toast.LENGTH_SHORT).show();

                }
            }
        });


        if(connected==true) {
            Expense.setTextColor(getResources().getColor(R.color.colorPrimary));
            Expence_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Balance.setTextColor(Color.LTGRAY);
            Balance_v.setBackgroundColor(Color.LTGRAY);
            addFragment(new Fragment_show(str, gid), false, "Fragment_show");
        }
        else
        {
            Toast.makeText(ViewTripDetail.this,"Please check internet connectivity",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signout() {
        SharedPreferences preferences = getSharedPreferences("LoginStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        FirebaseAuth.getInstance().signOut();
        Intent Main = new Intent(ViewTripDetail.this, MainActivity.class);
        startActivity(Main);
        finish();
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.frame_split, fragment, tag);
        ft.commitAllowingStateLoss();
    }


    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

    }

    public void header(String s)
    {
     Heading.setText(s);
     if(s.equalsIgnoreCase("Add expenses")) {
         Header2.setVisibility(View.GONE);

     }
     else
     {
         Header2.setVisibility(View.VISIBLE);


     }
    }




    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag("Update") != null){
            //add homeFragment;
            Expense.setTextColor(getResources().getColor(R.color.colorPrimary));
            Expence_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Balance.setTextColor(Color.LTGRAY);
            Balance_v.setBackgroundColor(Color.LTGRAY);
            addFragment(new Fragment_show(str, gid), false, "Fragment_show");
        }

        else if(getSupportFragmentManager().findFragmentByTag("Fragment_balance") != null)
        {
            Expense.setTextColor(getResources().getColor(R.color.colorPrimary));
            Expence_v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Balance.setTextColor(Color.LTGRAY);
            Balance_v.setBackgroundColor(Color.LTGRAY);
            addFragment(new Fragment_show(str, gid), false, "Fragment_show");

        }
        else{
            if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            }
            else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
