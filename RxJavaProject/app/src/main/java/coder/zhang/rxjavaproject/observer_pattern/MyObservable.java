package coder.zhang.rxjavaproject.observer_pattern;

public interface MyObservable {

    void registObserver(MyObserver observer);

    void unregistObserver(MyObserver observer);

    void notifyObserver();
}
