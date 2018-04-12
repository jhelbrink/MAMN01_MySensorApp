package com.example.john.mysensorapp;

        import android.hardware.SensorListener;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.view.MenuItem;
        import android.widget.TextView;

        import android.hardware.Sensor;
        import android.hardware.SensorManager;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;

public class Accelerometer extends AppCompatActivity implements SensorEventListener{


    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;
    private float[] lpAcc = new float[3];
    static final float ALPHA = 0.25f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);


        // Create our Sensor Manger
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register Sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign Text
        xText = findViewById(R.id.xText);
        yText = findViewById(R.id.yText);
        zText = findViewById(R.id.zText);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        lpAcc = lowPass(sensorEvent.values.clone(), lpAcc); // not sure if it works as intended.

        //format raw input data to only have 1 decimal.
        xText.setText("X: " + (String.format("%.1f", lpAcc[0] )));
        yText.setText("Y: " + (String.format("%.1f",lpAcc[1] )));
        zText.setText("Z: " + (String.format("%.1f",lpAcc[2] )));


        //changes size of the xyz values when moving the phone.
        if(lpAcc[0] > 0){
            xText.setTextSize(38);

        }else{
            xText.setTextSize(28);
        }
        if(lpAcc[1] > 0){
            yText.setTextSize(38);
        }else{
            yText.setTextSize(28);
        }
        if(lpAcc[2] > 0){
            zText.setTextSize(38);
        }else{
            zText.setTextSize(28);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //LP filter from moodle.
    private float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

}
