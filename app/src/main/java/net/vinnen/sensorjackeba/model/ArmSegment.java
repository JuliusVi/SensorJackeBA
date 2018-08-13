package net.vinnen.sensorjackeba.model;

import android.util.Log;

/**
 * Created by Julius on 25.06.2018.
 */

/**
 * This class is a Model for the Arm Segments. It mainly provides information about the segments that can be used for rendering.
 * Mainly it provides the getEndpoint() Method, which calculates the Endpoint of the Arm Segment.
 */
public class ArmSegment {
    public ArmSegment(float posX, float posY, float posZ, float dimX, float dimY, float dimZ, float rotX, float rotY, float rotZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dimX = dimX;
        this.dimY = dimY;
        this.dimZ = dimZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public ArmSegment(){

    }

    public float posX;
    public float posY;
    public float posZ;
    public float dimX;
    public float dimY;
    public float dimZ;
    public float rotX;
    public float rotY;
    public float rotZ;

    public float vecX = dimY;
    public float vecY = 0;//dimY;
    public float vecZ = 0;

    public float endX = 0;
    public float endY = 0;
    public float endZ = 0;

    /**
     * Calculates the Endpoint of the Arm Segment
     */
    public void getEndpoint(){
        //Reset
        vecX = dimY;
        vecY = 0;
        vecZ = 0;

        rotateAllAchses(vecX,vecY,vecZ, rotY, rotZ-90, rotX);//rotY-90, -rotZ, 90);
        //Log.d("Tst", "VecX: " + vecX + "Vecy: " + vecY + "Vecz: " + vecZ);
        endX = posX - vecX;
        endY = posY + vecZ;
        endZ = posZ + vecY;
    }

    public void rotateAllAchses(double x, double y, double z, double yawDeg, double pitchDeg ,double rollDeg){
        double yaw, pitch, roll;
        yaw = Math.toRadians(yawDeg);
        pitch = Math.toRadians(pitchDeg);
        roll = Math.toRadians(rollDeg);

        vecX = (float) (x*(Math.cos(yaw)*Math.cos(pitch)) + y*(Math.cos(yaw)*Math.sin(pitch)*Math.sin(roll)- Math.sin(yaw)*Math.cos(roll)) + z*((Math.cos(yaw)*Math.sin(pitch)*Math.cos(roll)+ Math.sin(yaw)*Math.sin(roll))));
        vecY = (float) (x*(Math.sin(yaw)*Math.cos(pitch)) + y*(Math.sin(yaw)*Math.sin(pitch)*Math.sin(roll)+ Math.cos(yaw)*Math.cos(roll)) + z*(Math.sin(yaw)*Math.sin(pitch)*Math.cos(roll)- Math.cos(yaw)*Math.sin(roll)));
        vecZ = (float) (x*(-Math.sin(pitch)) + y*(Math.cos(pitch)*Math.sin(roll)) + z*(Math.cos(pitch)*Math.cos(roll)));
    }

    public void setPosXYZ(float x, float y, float z){
        posX = x;
        posY = y;
        posZ = z;
    }
}
