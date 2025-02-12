package com.isl.audit.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {AuditModel.class},exportSchema = false,version = 1)
@TypeConverters({AuditTypeConverter.class,AuditAssetConverter.class,AssetListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"ITowerDatabase").
                    fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract AuditDao auditDao();
}