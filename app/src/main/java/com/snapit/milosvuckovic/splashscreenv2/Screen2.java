package com.snapit.milosvuckovic.splashscreenv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
//import android.support.design.widget.Snackbar;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.security.*;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.snapit.milosvuckovic.splashscreenv2.receiver.ConnectionDetector;
import com.snapit.milosvuckovic.splashscreenv2.receiver.NetworkStateChangeReceiver;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

import static com.snapit.milosvuckovic.splashscreenv2.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class Screen2 extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    Button btn;
    EditText unesiKod;
    ConnectionDetector cd;
    private Context context= this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        cd = new ConnectionDetector(this);

        setContentView(R.layout.activity_screen2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //Novi toolbar sa logoom u centru
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        btn = (Button)findViewById(R.id.posalji_btn); //Formiranje objekta za dugme
        btn.setOnClickListener(this); //Osluskivanje dugmeta

        if(proveri()){
            //  Intent intent = new Intent(Screen2.this, Screen2.class);
            // startActivity(intent);
        } else{
            Intent intent = new Intent(Screen2.this, Screen3.class);
            startActivity(intent);
            finish();
        }

        //Obaveštenje o statusu internet konekcije
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unesiKod = (EditText)findViewById(R.id.unesi_kod_polje);
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "ukljucena" : "iskljucena";

                if (cd.isConnected()){ //Ispitivanje da li je promenljiva networkStatus jednaka vrednosti ukljucena
                    unesiKod.setEnabled(true);//Ako jeste moguce je kucanje
                    btn.setEnabled(true);//Ako jeste moguc je klik na dugme
                    unesiKod.setText("");//Ako jeste vratiti na pocetak inputa
                }else
                {
                    unesiKod.setEnabled(false);//Ako nije onemoguciti input polje
                    btn.setEnabled(false);//Ako nije onemoguciti dugme
                    unesiKod.setError("Ukljucite internet konekciju");//Ako nije postaviti gresku "Ukljucite internet konekciju"
                    unesiKod.setText("");//Ako nije vratiti na pocetak inputa
                }
            }
        }, intentFilter);

    }//Kraj onCreate metode

    public boolean proveri(){
        SharedPreferences sharedPreferences= getSharedPreferences("Verifikacija", Context.MODE_PRIVATE);
        String kod=sharedPreferences.getString("verifikacioniKod","");
        String s="";
        return kod.equals(s);
    }

    @Override
    public void processFinish(String result) { //Metoda u cijem argumentu se upisuje povratna poruka iz Baze
        if (result.equals("Code already used! 401 UNAUTHORISED[]")){//Isputuje da li kod vec postoji
            Toast.makeText(Screen2.this, "Kod je vec koriscen! 401 UNAUTHORISED", Toast.LENGTH_LONG).show();//Izbacuje poruku da vec postoji upisan telefon sa tim kodom, Kod je vec koriscen! 401 UNAUTHORISED
        }
        if (result.equals("200 OK[]")){//Ispituje da li unosi kod u bazu
            Toast.makeText(Screen2.this, "Vasi podaci su uspesno dodati. Dobrodosli!", Toast.LENGTH_LONG).show();//Izbacuje poruku Vasi podaci su uspesno dodati. Dobrodosli!
            EditText minimalniUnos = (EditText) findViewById(R.id.unesi_kod_polje); //Objekat minimalniUnos
            String minUnos = minimalniUnos.getText().toString();//Objekat minUnos u koji upisujemo unesenu vrednost
            SharedPreferences sharedPreferences = getSharedPreferences("Verifikacija", Context.MODE_PRIVATE);   /**stvaranje objekta za deljene preference*/
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //Hesiranje
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(minUnos.getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                String hashtext = bigInt.toString(16);
                // Now we need to zero pad it if you actually want the full 32 chars.
                while(hashtext.length() < 32 ){
                    hashtext = "0"+ hashtext;
                }
                editor.putString("verifikacioniKod", hashtext);
                editor.apply();

                Intent intent = new Intent(Screen2.this, Screen3.class);
                startActivity(intent);
                finish();

                Toast.makeText(Screen2.this, "Vas verifikacioni kod je upamcen!", Toast.LENGTH_SHORT).show();
            } catch (java.security.NoSuchAlgorithmException e) {
                e.getMessage();
            }
            //kraj hesiranja
            Intent intent = new Intent(Screen2.this, Screen3.class);//Ako su podaci uspesno dodati Prebacuje na kameru
            startActivity(intent);
        }
        if (result.equals("BAD PARAMETERS! 401 UNAUTHORISED[]")){//Ispituje da li su svi podaci tu
            Toast.makeText(Screen2.this, "Vasi podaci nisu potpuni", Toast.LENGTH_SHORT).show();//Izbacuje poruku da Vasi podaci nisu potpuni i da nesto aplikacija nije ocitala
        }

    }//kraj processFinish metode

    @Override
    public void onClick(View v) {
        EditText minimalniUnos = (EditText) findViewById(R.id.unesi_kod_polje); //Objekat minimalniUnos
        String minUnos = minimalniUnos.getText().toString();//Objekat minUnos u koji upisujemo unesenu vrednost

        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID); //Uzimanje android ID-a


            if (minimalniUnos.getText().toString().length()<3){ //Uslov da kod mora da sadrzi minimum 3 cifre
                minimalniUnos.setError("Kod mora da sadrzi najmanje 3 cifre!");
                minimalniUnos.requestFocus();
                minimalniUnos.setText("");
             } else { //Provera da li uneti broj odgovara broju po modulu 97
                    int x=minUnos.length();
                    String srt = minUnos.substring(x-2,x);//Upisivanje u promenljivu srt poslednje dve cifre(Kontolne cifre)
                    String p1=minUnos.substring(0,x-2);//Upisivanje u promenljivu pl sve cifre osim poslednje dve

                    BigInteger bd = new BigInteger(p1);//Dodeljivanje ciframa tip BigInteger

                    bd = bd.multiply(new BigInteger("100"));//Mnozenje Cifri sa 100
                    BigInteger x321 = bd.mod(new BigInteger("97"));//Deljenje Cifri sa 97
                    BigInteger mod97 = new BigInteger("97");
                    mod97 = mod97.subtract(x321);
                    mod97 = mod97.add(new BigInteger("1"));

                if (mod97.toString().equals(srt)) {//Provera da li poslednje dve cifre odgovaraju jedna drugoj, tj da li su iste
                    HashMap postData = new HashMap();//HashMapa, upisivanje vrednosti
                    postData.put("mobile", "android");//Govoris APIJU da se radi o telefonu i da je android
                    postData.put("kod", minimalniUnos.getText().toString());//Prosledjujes  Apiju vrednost koju korisnik unese
                    postData.put("androidID", androidID);//Prosledjujes Apiju androidID-a

                    PostResponseAsyncTask task = new PostResponseAsyncTask(Screen2.this,postData, Screen2.this);//Putanja za lokalni server
                    task.execute("http://10.0.2.2/CodeWorriors/api.php?apicall=registration");//Putanja za lokalni server
                    minimalniUnos.setText("");
                } else {
                    minimalniUnos.setError("Vas kod ne odgovara kodu po modulu 97");
                    minimalniUnos.setText("");
                }
        }

    }//Kraj onClick metode.

}



































