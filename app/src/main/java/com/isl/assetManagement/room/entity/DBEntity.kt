package  com.isl.assetManagement.room.entity
import androidx.room.*
import com.isl.assetManagement.responses.*

@Entity(tableName = "LEVEL")
data class LevelDBEntity(
    @PrimaryKey val key: String,
    val desc: String,
    val value: String
)

@Entity(tableName = "PARAM")
data class ParamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-generated ID
    val paramType: String,
    val paramId: String,
    val paramValue: String,
    val localValue: String,
    val parentId: String
    )

@Entity(tableName = "TASK_SUMMARY")
data class TaskSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-generated ID
    val assigned: Int,
    val raised: Int,
    val completed: Int,
    val rejected: Int
)

@Entity(tableName = "ASSET_REQUEST")
data class AssetRequests(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-generated ID
    val requestId: String,  // Unique String identifier
    val fromLocation: String,
    val toLocation: String,
    val requestDate: String,
    val reasonCategory: String,
    val reasonSubCategory: String,
    val requestStatus: String,  // Status of the request (e.g., "Raised", "Awaiting")
    val totalAssetCount: Int,
    val tabName: String  // Flag for the tab (e.g., "Raised", "Awaiting")
)

@Entity(tableName = "TASK_DETAILS")
data class TaskDetailEntity(
    @PrimaryKey val requestId: String,
    val movementDate: String,
    val siteCategory: String,
    val requestDate: String,
    val reasonCategory: String,
    val reasonSubCategory: String,
    val status: String,
    val requestor: String,
    val fromLocation: Location,
    val toLocation: Location,
    val assets: List<Assets>,
    val documents: List<Documents>,
    val timelines: List<Timeline>
)