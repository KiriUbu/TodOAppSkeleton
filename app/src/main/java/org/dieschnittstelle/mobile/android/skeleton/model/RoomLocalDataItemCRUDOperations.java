package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

public class RoomLocalDataItemCRUDOperations implements IDataItemCRUDOperations{

    @Dao
    public interface DataItemDao{
        @Query("select * from DataItem")
        public List<DataItem> readAll();
        @Query("select * from DataItem where id == (:id)")
        public DataItem readById(long id);
        @Insert
        public long create(DataItem item);
        @Update
        public void update(DataItem item);
        @Delete
        public void delete(DataItem item);
    }

    @Database(entities = {DataItem.class}, version = 1)
    public static abstract class DataItemDatabase extends RoomDatabase {
        public abstract DataItemDao getDao();
    }

    private DataItemDatabase db;
    public RoomLocalDataItemCRUDOperations(Context context){
        db = Room.databaseBuilder(context, DataItemDatabase.class,"dataItem.db").build();
        Log.i("RoomLocalDataItemCRUDOperations"," db "+ db);
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        long id = db.getDao().create(item);
        item.setId(id);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return db.getDao().readAll();
    }

    @Override
    public DataItem readDataItem(long id) {
        return db.getDao().readById(id);
    }

    @Override
    public DataItem updateDataItem(DataItem itemToBeUpdated) {
         db.getDao().update(itemToBeUpdated);
         return itemToBeUpdated;
    }

    @Override
    public boolean deleteDataItem(long id) {
        db.getDao().delete(readDataItem(id));
        return true;
    }
}
