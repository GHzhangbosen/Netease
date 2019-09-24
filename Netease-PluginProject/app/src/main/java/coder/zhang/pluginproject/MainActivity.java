package coder.zhang.pluginproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 加载插件
     */
    public void loadPlugin(View view) {
        PluginManager.getInstance(this).loadPlugin();
    }

    /**
     * 启动插件
     */
    public void startPlugin(View view) {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(PluginManager.getInstance(this).getPluginPath(), PackageManager.GET_ACTIVITIES);

            Intent intent = new Intent(this, ProxyActivity.class);
            intent.putExtra("className", packageInfo.activities[0].name);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadStaticReceiver(View view) {
        PluginManager.getInstance(this).parserApkAction();
    }

    public void sendStaticBroadcast(View view) {
        Intent intent = new Intent();
        intent.setAction("plugin.static.receiver");
        sendBroadcast(intent);
    }
}
