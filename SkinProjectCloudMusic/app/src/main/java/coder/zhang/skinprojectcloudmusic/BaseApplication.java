package coder.zhang.skinprojectcloudmusic;

import android.app.Application;

import coder.zhang.skin_library.SkinManager;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
