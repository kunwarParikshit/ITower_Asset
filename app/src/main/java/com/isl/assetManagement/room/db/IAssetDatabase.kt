package  com.isl.assetManagement.room.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isl.assetManagement.room.dao.DataDao
import com.isl.assetManagement.room.entity.*

@Database(entities = [LevelDBEntity::class,
                      AssetRequests::class,
                      ParamEntity::class,
                      TaskSummaryEntity::class,
                      TaskDetailEntity::class],
    version = 12, exportSchema = false)
@TypeConverters(Converters::class)
abstract class IAssetDatabase : RoomDatabase() {

    abstract fun dataDao(): DataDao  // Your DAO class

    companion object {
        @Volatile
        private var INSTANCE: IAssetDatabase? = null

        fun getDatabase(context: Context): IAssetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IAssetDatabase::class.java,
                    "asset_database"  // The name of your database
                )
                    .fallbackToDestructiveMigration() // Optional: for handling migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



