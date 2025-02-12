package  com.isl.assetManagement.utils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.room.repository.RoomRepository

class DataViewModelFactory(private val repository: RoomRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(repository) as T
        // Pass the repository to the DataViewModel constructor
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
