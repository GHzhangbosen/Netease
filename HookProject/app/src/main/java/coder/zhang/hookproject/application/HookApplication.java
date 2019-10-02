package coder.zhang.hookproject.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import coder.zhang.hookproject.ProxyActivity;
import coder.zhang.hookproject.classloader.PluginClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class HookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 如果本项目的TestActivity没有在AndroidManifest.xml里注册，那么如何既不让其报错，又能执行跳转？
        hookAMSAtion();
//        hookLaunchActivity();

        // 将宿主和插件的dexElements合并成一个全新的dexElementsNew，然后用宿主的LoadedApk(里面有一个mClassLoader的成员变量)去加载dexElementsNew
//        pluginToAppAction();

        // 定义一个单独的LoadedApk，专门用来加载插件。这样的话，加载宿主和加载插件的LoadedApk就可以区分开来了
        customLoadedApkAction();
    }

    private void hookAMSAtion() {
        try {
            // 1、获取IActivityManagerSingleton里的mInstance字段
            Class<?> mSingletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true); // 让jvm忽略权限修饰符

            // 2、将其替换成自己的代理对象
            // 获取ActivityManager对象里的IActivityManagerSingleton
            Class<?> mActivityManagerClass = Class.forName("android.app.ActivityManager");
            Field mIActivityManageSingletonField = mActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
            mIActivityManageSingletonField.setAccessible(true);
            Object mIActivityManagerOri = mIActivityManageSingletonField.get(null);// 获取真实对象，因为是静态方法，所以参数可以传空

            Method mGetServiceMethod = mActivityManagerClass.getMethod("getService");
            final Object mActivityManagerObject = mGetServiceMethod.invoke(null);

            // 创建代理对象
            Class<?> mIActivityManagerClass = Class.forName("android.app.IActivityManager");
            Object mIActivityManagerProxy = Proxy.newProxyInstance(HookApplication.class.getClassLoader(),
                    new Class[]{mIActivityManagerClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Log.d("succ2", "method name: " + method.getName());
                            if ("startActivity".equals(method.getName())) {
                                Intent intent = new Intent(HookApplication.this, ProxyActivity.class);
                                intent.putExtra("actionIntent", (Intent) args[2]);
                                args[2] = intent;
                            }
                            // 需要获取IActivityManager对象，所以需要执行IActivityManagerSingleton.get()方法
                            return method.invoke(mActivityManagerObject, args);
                        }
                    });
            mInstanceField.set(mIActivityManagerOri, mIActivityManagerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hookLaunchActivity() {
        // 在ActivityThread执行Handler handleMessage之前，将ProxyActivity替换回原来的TestActivity
        try {
            // 1、获取Handler mCallback，并替换成自己的
            Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
            Field mHField = mActivityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);

            Method mCurrentActivityThreadMethod = mActivityThreadClass.getMethod("currentActivityThread");
            Object mActivityTread = mCurrentActivityThreadMethod.invoke(null);
            Handler mH = (Handler) mHField.get(mActivityTread);

            Class<?> mHandlerClass = Class.forName("android.os.Handler");
            Field mCallbackField = mHandlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH, new MyCallback(mH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int RELAUNCH_ACTIVITY = 160;

    private class MyCallback implements Handler.Callback {

        private Handler mH;

        public MyCallback(Handler h) {
            mH = h;
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.d("succ2", "handleMessage: " + msg.what);
            switch (msg.what) {
                case RELAUNCH_ACTIVITY:
                    try {

                        Object obj = msg.obj; // ActivityClientRecord类型

                        Field mIntentField = obj.getClass().getDeclaredField("intent");
                        mIntentField.setAccessible(true);
                        Intent intent = (Intent) mIntentField.get(obj);
                        Intent actionIntent = intent.getParcelableExtra("actionIntent");
                        if (actionIntent == null) return false;

                        mIntentField.set(obj, actionIntent);

                        // 解决问题：PackageManager会自动检测intent对应的包名是否安装，如果未安装，则报错
                        Class<?> mActivityClientRecordClass = Class.forName("android.app.ActivityThread$ActivityClientRecord");
                        Field mActivityInfoField = mActivityClientRecordClass.getDeclaredField("activityInfo");
                        mActivityInfoField.setAccessible(true);
                        ActivityInfo activityInfo = (ActivityInfo) mActivityInfoField.get(obj);

                        if (actionIntent.getPackage() == null) {
                            // 当前即将加载的是插件
                            activityInfo.applicationInfo.packageName = actionIntent.getComponent().getPackageName();

                            // Hook getPackageInfo()方法
                            hookGetPackageInfoAction();
                        } else {
                            activityInfo.applicationInfo.packageName = actionIntent.getPackage();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            mH.handleMessage(msg);
            return true;
        }
    }

    private void pluginToAppAction() {
        try {
            // 1、找到宿主的dexElements
            Class<?> mBaseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field mPathListField = mBaseDexClassLoaderClass.getDeclaredField("pathList");
            mPathListField.setAccessible(true);
            ClassLoader mClassLoader = getClassLoader();
            Object mPathList = mPathListField.get(mClassLoader); // private final DexPathList pathList;

            Class<?> mDexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field mDexElementsField = mDexPathListClass.getDeclaredField("dexElements");
            mDexElementsField.setAccessible(true);
            Object mDexElements = mDexElementsField.get(mPathList); // private Element[] dexElements;

            // 2、找到插件的dexElements
            Class<?> mBaseDexClassLoaderPluginClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field mPathListPluginField = mBaseDexClassLoaderPluginClass.getDeclaredField("pathList");
            mPathListPluginField.setAccessible(true);
            String dexPath = getPluginPath();
            File file = new File(dexPath);
            if (!file.exists()) return;
            String optimizedDir = getDir("pDir", MODE_PRIVATE).getAbsolutePath();
            DexClassLoader mClassLoaderPlugin = new DexClassLoader(dexPath, optimizedDir, null, mClassLoader);
            Object mPathListPlugin = mPathListPluginField.get(mClassLoaderPlugin); // private final DexPathList pathList;

            Class<?> mDexPathListPluginClass = Class.forName("dalvik.system.DexPathList");
            Field mDexElementsPluginField = mDexPathListPluginClass.getDeclaredField("dexElements");
            mDexElementsPluginField.setAccessible(true);
            Object mDexElementsPlugin = mDexElementsPluginField.get(mPathListPlugin); // private Element[] dexElements;

            // 3、创建新的dexElements
            int dexLength = Array.getLength(mDexElements);
            int dexLengthPlugin = Array.getLength(mDexElementsPlugin);
            int dexLengthNew = dexLength + dexLengthPlugin;
            Object mDexElementsNew = Array.newInstance(mDexElements.getClass().getComponentType(), dexLengthNew);

            // 4、得到newDexElements
            for (int i = 0; i < dexLengthNew; i++) {
                if (i < dexLength) {
                    Array.set(mDexElementsNew, i, Array.get(mDexElements, i));
                } else {
                    Array.set(mDexElementsNew, i, Array.get(mDexElementsPlugin, i - dexLength));
                }
            }

            // 5、设置newDexElements
            mDexElementsField.set(mPathList, mDexElementsNew);

            loadPluginLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Resources resources;
    private AssetManager assetManager;
    // 加载插件里的layout
    private void loadPluginLayout() {
        try {
            Resources r = getResources();

            assetManager = AssetManager.class.newInstance();
            Method mAddAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            mAddAssetPathMethod.invoke(assetManager, getPluginPath());

            Class<?> mApkAssetsClass = Class.forName("android.content.res.ApkAssets");
            Method mLoadFromPathMethod = mApkAssetsClass.getMethod("loadFromPath", String.class, boolean.class);
            mLoadFromPathMethod.invoke(null, getPluginPath(), false);

            resources = new Resources(assetManager, r.getDisplayMetrics(), r.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPluginPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "hook-plugin-debug.apk";
    }

    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }

    public AssetManager getAssetManager() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    private void customLoadedApkAction() {
        try {
            // 1、获取mPackages对象
            Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
            Method mCurrentActivityThreadMethod = mActivityThreadClass.getMethod("currentActivityThread");
            Object mActivithThread = mCurrentActivityThreadMethod.invoke(null); // ActivityThread对象

            Field mPackagesField = mActivityThreadClass.getDeclaredField("mPackages");
            mPackagesField.setAccessible(true);
            Map mPackages = (Map) mPackagesField.get(mActivithThread); // ArrayMap<String, WeakReference<LoadedApk>>对象

            // 2、创建自定义的LoadedApk，专门用来加载插件
            // 模仿系统方法 public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo)
            Class<?> mCompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
            Method mLoadedApkMethod = mActivityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, mCompatibilityInfoClass);
            mLoadedApkMethod.setAccessible(true);
            Field defaultCompatibilityField = mCompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
            defaultCompatibilityField.setAccessible(true);
            Object mDefaultCompatibilityInfo = defaultCompatibilityField.get(null);
            ApplicationInfo applicationInfoAction = getApplicationInfoAction();
            Object mLoadedApk = mLoadedApkMethod.invoke(mActivithThread, applicationInfoAction, mDefaultCompatibilityInfo);

            // 3、为该LoadedApk对象里的mClassLoader赋值
            Field mClassLoaderField = mLoadedApk.getClass().getDeclaredField("mClassLoader");
            mClassLoaderField.setAccessible(true);
            DexClassLoader pluginClassLoader = new PluginClassLoader(getPluginPath(), getDir("pDir", MODE_PRIVATE).getAbsolutePath(), null, getClassLoader());
            mClassLoaderField.set(mLoadedApk, pluginClassLoader);

            // 4、将上述LoadedApk添加到mPackages里
            WeakReference weakReference = new WeakReference(mLoadedApk);
            mPackages.put(applicationInfoAction.packageName, weakReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApplicationInfo getApplicationInfoAction() throws Exception {
        // 模仿系统方法 PackageParser -> public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state)
        Class<?> mPackageParserClass = Class.forName("android.content.pm.PackageParser");
        Object mPackageParser = mPackageParserClass.newInstance();
        Class<?> m$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Class<?> mPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Method mGenerateApplicationInfoMethod = mPackageParserClass.getMethod("generateApplicationInfo", m$PackageClass, int.class, mPackageUserStateClass);
        // 获取Package public Package parsePackage(File packageFile, int flags)
        Method mParsePackageMethod = mPackageParserClass.getMethod("parsePackage", File.class, int.class);
        String pluginPath = getPluginPath();
        File plugin = new File(pluginPath);
        if (!plugin.exists()) throw new RuntimeException("插件包不存在");
        Object mPackage = mParsePackageMethod.invoke(mPackageParser, plugin, PackageManager.GET_ACTIVITIES);

        // TODO
        ApplicationInfo applicationInfo = (ApplicationInfo) mGenerateApplicationInfoMethod.invoke(mPackageParser, mPackage, 0, mPackageUserStateClass.newInstance());

        // 此时该Application对象里的sourceDir、publicSourceDir是没有值的
        applicationInfo.sourceDir = pluginPath;
        applicationInfo.publicSourceDir = pluginPath;
        return applicationInfo;
    }

    private void hookGetPackageInfoAction() {
        try {
            // 1、拿到ActivityThread里的sPackageManager字段
            Class<?> mActivityThreadClass = Class.forName("android.app.ActivityThread");
            Field mPackageManagerField = mActivityThreadClass.getDeclaredField("sPackageManager");
            mPackageManagerField.setAccessible(true);

            Method mCurrentActivityThreadMethod = mActivityThreadClass.getMethod("currentActivityThread");
            Object mActivityThread = mCurrentActivityThreadMethod.invoke(null);
            final Object mPackageManagerObj = mPackageManagerField.get(null);

            // 2、替换sPackageManager
            Class<?> mPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
            Object mPackageManagerProxy = Proxy.newProxyInstance(getClassLoader(), new Class[]{mPackageManagerClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("getPackageInfo".equals(method.getName())) {
                        return new PackageInfo();
                    }
                    return method.invoke(mPackageManagerObj, args);
                }
            });
            mPackageManagerField.set(null, mPackageManagerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
