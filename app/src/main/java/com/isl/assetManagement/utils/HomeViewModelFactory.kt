package com.isl.assetManagement.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RemoteViewModel
import com.isl.assetManagement.room.repository.RemoteRepository

class HomeViewModelFactory (private val repository: RemoteRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteViewModel::class.java)) {
            return RemoteViewModel(repository) as T
            // Pass the repository to the DataViewModel constructor
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}