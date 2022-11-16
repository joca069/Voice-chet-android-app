package com.voiceoverudp;

import android.media.AudioFormat;
import android.media.AudioRecord;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

public class settings {

    public static InetAddress serverIP;
    public static int serverPort=3069;

    public static int chunksize=1000000;


    //44100
    public static int SAMPLE_RATE_IN_HZ=8000;

    public static SecretKey AESkey;


    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;


    public static int BUFFER_SIZE= AudioRecord.getMinBufferSize(settings.SAMPLE_RATE_IN_HZ,settings.CHANNEL_CONFIG,settings.AUDIO_FORMAT) * 2;

    public static void resetBufferSize(){
        BUFFER_SIZE=AudioRecord.getMinBufferSize(settings.SAMPLE_RATE_IN_HZ,settings.CHANNEL_CONFIG,settings.AUDIO_FORMAT) * 2;
    }
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public static void saveConfig() throws Exception{
    }


    public static byte[] ID=ByteBuffer.allocate(4).putInt(new Random().nextInt()).array();

    public static String hash(byte[] pozitive) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(pozitive);
            return Base64.getEncoder().encodeToString(messageDigest.digest());
        }catch (Exception e){
            return "error";
        }
    }
    public static void loadDefaults(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverIP = InetAddress.getByName("192.168.1.3");
                    serverPort=3069;
                    SAMPLE_RATE_IN_HZ=8000;
                }catch (Exception e){}


            }
        }).start();

    }

}
