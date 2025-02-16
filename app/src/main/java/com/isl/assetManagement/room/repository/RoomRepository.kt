package  com.isl.assetManagement.room.repository
import AuthDetails
import android.util.Log
import androidx.compose.ui.semantics.SemanticsProperties.Error
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.isl.assetManagement.api.ApiClient.api_asset
import com.isl.assetManagement.api.ApiClient.api_onm
import com.isl.assetManagement.api.ApiClient.authService
import com.isl.assetManagement.constants.DefaultLevel
import com.isl.assetManagement.requests.TaskUploadPayload
import com.isl.assetManagement.responses.*
import com.isl.assetManagement.room.dao.DataDao
import com.isl.assetManagement.room.entity.*
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.*

class RoomRepository(private val dataDao: DataDao) {

    private val mail = "midc.admin@gmail.com";

    fun getLevel(key: String): LiveData<List<LevelDBEntity>> {
        return dataDao.getLevel(key)
    }

    fun getParam(type: String): LiveData<List<ParamEntity>> {
        return dataDao.getParam(type)
    }

    fun getTaskSummary(): LiveData<TaskSummaryEntity> {
        return dataDao.getTaskSummary()
    }

    fun getAssetRequestsByTabName(key: String): LiveData<List<AssetRequests>> {
        return dataDao.getAssetRequestsByTabName(key)
    }

    fun getTaskDetail(requestId: String): LiveData<TaskDetailEntity> {
    //fun getTaskDetail(requestId: String): LiveData<List<TaskDetailEntity>> {
        return dataDao.getTaskDetailByRequestId(requestId)
    }

    fun parseJson(): AuthDetails {
        val gson = Gson()
        var jsonString = KotlinPrefkeeper.assetinfo
        return gson.fromJson(jsonString, AuthDetails::class.java)
    }

