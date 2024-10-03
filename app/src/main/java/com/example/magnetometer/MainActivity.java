package com.example.magnetometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private FileWriter fileWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize sensor manager and magnetometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Set up a custom sample rate (50 Hz = 20000 microseconds)
        int customSampleRateInMicroseconds = 10000;  // 50 Hz

        // Register the magnetometer listener with the custom sample rate
        sensorManager.registerListener(this, magnetometer, customSampleRateInMicroseconds);

        // Set up file writer to store data in app-specific external storage
        try {
            File file = new File(getFilesDir(), "magnetometer_data.csv");
            fileWriter = new FileWriter(file, true);
            // Write header to file if the file is new
            if (file.length() == 0) {
                fileWriter.append("Timestamp,X,Y,Z\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long timestamp = System.currentTimeMillis();

            // Log data (optional)
            Log.d("Magnetometer", "X: " + x + ", Y: " + y + ", Z: " + z);

            // Write data to CSV file
            try {
                String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date(timestamp));
                fileWriter.append(timeString + "," + x + "," + y + "," + z + "\n");
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
