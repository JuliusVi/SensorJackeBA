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
import java.util.ArrayList;

/**
 * Created by Julius on 25.07.2018.
 */

public class FilePlayer implements Runnable{
    private final static String TAG = "FilePlayer";
    private MainActivity main;

    private int currentIndex = 0;
    private int maxIndex = 0;

    private long lastMillis;
    private long currentMillis;
    private long passedMillis;
    private long endMillis;
    private boolean playing=false;
    private boolean active = true;
    private File file;
    private double progressPercentage = 0;
    BufferedReader bRead = null;
    private ArrayList<String> fileContent;

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
        fileContent = new ArrayList<String>();
        String line = "";
        do {
            try {
                line = bRead.readLine();
                if(line != null && line != "") {
                    fileContent.add(line);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG, "Line to Memory: " + line);
        }while(line != null && line != "");
        maxIndex = fileContent.size()-1;
        Log.d(TAG, "File moved to Memory");
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
        if((int)progressPercentage != percent) {
            currentIndex = (maxIndex / 1000) * percent;
            //passedMillis = Long.parseLong(fileContent.get(currentIndex).split(";")[1]);
        }
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
                String line = fileContent.get(currentIndex);
                    Log.d(TAG, "Line from Filerun: " + line);

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
                currentIndex++;
            }
        }
    }

    public void setProgressBar(Long current){
        progressPercentage = ((double)current/(double)endMillis)*1000;
        //Log.d(TAG, "Progress: " + progressPercentage + " End: " + endMillis + " Current: " + current);
        ((SeekBar)main.findViewById(R.id.seekBar)).setProgress((int)progressPercentage);
    }

    public boolean isPlaying() {
        return playing;
    }

}
