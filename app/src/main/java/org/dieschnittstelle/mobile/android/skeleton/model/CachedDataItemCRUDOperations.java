package org.dieschnittstelle.mobile.android.skeleton.model;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CachedDataItemCRUDOperations implements IDataItemCRUDOperations {


    private Map<Long,DataItem> itemsMap = new HashMap<>();
    private IDataItemCRUDOperations realCrudOperations;


    public CachedDataItemCRUDOperations(IDataItemCRUDOperations realCrudOperations){
        this.realCrudOperations = realCrudOperations;

    }

    @Override
    public DataItem createDataItem(DataItem item) {
        DataItem created = realCrudOperations.createDataItem(item);
        itemsMap.put(created.getId(),created);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        if(itemsMap.size() == 0){
            realCrudOperations.readAllDataItems().forEach(item -> {
                itemsMap.put(item.getId(),item);
            });
        }
        return new ArrayList<>(itemsMap.values());
    }

    @Override
    public DataItem readDataItem(long id) {
        if(!itemsMap.containsKey(id)){
            DataItem item = realCrudOperations.readDataItem(id);
            if(item != null){
                itemsMap.put(item.getId(),item);
            }
            return item;
        }
        return itemsMap.get(id);
    }

    @Override
    public DataItem updateDataItem(DataItem itemToBeUpdated) {
        DataItem updated= this.realCrudOperations.updateDataItem(itemToBeUpdated);
        itemsMap.put(itemToBeUpdated.getId(),updated);
        return updated;
    }

    @Override
    public boolean deleteDataItem(long id) {
        if(this.realCrudOperations.deleteDataItem(id)){
            itemsMap.remove(id);
            return true;
        }
        else{
            return false;
        }
    }
}
