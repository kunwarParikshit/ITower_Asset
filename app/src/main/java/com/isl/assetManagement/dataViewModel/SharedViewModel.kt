package com.isl.assetManagement.dataViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _dialogVerifyAssetDismissed = MutableLiveData<Boolean>()
    val dialogVerifyAssetDismissed: LiveData<Boolean> get() = _dialogVerifyAssetDismissed

    // Function to call when DialogFragment verify asset is dismissed
    fun notifyDialogVerifyAssetDismissed() {
        _dialogVerifyAssetDismissed.value = true
    }
}
