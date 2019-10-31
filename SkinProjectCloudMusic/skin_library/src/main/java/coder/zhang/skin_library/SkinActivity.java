package coder.zhang.skin_library;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import coder.zhang.skin_library.inflater.CustomAppCompatViewInflater;
import coder.zhang.skin_library.utils.ActionBarUtils;
import coder.zhang.skin_library.utils.NavigationUtils;
import coder.zhang.skin_library.utils.StatusBarUtils;
import coder.zhang.skin_library.view.ViewMatch;

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
        if (openSkinView()) {
            if (mViewInflater == null) mViewInflater = new CustomAppCompatViewInflater(context);
            return mViewInflater.autoCreateView(name, attrs);
        }
        return super.onCreateView(parent, name, context, attrs);
    }

    protected boolean openSkinView() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void defaultSkin(int themeColorId) {
        skinDynamic(themeColorId);
    }

    /**
     * 动态换肤（api限制：5.0版本）
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void skinDynamic(int themeColorId) {
        SkinManager skinManager = SkinManager.getInstance();
        skinManager.loadSkinResources(skinManager.getSkinPath());
        if (themeColorId != 0) {
            int themeColor = skinManager.getColor(themeColorId);
            StatusBarUtils.forStatusBar(this, themeColor);
            NavigationUtils.forNavigation(this, themeColor);
            ActionBarUtils.forActionBar(this, themeColor);
        }

        applyViews(getWindow().getDecorView());
    }

    private void applyViews(View decorView) {
        if (decorView == null) return;

        if (decorView instanceof ViewMatch) {
            ((ViewMatch) decorView).skinnableView();
        }

        if (decorView instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) decorView;
            for (int i = 0, size = vg.getChildCount(); i < size; i++) {
                applyViews(vg.getChildAt(i));
            }
        }
    }
}
