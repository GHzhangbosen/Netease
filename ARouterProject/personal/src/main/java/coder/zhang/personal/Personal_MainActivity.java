package coder.zhang.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Parameter(name ="name")
    String namePersonal;

    @Parameter(name = "age")
    int agePersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        System.out.println("个人中心页面接收name: " + getIntent().getStringExtra("name"));
    }

    public void jumpApp(View view) {
    }

    public void jumpOrder(View view) {
    }
}
