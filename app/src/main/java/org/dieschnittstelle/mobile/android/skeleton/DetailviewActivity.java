package org.dieschnittstelle.mobile.android.skeleton;

import static org.dieschnittstelle.mobile.android.skeleton.OverviewActivity.LOGGER;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;
import org.dieschnittstelle.mobile.android.skeleton.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsynvOperationRunner;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewmodel {

    public static String ARG_ITEM_ID = "itemId";

    public static int STATUS_CREATED = 42;
    public static int STATUS_UPDATED = -42;

    private DataItem item;
    private ActivityDetailviewBinding binding;

    private IDataItemCRUDOperations crudOperations;
    private MADAsynvOperationRunner operationRunner;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding=DataBindingUtil.setContentView(this,R.layout.activity_detailview);
      //  this.crudOperations =  new RoomLocalDataItemCRUDOperations(this.getApplicationContext()); //SimpleDataItemCRUDOperations.getInstance();
        this.crudOperations =  crudOperations = ((DataItemApplication)getApplication()).getCrudOperations();
        this.progressBar = findViewById(R.id.progressBar);
        this.operationRunner = new MADAsynvOperationRunner(this,progressBar);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID,-1);
        if (itemId !=-1 ){
            operationRunner.run(
                    // operation
                    () -> this.item = this.crudOperations.readDataItem(itemId),
                    // onOperationResult
                    item -> {
                       this.item = item;
                       this.binding.setViewmodel(this);
                    });

        }
        Log.i(LOGGER,"showing detailview for item "+ item);

        if (this.item == null) {
            this.item = new DataItem();
        }
        this.binding.setViewmodel(this);
    }

    public DataItem getItem() {
        return this.item;
    }

    public void onSaveItem() {
        Intent returnIntent = new Intent();
        int resultCode = item.getId() >0 ? STATUS_UPDATED : STATUS_CREATED;
        operationRunner.run(() ->
                item.getId() > 0 ? crudOperations.updateDataItem(item) : crudOperations.createDataItem(item),
                item -> {
                    this.item = item;
                    returnIntent.putExtra(ARG_ITEM_ID, this.item.getId());
                    setResult(resultCode, returnIntent);
                    finish();
                });
    }
}
