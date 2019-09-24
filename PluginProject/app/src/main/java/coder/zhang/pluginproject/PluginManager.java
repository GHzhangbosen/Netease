package coder.zhang.pluginproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private static PluginManager pluginManager;
    private Context context;
    private DexClassLoader dexClassLoader; // 加载class文件(activity service ...)
    private Resources resources; // 加载layout

    private PluginManager(Context context) {
        this.context = context;
    }

    public static PluginManager getInstance(Context context) {
        if (pluginManager == null) {
            synchronized (PluginManager.class) {
                if (pluginManager == null) {
                    pluginManager = new PluginManager(context);
                }
            }
        }
        return pluginManager;
    }

    public void testLoadPlugin() {
        try {
            String dexPath = getPluginPath();
            String optimizedDir = context.getDir("pDir", Context.MODE_PRIVATE).getAbsolutePath();
            dexClassLoader = new DexClassLoader(dexPath, optimizedDir, null, context.getClassLoader());
        } catch (Exception e) {

        }
    }

    // 加载插件
    public void loadPlugin() {
        try {
            // 加载activity.class
            File file = new File(getPluginPath());
            if (!file.exists()) return;
            String pluginPath = file.getAbsolutePath();
            File fileDir = context.getDir("pDir", Context.MODE_PRIVATE);
            dexClassLoader = new DexClassLoader(pluginPath, fileDir.getAbsolutePath(), null, context.getClassLoader());

            // 加载layout
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, pluginPath);

            Resources r = context.getResources(); // 宿主的资源配置信息
            resources = new Resources(assetManager, r.getDisplayMetrics(), r.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getClassLoader() {
        return dexClassLoader;
    }

    public Resources getResources() {
        return resources;
    }

    public String getPluginPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "plugin_package-debug.apk";
    }

    public void parserApkAction() {

        try {
            // 1、获取AndroidManifest.xml对应的类： public Package parsePackage(File packageFile, int flags)
            Class<?> mPackageParserClass = Class.forName("android.content.pm.PackageParser");
            Method mParsePackageMethod = mPackageParserClass.getMethod("parsePackage", File.class, int.class);
            File file = new File(PluginManager.getInstance(context).getPluginPath());
            if (!file.exists()) return;
            Object mPackageParser = mPackageParserClass.newInstance();
            Object mPackage = mParsePackageMethod.invoke(mPackageParser, file, PackageManager.GET_ACTIVITIES);

            // 2、获取Package里的BroadcastReceivers对应的数组：public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
            Class<?> mPackageClsss = Class.forName("android.content.pm.PackageParser$Package");
            Field receiversField = mPackage.getClass().getField("receivers");
            ArrayList receivers = (ArrayList) receiversField.get(mPackage);
            if (receivers == null) return;

            // 3、遍历所有的BroadcastReceiver，获取每一个IntentFilter的action
            for (Object mActivity : receivers) { // <receiver android:name=".StaticReceiver">
                Class<?> mComponentClass = Class.forName("android.content.pm.PackageParser$Component"); // 换成Activity也可以
                Field intentsField = mComponentClass.getField("intents");
                ArrayList<IntentFilter> intentFilters = (ArrayList) intentsField.get(mActivity);
                if (intentFilters == null) return;

                // 4、 注册每一个广播接收者，需要获取广播接收者的全路径
                Class<?> mActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
                Field infoField = mActivityClass.getField("info");
                ActivityInfo mActivityInfo = (ActivityInfo) infoField.get(mActivity);
                String receiverClassName = mActivityInfo.name;
                Class<?> mStaticReceiverClass = getClassLoader().loadClass(receiverClassName);
                BroadcastReceiver mStaticReceiver = (BroadcastReceiver) mStaticReceiverClass.newInstance();

                for (IntentFilter intentFilter : intentFilters) {
                    context.registerReceiver(mStaticReceiver, intentFilter);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 反射系统源码，解析APK文件里的所有信息
    public void parserApkAction1() {
        try {
//        public Package parsePackage(File packageFile, int flags)
            Class<?> mPackageParserClass = Class.forName("android.content.pm.PackageParser");
            Method mPackageParserMethod = mPackageParserClass.getMethod("parsePackage", File.class, int.class);
            File file = new File(PluginManager.getInstance(context).getPluginPath());
            if (!file.exists()) return;
            Object mPackageParser = mPackageParserClass.newInstance();
            Object mPackage = mPackageParserMethod.invoke(mPackageParser, file, PackageManager.GET_ACTIVITIES);

            Field receiversField = mPackage.getClass().getDeclaredField("receivers");
            ArrayList receivers = (ArrayList) receiversField.get(mPackage); // AndroidManifest.xml中配置的<receiver>集合
            if (receivers == null) return;
            for (Object mActivity : receivers) { // mActivity -> <receiver android:name=".StaticReceiver">
//                public final ArrayList<II> intents; -> <intent-filter>
                Class<?> mComponentClass = Class.forName("android.content.pm.PackageParser$Component");
                Field intentsField = mComponentClass.getField("intents");
                ArrayList<IntentFilter> intentFilters = (ArrayList) intentsField.get(mActivity);
                if (intentFilters == null) return;
//              获取ActivityInfo ->  public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId)
                Class<?> mPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
                Method mGenerateActivityInfoMethod = mPackageParserClass.getMethod("generateActivityInfo", mActivity.getClass(), int.class, mPackageUserStateClass, int.class);
                Class<?> mUserHandleClass = Class.forName("android.os.UserHandle");
//              获取userId ->  public static @UserIdInt int getCallingUserId()
                Method mGetCallingUserIdMethod = mUserHandleClass.getMethod("getCallingUserId");
                int userId = (int) mGetCallingUserIdMethod.invoke(mUserHandleClass);
                ActivityInfo mActivityInfo = (ActivityInfo) mGenerateActivityInfoMethod.invoke(mPackageParserClass, mActivity, 0, mPackageUserStateClass.newInstance(), userId);
                String receiverClassName = mActivityInfo.name; // coder.zhang.plugin_package.StaticReceiver
                Class<?> mStaticReceiverClass = getClassLoader().loadClass(receiverClassName);
                BroadcastReceiver mStaticReceiver = (BroadcastReceiver) mStaticReceiverClass.newInstance();
                for (IntentFilter intentFilter : intentFilters) {
                    context.registerReceiver(mStaticReceiver, intentFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
