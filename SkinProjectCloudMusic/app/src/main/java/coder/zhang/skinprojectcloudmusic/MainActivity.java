package coder.zhang.skinprojectcloudmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import coder.zhang.skin_library.SkinActivity;
import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.utils.L;

public class MainActivity extends SkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(perms, 200);
    }

    public void skinDynamic(View view) {
        long startTime = System.currentTimeMillis();
        SkinManager.getInstance().setDefaultSkin(false);
        skinDynamic(R.color.skin_item_color);
        long endTime = System.currentTimeMillis();
        L.d("切换皮肤包耗时: " + (endTime - startTime) + "ms");
    }

    public void skinDefault(View view) {
        long startTime = System.currentTimeMillis();
        SkinManager.getInstance().setDefaultSkin(true);
        defaultSkin(R.color.colorPrimary);
        long endTime = System.currentTimeMillis();
        L.d("恢复默认皮肤耗时: " + (endTime - startTime) + "ms");
    }

    public void jumpSelf(View view) {
        startActivity(new Intent(this, getClass()));
    }

    @Override
    protected boolean openSkinView() {
        return true;
    }
}
