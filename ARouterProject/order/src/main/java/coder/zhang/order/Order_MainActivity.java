package coder.zhang.order;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;
import coder.zhang.arouter_api.ARouterManager;
import coder.zhang.arouter_api.ParameterManager;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Parameter(name = "name")
    String nameOrder;

    @Parameter(name = "age")
    int ageOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        ParameterManager.getInstance().loadParameter(this);
        Log.d("succ2", "订单页面接收name: " + getIntent().getStringExtra("name"));
    }

    public void jumpApp(View view) {
        ARouterManager.getInstance().build("/app/MainActivity")
                .withString("name", "from_order")
                .withInt("age", 1)
                .withBoolean("sex", true)
                .navigation(this, 163);
    }

    public void jumpPersonal(View view) {
        ARouterManager.getInstance().build("/personal/Personal_MainActivity")
                .withStringResult("name", "from_order")
                .withInt("age", 3)
                .navigation(this, 163);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.d("succ2", "Order onActivityResult: " + data.getStringExtra("call"));
        }
    }
}
