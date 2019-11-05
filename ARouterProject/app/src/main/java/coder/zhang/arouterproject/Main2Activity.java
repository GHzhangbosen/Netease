package coder.zhang.arouterproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;

@ARouter(path = "/app/Main2Activity")
public class Main2Activity extends AppCompatActivity {

    @Parameter(name = "name")
    String name02;

    @Parameter(name = "age")
    int age02;

    @Parameter(name = "sex")
    boolean sex02;

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
