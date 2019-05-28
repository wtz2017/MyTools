package com.wtz.tools.test.aac.repository.local.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "table_city")
public class City {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;

    @SerializedName("cip")
    @ColumnInfo(name = "ip")
    public String ip;

    @SerializedName("cname")
    @ColumnInfo(name = "name")
    public String name;

    @Override
    public String toString() {
        return "id:" + id + ";ip:" + ip + ";name:" + name;
    }

}
