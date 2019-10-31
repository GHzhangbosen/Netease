package coder.zhang.skinproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import coder.zhang.skin_package.SkinActivity;
import coder.zhang.skin_package.utils.PreferencesUtils;
import coder.zhang.skinproject.test.TestActivity;

public class MainActivity extends SkinActivity {

    private static final String ISNIGHT = "isnight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isNight = PreferencesUtils.getBoolean(this, ISNIGHT, false);
        getDelegate().setLocalNightMode(isNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void changeSkin(View view) {
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (uiMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                // 日间模式
                PreferencesUtils.putBoolean(this, ISNIGHT, false);
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                // 夜间模式
                PreferencesUtils.putBoolean(this, ISNIGHT, false);
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    @Override
    protected boolean openChangeSkin() {
        return true;
    }
}
