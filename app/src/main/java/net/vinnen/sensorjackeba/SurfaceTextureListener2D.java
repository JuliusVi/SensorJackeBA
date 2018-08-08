package net.vinnen.sensorjackeba;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

/**
 * Created by Julius on 28.07.2018.
 */

public class SurfaceTextureListener2D implements TextureView.SurfaceTextureListener {

    private final static String TAG = "TextureListener2D";
    private MainActivity mainActivity;
    private TextureView textureView;
    private int mode = 0;
    private int x,y,z = 0;
    Canvas pic = null;

    public SurfaceTextureListener2D(MainActivity mainActivity, TextureView textureView){
        this.mainActivity = mainActivity;
        this.textureView = textureView;
        Log.d(TAG, "Surface Texture Listener created");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "Surface Texture available");

        //pic = textureView.lockCanvas();
        //pic.drawCircle(50,50,50,new Paint(Color.GREEN));
        //textureView.unlockCanvasAndPost(pic);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        this.x = (int)(mainActivity.lRA.endX*100);
        this.y = (int)(mainActivity.lRA.endY*100);
        this.z = (int)(mainActivity.lRA.endZ*100);
        Log.d(TAG, "X: " + x + " Y: " + y + " Z: " + z);
        pic = textureView.lockCanvas();
        //pic.drawRGB(0, 0,0);
        pic.drawColor(-1);
        if(pic == null){
            Log.d(TAG, "Pic is null");
        }
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setStrokeWidth(10);
        pic.drawRect(new Rect(pic.getWidth() / 2 - 3, pic.getHeight(), pic.getWidth() / 2 + 3, 0), black);
        pic.drawRect(new Rect(0, pic.getHeight() / 2 + 3, pic.getWidth(), pic.getHeight() / 2 - 3), black);
        if(mode == 0){
            pic.drawCircle((pic.getWidth() / 2) + x, (pic.getHeight() / 2) + y, 10, red);
        } else if(mode == 1){
            pic.drawCircle((pic.getWidth() / 2) + x, (pic.getHeight() / 2) + z, 10, red);
        }else if(mode == 2){
            pic.drawCircle((pic.getWidth() / 2) + y, (pic.getHeight() / 2) + z, 10, red);
        }

        textureView.unlockCanvasAndPost(pic);
    }

    public void updateXYZ(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setMode(int mode){
        //mode = 0: XY
        //mode = 1: XZ
        //mode = 2: YZ
        this.mode = mode;
    }
}
