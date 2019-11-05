package coder.zhang.arouter_api;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class BundleManager {

    private Bundle bundle = new Bundle();
    private boolean isResult;

    public Bundle getBundle() {
        return bundle;
    }

    public boolean isResult() {
        return isResult;
    }

    public BundleManager withInt(@NonNull String key, @NonNull int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withIntResult(@NonNull String key, @NonNull int value) {
        bundle.putInt(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withString(@NonNull String key, @NonNull String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withStringResult(@NonNull String key, @NonNull String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, @NonNull Boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withBooleanResult(@NonNull String key, @NonNull Boolean value) {
        bundle.putBoolean(key, value);
        isResult = true;
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context) {
        return navigation(context, -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context, int code) {
        return ARouterManager.getInstance().navigation(context, this, code);
    }
}
