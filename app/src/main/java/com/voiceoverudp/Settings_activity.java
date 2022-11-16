package com.voiceoverudp;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Base64;

public class Settings_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView ip=findViewById(R.id.ipedit);
        TextView port=findViewById(R.id.portedit);
        TextView freq=findViewById(R.id.frequencyedit);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                ip.setText(settings.serverIP.getHostAddress());
                port.setText(Integer.toString(settings.serverPort));
                freq.setText(Integer.toString(settings.SAMPLE_RATE_IN_HZ));
                log("configuration loaded");
                }catch (Exception ext){
                    logErr(ext);
                }

            }
        }).start();

    }

    private void log(String s){
        TextView tv=findViewById(R.id.consoleSettings);
        tv.setText(tv.getText()+s+"\n");
    }
    private void logErr(Exception e){
        log(e.toString());
    }

    public void Save(View v){
        Runnable r=new Runnable() {
            @Override
            public void run() {
                try{
                    TextView ip=findViewById(R.id.ipedit);
                    TextView port=findViewById(R.id.portedit);
                    TextView freq=findViewById(R.id.frequencyedit);
                    TextView key=findViewById(R.id.aeskeyedit);
                    settings.serverIP= InetAddress.getByName(ip.getText().toString());
                    settings.serverPort=Integer.parseInt(port.getText().toString());
                    settings.SAMPLE_RATE_IN_HZ=Integer.parseInt(freq.getText().toString());
                    settings.resetBufferSize();
                    if(!(key.getText().toString().equals("") || key.getText().toString().equals(" ") || key.getText()==null)){
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        messageDigest.update(key.getText().toString().getBytes());
                        settings.AESkey=new SecretKeySpec(messageDigest.digest(), "AES");
                        Log.e("radi","key generated ");
                    }else{
                        Log.e("err","key doesnt exist therefore no encryption");
                        settings.AESkey=null;
                    }

                    String config=ip.getText()+"\n"+Integer.toString(settings.serverPort)+"\n"+Integer.toString(settings.SAMPLE_RATE_IN_HZ)+"\n";
                    if(settings.AESkey==null){
                        config+="\n";
                    }else{
                        config+= Base64.getEncoder().encodeToString(settings.AESkey.getEncoded())+"\n";
                    }
                    FileOutputStream fos=openFileOutput("config",MODE_PRIVATE);
                    fos.write(config.getBytes());
                    fos.flush();
                    fos.close();


                log("configuration saved");
                }catch (Exception ee){
                logErr(ee);
        }
            }
        };

        new Thread(r).start();

    }

}