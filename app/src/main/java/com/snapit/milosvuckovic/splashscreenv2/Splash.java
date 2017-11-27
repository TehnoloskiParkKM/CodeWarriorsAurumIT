package com.snapit.milosvuckovic.splashscreenv2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snapit.milosvuckovic.splashscreenv2.receiver.ConnectionDetector;

public class Splash extends AppCompatActivity {
    LinearLayout l1, l2;
    Animation uptodown, downtoup;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        final Intent i = new Intent(this, Screen2.class);
        Thread timer =new Thread(){
            public void run () {
                try {
                    sleep(1800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();

                }
            }
        };
        timer.start();

        cd = new ConnectionDetector(this);

        if (cd.isConnected()) {
             //Nema potrebe da pise da je korisnik konektovan na internetu pri ulasku u app
        } else {
            Toast.makeText(Splash.this, "Internet konekcija: isključena", Toast.LENGTH_SHORT).show();
        }
    }
}