package coder.zhang.rxjavaproject.observer_pattern;

import android.util.Log;

public class MyObserverImpl implements MyObserver {

    @Override
    public void onChange(String type) {
        Log.d("succ2", hashCode() + ": " + type);
    }
}
