package coder.zhang.order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;

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

        System.out.println("订单页面接收name: " + getIntent().getStringExtra("name"));
    }

    public void jumpApp(View view) {
    }

    public void jumpPersonal(View view) {
    }
}
