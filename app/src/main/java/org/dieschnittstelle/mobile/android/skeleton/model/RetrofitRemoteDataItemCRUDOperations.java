package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.dieschnittstelle.mobile.android.skeleton.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitRemoteDataItemCRUDOperations implements IDataItemCRUDOperations{

    public interface TodoWebApi{
        @POST("/api/todos")
        public Call<DataItem> createTodo(@Body DataItem item);
        @GET("/api/todos")
        public Call<List<DataItem>> readAllTodos();
        @GET("/api/todos/{todoId}")
        public Call<DataItem> readTodo(@Path("todoId")long id);
        @PUT("/api/todos/{todoId}")
        public Call<DataItem> updateTodo(@Path("todoId") long id,@Body DataItem item);
        @DELETE("/api/todos/{todoId}")
        public Call<Boolean> deleteTodo(@Path("todoId") long id);

    }

    private TodoWebApi webApi;

    public RetrofitRemoteDataItemCRUDOperations(){

        System.out.println((R.string.IP_of_Rest_Api_Server+"+++++++++++"));

        Retrofit apiBase = new Retrofit.Builder()
                .baseUrl("http://192.168.10.106:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webApi = apiBase.create(TodoWebApi.class);
    }



    @Override
    public DataItem createDataItem(DataItem item) {
        try {
            return webApi.createTodo(item).execute().body();
        }catch (Exception e){
            throw new RuntimeException("got exception: "+e,e);
        }
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            return webApi.readAllTodos().execute().body();
        }catch (Exception e){
            throw new RuntimeException("got exception: "+e,e);
        }
    }

    @Override
    public DataItem readDataItem(long id) {
        try {
            return webApi.readTodo(id).execute().body();
        }catch (Exception e){
            throw new RuntimeException("got exception: "+e,e);
        }
    }

    @Override
    public DataItem updateDataItem(DataItem itemDataToBeUpdated) {
        try {
            return webApi.updateTodo(itemDataToBeUpdated.getId(),itemDataToBeUpdated).execute().body();
        }catch (Exception e){
            throw new RuntimeException("got exception: "+e,e);
        }

    }

    @Override
    public boolean deleteDataItem(long id) {
        try {
            return webApi.deleteTodo(id).execute().body();
        }catch (Exception e){
            throw new RuntimeException("got exception: "+e,e);
        }
    }
}
