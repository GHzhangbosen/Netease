package coder.zhang.plugin_package.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import coder.zhang.plugin_package.receiver.TestBroadcastReceiver;
import coder.zhang.plugin_package.service.TestService;

public class PluginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(appActivity);
        tv.setText("我是插件");
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.WHITE);
        tv.setTextColor(Color.RED);
        tv.setTextSize(20);

        Button btnStartActivity = new Button(appActivity);
        btnStartActivity.setBackgroundColor(Color.WHITE);
        btnStartActivity.setTextColor(Color.RED);
        btnStartActivity.setText("跳转");
        btnStartActivity.setTextSize(20);

        Button btnStartService = new Button(appActivity);
        btnStartService.setBackgroundColor(Color.WHITE);
        btnStartService.setTextColor(Color.RED);
        btnStartService.setText("启动服务");
        btnStartService.setTextSize(20);

        Button btnRegistReceiver = new Button(appActivity);
        btnRegistReceiver.setBackgroundColor(Color.WHITE);
        btnRegistReceiver.setTextColor(Color.RED);
        btnRegistReceiver.setText("注册广播接受者");
        btnRegistReceiver.setTextSize(20);
        Button sendRegistReceiver = new Button(appActivity);
        sendRegistReceiver.setBackgroundColor(Color.WHITE);
        sendRegistReceiver.setTextColor(Color.RED);
        sendRegistReceiver.setText("发送广播");
        sendRegistReceiver.setTextSize(20);

        LinearLayout ll = new LinearLayout(appActivity);
        ll.setBackgroundColor(Color.RED);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        appActivity.addContentView(ll, lp);

        ll.addView(tv);
        ll.addView(btnStartActivity);
        ll.addView(btnStartService);
        ll.addView(btnRegistReceiver);
        ll.addView(sendRegistReceiver);

        btnStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(appActivity, TestActivity.class));
            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(appActivity, TestService.class);
                startService(intent);
            }
        });

        btnRegistReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("coder.zhang.plugin_package.ACTION");
                registReceiver(new TestBroadcastReceiver(), filter);
            }
        });

        sendRegistReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("coder.zhang.plugin_package.ACTION");
                sendBroadcast(intent);
            }
        });

        Toast.makeText(appActivity, "我是插件", Toast.LENGTH_SHORT).show();
    }
}
