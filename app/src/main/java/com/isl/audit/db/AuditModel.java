package com.isl.audit.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.isl.audit.model.AssetListResult;
import com.isl.audit.model.AuditAssetResponse;
import com.isl.audit.model.AuditListResult;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "audit")
public class AuditModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "DataGrid")
    @SerializedName("DataGrid")
    @Expose
    private List<AuditListResult> dataGrid;

    @ColumnInfo(name = "assetTypes")
    private List<AuditAssetResponse> assetTypes;

    @ColumnInfo(name = "assetList")
    private List<AssetListResult> assetList;

    public List<AssetListResult> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<AssetListResult> assetList) {
        this.assetList = assetList;
    }

    public List<AuditAssetResponse> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AuditAssetResponse> assetTypes) {
        this.assetTypes = assetTypes;
    }
    public List<AuditListResult> getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(List<AuditListResult> dataGrid) {
        this.dataGrid = dataGrid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}

class AuditTypeConverter{
    Gson gson = new Gson();
    @TypeConverter
    public List<AuditListResult> fromString(String data){
        if (data == null) {
            Collections.emptyList();
        }

        Type listType = new TypeToken<List<AuditListResult>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String toString(List<AuditListResult> someObjects) {
        return gson.toJson(someObjects);
    }
}

class AuditAssetConverter{
    Gson gson = new Gson();
    @TypeConverter
    public List<AuditAssetResponse> fromString(String data){
        if (data == null) {
            Collections.emptyList();
        }

        Type listType = new TypeToken<List<AuditAssetResponse>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String toString(List<AuditAssetResponse> someObjects) {
        return gson.toJson(someObjects);
    }
}

class AssetListConverter{
    Gson gson = new Gson();
    @TypeConverter
    public List<AssetListResult> fromString(String data){
        if (data == null) {
            Collections.emptyList();
        }

        Type listType = new TypeToken<List<AssetListResult>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String toString(List<AssetListResult> someObjects) {
        return gson.toJson(someObjects);
    }
}
