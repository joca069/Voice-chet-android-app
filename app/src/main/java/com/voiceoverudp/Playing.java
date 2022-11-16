package com.voiceoverudp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import javax.crypto.Cipher;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Playing extends Thread{
    private DatagramSocket socket;
    private Log log;

    private AudioTrack audioTrack;

    public int volume=30;

    public void build(DatagramSocket socket, Log log){
        this.socket=socket;
        this.log=log;
    }

    public Playing(DatagramSocket socket,Log log){
        build(socket,log);
    }

    public Playing(DatagramSocket socket){

        Log l=new Log() {
            @Override
            public void log(String s) {

            }

            @Override
            public void logError(Exception e) {

            }
        };

        build(socket,l);
    }

    private byte[] decrypt(byte[] positive){
        /*
        if(settings.AESkey==null) {
            android.util.Log.i("recived","recived unencrypted");
            return positive;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,settings.AESkey);
            return cipher.doFinal(positive);
        }catch (Exception e){
            android.util.Log.i("err","cant decrypt");
            return positive;
        }

         */
        return positive;

    }
    public static byte[] removeZeros(byte[] original){
        int size=0;
        for(int i=original.length-1;i>=0;i--){
            if(original[i]!=0){
                size=i;
                break;
            }
        }
        byte[] ret=new byte[size];
        for(int i=0;i<ret.length;i++){
            ret[i]=original[i];
        }
        return ret;
    }

    private byte[] recive() throws Exception{

        int buf=(settings.BUFFER_SIZE/16 + 1) * 16;
        if(settings.AESkey==null){
            buf=settings.BUFFER_SIZE;
        }
        buf=settings.BUFFER_SIZE;

        DatagramPacket packet=new DatagramPacket(new byte[buf],buf);
        socket.receive(packet);
        byte[] decrypted=decrypt(packet.getData());

        return decrypted;
    }

    @Override
    public void run(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, settings.SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, settings.AUDIO_FORMAT,
                settings.BUFFER_SIZE, AudioTrack.MODE_STREAM);
        audioTrack.play();
        while (true) {
            try {
                byte[] b=recive();
                audioTrack.write(b,0,settings.BUFFER_SIZE);

            }catch (Exception ee){
            log.logError(ee);
            }
        }

    }

    public void setVolume(int volume){
        audioTrack.setVolume(volume);
    }



}
