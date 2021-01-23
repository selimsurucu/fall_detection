package com.example.fall_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class ThresholdActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    Sensor accelerometer;

    TextView xValue,yValue,zValue,svmValue,svmFarkValue;
    private static final String TAG = "ThresholdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        Log.d(TAG,"onCerate:Initializing Sensor Services");


        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);
        svmValue = (TextView) findViewById(R.id.svmValue);
        svmFarkValue = (TextView) findViewById(R.id.svmFarkValue);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG,"onCerate:Registered accelerometer listener");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double x_acc,y_acc,z_acc;
        x_acc =sensorEvent.values[0];
        y_acc = sensorEvent.values[1];
        z_acc = sensorEvent.values[2];

        double svm_acc = Math.sqrt(Math.pow(x_acc,2)
                +Math.pow(y_acc,2)
                +Math.pow(z_acc,2));

        Log.d(TAG,"OnSensorChanged: X:"+x_acc+" Y:"+y_acc+" Z:"+z_acc);
        xValue.setText("X Value= " + x_acc);
        yValue.setText("Y Value= " + y_acc);
        zValue.setText("Z Value= " + z_acc);
        String[] svmValueList =  svmValue.getText().toString().split("Svm= ");

        double eski;
        if(svmValueList.length>1) {
            Log.d(TAG, svmValueList[0] + svmValueList[1]);
            eski = Double.parseDouble(svmValueList[1]);
        }else {
            eski = svm_acc;
            Log.d(TAG, eski+"");

        }
        double fark =  eski - svm_acc ;
        svmFarkValue.setText("Svm Fark = " + fark);

        DecimalFormat precision = new DecimalFormat("0.00");
        //Log.d(TAG,"Format" + precision.format( svm_acc).replace(",","."));
        double n_svm_acc = Double.parseDouble( precision.format( svm_acc).replace(",","."));
        svmValue.setText("Svm= "+n_svm_acc);

        if (fark > 5 && y_acc< 0 ) {
            //Do your stuff
            Toast.makeText(getApplicationContext(),"Düşme Tespit Edildi!",Toast.LENGTH_LONG).show();
        }





    }


}