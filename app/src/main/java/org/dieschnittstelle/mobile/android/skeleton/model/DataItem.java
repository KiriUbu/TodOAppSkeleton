package org.dieschnittstelle.mobile.android.skeleton.model;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class DataItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;

    @SerializedName("done")  // mapping für Api da dort checked nicht existiert da es done heißt
    private boolean checked;

    //private transient boolen checked; // transient heißt wird nciht übertragen ! bei Api anftrage!


    public DataItem(){
        Log.i("Dataitem","construtor() ivoked");
    }
    public DataItem(String name){
        this.name = name;
    }
    public DataItem(String name, String description){this.name = name; this.description = description;}
    public DataItem(String name, String description,boolean checked){this.name = name; this.description = description; this.checked=checked;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataItem dataItem = (DataItem) o;
        return id == dataItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                ", super.toString(): "+super.toString() +
                '}';
    }
}
