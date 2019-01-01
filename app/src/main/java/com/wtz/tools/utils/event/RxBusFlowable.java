package com.wtz.tools.utils.event;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * 做了背压处理
 * RXJava2.0 中 Observable 不再支持背压，多出了 Flowable 来支持背压操作
 */
public class RxBusFlowable {

    private volatile static RxBusFlowable INSTANCE = null;
    private final FlowableProcessor<Object> mBus;

    public static RxBusFlowable getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBusFlowable.class) {
                if (INSTANCE == null)
                    INSTANCE = new RxBusFlowable();
            }
        }
        return INSTANCE;
    }

    private RxBusFlowable() {
        mBus = PublishProcessor.create().toSerialized();
    }

    /**
     * 返回所有事件类型的被观察者
     *
     * @return
     */
    public Flowable<Object> toFlowable() {
        return mBus;
    }

    /**
     * 根据传入的事件类型，返回特定事件类型event的被观察者
     *
     * @param event
     * @param <T>
     * @return
     */
    public <T extends Object> Flowable<T> toFlowable(Class<T> event) {
        // ofType 操作符只发射指定类型的数据，其内部就是 filter+cast
        return mBus.ofType(event);
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void send(Object o) {
        if (mBus.hasSubscribers()) {
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
        return RxBusFlowable.getInstance().toFlowable(event)
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
        return RxBusFlowable.getInstance().toFlowable(event)
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
