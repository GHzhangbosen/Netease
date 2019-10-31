package coder.zhang.skin_package;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import coder.zhang.skin_package.inflater.CustomAppCompatViewInflater;
import coder.zhang.skin_package.utils.ActionBarUtils;
import coder.zhang.skin_package.utils.NavigationUtils;
import coder.zhang.skin_package.utils.StatusBarUtils;
import coder.zhang.skin_package.view.SkinChange;

public class SkinActivity extends AppCompatActivity {

    private CustomAppCompatViewInflater mViewInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        if (openChangeSkin()) {
            if (mViewInflater == null) mViewInflater = new CustomAppCompatViewInflater(context);
            return mViewInflater.autoMatch(name, attrs);
        }
        return super.onCreateView(parent, name, context, attrs);
    }

    protected boolean openChangeSkin() {
        return false;
    }

    protected void setDayNightMode(@AppCompatDelegate.NightMode int uiMode) {
        getDelegate().setLocalNightMode(uiMode);

        final boolean isPost21 = Build.VERSION.SDK_INT >= 21;

        if (isPost21) {
            // 换状态栏
            StatusBarUtils.forStatusBar(this);
            // 换标题栏
            ActionBarUtils.forActionBar(this);
            // 换底部导航栏
            NavigationUtils.forNavigation(this);
        }

        View decorView = getWindow().getDecorView();
        applyDayNightForView(decorView);
    }

    protected void applyDayNightForView(View view) {
        if (view == null) return;
        if (view instanceof SkinChange) {
            ((SkinChange) view).changeSkin();
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0, size = vg.getChildCount(); i < size; i++) {
                applyDayNightForView(vg.getChildAt(i));
            }
        }
    }
}
