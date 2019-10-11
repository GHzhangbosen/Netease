package coder.zhang.rxjavaproject.observer_pattern;

import java.util.ArrayList;
import java.util.List;

public class MyObservableImpl implements MyObservable {

    private List<MyObserver> mObservers = new ArrayList<>();

    @Override
    public void registObserver(MyObserver observer) {
        if (!mObservers.contains(observer)) mObservers.add(observer);
    }

    @Override
    public void unregistObserver(MyObserver observer) {
        if (mObservers.contains(observer)) mObservers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        if (mObservers.isEmpty()) return;
        for (MyObserver observer : mObservers) {
            observer.onChange("被观察者发生了改变...");
        }
    }
}
