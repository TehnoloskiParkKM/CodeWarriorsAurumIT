package com.snapit.milosvuckovic.splashscreenv2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;
import static java.lang.System.out;
public class Screen4 extends AppCompatActivity {
    private Context context= this;
    private LruCache<String, Bitmap> mMemoryCache ;
    private ImageView imageView;
    PhotoViewAttacher pAttacher;

    public final String URL="http://79.175.125.13/cw/api.php?apicall=imgupload";
    public String KomprePutanja;
    Button upload;
    String putanjaSlikeUKesu;
    String imeSlike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen4);
        upload=(Button)findViewById(R.id.posalji_btn);
        String userName;
        imageView = (ImageView) findViewById(R.id.slika123);
        Intent i = getIntent();
        //Prenete putanje slike
        KomprePutanja = i.getStringExtra("kompresovana");
        Log.d("Kompresovana slika","preneta putanja "+ KomprePutanja);
        userName = i.getStringExtra("slika");
        // and get whatever type user account id is
        Uri uri = Uri.parse(KomprePutanja);
        imageView.setImageURI(uri);
        pAttacher = new PhotoViewAttacher(imageView);
        pAttacher.update();
        File traziIme = new File(KomprePutanja);
        final String imeSlike= traziIme.getName();


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID); //Android ID

                    SharedPreferences sharedPreferences = getSharedPreferences("Verifikacija", Context.MODE_PRIVATE);
                    String verifikacioniKod= sharedPreferences.getString("verifikacioniKod",""); //Citanje verifikacionog koda iz shared preferences

                    //Uzimanje naziva slike iz screen3
                   // Intent intent = getIntent();
                   // String nazivSlike = intent.getExtras().getString("nazivS");

                    Bitmap bitMap= ImageLoader.init().from(KomprePutanja).requestSize(512,512).getBitmap();//Uzimanje slike iz kesa i upis u bitMap
                    String slikaZaSlanje= ImageBase64.encode(bitMap); //kodovanje slike u String
                    // Log.d.(TAG, encodedImage);

                    HashMap<String, String> postData=new HashMap<String, String>();
                    postData.put("image", slikaZaSlanje);
                    postData.put("image_name", imeSlike);
                    postData.put("androidID",androidID);
                    postData.put("kod", verifikacioniKod);

                    PostResponseAsyncTask task=new PostResponseAsyncTask(Screen4.this, postData, new AsyncResponse(){
                        @Override
                        public void processFinish(String s) {
                            SharedPreferences sharedPreferences = getSharedPreferences("Verifikacija", Context.MODE_PRIVATE);
                            String verifikacioniKod= sharedPreferences.getString("verifikacioniKod","");
                            if(s.contains("UPLOAD SUCCESSFULL!")){
                                Toast.makeText(getApplicationContext(),
                                        "Slika je uspesno poslata na server!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Screen4.this, Screen3.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Problem sa slanjem slike!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    task.execute(URL);

                }catch(FileNotFoundException e){
                    Toast.makeText(getApplicationContext(),
                            "Nesto nije u redu sa kodiranjem slike!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean clearCache() {
        try {
            String imeDir = "slike";
            File Cachedir = new File(getCacheDir().getPath()+"/"+imeDir+"/");
            File[] files = Cachedir.listFiles();
            File[] filess = getBaseContext().getCacheDir().listFiles();
            for (File file : files ) {
                // delete returns boolean we can use
                if (!file.delete()) {
                    return false;
                }
            }
            for (File file : filess ) {
                // delete returns boolean we can use
                if (!file.delete()) {
                    return false;
                }
            }
            // if for completes all
            return true;
        } catch (Exception e) {}
        // try stops clearing cache
        return false;
    }
    public void onClickClose(View view){
        clearCache();
        finish();
    }
    //Izlaz iz aplikacije preko android back dugmeta
    @Override
    public void onBackPressed() {
        clearCache();
        finish();
    }
}