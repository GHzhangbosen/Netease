package coder.zhang.skin_library;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;

import coder.zhang.skin_library.utils.L;

public class SkinManager {

    private static SkinManager sInstance;
    private SkinManager(Application application) {
        this.application = application;
        appResources = application.getResources();
    }

    public static void init(Application application) {
        if (sInstance == null) {
            synchronized (SkinManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinManager(application);
                }
            }
        }
    }
    public static SkinManager getInstance() {
        return sInstance;
    }

    public void setDefaultSkin(boolean isDefaultSkin) {
        this.isDefaultSkin = isDefaultSkin;
    }

    public boolean isDefaultSkin() {
        return isDefaultSkin;
    }

    public String getSkinPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "net163.skin";
    }

    private boolean isLoaded;
    private boolean isDefaultSkin;
    private Application application;
    private Resources appResources;
    private Resources skinResources;
    private String skinPackageName;

    public void loadSkinResources(String skinPath) {
        if (isLoaded) return;
        if (TextUtils.isEmpty(skinPath)) return;

        try {
            Class assetManagerClass = Class.forName("android.content.res.AssetManager");
//            Class assetManagerClass = AssetManager.class;
            AssetManager assetManager = (AssetManager) assetManagerClass.newInstance();
            Method addAssetPathMethod = assetManagerClass.getMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, skinPath);

            skinResources = new Resources(assetManager, appResources.getDisplayMetrics(), appResources.getConfiguration());
            PackageManager packageManager = application.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
            skinPackageName = packageInfo.packageName;

            isLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过内置资源id，查找到皮肤包对应的资源id
     * @param appResourceId
     * @return
     */
    private int getSkinResourceId(int appResourceId) {

        String resourceEntryName = appResources.getResourceEntryName(appResourceId);
        String resourceTypeName = appResources.getResourceTypeName(appResourceId);
//        L.d("resource entry name: " + resourceEntryName + ", resource type name: " + resourceTypeName);

        int skinResourceId = skinResources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        return skinResourceId <= 0 ? appResourceId : skinResourceId;
    }

    public int getColor(int appResourceId) {
        int skinResourceId = getSkinResourceId(appResourceId);
        return isDefaultSkin ? appResources.getColor(appResourceId) : skinResources.getColor(skinResourceId);
    }

    public ColorStateList getColorStateList(int appResourceId) {
        int skinResourceId = getSkinResourceId(appResourceId);
        return isDefaultSkin ? appResources.getColorStateList(appResourceId) : skinResources.getColorStateList(skinResourceId);
    }

    public String getString(int appResourceId) {
        int skinResourceId = getSkinResourceId(appResourceId);
        return isDefaultSkin ? appResources.getString(appResourceId) : skinResources.getString(skinResourceId);
    }

    public Drawable getDrawableOrMipmap(int appResourceId) {
        int skinResourceId = getSkinResourceId(appResourceId);
        return isDefaultSkin ? appResources.getDrawable(appResourceId) : skinResources.getDrawable(skinResourceId);
    }

    // 返回值比较特殊: color/drawable/mipmap
    public Object getBackgroundOrSrc(int appResourceId) {
        int skinResourceId = getSkinResourceId(appResourceId);

        String resourceTypeName = appResources.getResourceTypeName(appResourceId);
        switch (resourceTypeName) {
            case "color":
                return getColor(appResourceId);

            case "mipmap":
            case "drawable":
                return getDrawableOrMipmap(appResourceId);
        }
        return null;
    }

    public Typeface getTypeface(int appResourceId) {
        // 通过资源id获取资源path。参考: resources.arsc资源映射表
        String skinTypefacePath = getString(appResourceId);
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT;
        return isDefaultSkin ? Typeface.createFromAsset(appResources.getAssets(), skinTypefacePath)
                : Typeface.createFromAsset(skinResources.getAssets(), skinTypefacePath);
    }
}
