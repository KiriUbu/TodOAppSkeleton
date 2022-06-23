package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public class SyncedDataItemCRUDOperations implements IDataItemCRUDOperations {

    private IDataItemCRUDOperations localOperations;
    private IDataItemCRUDOperations remoteOperations;

    public SyncedDataItemCRUDOperations(IDataItemCRUDOperations localOperations, IDataItemCRUDOperations remoteOperations) {
        this.localOperations = localOperations;
        this.remoteOperations = remoteOperations;
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        DataItem created = localOperations.createDataItem(item);
        remoteOperations.createDataItem(created);
        return created;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return localOperations.readAllDataItems();
    }

    @Override
    public DataItem readDataItem(long id) {
        return localOperations.readDataItem(id);
    }

    @Override
    public DataItem updateDataItem(DataItem itemDataToBeUpdated) {
        DataItem updated = localOperations.updateDataItem(itemDataToBeUpdated);
        remoteOperations.updateDataItem(updated);
        return updated;
    }

    @Override
    public boolean deleteDataItem(long id) {
        if(localOperations.deleteDataItem(id)){
            return remoteOperations.deleteDataItem(id);
        }
        else
        {
           return false;
        }
    }
}
