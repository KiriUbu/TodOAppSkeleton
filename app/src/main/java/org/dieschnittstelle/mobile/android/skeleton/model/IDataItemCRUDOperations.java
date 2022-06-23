package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public interface IDataItemCRUDOperations {

    // Create
    public DataItem createDataItem(DataItem item);

    // Read
    public List<DataItem> readAllDataItems();
    public DataItem readDataItem(long id);

    // Update
    public DataItem updateDataItem(DataItem itemDataToBeUpdated);

    // Delete
    public boolean deleteDataItem(long id);



}
