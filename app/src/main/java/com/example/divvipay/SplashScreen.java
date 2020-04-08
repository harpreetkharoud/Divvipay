package com.divvipay.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.appcolor));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));

        }
        setContentView(R.layout.activity_splash_screen);
 final ImageView splash = (ImageView) findViewById(R.id.logo);
        final TextView appName=(TextView)findViewById(R.id.app_name);

        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        Animation animShake = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.shake);

        appName.startAnimation(animShake);

        new CountDownTimer(2000, 50) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent=new Intent(SplashScreen.this,google_sign_in.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
