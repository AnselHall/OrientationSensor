package com.example.zbj.testdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "tag";
    private int systemGravity = -1;

    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private RotationObserver rotationObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSystemGravity();

        rotationObserver = new RotationObserver(new Handler());

        //0:竖直方向    1:水平
//        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
//        Log.e(TAG, "onCreate: rotation = " + rotation);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : sensorList
//                ) {
//            Log.e(TAG, "onCreate: sensor" + sensor.getType() + "    name = " + sensor.getName());
//        }

        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


//        int rotation0 = Surface.ROTATION_0;
//        int rotation90 = Surface.ROTATION_90;
//        int rotation180 = Surface.ROTATION_180;
//        int rotation270 = Surface.ROTATION_270;

    }

    private void getSystemGravity() {
        try {
            //1:自动旋转    0:没打开屏幕自动旋转
            systemGravity = Settings.System.getInt(this
                            .getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);

//            Log.e(TAG, "onCreate: systemGravity = " + systemGravity);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.e(TAG, "onConfigurationChanged: " + newConfig.orientation);
////        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        int orientation = getResources().getConfiguration().orientation;
//
//        Log.e(TAG, "onConfigurationChanged: orientation = " + orientation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_GAME);
        rotationObserver.startObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rotationObserver.stopObserver();
    }

    private boolean isShow = true;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth_angle = event.values[0];//取值范围:0 - 360  由手机在水平方向的角度决定
        float pitch_angle = event.values[1];//取值范围:-180 - 180 由手机正面的角度决定
        float roll_angle = event.values[2];//取值范围:-90 - 90  由手机侧面的角度决定
        // Do something with these orientation angles.

//        Log.e(TAG, "onSensorChanged: azimuth_angle = " + azimuth_angle + "  pitch_angle = " + pitch_angle + "   roll_angle = " + roll_angle);

//        getSystemGravity();

        if (0 == systemGravity && Math.abs(roll_angle) > 75) {

            if (isShow) {
                Toast.makeText(MainActivity.this, "如果想要使用横屏查看,请开启自动转屏!", Toast.LENGTH_SHORT).show();
                isShow = false;
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isShow = true;
//                    }
//                }, 2000);
            }
        } else if (0 == systemGravity && Math.abs(roll_angle) < 20) {
            isShow = true;
        }
    }

    //精确度改变
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
//        Log.e(TAG, "onAccuracyChanged: accuracy = " + accuracy);
    }

    public void rotation(View view) {
        startActivity(new Intent(this, ScreenRotationSwitch.class));
    }

    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = getContentResolver();
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            //更新自动旋转的状态值
            getSystemGravity();

            Toast.makeText(MainActivity.this, "旋转屏幕设置有变化",
                    Toast.LENGTH_SHORT).show();
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }
}
/*
* Sensor Type
*
* 06-03 15:26:16.235 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor4    name = MPL Gyroscope
06-03 15:26:16.235 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor16    name = MPL Raw Gyroscope
06-03 15:26:16.235 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor1    name = MPL Accelerometer
06-03 15:26:16.235 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor2    name = MPL Magnetic Field
06-03 15:26:16.235 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor14    name = MPL Raw Magnetic Field
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor3    name = MPL Orientation
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor11    name = MPL Rotation Vector
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor15    name = MPL Game Rotation Vector
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor10    name = MPL Linear Acceleration
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor9    name = MPL Gravity
06-03 15:26:16.245 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor17    name = MPL Significant Motion
06-03 15:26:16.255 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor18    name = MPL Step Detector
06-03 15:26:16.255 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor19    name = MPL Step Counter
06-03 15:26:16.255 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor20    name = MPL Geomagnetic Rotation Vector
06-03 15:26:16.255 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor8    name = TMD27723 Proximity Sensor
06-03 15:26:16.255 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor5    name = TMD27723 Light Sensor
06-03 15:26:16.265 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor22    name = Auto Rotation Sensor
06-03 15:26:16.265 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor3    name = Orientation Sensor
06-03 15:26:16.265 1544-1544/com.example.zbj.testdemo E/tag: onCreate: sensor4    name = Corrected Gyroscope Sensor
*
* */