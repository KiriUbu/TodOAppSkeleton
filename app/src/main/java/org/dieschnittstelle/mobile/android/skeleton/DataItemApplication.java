package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.dieschnittstelle.mobile.android.skeleton.model.CachedDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.IDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalDataItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SyncedDataItemCRUDOperations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public class DataItemApplication extends Application {

    private IDataItemCRUDOperations crudOperations;

    @Override
    public void onCreate(){
        super.onCreate();
        try{
            if(checkConnectivity().get()){
                IDataItemCRUDOperations crudOperations = new SyncedDataItemCRUDOperations(
                        new RoomLocalDataItemCRUDOperations(this),
                        new RetrofitRemoteDataItemCRUDOperations());
                this.crudOperations = new CachedDataItemCRUDOperations(crudOperations);
                Toast.makeText(this, "Using synced data access.... ", Toast.LENGTH_LONG).show();
            }
            else {
                this.crudOperations = new CachedDataItemCRUDOperations(
                        new RoomLocalDataItemCRUDOperations(this));
                Toast.makeText(this, "Remote api not accessible! Using local data access.... ", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            throw new RuntimeException("Got exception on future.get ", e);
        }
    }

    public IDataItemCRUDOperations getCrudOperations(){
        return this.crudOperations;
    }

    public Future<Boolean> checkConnectivity(){
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Thread(()-> {
            try {
                HttpURLConnection conn =(HttpURLConnection ) new URL("http://192.168.10.106:8080/api/todos").openConnection();
                conn.setConnectTimeout(500);
                conn.setReadTimeout(500);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                conn.getInputStream();
                result.complete(true);
            } catch (IOException e) {
                Log.e("DataItemApplication", "Got exception during connections");
                result.complete(false);
            }
        }).start();
        return result;
    }
}
