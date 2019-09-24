package coder.zhang.plugin_package;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends BaseActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(appActivity);
        ll.setBackgroundColor(Color.WHITE);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(appActivity);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.RED);
        tv.setTextSize(20);
        tv.setText("我是插件的测试页面");

        ll.addView(tv);

        appActivity.addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
