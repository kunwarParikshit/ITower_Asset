package com.isl.audit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.isl.audit.model.AssetListResult;
import com.isl.audit.model.AuditAssetResponse;
import com.isl.audit.model.AuditListResult;

import java.util.List;

@Dao
public interface AuditDao {
    @Query("Select * from audit")
    List<AuditModel> getAuditList();

    @Query("Select * from audit WHERE type=:type")
    AuditModel getAuditData(int type);

    @Insert
    void insertAudit(AuditModel auditModel);

    @Query("DELETE FROM audit")
    void deleteAllData();

    @Delete
    void delete(AuditModel model);

    @Query("DELETE FROM audit WHERE type = :type")
    void deleteData(int type);

    @Query("UPDATE audit SET DataGrid =:list WHERE type = :type")
    void updateDataGrid(int type, List<AuditListResult> list);

    @Query("UPDATE audit SET assetTypes =:assetType WHERE type = :type")
    void updateAssetType(int type, List<AuditAssetResponse> assetType);

    @Query("UPDATE audit SET assetList =:assetList WHERE type = :type")
    void updateAssetList(int type, List<AssetListResult> assetList);
}
