package com.example.android.droidcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private int rcSpeed = 0;

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
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("Brake","Brake: Action Up");
                }
                return false;
            }
        });

        final ToggleButton shift = (ToggleButton) findViewById(R.id.shiftButton);
        shift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    Log.d("Shift", "RC is in Reverse");
                    //0
                }
                else{
                    Log.d("Shift", "RC is in Drive");
                    //1
                }
            }
        });


        final SeekBar throttle = (SeekBar) findViewById(R.id.seekBar);
        throttle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int change = progress - rcSpeed;

                if(change > 0){
                    Log.d("Speed", "Speed increased by:"+change);
                }
                else{
                    Log.d("Speed", "Speed decreased by:"+change);
                }
                rcSpeed = progress;

                Log.d("RCSpeed", "RCSpeed = "+rcSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


}
