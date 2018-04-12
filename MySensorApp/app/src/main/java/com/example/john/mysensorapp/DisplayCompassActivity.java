package com.example.john.mysensorapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class DisplayCompassActivity extends AppCompatActivity implements SensorEventListener{
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.
    protected float[] gravSensorVals;
    protected float[] magSensorVals;
    ImageView img;
    TextView degree;
    TextView textView2;
    ConstraintLayout cs;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        img = (ImageView) findViewById(R.id.imageView);
        degree = (TextView) findViewById(R.id.degree);
        textView2 = (TextView) findViewById(R.id.textView2);
        cs = (ConstraintLayout) findViewById(R.id.background);

        start();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);

            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        img.setRotation(-mAzimuth);

        String where = "NW";
        String directionNotification = "";

        if (mAzimuth >= 350 || mAzimuth <= 10){
            where = "N";
            directionNotification = "DU GÅR NORR";
            cs.setBackgroundColor(GREEN);
        }
        if (mAzimuth < 350 && mAzimuth > 280) {
            where = "NW";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);
        }
        if (mAzimuth <= 280 && mAzimuth > 260) {
            where = "W";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);
        }
        if (mAzimuth <= 260 && mAzimuth > 190) {
            where = "SW";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);

        }
        if (mAzimuth <= 190 && mAzimuth > 170) {
            where = "S";
            directionNotification = "DU GÅR SÖDER";
            cs.setBackgroundColor(RED);
            if(mAzimuth > 177 && mAzimuth < 183) {
                vibrate(1000);
            }
        }
        if (mAzimuth <= 170 && mAzimuth > 100) {
            where = "SE";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);
        }
        if (mAzimuth <= 100 && mAzimuth > 80) {
            where = "E";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);
        }
        if (mAzimuth <= 80 && mAzimuth > 10) {
            where = "NE";
            directionNotification = "";
            cs.setBackgroundColor(WHITE);
        }



        degree.setText(mAzimuth + "° " + where);
        textView2.setText(directionNotification);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }
    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }
    public void stop() {
        if(haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this,mMagnetometer);
        }
        else{
            if(haveSensor)
                mSensorManager.unregisterListener(this,mRotationV);
        }
    }


    public void vibrate(int duration){
        Vibrator vibb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibb.vibrate(duration);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

}
