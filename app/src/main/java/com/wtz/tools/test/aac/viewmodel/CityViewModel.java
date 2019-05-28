package com.wtz.tools.test.aac.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.wtz.tools.test.aac.repository.DataRepository;
import com.wtz.tools.test.aac.repository.local.entities.City;

public class CityViewModel extends ViewModel {

    private LiveData<City> cityLiveData;

    public void init(Context context, long id) {
        if (this.cityLiveData != null) {
            // ViewModel is created per Fragment so
            // we know the id won't change
            return;
        }
        this.cityLiveData = DataRepository.getInstance(context).getCity(id);
    }

    public LiveData<City> getCity() {
        return this.cityLiveData;
    }
}
