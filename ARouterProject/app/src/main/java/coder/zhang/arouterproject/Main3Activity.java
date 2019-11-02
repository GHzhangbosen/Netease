package coder.zhang.arouterproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import coder.zhang.arouter.ARouter;

@ARouter(path = "/app/Main3Activity")
public class Main3Activity extends AppCompatActivity {

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
