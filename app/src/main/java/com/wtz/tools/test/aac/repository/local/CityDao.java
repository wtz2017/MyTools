package com.wtz.tools.test.aac.repository.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wtz.tools.test.aac.repository.local.entities.City;


@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(City user);

    @Query("SELECT * FROM table_city WHERE id = :id")
    LiveData<City> load(long id);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(List<MessageEntity> messages);
//
//    @Delete
//    void delete(List<MessageEntity> messages);
//
//    @Query("DELETE FROM tb_message WHERE time = :time")
//    void deleteByTime(long time);
//
//    @Query("DELETE FROM tb_message WHERE time < :timeline")
//    void deleteBeforeTimeline(long timeline);
//
//    @Query("DELETE FROM tb_message")
//    void deleteAll();
//
//    @Query("SELECT * FROM tb_message ORDER BY time DESC")
//    List<MessageEntity> getAll();
//
//    @Query("SELECT * FROM tb_message WHERE time BETWEEN :start AND :end ORDER BY time DESC")
//    List<MessageEntity> getBetweenTime(long start, long end);

}
