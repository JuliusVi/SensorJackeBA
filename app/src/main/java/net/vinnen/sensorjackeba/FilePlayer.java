package net.vinnen.sensorjackeba;

import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

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
        endMillis = Long.parseLong(filename.split("_")[2]);
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
            ((ImageButton)main.findViewById(R.id.play)).setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }else {
            currentMillis = System.currentTimeMillis();
            playing = true;
            ((ImageButton)main.findViewById(R.id.play)).setImageResource(R.drawable.ic_pause_black_24dp);
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
                setProgressBar(passedMillis);
                passedMillis = passedMillis + (currentMillis-lastMillis);
                String line = null;
                try {
                    line = bRead.readLine();
                    //Log.d(TAG, "Line from File: " + line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(line == null || line == ""){
                    Log.d(TAG, "Line was empty");
                    playing = false;
                } else if(line.startsWith("m")){
                    Log.d(TAG, "Timestamp read");
                } else if(line.startsWith("C") || line.startsWith("c")){
                    for (int i = 0; i < main.valuesOffset.length; i++) {
                        if(i%4 != 0){
                            main.valuesOffset[i] = main.values[i];
                        }
                    }
                }else{
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

    public void setProgressBar(Long current){
        double prog = ((double)current/(double)endMillis)*1000;
        Log.d(TAG, "Progress: " + prog + " End: " + endMillis + " Current: " + current);
        ((SeekBar)main.findViewById(R.id.seekBar)).setProgress((int)prog);
    }

    public boolean isPlaying() {
        return playing;
    }

}
