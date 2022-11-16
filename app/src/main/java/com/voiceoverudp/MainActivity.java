package com.voiceoverudp;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    DatagramSocket socket;

    private void log(String s){
        TextView tv=findViewById(R.id.consoleMain);
        tv.setText(tv.getText()+"\n"+s);
    }
    private void logErr(Exception e){
        log(e.toString());
    }

    private void requestPremissions(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET,  Manifest.permission.RECORD_AUDIO},69);
    }



    private void loadConfiguration(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fis = openFileInput("config");
                    Scanner scanner = new Scanner(fis);
                    settings.serverIP=InetAddress.getByName(scanner.nextLine());
                    settings.serverPort=Integer.parseInt(scanner.nextLine());
                    settings.SAMPLE_RATE_IN_HZ=Integer.parseInt(scanner.nextLine());
                    String keybase= scanner.nextLine();
                    if(keybase.equals("")){

                    }else {
                        byte[] keybyte = Base64.getDecoder().decode(keybase);
                        //now implement byte[] to AES key
                    }

                    log("configurations loaded");
                    fis.close();
                } catch (Exception e) {
                    log("error loading configuration therefore loading defaults"+e.toString());
                    settings.loadDefaults();
                }

            }
        }).start();

    }

    Recording recording;
    Playing playing;
    private void build(){
        Runnable run=new Runnable() {
            @Override
            public void run() {
                try{
                    loadConfiguration();
                    socket=new DatagramSocket();
                    Log l=new Log() {
                        @Override
                        public void log(String s) {
                            log(s);
                        }
                        @Override
                        public void logError(Exception e) {
                            logErr(e);
                        }
                    };

                    recording=new Recording(socket,l);
                    playing=new Playing(socket,l);
                    recording.start();
                    playing.start();


                }catch (Exception v){
                    logErr(v);
                }
            }
        };

        new Thread(run).start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPremissions();

        try {
            build();

        }catch (Exception ee){logErr(ee);}

    }


    public void goToSettings(View v){
        Intent switchActivityIntent = new Intent(this, Settings_activity.class);
        startActivity(switchActivityIntent);
    }



}