package com.example.fall_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CnnActivity extends AppCompatActivity  implements SensorEventListener {

    Interpreter interpreter;
    TextView sayac,values;
    private SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnn);
        sayac= (TextView)findViewById(R.id.valueCounter);
        values = (TextView) findViewById(R.id.values);
        sayac.setText("0");
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // Log.d("M","Tahmin = "+doInference("10"));

    }

    public float doInference(float[] input ){

        //float[] input = new float[1];

        //input[0] = Float.parseFloat(val);
       // float[] input = new float[9];

        /*
        List<Double> my_list = new ArrayList<>();
        input[0]=Float.parseFloat("-1.3982137");
        input[1]=Float.parseFloat("-1.3982137");
        input[2]=Float.parseFloat("-1.3982137");
        input[3]=Float.parseFloat("-1.3982137");
        input[4]=Float.parseFloat("-1.3982137");
        input[5]=Float.parseFloat("-1.3982137");
        input[6]=Float.parseFloat("-1.3982137");
        input[7]=Float.parseFloat("-1.3982137");
        input[8]=Float.parseFloat("-1.3982137");
        */

        float[][] output= new float[1][1];
        interpreter.run(input,output);
        return output[0][0];
    }


    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        String eskiValue = values.getText().toString();
        String newValue = eskiValue+","+sensorEvent.values[0]+","+sensorEvent.values[1]+","+sensorEvent.values[2];
        values.setText(newValue);
        int i = Integer.parseInt(sayac.getText().toString());
        i +=1;
        if(i == 3){
            tahminDatasiOlustur();
            i = 0;
            values.setText("");
        }
        sayac.setText(String.valueOf(i));


    }

    private void tahminDatasiOlustur() {
        String eskiValue = values.getText().toString();
        //Log.d("Data",eskiValue);

        String[] split = eskiValue.split(",");
        float[] input = new float[9];
        Log.d("data", split.length+"");
        for (int x = 1;x <split.length;x++) {

            Log.d("Datalar", split[x]);
            input[x]=Float.parseFloat(split[x]);
            if(x==8)
                break;
        }
        if(doInference(input)<4.5 && input[2]<0){
            Toast.makeText(getApplicationContext(),"Düşme Tespit Edildi!!!",Toast.LENGTH_SHORT).show();
        }




    }
}