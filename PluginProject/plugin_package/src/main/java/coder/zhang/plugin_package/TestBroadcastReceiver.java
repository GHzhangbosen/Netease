package coder.zhang.plugin_package;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestBroadcastReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "我是广播接收者，我收到消息了", Toast.LENGTH_SHORT).show();
    }
}
