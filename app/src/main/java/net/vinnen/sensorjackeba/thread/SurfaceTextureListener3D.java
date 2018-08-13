package net.vinnen.sensorjackeba.thread;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.TextureView;

import net.vinnen.sensorjackeba.MainActivity;

/**
 * Created by Julius on 28.07.2018.
 */

/**
 * This Class is the Base Listener for the 3D Display, the rendering itself is done in the rendererThread class
 */
public class SurfaceTextureListener3D implements TextureView.SurfaceTextureListener {

    private final static String TAG = "Texture3D";
    private RendererThread rendererThread = null;
    private MainActivity mainActivity;

    public SurfaceTextureListener3D(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "Surface Texture available");

        rendererThread = new RendererThread(surface, mainActivity);
        rendererThread.start();

        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(rendererThread.mProjectionMatrix, 0, -ratio, ratio, -1, 1, 0.8f, 3);
        Log.d(TAG, "Surface Texture complete");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        if(rendererThread!=null) {
            rendererThread.isStopped = true;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public RendererThread getRendererThread() {
        return rendererThread;
    }
}
