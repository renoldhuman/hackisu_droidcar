package com.example.android.droidcar;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity{

    private int rcSpeed = 0;
    private int storeSpeed = 0;
    private int brakeSpeed = 0;
    private int rcShift = 1;
    private int rcTurn = 1;
    private float rcTurnAmt = 0.0f;
    private boolean brakePress = false;

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    //private static final int SENSOR_DELAY = 1000000;
    private static final int FROM_RADS_TO_DEGS = -57;

    boolean maxLeft = false;
    boolean left = false;
    boolean neutral = true;
    boolean right = false;
    boolean maxRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button brake = (Button) findViewById(R.id.brakeButton);
        brake.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Brake","Brake: Action Down");
                        brakeSpeed = rcSpeed;
                        rcSpeed = 0;
                        brakePress = true;
                        sendRequest();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("Brake","Brake: Action Up");
                        rcSpeed = brakeSpeed;
                        brakePress = false;
                        sendRequest();
                        brakeSpeed = 0;
                        return true;
                }
                return false;
            }
        });
        final Button leftTurn = (Button) findViewById(R.id.leftTurn);
        leftTurn.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Turn","Left: Action Down");
                        rcTurn = 1;
                        rcTurnAmt = 4095;
                        sendRequest();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("Turn","Left: Action Up");
                        rcTurn = 1;
                        rcTurnAmt = 4095;
                        return true;
                }
                return false;
            }
        });

        final Button rightTurn = (Button) findViewById(R.id.rightTurn);
        rightTurn.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Turn","Right: Action Down");
                        rcTurn = 0;
                        rcTurnAmt = 4095;
                        sendRequest();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("Turn","Right: Action Up");
                        rcTurn = 0;
                        rcTurnAmt = 4095;
                        return true;
                }
                return false;
            }
        });

        final ToggleButton shift = (ToggleButton) findViewById(R.id.shiftButton);
        shift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    Log.d("Shift", "RC is in Reverse");
                    rcShift = 0;
                    storeSpeed = rcSpeed;
                    rcSpeed = 0;
                    sendRequest();
                    try{
                       // Log.d("Shift", "SENT REQUEST-NOW SLEEPING");
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    rcSpeed = storeSpeed;
                    sendRequest();
                    //Log.d("Shift", "AFTER SLEEP: RC SPEED ="+rcSpeed);
                }
                else{
                    Log.d("Shift", "RC is in Drive");
                    rcShift = 1;
                    storeSpeed = rcSpeed;
                    rcSpeed = 0;
                    sendRequest();
                    try{
                        //Log.d("Shift", "SENT REQUEST-NOW SLEEPING");
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    rcSpeed = storeSpeed;
                    sendRequest();
                   // Log.d("Shift","AFTER SLEEP: RC SPEED = "+rcSpeed);
                }
            }
        });


        final SeekBar throttle = (SeekBar) findViewById(R.id.seekBar);
        throttle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int change = progress - rcSpeed;
                rcSpeed = progress;
                if(change>20 || change < -20){
                    Log.d("Speed", "Speed increased by:"+change);
                    sendRequest();
                }
                else{
                    Log.d("Speed", "Speed decreased by:"+change);
                }

                Log.d("RCSpeed", "RCSpeed = "+rcSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        try {
//            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
//            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//            mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        } catch (Exception e) {
//
//        }

    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
////        try{
////           // Thread.sleep(1000);
////        }catch(InterruptedException e){
////            e.printStackTrace();
////        }
//        if (event.sensor == mRotationSensor) {
//            if (event.values.length > 4) {
//                float[] truncatedRotationVector = new float[4];
//                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
//                update(truncatedRotationVector);
//            } else {
//                update(event.values);
//            }
//        }
//    }

//    private void update(float[] vectors) {
//        float[] rotationMatrix = new float[9];
//        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
//        int worldAxisX = SensorManager.AXIS_X;
//        int worldAxisZ = SensorManager.AXIS_Z;
//        float[] adjustedRotationMatrix = new float[9];
//        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
//        float[] orientation = new float[3];
//        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
//        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
//        float roll = orientation[0] * FROM_RADS_TO_DEGS;
//        //Log.d("pitch", "= "+pitch);
//       // Log.d("roll", "= "+roll);
//
////        boolean maxLeft = false;
////        boolean left = false;
////        boolean neutral = true;
////        boolean right = false;
////        boolean maxRight = false;
//        //Neutral
//        if(roll > 30 && roll < 90 && !neutral){
//            maxLeft=left=right=maxRight = false;
//            neutral = true;
//            rcTurnAmt = 0;
//            sendRequest();
//        }else if(roll > 0 && roll < 30 && !left){
//            maxLeft=neutral=right=maxRight = false;
//            left = true;
//            rcTurnAmt = 2048;
//            rcTurn = 1;
//            sendRequest();
//        }else if(roll < 0 && !maxLeft){
//            left=neutral=right=maxRight = false;
//            maxLeft = true;
//            rcTurnAmt = 4095;
//            rcTurn = 1;
//            sendRequest();
//        }else if(roll > 90 && roll < 130 && !right){
//            maxLeft=left=neutral=maxRight = false;
//            right = true;
//            rcTurnAmt = 2048;
//            rcTurn = 0;
//            sendRequest();
//        }else if(roll > 130 && !maxRight){
//            maxLeft=left=neutral=right = false;
//            maxRight = true;
//            rcTurnAmt = 4095;
//            rcTurn = 0;
//        }
//
//    }

    private void sendRequest(){
        Log.d("sendRequest", "inside of send request");
        StringRequest sendRequest = new StringRequest(Request.Method.POST,
                "http://10.35.247.153/runcontrol.php",
                new Response.Listener<String>() {
                    public void onResponse(String Response) {
                        // get response
                        Log.d("Response", Response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("direction", ""+rcShift);
                params.put("speed", ""+rcSpeed);
                params.put("turn", ""+rcTurn);
                params.put("turn_amount", ""+rcTurnAmt);
                Log.d("URL","10.35.247.153/runcontrol.php"+params);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(sendRequest);
        }
}