    suspend fun fetchToken(): String? {
        if(KotlinPrefkeeper.isauth.equals("1")) {
            val authDetails = parseJson()
             return withContext(Dispatchers.IO) {
                val call = authService.getAuthToken(
                    clientId = authDetails.clientId,
                    clientSecret = authDetails.clientSecret,
                    grantType = authDetails.grantType,
                    username = authDetails.username,
                    password = authDetails.password

                )
                try {
                    val response =
                        call.execute() // Using execute() for a synchronous call in coroutines
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        return@withContext tokenResponse?.access_token
                    } else {
                        Log.e("DataRepository", "Error: ${response.errorBody()?.string()}")
                        return@withContext null
                    }
                } catch (e: Exception) {
                    Log.e("DataRepository", "Network error: ${e.message}")
                    return@withContext null
                }
            }
        }else{
            return UUID.randomUUID().toString()
        }

    }

    fun fetchAndSaveLevel() {
        // Make the API call to fetch data with GET request
        api_onm.fetchConfiguration(moduleName = "iAsset", configName = "Level")
            .enqueue(object : Callback<List<LevelResponse>> {
                override fun onResponse(
                    call: Call<List<LevelResponse>>,
                    response: Response<List<LevelResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            // Convert API response to Room entities
                            val dataEntities = data.map {
                                LevelDBEntity(
                                    key = it.key?: "",
                                    desc = it.desc?: "",
                                    value = it.value ?: ""  // Use a default value if `value` is null
                                )
                            }

                            // Insert into Room DB in a background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    dataDao.insertLevel(dataEntities)
                                    Log.d("DataRepository", "Data inserted successfully.")
                                } catch (e: Exception) {
                                    Log.e("DataRepository", "Failed to insert data into DB", e)
                                }
                            }
                        } else {
                            Log.e("DataRepository", "API response body is null or empty.")
                        }
                    } else {
                        // Handle error
                        Log.e(
                            "DataRepository",
                            "Error in API response: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<List<LevelResponse>>,
                    t: Throwable
                ) {
                    Log.e("DataRepository", "Network call failed", t)
                }
            })
    }

    fun fetchAndSaveAssetRequests(
        token: String, // Accept token here
        requestId: String,
        requestFlag: String,
        requestStatus: String,
        fromLocation: String,
        toLocation: String,
        fromDate: String,
        toDate: String,
        onDataInserted: (Int) -> Unit // Callback to notify insertion status
    ) {
        val authHeader = "Bearer $token"
        KotlinPrefkeeper.assetUserId?.let {
            api_asset.fetchAssetRequest(
                authHeader = authHeader,
                //userId = "1",
                userId = it,
                requestId = requestId,
                requestFlag = requestFlag,
                requestStatus = requestStatus,
                fromLocation = fromLocation,
                toLocation = toLocation,
                fromDate = fromDate,
                toDate = toDate

            )
                .enqueue(object : Callback<List<AssetRequestResponse>> {
                    override fun onResponse(
                        call: Call<List<AssetRequestResponse>>,
                        response: Response<List<AssetRequestResponse>>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {

                                // Convert API response to Room entities
                                val dataEntities = data.map {
                                    AssetRequests(
                                        requestId = it.requestId?: "",  // Default value if null
                                        fromLocation = it.fromLocation?: "",  // Default value if null
                                        toLocation = it.toLocation ?: "",  // Default value if null
                                        requestDate = it.requestDate?: "",  // Default value if null
                                        reasonCategory = it.reasonCategory?: "",  // Default value if null
                                        reasonSubCategory = it.reasonSubCategory?: "",  // Default value if null
                                        requestStatus = it.requestStatus?: "",  // Default value if null
                                        totalAssetCount = it.totalAssetCount?: 0,  // Default value if null
                                        tabName = requestFlag
                                    )
                                }

                                // Insert into Room DB in a background thread
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        dataDao.AssetRequestsDeleteByTabName(requestFlag);
                                        dataDao.insertAssetRequests(dataEntities)
                                        Log.d("DataRepository", "Data inserted successfully.")
                                        onDataInserted(1)
                                    } catch (e: Exception) {
                                        onDataInserted(0)
                                        Log.e("DataRepository",
                                            "Failed to insert data into DB",e
                                        )
                                    }
                                }
                            } else {
                                onDataInserted(0)
                                Log.e("DataRepository", "API response body is null or empty.")
                            }
                        } else {
                            onDataInserted(0)
                            // Handle error

                            /*// Assuming you have a List of AssetRequests
                                val dataEntities: List<AssetRequests> = listOf(
                                    AssetRequests(
                                        requestID = "R1001",
                                        fromLocation = "IZ10001",
                                        toLocation = "IZ10002",
                                        requestDate = "26-Dec-2024",
                                        reasonCategory = "Test11",
                                        reasonSubCategory = "Test12",
                                        requestStatus = "Waiting approval1",
                                        totalAssetCount = 8,
                                        tabName = "Assigned"
                                    ),
                                    AssetRequests(
                                        requestID = "R1002",
                                        fromLocation = "IZ10003",
                                        toLocation = "IZ10004",
                                        requestDate = "27-Dec-2024",
                                        reasonCategory = "Test21",
                                        reasonSubCategory = "Test22",
                                        requestStatus = "Waiting approval2",
                                        totalAssetCount = 10,
                                        tabName = "Assigned"
                                    )
                                    // Add more AssetRequests objects if needed
                                )

                                // Insert into Room DB in a background thread
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        dataDao.insertAssetRequests(dataEntities)
                                        Log.d("DataRepository", "Data inserted successfully.")
                                    } catch (e: Exception) {
                                        Log.e(
                                            "DataRepository",
                                            "Failed to insert data into DB",
                                            e
                                        )
                                    }
                                }*/

                            Log.e("DataRepository",
                                "Error in API response: ${response.errorBody()?.string()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<List<AssetRequestResponse>>,
                        t: Throwable
                    ) {
                        // Handle failure
                        var a = 5;
                    }
                })
        }
    }

    fun fetchAndSaveParam() {
        // Make the API call to fetch data with GET request
        api_asset.paramData(paramType = "All")
            .enqueue(object : Callback<List<ParamTypeResponse>> {
                override fun onResponse(
                    call: Call<List<ParamTypeResponse>>,
                    response: Response<List<ParamTypeResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            // Convert API response to Room entities
                            val dataEntities = data.map {
                                ParamEntity(paramType = it.paramType,
                                    paramId = it.paramId, paramValue = it.paramValue,
                                            localValue = it.localValue,parentId = it.parentId)
                            }

                            // Insert into Room DB in a background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    dataDao.insertParam(dataEntities)
                                    Log.d("DataRepository", "Data inserted successfully.")
                                } catch (e: Exception) {
                                    Log.e("DataRepository", "Failed to insert data into DB", e)
                                }
                            }
                        } else {
                            Log.e("DataRepository", "API response body is null or empty.")
                        }
                    } else {
                        // Handle error
                        Log.e(
                            "DataRepository",
                            "Error in API response: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<List<ParamTypeResponse>>,
                    t: Throwable
                ) {
                    Log.e("DataRepository", "Network call failed", t)
                }
            })
    }

    fun fetchAndSaveTaskSummary(
        token: String,
        requestId: String,
        requestStatus: String,
        fromLocation: String,
        toLocation: String,
        fromDate: String,
        toDate: String,
        onDataInserted: (Int) -> Unit // Callback to notify insertion status
    ) {
        val authHeader = "Bearer $token"
        KotlinPrefkeeper.assetUserId?.let {
            api_asset.getTaskSummary(
                authHeader = authHeader,
                //userId = "1",
                userId = it,
                requestId = requestId,
                requestStatus = requestStatus,
                fromLocation = fromLocation,
                toLocation = toLocation,
                fromDate = fromDate,
                toDate = toDate
            ).enqueue(object : Callback<List<TaskSummaryResponse>> {
                override fun onResponse(
                    call: Call<List<TaskSummaryResponse>>,
                    response: Response<List<TaskSummaryResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            // Convert API response to Room entities
                            val dataEntity = data.map {
                                TaskSummaryEntity(
                                    assigned = it.assigned ?: 0,  // Default value if null
                                    raised = it.raised ?: 0,  // Default value if null
                                    completed = it.completed ?: 0,  // Default value if null
                                    rejected = it.rejected ?: 0

                                )
                            }

                            // Insert into Room DB in a background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    dataDao.deleteTaskSummary();
                                    dataDao.insertTaskSummary(dataEntity)
                                    Log.d("DataRepository", "Data inserted successfully.")

                                    // Call the callback after successful insert
                                    withContext(Dispatchers.Main) {
                                        onDataInserted(1)  // 1 for success
                                    }

                                } catch (e: Exception) {
                                    Log.e("DataRepository", "Failed to insert data into DB", e)
                                    withContext(Dispatchers.Main) {
                                        onDataInserted(0)  // 0 for failure
                                    }
                                }
                            }
                        } else {
                            Log.e("DataRepository", "API response body is null or empty.")
                            // withContext(Dispatchers.Main) {
                            onDataInserted(0)  // 0 for failure
                            //}
                        }
                    } else {
                        // Handle error
                        Log.e(
                            "DataRepository",
                            "Error in API response: ${response.errorBody()?.string()}"
                        )
                        // withContext(Dispatchers.Main) {
                        onDataInserted(0)  // 0 for failure
                        //  }
                    }
                }

                override fun onFailure(
                    call: Call<List<TaskSummaryResponse>>,
                    t: Throwable
                ) {
                    Log.e("DataRepository", "Network call failed", t)
                    // withContext(Dispatchers.Main) {
                    onDataInserted(0)  // 0 for failure
                    // }
                }
            })
        }
    }

    fun fetchAndSaveTaskDetails(
        token: String,
        requestId: String,
        onDataInserted: (Int) -> Unit // Callback to notify insertion status
    ) {
        val authHeader = "Bearer $token"
        KotlinPrefkeeper.assetUserId?.let {
            api_asset.fetchTaskDetail(
                authHeader = authHeader,
                //userId = "1",
                userId = it,
                requestId = requestId
            ).enqueue(object : Callback<TaskDetailResponce> {
                override fun onResponse(
                    call: Call<TaskDetailResponce>,
                    response: Response<TaskDetailResponce>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {

                            /*val expandedDocument = mutableListOf<Documents>()
                            data.documents?.let { documents ->
                                documents.forEach { document ->
                                    //expandedAssets.add(asset.copy(status = 0,id =""))
                                    expandedDocument.add(
                                        document.copy(status = 1)
                                    )
                                }
                            }*/

                            val expandedAssets = mutableListOf<Assets>()
                            // Iterate through the assets and modify based on approvedQty
                            data.assets?.let { assets ->
                                assets.forEach { asset ->

                                    if (asset.approvedQty >= 1) {
                                           //expandedAssets.add(asset.copy(status = 0,id =""))
                                        expandedAssets.add(asset.copy(status = 0,id = UUID.randomUUID().toString()))

                                    }/*else if (asset.approvedQty == 1) {
                                        expandedAssets.add(asset.copy(status = 0,id = UUID.randomUUID().toString()))
                                    }*/

                                   /* if (asset.approvedQty > 1) {
                                        repeat(asset.approvedQty){
                                            expandedAssets.add(asset.copy(status = 0,approvedQty = 1,
                                                id = UUID.randomUUID().toString()))
                                        }
                                    }else if (asset.approvedQty == 1) {
                                            expandedAssets.add(asset.copy(status = 0,id = UUID.randomUUID().toString()))
                                    }*/



                                   /* if (asset.approvedQty >= 1) {
                                        expandedAssets.add(asset.copy(status = 0,id = UUID.randomUUID().toString()))
                                    }*/
                                    //excluded 0 item
                                }
                            }

                            // Convert API response to Room entities
                            val dataEntity =
                                //data.map {
                                TaskDetailEntity(
                                    requestId = data.requestId ?: "",
                                    movementDate = data.movementDate
                                        ?: "",  // Default value if null
                                    siteCategory = data.siteCategory
                                        ?: "",  // Default value if null
                                    requestDate = data.requestDate ?: "",  // Default value if null
                                    reasonCategory = data.reasonCategory ?: "",
                                    reasonSubCategory = data.reasonSubCategory ?: "",
                                    status = data.status ?: "",
                                    requestor = data.requestor ?: "",
                                    //fromLocation = data.fromLocation ?: defaultLocation(), // Provide a default value
                                    //toLocation = data.toLocation ?: defaultLocation(),     // Provide a default value
                                    fromLocation = data.fromLocation
                                        ?: defaultLocation(), // Provide a default value
                                    toLocation = data.toLocation
                                        ?: defaultLocation(),     // Provide a default value
                                    assets = expandedAssets?: emptyList(),
                                    //assets = data.assets?: emptyList(),
                                    documents = data.documents ?: emptyList(),
                                    timelines = data.timelines ?: emptyList()
                                )
                            // }

                            // Insert into Room DB in a background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    dataDao.deleteTaskDetailsByRequestId(requestId);
                                    dataDao.insertTaskDetails(dataEntity)
                                    Log.d("DataRepository", "Data inserted successfully.")

                                    // Call the callback after successful insert
                                    withContext(Dispatchers.Main) {
                                        onDataInserted(1)  // 1 for success
                                    }

                                } catch (e: Exception) {
                                    Log.e("DataRepository", "Failed to insert data into DB", e)
                                    withContext(Dispatchers.Main) {
                                        onDataInserted(0)  // 0 for failure
                                    }
                                }
                            }
                        } else {
                            Log.e("DataRepository", "API response body is null or empty.")
                            // withContext(Dispatchers.Main) {
                            onDataInserted(0)  // 0 for failure
                            //}
                        }
                    } else {
                        // Handle error
                        Log.e(
                            "DataRepository",
                            "Error in API response: ${response.errorBody()?.string()}"
                        )
                        // withContext(Dispatchers.Main) {
                        onDataInserted(0)  // 0 for failure
                        //  }
                    }
                }

                override fun onFailure(
                    call: Call<TaskDetailResponce>,
                    t: Throwable
                ) {
                    Log.e("DataRepository", "Network call failed", t)
                    // withContext(Dispatchers.Main) {
                    onDataInserted(0)  // 0 for failure
                    // }
                }
            })
        }
    }

    fun defaultLocation() = Location(
        siteId = "",
        address = "",
        locationLevel2 = "",
        locationLevel3 = "",
        locationLevel4 = "",
        latitude = 0.0,
        longitude = 0.0
    )

    /*fun defaultDocument() =
        Documents(
            latitude = 1012.00,
            longitude = 234.12,
            tagName = "RM0001",
            timeStamp = "20-Jun-2024, 14:30",
            url = "https://fastly.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U",
            type = "Site Address 111",
            tempDocId = "Site Name 1111",
            localPath = "QWERT23",
            status = 0
        )

    fun defaultDocumentsList(): List<Documents> {
        return List(5) { defaultDocument() }
    }*/


    suspend fun addAssetToExistingRequestDetails(requestId: String, newAsset: Assets): Boolean {
        val existingData = dataDao.getTaskDetailByRequestIdSync(requestId)

        return if (existingData != null) {
            // Create a mutable copy of the assets list
            val updatedAssets = existingData.assets.toMutableList()

            // Check if the asset with the same ID already exists

            val existingAssetIndex = updatedAssets.indexOfFirst {
                it.id == newAsset.id
            }


            if (existingAssetIndex != -1 && newAsset.approvedQty==1) {
                // Update the existing asset's status
                updatedAssets[existingAssetIndex] = updatedAssets[existingAssetIndex].copy(
                    status = 1,
                    assetId = newAsset.assetId,
                    qrCode = newAsset.qrCode)
            } else {

                val newAsset1 = Assets(
                                assetType = newAsset.assetType,
                                assetId = newAsset.assetId,
                                itemCode = newAsset.itemCode,
                                qrCode = newAsset.qrCode,
                                availableQty = 0,
                                requestedQty = 0,
                                approvedQty = 0,
                                status = 1,
                                id = UUID.randomUUID().toString()
                               )

                // If the asset does not exist, update the ID if needed
                  // Set or generate the ID as needed
                    //newAsset.copy(id = UUID.randomUUID().toString(),
                    //              approvedQty = 1) // Replace with logic to generate or default ID
                // Add the new asset to the list
                updatedAssets.add(newAsset1)
                //updatedAssets.add(newAsset.copy(status = 1))  // Ensure new asset gets status 1
            }

            // Create an updated TaskDetailEntity with modified assets
            val updatedData = existingData.copy(assets = updatedAssets)

            // Insert or update the TaskDetailEntity in the database
            dataDao.insertTaskDetails(updatedData)

            true  // Success
        } else {
            false // Failure because no existing data found
        }
    }


    suspend fun addDocumentToExistingRequestDetails(status : Int , requestId: String, documents: Documents): Boolean {
        val existingData = dataDao.getTaskDetailByRequestIdSync(requestId)

        return if (existingData != null) {
            // Create a mutable copy of the assets list
            val addDoc = existingData.documents.toMutableList()
            addDoc.add(documents.copy(status = status))
            // Create an updated TaskDetailEntity with modified assets
            val updatedData = existingData.copy(documents = addDoc)

            // Insert or update the TaskDetailEntity in the database
            dataDao.insertTaskDetails(updatedData)

            true  // Success
        } else {
            false // Failure because no existing data found
        }
    }

    suspend fun uploadDocument(token: String, requestId: String, body: Documents): DocUploadApiResponse {
        val authHeader = "Bearer $token"
        return withContext(Dispatchers.IO) {  // Run on background thread
            try {
                val response = api_asset.uploadDocument(authHeader, requestId, body)

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext DocUploadApiResponse.Success(it) // Success Case
                    } ?: throw Exception("Empty Response")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    return@withContext DocUploadApiResponse.Error(errorResponse) // Error Case
                }

            } catch (e: HttpException) {
                // Handle API errors (e.g., 400, 500)
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext DocUploadApiResponse.Error(errorResponse)

            } catch (e: Exception) {
                // Handle unexpected exceptions
                return@withContext DocUploadApiResponse.Error(
                    ErrorResponse("1", listOf(ErrorDetail("UNKNOWN_ERROR", e.message ?: "Unknown error occurred")))
                )
            }
        }
    }

    suspend fun addUpdateTaskDetails(
        token: String,
        requestId: String,
        body: TaskUploadPayload
    ): TaskAddUpdateApiRespose {
        val authHeader = "Bearer $token"

        return try {
            val response = api_asset.addUpdateTaskDetails(authHeader, requestId, body)
            //val response = api_asset.addUpdateTaskDetails(requestId, body)

            if (response.isSuccessful) {
                // Parse success response directly
                response.body() ?: TaskAddUpdateApiRespose(
                    flag = "1", // Treat empty body as error
                    message = "Empty response from server",
                    data = null,
                    errors = listOf(ErrorDetail("EMPTY_RESPONSE", "No data received"))
                )
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    Gson().fromJson(errorBody, TaskAddUpdateApiRespose::class.java)
                } catch (e: Exception) {
                    TaskAddUpdateApiRespose(
                        flag = "1",
                        message = "Error parsing error response",
                        data = null,
                        errors = listOf(ErrorDetail("PARSE_ERROR", e.message ?: "Unknown error"))
                    )
                }
                errorResponse
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = try {
                Gson().fromJson(errorBody, TaskAddUpdateApiRespose::class.java)
            } catch (ex: Exception) {
                TaskAddUpdateApiRespose(
                    flag = "1",
                    message = "HTTP error occurred",
                    data = null,
                    errors = listOf(ErrorDetail("HTTP_ERROR", ex.message ?: "Unknown error"))
                )
            }
            errorResponse
        } catch (e: Exception) {
            TaskAddUpdateApiRespose(
                flag = "1",
                message = "Unexpected error occurred",
                data = null,
                errors = listOf(ErrorDetail("UNKNOWN_ERROR", e.message ?: "Unknown error"))
            )
        }
    }


}






