package  com.isl.assetManagement.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.isl.assetManagement.room.entity.*

@Dao
interface DataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLevel(levels: List<LevelDBEntity>)

    @Query("SELECT * FROM Level WHERE key = :key")
    fun getLevel(key: String): LiveData<List<LevelDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParam(param: List<ParamEntity>)

    @Query("SELECT * FROM PARAM WHERE paramType = :type")
    fun getParam(type: String): LiveData<List<ParamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskSummary(summary: List<TaskSummaryEntity>)

    @Query("SELECT * FROM TASK_SUMMARY")
    fun getTaskSummary(): LiveData<TaskSummaryEntity>

    @Query("DELETE FROM TASK_SUMMARY")
    fun deleteTaskSummary()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssetRequests(requests: List<AssetRequests>)

    @Query("SELECT * FROM ASSET_REQUEST WHERE tabName = :tabName")
    fun getAssetRequestsByTabName(tabName: String): LiveData<List<AssetRequests>>

    @Query("DELETE FROM ASSET_REQUEST WHERE tabName = :tabName")
    fun AssetRequestsDeleteByTabName(tabName: String)

    @Delete
    suspend fun deleteRequest(request: AssetRequests)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskDetails(taskDetailsEntity: TaskDetailEntity)

    @Query("SELECT * FROM TASK_DETAILS WHERE requestId = :requestId")
    fun getTaskDetailByRequestId(requestId: String): LiveData<TaskDetailEntity>

    @Query("SELECT * FROM TASK_DETAILS WHERE requestId = :requestId")
    suspend fun getTaskDetailByRequestIdSync(requestId: String): TaskDetailEntity?

    @Query("DELETE FROM TASK_DETAILS WHERE requestId = :requestId")
    fun deleteTaskDetailsByRequestId(requestId: String)

}

