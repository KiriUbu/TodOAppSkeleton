package org.dieschnittstelle.mobile.android.skeleton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityOverviewListitemViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;
import org.dieschnittstelle.mobile.android.skeleton.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncTask;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsynvOperationRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    public static final String LOGGER = "OverviewActivity";

    public static  final Comparator<DataItem> NAME_COPARATOR = Comparator.comparing(DataItem::getName);
    public static  final Comparator<DataItem> CHECKED_AMD_NAME_COPARATOR = Comparator.comparing(DataItem::isChecked).thenComparing(DataItem::getName);
    private ViewGroup viewRoot;
    private FloatingActionButton addNewItemButton;

    private ProgressBar progressBar;
    private ListView listView;
    private ArrayAdapter<DataItem> listViewAdapter;
    private List<DataItem> listViewItems = new ArrayList<>();

    private IDataItemCRUDOperations crudOperations;

    private ActivityResultLauncher<Intent> detailviewForNewItemActivityLauncher;

    private MADAsynvOperationRunner operationRunner;

    private Comparator<DataItem> currentComparator = CHECKED_AMD_NAME_COPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        viewRoot = findViewById(R.id.viewRoot);
        listView = findViewById(R.id.listView);
        addNewItemButton = findViewById(R.id.fab);
        progressBar= findViewById(R.id.progressBar);

        operationRunner = new MADAsynvOperationRunner(this,progressBar);

        listViewAdapter = initialiseListViewAdapter();
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener((adapterView, view, pos, l) -> {
            DataItem selectedItem = listViewAdapter.getItem(pos);
            onListItemSelected(selectedItem);
        });

        initialiseActivityResultLaunchers();

        addNewItemButton.setOnClickListener(view -> onAddNewItem());
      //  crudOperations = SimpleDataItemCRUDOperations.getInstance();
     //   crudOperations = new RoomLocalDataItemCRUDOperations(this.getApplicationContext());
        crudOperations = ((DataItemApplication)getApplication()).getCrudOperations();

        operationRunner.run(
                // call readAllDataItems operations
                () -> crudOperations.readAllDataItems(),
                // once its done process the items returned from it
                items -> {
                    items.forEach(item -> this.addListItemView(item));
                    sortItems();
                    });

    }

    private ArrayAdapter<DataItem>initialiseListViewAdapter(){
       return new ArrayAdapter<>(this,R.layout.activity_overview_listitem_view,listViewItems){
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingListitemView, @NonNull ViewGroup parent) {
                Log.i(LOGGER,"getView() for position "+position+", where existingListitemView: "+existingListitemView);
                //Data we want to show
                DataItem item = super.getItem(position);
                //Data binding object to show the data
                ActivityOverviewListitemViewBinding itemBinding = existingListitemView != null
                        ? (ActivityOverviewListitemViewBinding) existingListitemView.getTag()
                        : DataBindingUtil.inflate(getLayoutInflater(),R.layout.activity_overview_listitem_view,null,false);

                itemBinding.setItem(item);
                itemBinding.setController(OverviewActivity.this);

                //the view in which the data is showing
                View itemView= itemBinding.getRoot();
                itemView.setTag(itemBinding);
                return itemView;
            }
        };
    }
    private void initialiseActivityResultLaunchers(){
        detailviewForNewItemActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    Log.i(LOGGER, "resultCode: "+result.getResultCode()+" data: "+result.getData() );
                    if(result.getResultCode() == DetailviewActivity.STATUS_CREATED || result.getResultCode() ==  DetailviewActivity.STATUS_UPDATED){
                        long itemId =  result.getData().getLongExtra(DetailviewActivity.ARG_ITEM_ID,-1);
                        this.operationRunner.run(
                                () -> crudOperations.readDataItem(itemId),
                                item ->{
                                    if(result.getResultCode() == DetailviewActivity.STATUS_CREATED){
                                        onDataItemCreated(item);
                                    }else if (result.getResultCode() == DetailviewActivity.STATUS_UPDATED){
                                        onDataItemUpdated(item);
                                    }
                                }
                        );
                    }
                }
        );
    }
    private void addListItemView(DataItem item){
        listViewAdapter.add(item);
        listView.setSelection(listViewAdapter.getPosition(item));
    }
    private void onListItemSelected(DataItem item){
       Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
       detailviewIntent.putExtra("testserializable",item);
       detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID,item.getId());
       Log.i(LOGGER,"calling detailview for item "+ item);
       detailviewForNewItemActivityLauncher.launch(detailviewIntent);
    }
    private void onAddNewItem(){
        Intent detailviewIntentforAddNewItem = new Intent(this, DetailviewActivity.class);
        detailviewForNewItemActivityLauncher.launch(detailviewIntentforAddNewItem);
    }
    private void onDataItemCreated(DataItem item){
        this.addListItemView(item);
        sortItems();
    }
    private void onDataItemUpdated(DataItem item){
        DataItem itemToBeUpdated = this.listViewAdapter.getItem(this.listViewAdapter.getPosition(item));
        itemToBeUpdated.setName(item.getName());
        itemToBeUpdated.setChecked(item.isChecked());
        itemToBeUpdated.setDescription(item.getDescription());
        sortItems();
    }
    private void showMessage(String msg){
        Snackbar.make(viewRoot,msg,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sortList){
            showMessage("Alles Sortiert ");
            this.currentComparator = CHECKED_AMD_NAME_COPARATOR;
            sortItems();
            return true;
        }
        else if (item.getItemId() == R.id.deleteAllItemsLocaly){
            showMessage("Alles gelöscht");
            sortItems();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void sortItems(){
        this.listViewItems.sort(this.currentComparator);
        this.listViewAdapter.notifyDataSetChanged();

    }

    public void onCheckedChangedInListView(DataItem item){
        this.operationRunner.run(
                () -> crudOperations.updateDataItem(item),
                updateditem ->{
                    onDataItemUpdated(updateditem);
                    showMessage("Checked change: "+updateditem.isChecked());
                }
        );

    }

}
