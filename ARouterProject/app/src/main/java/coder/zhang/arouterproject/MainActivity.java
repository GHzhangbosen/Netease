package coder.zhang.arouterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import coder.zhang.arouter.ARouter;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("首页接收name: " + getIntent().getStringExtra("name"));
    }

    public void jumpOrder(View view) {
    }

    public void jumpPersonal(View view) {
    }
}
