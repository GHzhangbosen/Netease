package coder.zhang.plugin_package.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import coder.zhang.stander.ActivityInterface;

public class BaseActivity extends AppCompatActivity implements ActivityInterface {

    public AppCompatActivity appActivity;

    @Override
    public void insertAppContext(AppCompatActivity app) {
        appActivity = app;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStart() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {

    }

    public void setContentView(int resId) {
        appActivity.setContentView(resId);
    }

    public void startActivity(Intent intent) {
        Intent intentNew = new Intent();
        intentNew.putExtra("className", intent.getComponent().getClassName());
        appActivity.startActivity(intentNew);
    }

    public ComponentName startService(Intent intent) {
        Intent intentNew = new Intent();
        intentNew.putExtra("className", intent.getComponent().getClassName());
        return appActivity.startService(intentNew);
    }

    public void registReceiver(BroadcastReceiver receiver, IntentFilter intentFilter) {
        appActivity.registerReceiver(receiver, intentFilter);
    }

    public void sendBroadcast(Intent intent) {
        appActivity.sendBroadcast(intent);
    }
}
