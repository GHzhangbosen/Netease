package coder.zhang.pluginproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;

import coder.zhang.stander.ServiceInterface;

public class ProxyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            String className = intent.getStringExtra("className");
            Class<?> mTestServiceClass = PluginManager.getInstance(this).getClassLoader().loadClass(className);
            Object mTestService = mTestServiceClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
//            Object mTestService = mTestServiceClass.newInstance();
            ServiceInterface serviceInterface = (ServiceInterface) mTestService;
            serviceInterface.insertAppContext(this);

            serviceInterface.onStartCommand(intent, flags, startId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
