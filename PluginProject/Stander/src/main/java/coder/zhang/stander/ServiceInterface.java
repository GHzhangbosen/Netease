package coder.zhang.stander;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public interface ServiceInterface {

    /**
     * 把宿主(app)的环境给插件
     * @param app
     */
    void insertAppContext(Service app);

    public IBinder onBind(Intent intent);

    public void onCreate();

    public int onStartCommand(Intent intent, int flags, int startId);

    public void onDestroy();
}
