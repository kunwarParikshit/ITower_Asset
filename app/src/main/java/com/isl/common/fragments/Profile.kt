package com.isl.common.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.utils.DataViewModelFactory

import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.itower.MyApp
import infozech.itower.R

class Profile : Fragment(R.layout.fragment_task) {

    private lateinit var dataRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the data repository
        dataRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())

        // In your ViewModel or Activity/Fragment
       /* CoroutineScope(Dispatchers.IO).launch {
            val value = MyApp.getAssetDatabase().dataDao().getLevel1("4")
            withContext(Dispatchers.Main) {
                var a = value;
            }
        }*/

        // Initialize ViewModel using ViewModelProvider and DataViewModelFactory
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(dataRepository)
        ).get(RoomViewModel::class.java)

      // Pass the 'someKey' to the ViewModel
        val someKey = "2" // Replace with the dynamic key you want
        viewModel.getLevelData(someKey)

        // Observe the LiveData from the ViewModel
        viewModel.getiAssetLevel.observe(viewLifecycleOwner, Observer { levelEntity ->
            // Handle the updated data (e.g., update UI with all fields)
            // 'levelEntity' contains all the fields, such as 'desc', 'value', etc.
            //updateUI(levelEntity)
            val a = levelEntity.get(0).desc
            val b = levelEntity.get(0).value
            val c = levelEntity.get(0).key
        })


        // Set up the Compose UI
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
           // DataListScreen(viewModel = viewModel)
        }
    }

   /* @Composable
    fun DataListScreen(viewModel: DataViewModel) {
        // Observe LiveData from ViewModel using observeAsState
        val data by viewModel.getCaption.observeAsState(emptyList())

        // Display data in LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(data) { item ->
                DataItem(item)  // Create a composable to display each item
            }
        }

        // Fetch data from API and save to Room DB when the screen is displayed
        viewModel.fetchLevelFromApi()
    }

    @Composable
    fun DataItem(item: getMovementList) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ID: ${item.requestID}", // Displaying ID
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Value: ${item.reasonCategory}", // Displaying Value
                style = MaterialTheme.typography.body2
            )
        }
    }*/


}
