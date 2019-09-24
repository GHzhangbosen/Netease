package coder.zhang.hookproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, ((Button) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    public void replaceClickEvent(View view) {
        try {
            hook(btn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hook(View view) throws Exception {
        // 1、获取view里的mListenerInfo对象，首次获取需要先创建该对象
        Class<?> mViewClass = Class.forName("android.view.View");
        Method mGetListenerInfoMethod = mViewClass.getDeclaredMethod("getListenerInfo");
        mGetListenerInfoMethod.setAccessible(true);
        Object mListenerInfo = mGetListenerInfoMethod.invoke(view);

        // 2、获取mListenerInfo对象里的mOnClickListener
        Class<?> mListenerInfoClass = Class.forName("android.view.View$ListenerInfo");
        Field mOnClickListenerField = mListenerInfoClass.getField("mOnClickListener");
        final Object mOnClickListenerOri = mOnClickListenerField.get(mListenerInfo);

        // 3、创建代理对象
        Object mOnClickListenerProxy = Proxy.newProxyInstance(getClassLoader(), new Class[]{View.OnClickListener.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                btn.setText("你好啊");
                return method.invoke(mOnClickListenerOri, args);
            }
        });
        // 4、替换点击事件
        mOnClickListenerField.set(mListenerInfo, mOnClickListenerProxy);
    }

    public void startNewActivity(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    public void startPluginActivity(View view) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("coder.zhang.plugin_package", "coder.zhang.plugin_package.PluginActivity");
        intent.setComponent(componentName);
        startActivity(intent);
    }
}
