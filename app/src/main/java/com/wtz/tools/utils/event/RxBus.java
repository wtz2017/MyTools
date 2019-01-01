package com.wtz.tools.utils.event;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 未做背压处理
 */
public class RxBus {

    private volatile static RxBus INSTANCE = null;
    private final Subject<Object> mBus;

    public static RxBus getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBus.class) {
                if (INSTANCE == null)
                    INSTANCE = new RxBus();
            }
        }
        return INSTANCE;
    }

    private RxBus() {
        // Subject 是非线程安全的，要避免该问题，需要将 Subject 转换为 Serialized Subject
        // PublishSubject 只会把在订阅发生的时间点之后来自原始 Observable 的数据发射给观察者
        mBus = PublishSubject.create().toSerialized();
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
            mBus.onNext(o);
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
        return RxBus.getInstance().toObservable(event)
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
        return RxBus.getInstance().toObservable(event)
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
