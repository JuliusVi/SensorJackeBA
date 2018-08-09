package net.vinnen.sensorjackeba;

import android.util.Log;

/**
 * Created by Julius on 25.06.2018.
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

    public void getEndpoint(){
        //Reset
        vecX = dimY;
        vecY = 0;
        vecZ = 0;

        /*
        //RotZ
        vecX = (float) (Math.cos(Math.toRadians(rotZ) * vecX) - (Math.sin(Math.toRadians(rotZ))) * vecY);
        vecY = (float) (-Math.sin(Math.toRadians(rotZ) * vecX) + (Math.cos(Math.toRadians(rotZ))) * vecY);
        vecZ = 1 * vecZ;

        //RotX
        vecX = 1 * vecX;
        vecY = (float) (Math.cos(Math.toRadians(rotX) * vecY) - (Math.sin(Math.toRadians(rotX))) * vecZ);
        vecZ = (float) (Math.sin(Math.toRadians(rotX) * vecY) + (Math.cos(Math.toRadians(rotX))) * vecZ);

        //RotY
        vecX = (float) (Math.cos(Math.toRadians(rotY) * vecX) + (Math.sin(Math.toRadians(rotY))) * vecZ);
        vecY = 1 * vecY;
        vecZ = (float) (-Math.sin(Math.toRadians(rotY) * vecX) + (Math.cos(Math.toRadians(rotY))) * vecZ);
*/

        //Roll des Vektors ist egal
        //rotateVector(vecX,vecY, vecZ, "X", rotX);

        //rotateVector(vecX,vecY,vecZ, "Z", rotZ);
        //rotateVector(vecX,vecY,vecZ, "Y", rotY);
        //Log.d("Tst", "Rotx: " + rotX + "Roty: " + rotY + "Rotz: " + rotZ);
        //Log.d("Tstbf", "VecX: " + vecX + "Vecy: " + vecY + "Vecz: " + vecZ);
        rotateAllAchses(vecX,vecY,vecZ, rotY, rotZ-90, rotX);//rotY-90, -rotZ, 90);
        //Log.d("Tst", "VecX: " + vecX + "Vecy: " + vecY + "Vecz: " + vecZ);
        endX = posX - vecX;
        endY = posY + vecZ;
        endZ = posZ + vecY;
    }

    public void rotateVector(double x, double y, double z, String axis, double deg){
        double u, v, w;
        u=0;v=0;w=0;
        if(axis.equals("X")){
            u=1;v=0;w=0;
        } else if(axis.equals("Y")){
            u=0;v=1;w=0;
        } else if(axis.equals("Z")){
            u=0;v=0;w=1;
        }
        vecX =(float)( u*(u*x + v*y + w*z)*(1d - Math.cos(Math.toRadians(deg)))
                + x*Math.cos(Math.toRadians(deg))
                + (-w*y + v*z)*Math.sin(Math.toRadians(deg)));
        vecY = (float)( v*(u*x + v*y + w*z)*(1d - Math.cos(Math.toRadians(deg)))
                + y*Math.cos(Math.toRadians(deg))
                + (w*x - u*z)*Math.sin(Math.toRadians(deg)));
        vecZ =  (float)( w*(u*x + v*y + w*z)*(1d - Math.cos(Math.toRadians(deg)))
                + z*Math.cos(Math.toRadians(deg))
                + (-v*x + u*y)*Math.sin(Math.toRadians(deg)));
    }

    //vecX = x*(cos(yaw)*cos(pitch) + y*(cos(yaw)*sin(pitch)*sin(roll)- sin(yaw)*cos(roll)) + z*
    //vecX = x*(cos(yaw)*cos(pitch)) + y*(sin(yaw)*cos(pitch)) - z*(sin(pitch))
    //vecY = x*(cos(yaw)*sin(pitch)*sin(roll)- sin(yaw)*cos(roll)) + y*(sin(yaw)*sin(pitch)*sin(roll)+ cos(yaw)*cos(roll)) + z*(cos(pitch)*sin(roll))
    //vecZ = x*(cos(yaw)*sin(pitch)*cos(roll)+ sin(yaw)*sin(roll)) + y*(sin(yaw)*sin(pitch)*cos(roll)- cos(yaw)*sin(roll)) + z*(cos(pitch)*cos(roll))
    public void rotateAllAchses(double x, double y, double z, double yawDeg, double pitchDeg ,double rollDeg){
        double yaw, pitch, roll;
        yaw = Math.toRadians(yawDeg);
        pitch = Math.toRadians(pitchDeg);
        roll = Math.toRadians(rollDeg);

        /*
        vecX = (float) (x*(Math.cos(yaw)*Math.cos(pitch)) + y*(Math.sin(yaw)*Math.cos(pitch)) - z*(Math.sin(pitch)));
        vecY = (float) (x*(Math.cos(yaw)*Math.sin(pitch)*Math.sin(roll)- Math.sin(yaw)*Math.cos(roll)) + y*(Math.sin(yaw)*Math.sin(pitch)*Math.sin(roll)+ Math.cos(yaw)*Math.cos(roll)) + z*(Math.cos(pitch)*Math.sin(roll)));
        vecZ = (float) (x*(Math.cos(yaw)*Math.sin(pitch)*Math.cos(roll)+ Math.sin(yaw)*Math.sin(roll)) + y*(Math.sin(yaw)*Math.sin(pitch)*Math.cos(roll)- Math.cos(yaw)*Math.sin(roll)) + z*(Math.cos(pitch)*Math.cos(roll)));
    */
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
