package coder.zhang.pluginproject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Constructor;

import coder.zhang.stander.ActivityInterface;

public class ProxyActivity extends AppCompatActivity {

//    @Override
//    public Resources getResources() {
//        return PluginManager.getInstance(this).getResources();
//    }
//
//    @Override
//    public ClassLoader getClassLoader() {
//        return PluginManager.getInstance(this).getClassLoader();
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 代理插件里的activity
        String className = getIntent().getStringExtra("className");

        try {
            Class<?> mPluginActivityClass = PluginManager.getInstance(this).getClassLoader().loadClass(className);
            Constructor<?> constructor = mPluginActivityClass.getConstructor();
            Object mPluginActivity = constructor.newInstance();
            ActivityInterface activityInterface = (ActivityInterface) mPluginActivity;
            activityInterface.insertAppContext(this);

            Bundle bundle = new Bundle();
            bundle.putString("appName", "我是宿主传递过来的信息");
            activityInterface.onCreate(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");
        Intent i = new Intent(this, ProxyActivity.class);
        i.putExtra("className", className);
        super.startActivity(i);
    }

    @Override
    public ComponentName startService(Intent service) {
        String className = service.getStringExtra("className");
        Intent intent = new Intent(this, ProxyService.class);
        intent.putExtra("className", className);
        return super.startService(intent);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        String className = receiver.getClass().getName();
        return super.registerReceiver(new ProxyBroadcastReceiver(this, className), filter);
    }
}
