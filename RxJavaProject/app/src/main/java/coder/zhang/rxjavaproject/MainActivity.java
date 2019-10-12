package coder.zhang.rxjavaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.TimeUnit;

import coder.zhang.rxjavaproject.observer_pattern.MyObservable;
import coder.zhang.rxjavaproject.observer_pattern.MyObservableImpl;
import coder.zhang.rxjavaproject.observer_pattern.MyObserver;
import coder.zhang.rxjavaproject.observer_pattern.MyObserverImpl;
import coder.zhang.rxjavaproject.retrofit_rxjava.RetrofitRxJavaActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollView scrollView = findViewById(R.id.scrollview);
        LinearLayout linearLayout = findViewById(R.id.linearlayout);
    }

    public void myRxjava(View view) {
        MyObserver observer01 = new MyObserverImpl();
        MyObserver observer02 = new MyObserverImpl();
        MyObserver observer03 = new MyObserverImpl();
        MyObserver observer04 = new MyObserverImpl();
        MyObserver observer05 = new MyObserverImpl();

        MyObservable observable = new MyObservableImpl();
        observable.registObserver(observer01);
        observable.registObserver(observer02);
        observable.registObserver(observer03);
        observable.registObserver(observer04);
        observable.registObserver(observer05);

        observable.notifyObserver();
    }

    public void subscribe(View view) {
        // 上游 被观察者 事件起源
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                log("上游 发射事件");
                emitter.onNext(1);
                log("上游 发射完成");
            }
        });

        // 下游 观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                log("下游 接收处理 onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        // 注册/订阅
        observable.subscribe(observer);
    }

    private void log(String msg) {
        Log.d("succ2", msg);
    }

    public void subscribe02(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                log("上游 开始发射");
                emitter.onNext("RxJava");
                emitter.onNext("RxJava02");
                emitter.onNext("RxJava03");

//                emitter.onError(new Throwable(""));

                emitter.onComplete();

                emitter.onNext("RxJava04");
                log("上游 发射完成");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("上下游订阅成功");
            }

            @Override
            public void onNext(String s) {
                log("下游接收成功: " + s);
            }

            @Override
            public void onError(Throwable e) {
                log("下游接收失败: " + e);
            }

            @Override
            public void onComplete() {
                log("下游接收完毕");
            }
        });
    }

    private Disposable disposable;

    // 切断下游
    public void subscribe03(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                log("下游接收: " + integer);
                disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void operator_just(View view) {
        Observable.just("A", "B", 1, 2)
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        log(o.toString());
                        disposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void operator_fromArray(View view) {
        String[] strings = {"A", "B", "C"};
        Integer[] integers = {1, 2, 3};
        Observable.fromArray(strings, integers)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object s) {
                        log(s.toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void operator_empty(View view) {
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        log("下游接收事件");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        log("下游接收完毕");
                    }
                });
    }

    public void operator_range(View view) {
        Observable.range(1, 3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("下游接收事件: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void operator_map(View view) {
        Observable.just(1)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer + 1;
                    }
                })
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        log("再次变换: " + integer);
                        return integer * 2;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("下游接收成功: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void operator_flatMap(View view) {
        Observable.just("雄霸", "步惊云", "聂风")
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String string) throws Exception {
                        return Observable.just("新的被观察者: " + string).delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        log(string);
                    }
                });
    }

    public void operator_concatMap(View view) {
        Observable.just("雄霸", "步惊云", "聂风")
                .concatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return Observable.just("新的被观察者: " + s).delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        log(s);
                    }
                });
    }

    public void operator_groupBy(View view) {
        Observable.just(6000, 7000, 8000, 9000)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer > 6000 ? "高端" : "普通";
                    }
                }).subscribe(new Consumer<GroupedObservable<String, Integer>>() {
            @Override
            public void accept(final GroupedObservable<String, Integer> groupedObservable) throws Exception {
                groupedObservable.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        log(groupedObservable.getKey() + ": " + integer);
                    }
                });
            }
        });
    }

    public void operator_buffer(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).buffer(20).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                log("下游接收事件: " + integers);
            }
        });
    }

    public void operator_filter(View view) {
        Observable.just("蒙牛", "伊利")
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return "蒙牛".equals(s);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        log(s);
                    }
                });
    }

    public void operator_take(View view) {
        Observable.interval(2, TimeUnit.SECONDS)
                .take(3)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        log("下游接收事件: " + aLong);
                    }
                });
    }

    public void operator_distinct(View view) {
        Observable.just(0, 0, 1, 2, 3, 3)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        log("下游接收事件: " + integer);
                    }
                });
    }

    public void operator_elementAt(View view) {
        Observable.just("A", "B", "C")
                .elementAt(0)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        log(s);
                    }
                });
    }

    public void operator_all(View view) {
        String s1 = "A";
        String s2 = "B";
        String s3 = "C";
        String s = "cc";
        Observable.just(s1, s2, s3, s)
                .all(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !"cc".equals(s);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        log("是否不包含cc: " + aBoolean);
                    }
                });
    }

    public void operator_contains(View view) {
        String s1 = "A";
        String s2 = "B";
        String s3 = "C";
        String s = "cc";
        Observable.just(s1, s2, s3, s)
                .contains("CC")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        log("是否包含A: " + aBoolean);
                    }
                });
    }

    public void operator_any(View view) {
        String s1 = "A";
        String s2 = "B";
        String s3 = "C";
        String s = "cc";
        Observable.just(s1, s2, s3, s)
                .any(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return "cc".equals(s);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        log("是否包含cc: " + aBoolean);
                    }
                });
    }

    public void operator_startWith(View view) {
        Observable.just(1, 2, 3)
                .startWith(Observable.just(10, 20, 30))
                .startWith(Observable.just(100, 200, 300))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        log("下游接收事件: " + integer);
                    }
                });
    }

    public void operator_concatWith(View view) {
        Observable.just(1, 2, 3)
                .concatWith(Observable.just(10, 20, 30))
                .concatWith(Observable.just(100, 200, 300))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        log("下游接收事件: " + integer);
                    }
                });
    }

    public void operator_concat(View view) {
        Observable.concat(
                Observable.just(1, 2, 3),
                Observable.just(10, 20, 30),
                Observable.just(100, 200, 300)
        ).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                log("下游接收事件: " + integer);
            }
        });
    }

    public void operator_merge(View view) {
        Observable.merge(
                Observable.intervalRange(1, 5, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(6, 5, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(10, 5, 1, 1, TimeUnit.SECONDS)
        ).subscribe(new Consumer<Long>() {

            @Override
            public void accept(Long aLong) throws Exception {
                log("下游接收事件: " + aLong);
            }
        });
    }

    public void operator_zip(View view) {
        Observable.zip(
                Observable.just("语文", "数学", "英语"),
                Observable.just(100, 100, 99), new BiFunction<String, Integer, String>() {
                    @Override
                    public String apply(String s, Integer integer) throws Exception {
                        return new StringBuilder().append(s + ": " + integer).toString();
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                log("收到成绩表格: " + s);
            }
        });
    }

    public void operator_onErrorReturn(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    if (i == 4) {
//                        throw new IllegalAccessError("报错了");
                        e.onError(new IllegalAccessError("报错了"));
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                log("RxJava捕获到报错: " + throwable.getMessage());
                return 404;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("订阅完成");
            }

            @Override
            public void onNext(Integer integer) {
                log("下游接收事件: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                log("下游接收报错: " + e);
            }

            @Override
            public void onComplete() {
                log("下游接收完成");
            }
        });
    }

    public void operator_onErrorResumeNext(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    if (i == 4) {
                        throw new IllegalAccessException("报错了");
//                        e.onError(new IllegalAccessError("报错了"));
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                log("RxJava捕获到报错: " + throwable.getMessage());
                return Observable.just(500);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("订阅完成");
            }

            @Override
            public void onNext(Integer integer) {
                log("下游接收事件: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                log("下游接收报错: " + e);
            }

            @Override
            public void onComplete() {
                log("下游接收完成");
            }
        });
    }

    public void operator_onExceptionResumeNext(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    if (i == 4) {
                        e.onError(new IllegalAccessException("报错了"));
//                        e.onError(new IllegalAccessError("报错了"));
//                        new IllegalAccessError("报错了");
//                        new IllegalAccessException("报错了");
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).onExceptionResumeNext(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                log("RxJava捕获到报错");
                observer.onNext(404);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("订阅完成");
            }

            @Override
            public void onNext(Integer integer) {
                log("下游接收事件: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                log("下游接收报错: " + e);
            }

            @Override
            public void onComplete() {
                log("下游接收完成");
            }
        });
    }

    public void operator_retry(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    if (i == 4) {
                        e.onError(new IllegalAccessException("报错了"));
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).retry(2, new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                log("RxJava捕获到异常: " + throwable);
                return true;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("订阅完成");
            }

            @Override
            public void onNext(Integer integer) {
                log("下游接收事件: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                log("下游接收报错: " + e);
            }

            @Override
            public void onComplete() {
                log("下游接收完成");
            }
        });
    }

    public void thread(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
                log("上游线程: " + Thread.currentThread().getName());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        log("下游线程: " + Thread.currentThread().getName());
                    }
                });
    }

    private Subscription subscription;
    public void flowable(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(100);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        SystemClock.sleep(100);
                        log("下游接收事件: " + integer);
//                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        log("下游接收报错: " + t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void RetrofitRxJava(View view) {
        startActivity(new Intent(this, RetrofitRxJavaActivity.class));
    }
}
