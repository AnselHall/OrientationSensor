package com.example.zbj.testdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScreenRotationSwitch extends AppCompatActivity implements View.OnClickListener {


    private Button mRotationButton;
    private RotationObserver mRotationObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_rotation_switch);

        //创建观察类对象
        mRotationObserver = new RotationObserver(new Handler());

        mRotationButton = (Button) findViewById(R.id.rotation);
        refreshButton();
        mRotationButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除观察变化
        mRotationObserver.stopObserver();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //注册观察变化
        mRotationObserver.startObserver();
    }


    //更新按钮状态
    private void refreshButton() {
        if (getRotationStatus(this) == 1) {
            mRotationButton.setText("on");
        } else {
            mRotationButton.setText("off");
        }
    }

    //得到屏幕旋转的状态
    private int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = android.provider.Settings.System.getInt(context.getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    //设置系统属性,需要权限,<uses-permission android:name="android.permission.WRITE_SETTINGS"/>
    private void setRotationStatus(ContentResolver resolver, int status) {
        //得到uri
        Uri uri = android.provider.Settings.System.getUriFor("accelerometer_rotation");
        //沟通设置status的值改变屏幕旋转设置
        android.provider.Settings.System.putInt(resolver, "accelerometer_rotation", status);
        //通知改变
        resolver.notifyChange(uri, null);
    }

    @Override
    public void onClick(View v) {
        if (getRotationStatus(this) == 1) {

            setRotationStatus(getContentResolver(), 0);
        } else {
            setRotationStatus(getContentResolver(), 1);
        }
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
            //更新按钮状态
            refreshButton();
            Toast.makeText(ScreenRotationSwitch.this, "旋转屏幕设置有变化",
                    Toast.LENGTH_SHORT).show();
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }
}
