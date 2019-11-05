package coder.zhang.arouter_api;

import android.app.Activity;
import android.util.LruCache;

import androidx.annotation.NonNull;

public class ParameterManager {

    // key: 类名
    private LruCache<String, LoadParameter>  cache;
    private static final String FILE_NAME_SUFFIX = "$$Parameter";

    private static volatile ParameterManager sInstance;
    private ParameterManager() {
        cache = new LruCache<>(163);
    }
    public static ParameterManager getInstance() {
        if (sInstance == null) {
            synchronized (ParameterManager.class) {
                if (sInstance == null) sInstance = new ParameterManager();
            }
        }
        return sInstance;
    }

    public void loadParameter(@NonNull Activity activity) {
        String className = activity.getClass().getName() + FILE_NAME_SUFFIX;
        LoadParameter iLoadParameter = cache.get(className);
        if (iLoadParameter == null) {
            try {
                Class<?> aClass = Class.forName(className);
                iLoadParameter = (LoadParameter) aClass.newInstance();
                cache.put(className, iLoadParameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        iLoadParameter.loadParameter(activity);
    }
}
