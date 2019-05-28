package com.wtz.tools.test.aac.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.test.aac.repository.local.entities.City;
import com.wtz.tools.test.aac.viewmodel.CityViewModel;

public class CityIpActivity extends FragmentActivity {
    private static final String TAG = CityIpActivity.class.getSimpleName();

    private TextView mCityName;
    private TextView mIpName;

    private CityViewModel mCityViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_city_ip);

        mCityName = findViewById(R.id.tv_city);
        mIpName = findViewById(R.id.tv_ip);

        mCityViewModel = ViewModelProviders.of(this).get(CityViewModel.class);
        mCityViewModel.init(this, 1);

        LiveData<City> cityLiveData = mCityViewModel.getCity();
        Log.d(TAG, "cityLiveData = " + cityLiveData);
        if (cityLiveData != null) {
            cityLiveData.observe(this, new Observer<City>() {
                @Override
                public void onChanged(@Nullable City city) {
                    Log.d(TAG, "cityLiveData onChanged = " + city);
                    if (city != null) {
                        mCityName.setText(city.name);
                        mIpName.setText(city.ip);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

}
