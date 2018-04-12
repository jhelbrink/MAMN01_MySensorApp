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

    private TextView mTextMessage;
    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        mTextMessage = findViewById(R.id.message);
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
        //format raw input data to only have 1 decimal.
        xText.setText("X: " + (String.format("%.1f", sensorEvent.values[0] )));
        yText.setText("Y: " + (String.format("%.1f",sensorEvent.values[1] )));
        zText.setText("Z: " + (String.format("%.1f",sensorEvent.values[2] )));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
