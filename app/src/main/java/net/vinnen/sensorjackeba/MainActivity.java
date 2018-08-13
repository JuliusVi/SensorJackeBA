package net.vinnen.sensorjackeba;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.vinnen.sensorjackeba.activities.SelectFile;
import net.vinnen.sensorjackeba.activities.SettingsActivity;
import net.vinnen.sensorjackeba.model.ArmSegment;
import net.vinnen.sensorjackeba.thread.ConnectThread;
import net.vinnen.sensorjackeba.thread.SurfaceTextureListener2D;
import net.vinnen.sensorjackeba.thread.SurfaceTextureListener3D;

import java.util.Set;

/**
 * @Author Julius Vinnen
 *
 * This class is the main Activity for the app. All sorts of data that has to be displayed or otherwise used here
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "MainActivity";
    private FilePlayer player;
    private SurfaceTextureListener3D surfaceTextureListener3D;
    private SurfaceTextureListener2D surfaceTextureListener2D;
    private float gsx, gsy, gsz = 0;

    //generate the ArmSegment model classes
    public ArmSegment uLA = new ArmSegment(0.15f,1.6f,0,0.065f,0.28f,0.065f,0,0,0);
    public ArmSegment uRA = new ArmSegment(-0.15f,1.6f,0,0.065f,0.28f,0.065f ,0, 0, 0);

    public ArmSegment lLA = new ArmSegment(0,0,0,0.065f,0.26f,0.065f,0,0,0);
    public ArmSegment lRA = new ArmSegment(0,0,0,0.065f,0.26f,0.065f,0,0,0);

    private TextureView mTextureView;
    private TextureView textureView2D;

    ConnectThread connectThread;
    String targetName = "TrackerJacket";

    ConstraintLayout[] cl = new ConstraintLayout[5];
    TextView tv[] = new TextView[20];

    public String[] valuesToDisplay = new String[20];
    public double[] values = new double[20];
    public double[] valuesOffset = new double[20];
    public String ctrlString = "r";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fileToPlay = getIntent().getStringExtra("PlayFile");
        Log.d(TAG, "Playing File: " + fileToPlay);
        if(fileToPlay != null && fileToPlay != ""){
            playFile(fileToPlay);
        }

        //Stuff for Looks
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTextureView = findViewById(R.id.texture_view);
        textureView2D = findViewById(R.id.texture_view2D);

        ((SeekBar)findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                player.jumpToPercentage(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button actionBtn = findViewById(R.id.actionBtn);
        actionBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startGesture();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    endGesture();
                }
                return true;
            }
        });

        surfaceTextureListener3D = new SurfaceTextureListener3D(this);
        mTextureView.setSurfaceTextureListener(surfaceTextureListener3D);

        surfaceTextureListener2D = new SurfaceTextureListener2D(this, textureView2D);
        textureView2D.setSurfaceTextureListener(surfaceTextureListener2D);

        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            private float firstDown = 0f;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    firstDown = event.getAxisValue(0);
                    return true;
                }
                surfaceTextureListener3D.getRendererThread().rota = (event.getAxisValue(0)-firstDown)/2;
                //Log.d(TAG,"Scroll: " + (event.getAxisValue(0)-firstDown));
                return true;
            }
        });

        final Button reqDownload = (Button)findViewById(R.id.reqDownloadBtn);
        reqDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadDialog d = new DownloadDialog(MainActivity.this,connectThread);
            }
        });

        cl[0] = (ConstraintLayout) findViewById(R.id.body);
        cl[1] = (ConstraintLayout) findViewById(R.id.upperLeftArm);
        cl[2] = (ConstraintLayout) findViewById(R.id.lowerLeftArm);
        cl[3] = (ConstraintLayout) findViewById(R.id.upperRightArm);
        cl[4] = (ConstraintLayout) findViewById(R.id.lowerRightArm);

        for (int i = 0; i < 5; i++) {
            tv[i*4] = cl[i].findViewById(R.id.Label);
            tv[i*4+1] = cl[i].findViewById(R.id.yaw);
            tv[i*4+2] = cl[i].findViewById(R.id.pitch);
            tv[i*4+3] = cl[i].findViewById(R.id.roll);
        }

        valuesToDisplay[16] = "Body:";
        valuesToDisplay[12] = "UpLeft:";
        valuesToDisplay[8] = "LowLeft";
        valuesToDisplay[4] = "UpRight:";
        valuesToDisplay[0] = "LowRight:";

        //Get Bluetooth connection here. Code from: https://developer.android.com/guide/topics/connectivity/bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(),"Bluetooth not Supported on this Device", Toast.LENGTH_LONG).show();
            return;
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice targetDevice = null;

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        targetName = p.getString("device_name", "TrackerJacket");

        Log.d(TAG, "Host Device Name: " + targetName);

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(targetName)){
                    targetDevice = device;
                }
            }
        }
        if(targetDevice==null){
            Log.e(TAG, "No target found");
            return;
        }
        connectThread = new ConnectThread(targetDevice, this);
        connectThread.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent openSettingsActivity = new Intent(this, SettingsActivity.class);
            openSettingsActivity.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            openSettingsActivity.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            startActivity(openSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent openMainActivity = new Intent(this, MainActivity.class);
            startActivity(openMainActivity);
        } else if (id == R.id.nav_gallery) {
            Intent openFilesActivity = new Intent(this, SelectFile.class);
            startActivity(openFilesActivity);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * This Method is called by the connect thread, to update the Values to be displayed
     */
    public void updateDisplay(){
        for (int i = 0; i < valuesToDisplay.length; i++) {
            if(i%4 != 0){
                try {
                    values[i] = Double.parseDouble(valuesToDisplay[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //valuesToDisplay[i] = String.valueOf(values[i]).split(",")[0];
            }
            if(i%4 == 0){
                tv[i].setText(valuesToDisplay[i]);
            }else {
                tv[i].setText(String.valueOf(values[i]).split("\\.")[0]);//valuesToDisplay[i]);
            }
        }
        double bodyRotation = values[17] - valuesOffset[17];

        lRA.rotX = (float)values[2];
        lRA.rotY = (float) (360 - (values[1] - valuesOffset[1] - bodyRotation));
        lRA.rotZ = (float) (180 - (values[3]+90));
        //Log.d(TAG, "Val: " + (values[1] - valuesOffset[1]));

        uRA.rotX = (float)values[6];
        uRA.rotY = (float) (360 - (values[5] - valuesOffset[5] - bodyRotation));
        uRA.rotZ = (float) (180 - (values[7]+90));

        lLA.rotX = (float)values[10];
        lLA.rotY = (float) (180 - (values[9] - valuesOffset[9] - bodyRotation));
        lLA.rotZ = (float) (180 - (values[11]+90));

        uLA.rotX = (float)values[14];
        uLA.rotY = (float) (180 - (values[13] - valuesOffset[13] - bodyRotation));
        uLA.rotZ = (float) (180 - (values[15]+90));
    }

    public void calibrateJacket(View v){
        for (int i = 0; i < valuesOffset.length; i++) {
            if(i%4 != 0){
                valuesOffset[i] = values[i];
            }
        }
        connectThread.sendString("C");
        Snackbar.make(findViewById(R.id.texture_view), "Calibration done", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        //Show/hide Raw Values
        super.onResume();
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if(p.getBoolean("show_debug", true)){
            Log.d(TAG, "Debug visible");
            findViewById(R.id.upperLeftArm).setVisibility(View.VISIBLE);
            findViewById(R.id.lowerLeftArm).setVisibility(View.VISIBLE);
            findViewById(R.id.upperRightArm).setVisibility(View.VISIBLE);
            findViewById(R.id.lowerRightArm).setVisibility(View.VISIBLE);
            findViewById(R.id.body).setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG, "Debug invisible");
            findViewById(R.id.upperLeftArm).setVisibility(View.INVISIBLE);
            findViewById(R.id.lowerLeftArm).setVisibility(View.INVISIBLE);
            findViewById(R.id.upperRightArm).setVisibility(View.INVISIBLE);
            findViewById(R.id.lowerRightArm).setVisibility(View.INVISIBLE);
            findViewById(R.id.body).setVisibility(View.INVISIBLE);
        }
    }

    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater mInf = popupMenu.getMenuInflater();
        mInf.inflate(R.menu.window_content, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.threeDRepresentation) {
                    mTextureView.setVisibility(View.VISIBLE);
                    textureView2D.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "3D");
                } else if(id == R.id.XYRepresentation){
                    mTextureView.setVisibility(View.INVISIBLE);
                    textureView2D.setVisibility(View.VISIBLE);
                    surfaceTextureListener2D.setMode(0);
                    Log.d(TAG, "XYo");
                } else if(id == R.id.XZRepresentation){
                    mTextureView.setVisibility(View.INVISIBLE);
                    textureView2D.setVisibility(View.VISIBLE);
                    surfaceTextureListener2D.setMode(1);
                    Log.d(TAG, "XZo");
                } else if(id == R.id.YZRepresentation){
                    mTextureView.setVisibility(View.INVISIBLE);
                    textureView2D.setVisibility(View.VISIBLE);
                    surfaceTextureListener2D.setMode(2);
                    Log.d(TAG, "YZo");
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void playFile(String filename){
        Snackbar.make(findViewById(R.id.texture_view), "Opend File: " + filename, Snackbar.LENGTH_LONG).show();
        ConnectThread.live = false;
        player = new FilePlayer(this, filename);
        Thread play = new Thread(player);
        play.start();
    }

    public void onPlayPressed(View view){
        player.togglePlay();
    }

    public void onNextPressed(View view){
        player.nextFrame();
    }

    public void onPreviousPressed(View view){
        player.previousFrame();
    }

    public void onLivePressed(View view){
        ConnectThread.live = true;
    }

    public void onSettingsPressed(View view){
        Intent openSettingsActivity = new Intent(this, SettingsActivity.class);
        openSettingsActivity.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        openSettingsActivity.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
        startActivity(openSettingsActivity);
    }

    public void startGesture(){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if(!p.getBoolean("left_handed", false)) {
            gsx = lRA.endX;
            gsy = lRA.endY;
            gsz = lRA.endZ;
        }
        else{
            gsx = lLA.endX;
            gsy = lLA.endY;
            gsz = lLA.endZ;
        }
        Log.d(TAG, "Gesture started");
    }
    public void endGesture(){
        float deltaX = 0;
        float deltaY = 0;
        float deltaZ = 0;
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if(!p.getBoolean("left_handed", false)) {
            deltaX = gsx - lRA.endX;
            deltaY = gsy - lRA.endY;
            deltaZ = gsz - lRA.endZ;
        }
        else{
            deltaX = gsx - lLA.endX;
            deltaY = gsy - lLA.endY;
            deltaZ = gsz - lLA.endZ;
        }
        String gestureResult = "No Gesture detected";
        if(deltaX > 0.2){
            gestureResult = "Swipe Right detected";
        }else if(deltaX < -0.2){
            gestureResult = "Swipe Left detected";
        }else if(deltaY > 0.2){
            gestureResult = "Swipe Down detected";
        }else if(deltaY < -0.2){
            gestureResult = "Swipe Up detected";
        }else if(deltaZ > 0.2){
            gestureResult = "Swipe Backward detected";
        }else if(deltaZ < -0.2){
            gestureResult = "Swipe Forward detected";
        }
        Snackbar.make(findViewById(R.id.texture_view), gestureResult, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Gesture end");
        Log.d(TAG, "deltaX: " + (gsx - lRA.endX) + "deltaY: " + (gsy - lRA.endY) + "deltaZ: " + (gsz - lRA.endZ));
    }
}
