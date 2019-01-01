package com.wtz.tools.utils.event;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 有异常处理能力的 Rxbus
 * 在订阅者处理事件出现异常后，订阅者无法再收到事件，这是 RxJava 当初本身的设计原则，
 * 但是在事件总线中这反而是个问题，不过 JakeWharton 实现了即使出现异常也不会终止订阅关系的 RxRelay
 * 所以基于 RxRelay 就能写出有异常处理能力的 Rxbus
 */
public class RxBusRelay {

    private volatile static RxBusRelay INSTANCE = null;
    private final Relay<Object> mBus;

    public static RxBusRelay getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBusRelay.class) {
                if (INSTANCE == null)
                    INSTANCE = new RxBusRelay();
            }
        }
        return INSTANCE;
    }

    private RxBusRelay() {
        // toSerialized 是为了保证线程安全
        mBus = PublishRelay.create().toSerialized();
    }

    /**
     * 返回所有事件类型的被观察者
     *
     * @return
     */
    public Observable<Object> toObservable() {
        return mBus;
    }

    /**
     * 根据传入的事件类型，返回特定事件类型event的被观察者
     *
     * @param event
     * @param <T>
     * @return
     */
    public <T extends Object> Observable<T> toObservable(Class<T> event) {
        // ofType 操作符只发射指定类型的数据，其内部就是 filter+cast
        return mBus.ofType(event);
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void send(Object o) {
        if (mBus.hasObservers()) {
            mBus.accept(o);
        }
    }

    /**
     * 注册事件，并在IO线程接收事件回调
     *
     * @param event
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T extends Object> Disposable registerOnIoThread(Class<T> event, Consumer<T> consumer) {
        return RxBusRelay.getInstance().toObservable(event)
                .observeOn(Schedulers.io())
                .subscribe(consumer);
    }

    /**
     * 注册事件，并在UI线程接收事件回调
     *
     * @param event
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T extends Object> Disposable registerOnMainThread(Class<T> event, Consumer<T> consumer) {
        return RxBusRelay.getInstance().toObservable(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 注销事件监听
     *
     * @param disposable
     */
    public static void unregister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
