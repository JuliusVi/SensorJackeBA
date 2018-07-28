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
        pic = textureView.lockCanvas();
        if(pic == null){
            Log.d(TAG, "Pic is null");
        }
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        Paint white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);
        pic.drawRect(new Rect(pic.getWidth() / 2 - 3, pic.getHeight(), pic.getWidth() / 2 + 3, 0), white);
        pic.drawRect(new Rect(0, pic.getHeight() / 2 + 3, pic.getWidth(), pic.getHeight() / 2 - 3), white);
        pic.drawCircle(pic.getWidth() / 2, pic.getHeight() / 2, 10, red);
        textureView.unlockCanvasAndPost(pic);
    }
}
