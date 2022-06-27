package lk.bitapp.garbfree;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressDialog=new ProgressDialog(this);

//this handler is used to play splash screen for few seconds;
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {

                //Check for Internet Connection first
                if (Network.getInstance(getApplicationContext()).isOnline()) {

                    //Internet is available, Toast It!
                    Toast.makeText(getApplicationContext(), "WiFi/Mobile Networks Connected!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(SplashScreen.this, Register.class));
                    finish();

                } else {
                    //Internet is NOT available, Toast It!
                    progressDialog.setMessage("Ooops!Not connected to internet! Please connect to internet and open app again!");
                    progressDialog.show();
                    //Toast.makeText(getApplicationContext(), "Ooops! No WiFi/Mobile Networks Connected!", Toast.LENGTH_SHORT).show();
                    return;

                }
            }

        }, secondsDelayed * 2000);

    }
}



