package com.voiceoverudp;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Recording extends Thread{
    private DatagramSocket socket;
    private Log log;
    private AudioRecord recorder;

    public Recording(DatagramSocket socket,Log log){
        build(socket,log);
    }

    public void build(DatagramSocket socket, Log log){
        this.socket=socket;
        this.log=log;

    }

    public Recording(DatagramSocket socket){

        Log log=new Log() {
            @Override
            public void log(String s) {

            }

            @Override
            public void logError(Exception e) {

            }
        };
        build(socket,log);

    }

    private byte[] encrypt(byte[] positive){

        /*
        if(settings.AESkey==null) {
            android.util.Log.i("sended","sended unencrypted");
            return positive;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,settings.AESkey);
            return cipher.doFinal(positive);
        }catch (Exception e){
            android.util.Log.i("err","cant encrypt");
            return positive;
        }

         */

        return positive;
    }

    //reason for this function is in case I want to implement encryption
    private void send(byte[] data) throws Exception{
        byte[] encrypted=encrypt(data);
        //android.util.Log.d("hash","sended bytes "+encrypted.length);
        //android.util.Log.d("hash","sended this "+settings.hash(encrypted));

        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bos.write(settings.ID);
        bos.write(encrypted);
        byte[] withID=bos.toByteArray();
        DatagramPacket packet=new DatagramPacket(withID,withID.length,settings.serverIP,settings.serverPort);
        socket.send(packet);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run(){
        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, settings.SAMPLE_RATE_IN_HZ,settings.CHANNEL_CONFIG, settings.AUDIO_FORMAT, settings.BUFFER_SIZE);
        byte[] b=new byte[settings.BUFFER_SIZE];
        recorder.startRecording();

        while (true) {
        try{

            recorder.read(b,0,settings.BUFFER_SIZE);
            send(b);
        }catch (Exception ev){
            log.logError(ev);
        }
        }
    }


}
