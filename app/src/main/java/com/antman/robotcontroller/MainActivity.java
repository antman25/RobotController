package com.antman.robotcontroller;

import android.bluetooth.BluetoothDevice;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    JoystickView joystickLeft;
    JoystickView joystickRight;
    TextView textXMove;
    TextView textYMove;
    TextView textArg1;
    TextView textArg2;

    double MAX_STEPSIZE = 50.0;
    Double currentXStepSize = 0.0;
    Double currentYStepSize = 0.0;
    Double currentZRot = 0.0;
    Double currentLiftHeight = 0.0;

    Double currentXTrans = 0.0;
    Double currentYTrans = 0.0;
    Double currentZTrans = 0.0;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;


    Double AngleLeftRight=0.0;
    Double AngleUpDown=0.0;

    long prevTime = SystemClock.elapsedRealtime();

    private static String address = "00:06:66:84:94:99";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTextViews();
        setupJoysticks();
        setupSensors();
        initListeners();
    }

    void setupSensors()
    {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    void setupTextViews()
    {
        textXMove = (TextView) findViewById(R.id.textViewXMove);
        textYMove = (TextView) findViewById(R.id.textViewYMove);
        textArg1 = (TextView) findViewById(R.id.textViewArg1);
        textArg2 = (TextView) findViewById(R.id.textViewArg2);

    }

    public void initListeners()
    {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    void setupJoysticks()
    {
        joystickLeft = (JoystickView)findViewById(R.id.joystickLeft);

        joystickLeft.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction)
            {
                // TODO Auto-generated method stub
                double Magnitude = MAX_STEPSIZE * (power/100.0);
                currentXStepSize = Magnitude * Math.cos(angle * (3.14/180.0));
                currentYStepSize = Magnitude * Math.sin(angle * (3.14/180.0));
                textXMove.setText(" " + String.valueOf(currentXStepSize));
                textYMove.setText(" " + String.valueOf(currentYStepSize));
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);


        joystickRight = (JoystickView)findViewById(R.id.joystickRight);
        joystickRight.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction)
            {
                // TODO Auto-generated method stub
                textArg1.setText(" " + String.valueOf(angle));
                textArg2.setText(" " + String.valueOf(power));
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        initListeners();
        super.onResume();

    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();






    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed()
    {
        mSensorManager.unregisterListener(this);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();

    }

    public void UpdateAccel(float []mGravity)
    {
        AngleLeftRight =90 - Math.atan2(mGravity[2], mGravity[1] ) * (180.0/3.1415);
        AngleUpDown = 90 - Math.atan2(mGravity[2] ,mGravity[0] ) * (180.0/3.1415);



        if (AngleLeftRight > 80.0)
            AngleLeftRight = 90.0;
        if (AngleLeftRight < -80.0)
            AngleLeftRight = -90.0;

        if (AngleUpDown > 80.0)
            AngleUpDown = 90.0;
        if (AngleUpDown < -80.0)
            AngleUpDown = -90.0;

    }






    @Override
    public void onSensorChanged(SensorEvent event) {
        //If type is accelerometer only assign values to global property mGravity
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            UpdateAccel(event.values);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            //mGeomagnetic = event.values;

            //TextView xout = (TextView) findViewById(R.id.textViewAngleX);
           // xout.setText("X: " + String.format("%.2f",mGeomagnetic[0]));

            //TextView yout = (TextView) findViewById(R.id.textViewAngleY);
            //yout.setText("Y: " + String.format("%.2f",mGeomagnetic[1]));

            //TextView zout = (TextView) findViewById(R.id.textViewAngleZ);
            //zout.setText("Z: " + String.format("%.2f",mGeomagnetic[2]));
        }




        if (SystemClock.elapsedRealtime() - prevTime > 50)
        {
            AngleLeftRight /= 3.0;
            AngleUpDown /= 3.0;
            prevTime = SystemClock.elapsedRealtime();
            textArg1.setText(" " + String.valueOf(AngleLeftRight));
            textArg2.setText(" " + String.valueOf(AngleUpDown));
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
}
