package coder.zhang.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.Parameter;
import coder.zhang.arouter_api.ARouterManager;
import coder.zhang.arouter_api.BundleManager;
import coder.zhang.arouter_api.ParameterManager;
import coder.zhang.common.OrderDrawable;

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

        ParameterManager.getInstance().loadParameter(this);
        Log.d("succ2", "个人中心页面接收name: " + getIntent().getStringExtra("name"));

        Intent intent = new Intent();
        intent.putExtra("call", "个人中心已经关闭");
        setResult(0, intent);

        ImageView iv = findViewById(R.id.textView2);
        Object obj = ARouterManager.getInstance().build("/order/getDrawable")
                .navigation(this);
        if (obj != null) {
            if (!(obj instanceof OrderDrawable)) return;
            iv.setImageResource(((OrderDrawable) obj).getDrawable());
        }
    }

    public void jumpApp(View view) {
        ARouterManager.getInstance().build("/app/MainActivity")
                .withString("name", "from_personal")
                .withInt("age", 1)
                .withBoolean("sex", true)
                .navigation(this, 163);
    }

    public void jumpOrder(View view) {
        ARouterManager.getInstance().build("/order/Order_MainActivity")
                .withString("name", "from_personal")
                .withInt("age", 2)
                .navigation(this, 163);
    }
}
