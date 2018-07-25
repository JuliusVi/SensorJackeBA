package net.vinnen.sensorjackeba;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Julius on 25.07.2018.
 */

public class FilePlayer implements Runnable{
    private final static String TAG = "FilePlayer";
    private MainActivity main;
    private long lastMillis;
    private long currentMillis;
    private long passedMillis;
    private long endMillis;
    private boolean playing=false;
    private boolean active = true;
    private File file;
    BufferedReader bRead = null;

    File dataDir = new File(Environment.getExternalStorageDirectory(), "trackerJacketData");
    public FilePlayer(MainActivity main, String filename){
        this.main = main;
        file = new File(dataDir, filename);
        try {
            FileInputStream inSt = new FileInputStream(file);
            InputStreamReader inRead = new InputStreamReader(inSt);
            bRead = new BufferedReader(inRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void togglePlay(){
        if (playing) {
            playing = false;
        }else {
            currentMillis = System.currentTimeMillis();
            playing = true;
        }
    }
    public void jumpToPercentage(int percent){

    }
    public void nextFrame(){

    }
    public void previousFrame(){

    }

    @Override
    public void run() {
        while (active) {
            if (playing) {
                lastMillis = currentMillis;
                currentMillis = System.currentTimeMillis();
                passedMillis = passedMillis + (currentMillis-lastMillis);
                String line = null;
                try {
                    line = bRead.readLine();
                    Log.d(TAG, "Line from File: " + line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String[] parts = line.split(",");
                int multi = Integer.parseInt(parts[0]);
                long fileNextMillis = Long.parseLong(parts[1]);
                while(fileNextMillis > passedMillis){
                    lastMillis = currentMillis;
                    currentMillis = System.currentTimeMillis();
                    passedMillis = passedMillis + (currentMillis-lastMillis);
                }
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
}
