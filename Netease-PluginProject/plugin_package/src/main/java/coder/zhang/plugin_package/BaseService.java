package coder.zhang.plugin_package;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import coder.zhang.stander.ServiceInterface;

public class BaseService extends Service implements ServiceInterface {

    public Service appService;

    @Override
    public void insertAppContext(Service app) {
        appService = app;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public void onDestroy() {
    }
}
