package coder.zhang.pluginproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import coder.zhang.stander.BroadcastReceiverInterface;

public class ProxyBroadcastReceiver extends BroadcastReceiver {

    private BroadcastReceiverInterface broadcastReceiverInterface;

    public ProxyBroadcastReceiver(Context context, String className) {
        try {
            Class<?> mTestBroadcastReceiverClass = PluginManager.getInstance(context).getClassLoader().loadClass(className);
            Object mTestBroadcastReceiver = mTestBroadcastReceiverClass.newInstance();
            broadcastReceiverInterface = (BroadcastReceiverInterface) mTestBroadcastReceiver;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        broadcastReceiverInterface.onReceive(context, intent);
    }
}
