package net.vinnen.sensorjackeba.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;

import net.vinnen.sensorjackeba.MainActivity;
import net.vinnen.sensorjackeba.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Created by Julius on 13.06.2018.
 * Passages of the Code are teaken from Android Guide for Bluetooth https://developer.android.com/guide/topics/connectivity/bluetooth
 */

/**
 * This class runs the Bluetooth connection to the ESP and handles the received data.
 */
public class ConnectThread extends Thread {
    public static boolean live = true;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final String TAG = "ConnectThread";

    long startTime = 0;
    long lastTime = 0;

    File dataDir = new File(Environment.getExternalStorageDirectory(), "trackerJacketData");
    BufferedWriter bWrite;

    private MainActivity main;
    char x = 'y';

    public ConnectThread(BluetoothDevice device, MainActivity main) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.main = main;

        try {
            UUID tmpo = device.getUuids()[0].getUuid();
            tmp = device.createRfcommSocketToServiceRecord(tmpo);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        try {
            //This part is from the Android Guide
            //from here
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG,"Could not establish connection",connectException);
            Snackbar.make(main.findViewById(R.id.texture_view), "Connection failed", Snackbar.LENGTH_LONG).show();
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }
        Log.d(TAG, "Connection sucessfull");
        //to here

        // The connection attempt succeeded.

        OutputStream outSt = null;
        InputStream inSt = null;
        try {
            outSt = mmSocket.getOutputStream();
            inSt = mmSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStreamReader inRead = new InputStreamReader(inSt);
        BufferedReader bRead = new BufferedReader(inRead);

        OutputStreamWriter outWrite = new OutputStreamWriter(outSt);
        bWrite = new BufferedWriter(outWrite);

        Log.d(TAG,"running");

        sendString("t" + System.currentTimeMillis() + "s");

        String line = "";
        int nextSens=0;

        try {
            while (!(line = bRead.readLine()).startsWith("S")) {
                if(line.startsWith("W")){ //Write
                    Log.d(TAG, "Writing File");
                    dataDir.mkdirs();
                    dataDir.mkdir();
                    File f = new File(dataDir, "tmp");
                    BufferedWriter bw = null;
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        OutputStreamWriter osw = new OutputStreamWriter(fo);
                        bw = new BufferedWriter(osw);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (!(line = bRead.readLine()).startsWith("w")) {
                        //Log.d(TAG, line);
                        if(line.startsWith("m")){
                            String[] parts = line.split(",");
                            long timestamp = Long.parseLong(parts[1]);
                            long timeInFile = Long.parseLong(parts[2]);
                            startTime = timestamp - timeInFile;
                        }
                        if(!(line.startsWith("m") || line.startsWith("C") || line == null || line == "")){
                            lastTime = Long.parseLong(line.split(",")[1]);
                        }
                        bw.write(line);
                        bw.write("\n");
                    }
                    f.renameTo(new File(dataDir, "jacketData_" + startTime + "_" + lastTime));
                    Log.d(TAG, "File transmitted");
                    bw.flush();
                    bw.close();
                }else if(line.startsWith("B")){
                    Log.d(TAG, "Button pressed");
                }else if(line.startsWith("b")){
                    Log.d(TAG, "Button released");
                }else{
                    if(live) {
                        final String[] parts = line.split(",");
                        int multi = Integer.parseInt(parts[0]);
                        //Log.d(TAG, line);
                        for (int i = 2; i < 5; i++) {
                            int glob = multi * 4 + (i - 1);
                            main.valuesToDisplay[glob] = parts[i];
                        }
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.updateDisplay();
                            }
                        });
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            outSt.close();
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"End of transmission");


    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public void sendString(String data){
        Log.d(TAG, "Sending to Jacket: " + data);
        try {
            bWrite.write(data);
            bWrite.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String fileNumber){
        sendString("r/output_" + fileNumber + ".txts");
    }


}
