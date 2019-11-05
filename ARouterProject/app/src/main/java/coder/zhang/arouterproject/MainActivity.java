package coder.zhang.arouterproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;
import coder.zhang.arouter_api.ARouterManager;
import coder.zhang.arouter_api.ParameterManager;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter
    String name;
    @Parameter
    int age;
    @Parameter
    boolean sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParameterManager.getInstance().loadParameter(this);
        Log.d("succ2", "首页接收name: " + getIntent().getStringExtra("name"));
    }

    public void jumpOrder(View view) {
        ARouterManager.getInstance().build("/order/Order_MainActivity")
                .withString("name", "from_app")
                .withInt("age", 2)
                .navigation(this, 163);
    }

    public void jumpPersonal(View view) {
        ARouterManager.getInstance().build("/personal/Personal_MainActivity")
                .withString("name", "from_app")
                .withInt("age", 3)
                .navigation(this, 163);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.d("succ2", "回到首页");
        }
    }
}
