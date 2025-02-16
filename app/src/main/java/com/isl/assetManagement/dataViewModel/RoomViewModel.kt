package  com.isl.assetManagement.dataViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.assetManagement.requests.TaskUploadPayload
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.responses.DocUploadApiResponse
import com.isl.assetManagement.responses.Documents
import com.isl.assetManagement.responses.TaskAddUpdateApiRespose
import com.isl.assetManagement.room.entity.*
import com.isl.assetManagement.room.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RoomViewModel(private val repository: RoomRepository) : ViewModel() {
    private val _getiAssetLevel = MutableLiveData<List<LevelDBEntity>>()
    val getiAssetLevel: LiveData<List<LevelDBEntity>> = _getiAssetLevel
    // Fetch data using the dynamic 'key'
    fun getLevelData(key: String) {
        repository.getLevel(key).observeForever { levelList ->
            // Update LiveData with the new data from the repository
            _getiAssetLevel.postValue(levelList)
        }
    }

    private val _getiAssetParam = MutableLiveData<List<ParamEntity>>()
    val getiAssetParam: LiveData<List<ParamEntity>> = _getiAssetParam
    // Fetch data using the dynamic 'key'
    fun getParam(key: String) {
        repository.getParam(key).observeForever { levelList ->
            // Update LiveData with the new data from the repository
            _getiAssetParam.postValue(levelList)
        }
    }

    private val _getTaskSummary = MutableLiveData<TaskSummaryEntity>()
    val getTaskSummary: LiveData<TaskSummaryEntity> = _getTaskSummary
    fun getTaskSummary() {
        repository.getTaskSummary().observeForever { levelList ->
            // Update LiveData with the new data from the repository
            _getTaskSummary.postValue(levelList)
        }
    }


    private val _getiAssetRequest = MutableLiveData<List<AssetRequests>>()
    val iAssetRequest: LiveData<List<AssetRequests>> = _getiAssetRequest
    // Fetch data using the dynamic 'key'
    fun getiAssetRequest(key: String) {
        repository.getAssetRequestsByTabName(key).observeForever { levelList ->
            // Update LiveData with the new data from the repository
            _getiAssetRequest.postValue(levelList)
        }
    }

    private val _getTaskDetail = MutableLiveData<TaskDetailEntity>()
    val getTaskDetail: LiveData<TaskDetailEntity> = _getTaskDetail

    // Fetch data using the dynamic 'key'
    fun getTaskDetail(requestId: String) {
        repository.getTaskDetail(requestId).observeForever { levelList ->
            // Update LiveData with the new data from the repository
            _getTaskDetail.postValue(levelList)
        }
    }

    fun addUpdateAssetToExistingRequestDetails(requestId: String, newAsset: Assets, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addAssetToExistingRequestDetails(requestId, newAsset)

            withContext(Dispatchers.Main) {
                onComplete(result) // Call the callback with the result
            }
        }
    }

    fun addDocToExistingRequestDetails(status : Int, requestId: String, documents: Documents,
                                       onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addDocumentToExistingRequestDetails(status,requestId, documents)

            withContext(Dispatchers.Main) {
                onComplete(result) // Call the callback with the result
            }
        }
    }




    fun fetchLevelFromApi() {
        viewModelScope.launch {
            repository.fetchAndSaveLevel()  // Trigger API call and save the result to Room DB
        }
    }

    fun fetchParamFromApi() {
        viewModelScope.launch {
            repository.fetchAndSaveParam()  // Trigger API call and save the result to Room DB
        }
    }

    fun fetchAndSaveTaskSummaryFromApi(
        token: String,
        requestId: String,
        requestStatus: String,
        fromLocation: String,
        toLocation: String,
        fromDate: String,
        toDate: String,
        onDataInserted: (Int) -> Unit // callback to pass success/failure result
    ) {
        viewModelScope.launch {
            repository.fetchAndSaveTaskSummary(
                token,requestId, requestStatus, fromLocation, toLocation,
                fromDate, toDate,onDataInserted)
        }
    }

    fun fetchAndSaveAssetRequestFromApi(
        token: String, // Accept token here
        requestFlag: String,
        requestId: String,
        requestStatus: String,
        fromLocation: String,
        toLocation: String,
        fromDate: String,
        toDate: String,
        onDataInserted: (Int) -> Unit // callback to pass success/failure result
    ) {
        viewModelScope.launch {
            repository.fetchAndSaveAssetRequests(
                token,requestId,requestFlag, requestStatus, fromLocation, toLocation,
                fromDate, toDate,onDataInserted)
        }
    }

    fun fetchAndSaveTaskDetailFromApi(
        token: String,
        requestId: String,
        onDataInserted: (Int) -> Unit // callback to pass success/failure result
    ) {
        viewModelScope.launch {
            repository.fetchAndSaveTaskDetails(
                token,requestId,onDataInserted)
        }
    }

    fun uploadDocument(
        token: String,
        requestId: String,
        body: Documents,
        onResult: (DocUploadApiResponse) -> Unit
    ) {
        viewModelScope.launch {
            val response = repository.uploadDocument(token, requestId, body)
            onResult(response)
        }
    }

    fun addUpdateResuest(
        token: String,
        requestId: String,
        body: TaskUploadPayload,
        onResult: (TaskAddUpdateApiRespose) -> Unit
    ) {
        viewModelScope.launch {
            val response = repository.addUpdateTaskDetails(token, requestId, body)
            onResult(response)
        }
    }

}



