package coder.zhang.stander;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public interface ActivityInterface {

    /**
     * 把宿主(app)的环境给插件
     * @param app
     */
    void insertAppContext(AppCompatActivity app);

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onDestroy();
}
