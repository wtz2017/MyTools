package com.wtz.tools.test.aac.repository.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wtz.tools.test.aac.repository.local.entities.City;

@Database(entities = {City.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "my_data_base";

    public abstract CityDao getCityDao();

    private static volatile MyDatabase INSTANCE;

    public static MyDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

}
