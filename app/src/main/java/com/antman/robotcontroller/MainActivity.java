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
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    JoystickView joystickLeft;
    JoystickView joystickRight;
    TextView textXMove;
    TextView textYMove;
    TextView textArg1;
    TextView textArg2;
    CheckBox checkboxPhoneAngles;
    CheckBox checkboxTransBody;
    VerticalSeekBar seekbarZHeight;
    VerticalSeekBar seekbarLiftHeight;

    Float MAX_STEPSIZE = 50.0F;
    Float MAX_TRANSLATESIZE = 40.0F;
    Float MAX_ROTATESIZE = 20.0F;

    Float currentXStepSize = 0.0F;
    Float currentYStepSize = 0.0F;
    Float currentZRot = 0.0F;
    Float currentLiftHeight = 0.0F;
    Boolean FollowPhoneAngle = false;
    Boolean TranslateBody = false;

    Float currentXTrans = 0.0F;
    Float currentYTrans = 0.0F;
    Float currentZTrans = 0.0F;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    BlueToothConnector btConnector;

    Float AngleLeftRight=0.0F;
    Float AngleUpDown=0.0F;

    long prevTime = SystemClock.elapsedRealtime();

    private static String address = "00:06:66:84:94:99";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTextViews();
        setupJoysticks();
        setupSensors();
        setupCheckBoxes();
        setupSeekBars();
        initListeners();
        btConnector = new BlueToothConnector();
        btConnector.InitializeBlueTooth(address);
    }

    void setupSeekBars()
    {
        seekbarZHeight = (VerticalSeekBar)findViewById(R.id.seekbarZHeight);
        seekbarZHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentZTrans = (float)i - 35.0F;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarLiftHeight = (VerticalSeekBar)findViewById(R.id.seekbarLiftHeight);
        seekbarLiftHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentLiftHeight = (float)i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    void setupCheckBoxes()
    {
        checkboxPhoneAngles = (CheckBox)findViewById(R.id.checkBoxPhoneAngle);
        checkboxPhoneAngles.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (FollowPhoneAngle == false)
                    FollowPhoneAngle = true;
                else
                    FollowPhoneAngle = false;

            }
        });

        checkboxTransBody = (CheckBox)findViewById(R.id.checkBoxTranslateBody);
        checkboxTransBody.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (TranslateBody == false)
                    TranslateBody = true;
                else
                    TranslateBody = false;

            }
        });
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
                Float Magnitude = MAX_STEPSIZE * (power/100.0F);
                currentXStepSize = Magnitude * (float)Math.sin(angle * (3.14/180.0));
                currentYStepSize = Magnitude * (float)Math.cos(angle * (3.14/180.0));
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
                //textArg1.setText(" " + String.valueOf(angle));
                //textArg2.setText(" " + String.valueOf(power));
                if (TranslateBody == true) {
                    Float Magnitude = MAX_TRANSLATESIZE * (power / 100.0F);
                    currentXTrans = Magnitude * (float) -Math.sin(angle * (3.14 / 180.0));
                    currentYTrans = Magnitude * (float) -Math.cos(angle * (3.14 / 180.0));
                }
                else
                {
                    Float Magnitude = MAX_ROTATESIZE * (power / 100.0F);
                    currentZRot = Magnitude * (float) -Math.sin(angle * (3.14 / 180.0));
                    currentXTrans = 0F;
                    currentYTrans = 0F;
                }
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
        btConnector.onResume();
        super.onResume();

    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        btConnector.onPause();
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

    public static void setFloat(byte []bdata,int index,float fdata)
    {
        byte[] tmp = ByteBuffer.allocate(4).putFloat(fdata).array();
        for (int i = 0;i<4;i++)
        {
            bdata[index+i] = tmp[i];
        }
    }

    public void sendClear()
    {
        byte[] cmd = new byte[1];
        cmd[0] = '\n';
        try {
            btConnector.writeData(cmd,1);
        }
        catch (Exception e)
        {

        }
    }


    public void setGaitParams(float Xmove, float Ymove,float Zrot, float LiftHeight)
    {
        sendClear();
        byte[] cmd = new byte[2 + 16];
        cmd[0] = 'G';
        setFloat(cmd,1,Xmove);
        setFloat(cmd,5,Ymove);
        setFloat(cmd,9,Zrot);
        setFloat(cmd,13,LiftHeight);

        cmd[17] = '\n';

        try {
            btConnector.writeData(cmd,18);
        }
        catch (Exception e)
        {

        }
    }

    public void setRotParams(float bRot_x,float bRot_y,float bRot_z)
    {
        sendClear();
        byte[] cmd = new byte[2 + 12];
        cmd[0] = 'R';
        setFloat(cmd,1,bRot_x);
        setFloat(cmd,5,bRot_y);
        setFloat(cmd,9,bRot_z);

        cmd[13] = '\n';
        try {
            btConnector.writeData(cmd,14);
        }
        catch (Exception e)
        {

        }
    }

    public void setTransParams(float bTrans_x,float bTrans_y,float bTrans_z)
    {
        sendClear();
        byte[] cmd = new byte[2 + 12];
        cmd[0] = 'T';
        setFloat(cmd,1,bTrans_x);
        setFloat(cmd,5,bTrans_y);
        setFloat(cmd,9,bTrans_z);

        cmd[13] = '\n';
        try {
            btConnector.writeData(cmd,14);
        }
        catch (Exception e)
        {

        }
    }

    public void UpdateAccel(float []mGravity)
    {
        AngleLeftRight =90.0F - (float)Math.atan2(mGravity[2], mGravity[1] ) * (180.0F/3.1415F);
        AngleUpDown = 90.0F - (float)Math.atan2(mGravity[2] ,mGravity[0] ) * (180.0F/3.1415F);




        if (AngleLeftRight > 80.0F)
            AngleLeftRight = 90.0F;
        if (AngleLeftRight < -80.0F)
            AngleLeftRight = -90.0F;

        if (AngleUpDown > 80.0F)
            AngleUpDown = 90.0F;
        if (AngleUpDown < -80.0F)
            AngleUpDown = -90.0F;

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
            AngleLeftRight /= -3.0F;
            AngleUpDown /= -3.0F;
            prevTime = SystemClock.elapsedRealtime();
            textArg1.setText(" " + String.valueOf(currentXTrans));
            textArg2.setText(" " + String.valueOf(currentYTrans));
            if (currentXStepSize == 0.0F && currentYStepSize == 0.0F && currentZRot == 0.0F)
                setGaitParams(0F,0F,0,0F);
            else if (currentXStepSize == 0.0F && currentYStepSize == 0.0F && currentZRot > 0.0F)
                setGaitParams(0F,0F,currentZRot,currentLiftHeight);
            else
                setGaitParams(currentXStepSize,currentYStepSize,currentZRot,currentLiftHeight);

            if (FollowPhoneAngle == true)
            {
                setRotParams(AngleUpDown,AngleLeftRight,currentZRot);
            }
            else
            {
                setRotParams(0,0,currentZRot);
            }
            setTransParams(currentXTrans,currentYTrans,currentZTrans);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
}
