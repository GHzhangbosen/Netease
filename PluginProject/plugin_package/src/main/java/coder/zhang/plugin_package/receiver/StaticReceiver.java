package coder.zhang.plugin_package.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import coder.zhang.stander.BroadcastReceiverInterface;

public class StaticReceiver extends BroadcastReceiver implements BroadcastReceiverInterface {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "我是静态广播，我也收到了", Toast.LENGTH_SHORT).show();
    }
}
