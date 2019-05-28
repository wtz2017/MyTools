package com.wtz.tools.test.aac.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.wtz.tools.test.aac.repository.local.CityDao;
import com.wtz.tools.test.aac.repository.local.MyDatabase;
import com.wtz.tools.test.aac.repository.local.entities.City;
import com.wtz.tools.test.aac.repository.remote.HttpManager;
import com.wtz.tools.test.aac.repository.remote.NetApi;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class DataRepository {
    private static final String TAG = "DataRepository";

    private NetApi mNetApi;
    private CityDao mCityDao;

    private volatile static DataRepository mInstance;

    public static DataRepository getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DataRepository.class) {
                if (mInstance == null)
                    mInstance = new DataRepository(context);
            }
        }
        return mInstance;
    }

    private DataRepository(Context context) {
        mCityDao = MyDatabase.getInstance(context).getCityDao();
        mNetApi = HttpManager.getInstance().getApi();
    }

    public LiveData<City> getCity(long id) {
        LiveData<City> cityLiveData = mCityDao.load(id);
        Log.d(TAG, "mCityDao.load(" + id + ")=" + cityLiveData);
//        if (cityLiveData == null) {
            // 测试发现：数据库里没有时返回的cityLiveData也不空
            loadCityData(id);
//        }
        return cityLiveData;
    }

    private void loadCityData(final long id) {
        mNetApi.getOuterNetIp()
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "loadCityData...onSubscribe");
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        String body = stringResponse.body().toString();
                        Log.d(TAG, "loadCityData...onNext:" + stringResponse + ";\nbody:" + body);
                        String jsonStr = body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1);
                        City city = (City) HttpManager.GSON.fromJson(jsonStr, City.class);
                        // Update the database.The LiveData will automatically refresh so
                        // we don't need to do anything else here besides updating the database
                        city.id = id;
                        mCityDao.save(city);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "loadCityData...onError:" + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loadCityData...onComplete");
                    }
                });
    }

    /**
     * 重点是实现 onError
     */
    public DefaultObserver<Object> getDefaultObserver() {
        return new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
    }

}
