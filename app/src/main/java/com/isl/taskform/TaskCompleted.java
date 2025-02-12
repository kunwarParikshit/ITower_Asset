package com.isl.taskform;

import com.isl.modal.Data;
import com.isl.modal.FormControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhakan on 11/16/2018.
 */

public interface TaskCompleted {
     // Define data you like to return from AysncTask
        public void dataSubmitted(String result);
        public void initializeForm();
        public void autoCompleteDataFetchComplete(ArrayList<Data> result, FormControl formControl);

}