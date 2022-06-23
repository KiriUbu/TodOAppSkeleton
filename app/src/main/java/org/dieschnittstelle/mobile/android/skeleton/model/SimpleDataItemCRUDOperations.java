package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleDataItemCRUDOperations implements IDataItemCRUDOperations {

    private static SimpleDataItemCRUDOperations instance;

    private Map<Long,DataItem> itemsMap = new HashMap<>();
    private long idcount = 0;

    public static SimpleDataItemCRUDOperations getInstance(){
        return instance = instance == null
                          ? new SimpleDataItemCRUDOperations()
                          : instance;

    }

    private SimpleDataItemCRUDOperations(){
        Arrays.asList("Käse","Wurst","Milch","bier","Block käse","salz","pfeffer")
                .forEach(name -> this.createDataItem(new DataItem(name)));
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        item.setId(++idcount);
        itemsMap.put(item.getId(),item);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
        }
        return new ArrayList<>(itemsMap.values());
    }

    @Override
    public DataItem readDataItem(long id) {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
        }
        return itemsMap.get(id);
    }

    @Override
    public DataItem updateDataItem(DataItem itemToBeUpdated) {
        return itemsMap.put(itemToBeUpdated.getId(),itemToBeUpdated);
    }

    @Override
    public boolean deleteDataItem(long id) {
        itemsMap.remove(id);
        return true;
    }
}
