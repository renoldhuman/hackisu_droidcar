package com.example.android.droidcar;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.TextView;
import android.widget.ToggleButton;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.lang.Math;


public class MainActivity extends AppCompatActivity implements Orientation.Listener {

    private int rcSpeed = 0;
    private int storeSpeed = 0;
    private int brakeSpeed = 0;
    private int rcShift = 1;
    private int rcTurn = 1;
    private float rcTurnAmt = 0.0f;
    private boolean brakePress = false;

    private SensorManager mSensorManager;
    private Sensor rotate;

    private Orientation mOrientation;
    //private AttitudeIndicator mAttitudeIndicator;


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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mOrientation = new Orientation(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientation.startListening(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOrientation.stopListening();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
       // mAttitudeIndicator.setAttitude(pitch, roll);
        try{
            Thread.sleep(1000);
            Log.d("Pitch","= "+pitch);
            Log.d("Roll", "= "+roll);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

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
