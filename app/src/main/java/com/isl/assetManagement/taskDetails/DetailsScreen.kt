package com.isl.assetManagement.taskDetails
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.BottomBar
import com.isl.assetManagement.jetpackcompose.DialogFragmentTopBar
import com.isl.assetManagement.jetpackcompose.RequestDetails
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.itower.MyApp

class DetailsScreen : BottomSheetDialogFragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    // Get the shared ViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(roomRepository)
        ).get(RoomViewModel::class.java)

        // Retrieve requestId from arguments
        arguments?.let {
            requestId = it.getString("requestId") // Get the requestId
        }

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT) // Set transparent background

                val behavior = BottomSheetBehavior.from(this)

                // Set the height to 70% of the screen size
                val seventyPercentHeight = (resources.displayMetrics.heightPixels * 0.85).toInt()
                behavior.peekHeight = seventyPercentHeight
                behavior.isFitToContents = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.isDraggable = false
                // Set the maximum height to 70% screen height
                layoutParams.height = seventyPercentHeight
                layoutParams = layoutParams

            }
        }
       return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          return ComposeView(requireContext()).apply {

            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = 0.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(Color.White) // Apply a background color
                ) {
                    DetailsScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe when DialogFragment B is dismissed
        sharedViewModel.isUpdateTaskDetails.observe(viewLifecycleOwner, Observer { dismissed ->
            if (dismissed) {
                dismissAllowingStateLoss()
            }
        })

    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "NotConstructor")
    @Composable
    fun DetailsScreen() {
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()
        //var map: HashMap<String, String> = hashMapOf()
        //map = Util.stringToHashMap("" + DefaultLevel.msg()["assetStatus"])

        Scaffold(
            topBar = {
                DialogFragmentTopBar(
                    onGrips = {
                        dismiss()
                    },
                    closed = {
                        dismiss()
                    },
                    "Details"
                )
            },

            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        //dismiss()
                        val bundle = Bundle()
                        bundle.putString("requestId",requestId)
                        bundle.putString("mode","Details")
                        val fragment = AllDetailsScreen()
                        fragment.arguments = bundle
                        val transaction = childFragmentManager.beginTransaction()
                        transaction.add(fragment, "AddDetailsScreen")
                        transaction.commitAllowingStateLoss()
                    },
                    onUpdateClicked = {
                        val bundle = Bundle()
                        bundle.putString("requestId",requestId)
                        val fragment = UpdateScreen()
                        fragment.arguments = bundle
                        val transaction = childFragmentManager.beginTransaction()
                        transaction.add(fragment, "UpdateScreen")
                        transaction.commitAllowingStateLoss()
                    },
                    "All Details >",
                    "Update"
                )
            },

            content = { paddingValues -> // Respect bottom bar height
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Apply scaffold padding to prevent overlap
                ) {
                    RequestDetails(taskDetail,0,requireContext())
                }
            }
        )
    }
}