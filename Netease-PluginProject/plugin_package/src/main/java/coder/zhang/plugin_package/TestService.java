package coder.zhang.plugin_package;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class TestService extends BaseService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    SystemClock.sleep(1000);
                    Log.d("succ2", "插件里的服务正在执行任务...");
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
