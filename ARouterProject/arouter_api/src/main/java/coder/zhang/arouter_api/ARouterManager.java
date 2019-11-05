package coder.zhang.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import coder.zhang.arouter.bean.RouterBean;

import static coder.zhang.arouter.bean.RouterBean.Type.ACTIVITY;

public class ARouterManager {

    private static volatile ARouterManager sInstance;
    private ARouterManager() {
        groupCache = new LruCache<>(163);
        pathCache = new LruCache<>(163);
    }
    public static ARouterManager getInstance() {
        if (sInstance == null) {
            synchronized (ARouterManager.class) {
                if (sInstance == null) sInstance = new ARouterManager();
            }
        }
        return sInstance;
    }

    private String group;
    private String path;
    // key: 组名
    private LruCache<String, ARouterLoadGroup> groupCache;
    // key: 路径名称
    private LruCache<String, ARouterLoadPath> pathCache;
    private static final String FILE_NAME_PREFIX_GROUP = "ARouter$$Group$$";
    private static final String FILE_NAME_PREFIX_PATH = "ARouter$$Path$$";

    // path: 路由地址
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("路径配置错误，如: /app/MainActivity");
        }

        // 错误配置，如"/MainActivity"
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("路径配置错误，如: /app/MainActivity");
        }

        // 错误配置，如"/app/a/MainActivity"
        String substring = path.substring(1, path.lastIndexOf("/"));
        if (substring.contains("/")) {
            throw new IllegalArgumentException("路径配置错误，如: /app/MainActivity");
        }
        group = path.substring(1, path.lastIndexOf("/"));
        this.path = path;
        return new BundleManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context, BundleManager bundleManager, int code) {
        String groupClassName = context.getPackageName() + ".apt." + FILE_NAME_PREFIX_GROUP + group;
        Log.d("succ2", "group: " + groupClassName);
        ARouterLoadGroup groupLoad = groupCache.get(group);
        try {
            if (groupLoad == null) {
                Class<?> aClass = Class.forName(groupClassName);
                groupLoad = (ARouterLoadGroup) aClass.newInstance();
                groupCache.put(group, groupLoad);
            }

            if (groupLoad.loadGroup() == null || groupLoad.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表加载失败");
            }

            ARouterLoadPath pathLoad = pathCache.get(path);
            if (pathLoad == null) {
                Class<? extends ARouterLoadPath> aClass = groupLoad.loadGroup().get(group);
                if (aClass == null) return null;
                pathLoad = aClass.newInstance();
                pathCache.put(path, pathLoad);
            }
            Map<String, RouterBean> paths = pathLoad.loadPath();
            if (paths == null || paths.isEmpty()) {
                throw new RuntimeException("");
            }
            RouterBean routerBean = paths.get(path);
            if (routerBean == null) return null;

            switch (routerBean.getType()) {
                case ACTIVITY:
                    Intent intent = new Intent(context, routerBean.getClazz());
                    intent.putExtras(bundleManager.getBundle());
                    AppCompatActivity activity = (AppCompatActivity) context;
//                    if (bundleManager.isResult()) {
//                        (activity).setResult(code, intent);
//                        activity.finish();
//                    }
                    if (code > 0) {
                        activity.startActivityForResult(intent, code, bundleManager.getBundle());
                    } else {
                        activity.startActivity(intent, bundleManager.getBundle());
                    }
                    break;

                case CALL:
                    return routerBean.getClazz().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
