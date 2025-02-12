package com.isl.energy;

import java.util.ArrayList;

/**
 * Created by dhakan on 11/16/2018.
 */

public interface TaskCompleted {
     // Define data you like to return from AysncTask
        public void onLocationTaskComplete(String result);
        public void onEnergyParamTaskComplete(String result);
        public void onPurchaseGridRPTComplete(String result);
        public void onFillingGridRPTComplete(String result);

}